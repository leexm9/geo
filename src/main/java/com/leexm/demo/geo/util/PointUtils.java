package com.leexm.demo.geo.util;

import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * @author leexm
 * @date 2019-09-01 18:23
 */
public class PointUtils {

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

}
