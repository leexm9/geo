package com.leexm.demo.geo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.io.IOException;
import java.util.*;

/**
 * @author leexm
 * @date 2019-09-01 18:23
 */
public class GeoUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeoUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static MongoGeoPoint point2MongoPoint(GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }
        MongoGeoPoint mongoGeoPoint = new MongoGeoPoint();
        mongoGeoPoint.setName(geoPoint.getName());
        mongoGeoPoint.setDetail(geoPoint.getDetail());
        GeoJsonPoint coordinate = new GeoJsonPoint(geoPoint.getLng(), geoPoint.getLat());
        mongoGeoPoint.setCoordinate(coordinate);
        return mongoGeoPoint;
    }

    /**
     * 将 geoJsonPoint 转成特定类型 { "type": "Point", "coordinates": [lng, lat] }
     * @return
     */
    public static String geoJsonPoint2String(GeoJsonPoint geoJsonPoint) {
        if (geoJsonPoint == null || geoJsonPoint.getCoordinates() == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", "Point");
        double[] coordinates = {geoJsonPoint.getX(), geoJsonPoint.getY()};
        map.put("coordinates", coordinates);
        String result = null;
        try {
            result = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("[GeoUtils] convert geoJsonPoint:{} to String exception:", geoJsonPoint, e);
        }
        return result;
    }

    /**
     * 将 { "type": "Point", "coordinates": [lng, lat] } 转成 GeoJsonPoint
     * @param str
     * @return
     */
    public static GeoJsonPoint str2GeoJsonPoint(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        GeoJsonPoint geoJsonPoint = null;
        try {
            JsonNode root = mapper.readTree(str);
            String type = root.path("type").asText();
            if (StringUtils.equals(type, "Point")) {
                JsonNode coordinates = root.get("coordinates");
                if (coordinates.isArray()) {
                    geoJsonPoint = new GeoJsonPoint(coordinates.get(0).asDouble(), coordinates.get(1).asDouble());
                }
            }
        } catch (IOException e) {
            logger.error("[GeoUtils] convert string:{} to GeoJsonPoint exception:", str, e);
        }
        return geoJsonPoint;
    }

    /**
     * 将 geoJsonPolygon 转成特定类型
     *
     * 没有洞
     * { "type": "Polygon",
     *    "coordinates": [
     *       [[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]
     *    ]
     * }
     *
     * 有洞
     * { "type": "Polygon",
     *    "coordinates": [
     *       [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],
     *       [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]
     *    ]
     * }
     *
     * @return
     */
    public static String geoJsonPolygon2String(GeoJsonPolygon geoJsonPolygon) {
        if (geoJsonPolygon == null || geoJsonPolygon.getCoordinates() == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("type", "Polygon");

        List<GeoJsonLineString> lineStrings = geoJsonPolygon.getCoordinates();
        List<List<double[]>> array = new ArrayList<>(lineStrings.size());
        for (GeoJsonLineString lineString : lineStrings) {
            List<Point> points = lineString.getCoordinates();
            List<double[]> arrayPoints = new ArrayList<>(points.size());
            for (Point point : points) {
                arrayPoints.add(point2Array(point));
            }
            array.add(arrayPoints);
        }
        map.put("coordinates", array.toArray());

        String result = null;
        try {
            result = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("[GeoUtils] convert geoJsonPolygon:{} to String exception:", geoJsonPolygon, e);
        }
        return result;
    }

    /**
     * 转成 GeoJsonPolygon 类型
     *
     * 没有洞
     * { "type": "Polygon",
     *    "coordinates": [
     *       [[100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0]]
     *    ]
     * }
     *
     * 有洞
     * { "type": "Polygon",
     *    "coordinates": [
     *       [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],
     *       [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]
     *    ]
     * }
     *
     * @param str
     * @return
     */
    public static GeoJsonPolygon str2GeoJsonPolygon(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        GeoJsonPolygon geoJsonPolygon = null;
        try {
            JsonNode root = mapper.readTree(str);
            String type = root.path("type").asText();
            if (!StringUtils.equals(type, "Polygon")) {
                return null;
            }
            JsonNode coordinates = root.get("coordinates");
            if (coordinates == null || !coordinates.isArray()) {
                return null;
            }
            List<List<Point>> array = new ArrayList<>(coordinates.size());
            for (int i = 0; i < coordinates.size(); i++) {
                JsonNode node = coordinates.get(i);
                if (node.isArray()) {
                    List<Point> points = new ArrayList<>(node.size());
                    Iterator<JsonNode> iter = node.iterator();
                    while (iter.hasNext()) {
                        JsonNode item = iter.next();
                        Point point = new Point(item.get(0).asDouble(), item.get(1).asDouble());
                        points.add(point);
                    }
                    array.add(points);
                }
            }
            geoJsonPolygon = new GeoJsonPolygon(array.get(0));
            if (array.size() > 1) {
                for (int i = 1; i < array.size(); i++) {
                    GeoJsonLineString item = new GeoJsonLineString(array.get(i));
                    geoJsonPolygon = geoJsonPolygon.withInnerRing(item);
                }
            }
        } catch (IOException e) {
            logger.error("[GeoUtils] convert string:{} to GeoJsonPolygon exception:", str, e);
        }
        return geoJsonPolygon;
    }

    private static double[] point2Array(Point point) {
        return new double[]{point.getX(), point.getY()};
    }

    public static void main(String[] args) {
        String str = "{\"x\":123.4141,\"y\":85.4141,\"type\":\"Point\",\"coordinates\":[123.4141,85.4141]}";
        GeoJsonPoint point = str2GeoJsonPoint(str);
        System.out.println(geoJsonPoint2String(point));

        str = "{ \"type\": \"Polygon\",\n" +
                "    \"coordinates\": [\n" +
                "      [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ],\n" +
                "      [ [100.2, 0.2], [100.8, 0.2], [100.8, 0.8], [100.2, 0.8], [100.2, 0.2] ]\n" +
                "      ]\n" +
                "   }";
        GeoJsonPolygon polygon = str2GeoJsonPolygon(str);
        System.out.println(geoJsonPolygon2String(polygon));
    }

}
