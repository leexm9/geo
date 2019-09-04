package com.leexm.demo.geo.dal.aerospike;

import com.aerospike.client.*;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPolygon;
import com.leexm.demo.geo.util.GeoUtils;
import com.leexm.demo.geo.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leexm
 * @date 2019-09-04 00:48
 */
@Component("aerospikeGeoDao")
public class AerospikeGeoDao {

    private static final Logger logger = LoggerFactory.getLogger(AerospikeGeoDao.class);

    private static final String NAME_SPACE = "ns1";

    // 地理坐标点对应的 Set
    private static final String POINT_SET = "geo_point";

    // 地理区域对应的 Set
    private static final String POLYGON_SET = "geo_polygon";

    // 主键
    private static final String ID = "id";

    private final WritePolicy defaultWritePolicy = new WritePolicy();

    @Autowired
    private AerospikeClient aerospikeClient;

    /**
     * 新增地址信息
     *
     * @param geoPoint  mongo 中使用的对象
     */
    public boolean insertPoint(MongoGeoPoint geoPoint) {
        if (geoPoint == null || geoPoint.getCoordinate() == null) {
            return false;
        }

        boolean flag = true;
        // 主键
        Key key = new Key(NAME_SPACE, POINT_SET, geoPoint.getId());
        // 将 Id 同时作为一个 bin 存储
        Bin id = new Bin(ID, geoPoint.getId());
        Bin name = new Bin("name", geoPoint.getName());
        Bin detail = new Bin("detail", geoPoint.getDetail());
        Value.GeoJSONValue pointValue = new Value.GeoJSONValue(GeoUtils.geoJsonPoint2String(geoPoint.getCoordinate()));
        Bin coordinate = new Bin("coordinate", pointValue);
        try {
            aerospikeClient.put(defaultWritePolicy, key, id, name, detail, coordinate);
        } catch (AerospikeException e) {
            flag = false;
            logger.error("[AerospikeGeoDao] insert point:{} occur exception:", geoPoint, e);
        }
        return flag;
    }

    /**
     * 根据 主键 取地址信息
     *
     * @param
     * @return
     */
    public MongoGeoPoint queryById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        MongoGeoPoint geoPoint = null;

        Key key = new Key(NAME_SPACE, POINT_SET, id);
        try {
            Record record = aerospikeClient.get(null, key);
            if (record != null) {
                geoPoint = new MongoGeoPoint();
                geoPoint.setId(record.getString(ID));
                geoPoint.setName(record.getString("name"));
                geoPoint.setDetail(record.getString("detail"));
                GeoJsonPoint point = GeoUtils.str2GeoJsonPoint(record.getGeoJSONString("coordinate"));
                geoPoint.setCoordinate(point);
            }
        } catch (AerospikeException e) {
            logger.error("[AerospikeGeoDao] query point, id:{} occur exception:", id, e);
        }
        return geoPoint;
    }

    /**
     * 取 ({lng} {lat}) 为圆心， 半径 {radius} 范围内的地址
     *
     * @param lng 经度
     * @param lat 纬度
     * @param radius 单位：米
     * @return
     */
    public List<MongoGeoPoint> queryWithinRadius(double lng, double lat, double radius) {
        List<MongoGeoPoint> geoPoints = new ArrayList<>();

        Statement stmt = new Statement();
        stmt.setNamespace(NAME_SPACE);
        stmt.setSetName(POINT_SET);
        stmt.setBinNames(ID, "name", "detail", "coordinate");
        stmt.setFilter(Filter.geoWithinRadius("coordinate", lng, lat, radius));
        try {
            RecordSet rs = aerospikeClient.query(null, stmt);
            while (rs.next()) {
                Record record = rs.getRecord();
                MongoGeoPoint geoPoint = new MongoGeoPoint();
                geoPoint.setId(record.getString(ID));
                geoPoint.setName(record.getString("name"));
                geoPoint.setDetail(record.getString("detail"));
                GeoJsonPoint point = GeoUtils.str2GeoJsonPoint(record.getGeoJSONString("coordinate"));
                geoPoint.setCoordinate(point);
                geoPoints.add(geoPoint);
            }
        } catch (AerospikeException e) {
            logger.error("[AerospikeGeoDao] query points, lng:{}, lat:{} occur exception:", lng, lat, e);
        }
        return geoPoints;
    }

    /**
     * 新增区域的地理信息
     *
     * @param geoPolygon
     * @return
     */
    public boolean insertPolygon(MongoGeoPolygon geoPolygon) {
        if (geoPolygon == null || geoPolygon.getRegional() == null) {
            return false;
        }

        boolean flag = true;
        // 主键
        Key key = new Key(NAME_SPACE, POLYGON_SET, geoPolygon.getId());
        // 将 Id 同时作为一个 bin 存储
        Bin id = new Bin(ID, geoPolygon.getId());
        Bin name = new Bin("name", geoPolygon.getName());
        Bin detail = new Bin("detail", geoPolygon.getDetail());
        Value.GeoJSONValue polygonValue = new Value.GeoJSONValue(GeoUtils.geoJsonPolygon2String(geoPolygon.getRegional()));
        Bin coordinate = new Bin("regional", polygonValue);
        try {
            aerospikeClient.put(defaultWritePolicy, key, id, name, detail, coordinate);
        } catch (AerospikeException e) {
            flag = false;
            logger.error("[AerospikeGeoDao] insert polygon:{} occur exception:", geoPolygon, e);
        }
        return flag;
    }

    /**
     * 查找指定坐标所在的地理区域
     *
     * @param coordinate 坐标
     * @return
     */
    public MongoGeoPolygon queryPolygonByPoint(Point coordinate) {
        MongoGeoPolygon geoPolygon = null;

        Statement stmt = new Statement();
        stmt.setNamespace(NAME_SPACE);
        stmt.setSetName(POLYGON_SET);
        stmt.setBinNames(ID, "name", "detail", "regional");
        GeoJsonPoint jsonPoint = new GeoJsonPoint(coordinate);
        stmt.setFilter(Filter.geoContains("regional", JsonUtils.obj2String(jsonPoint)));
        try {
            RecordSet rs = aerospikeClient.query(null, stmt);
            while (rs.next()) {
                Record record = rs.getRecord();
                geoPolygon = new MongoGeoPolygon();
                geoPolygon.setId(record.getString(ID));
                geoPolygon.setName(record.getString("name"));
                geoPolygon.setDetail(record.getString("detail"));
                GeoJsonPolygon polygon = GeoUtils.str2GeoJsonPolygon(record.getGeoJSONString("regional"));
                geoPolygon.setRegional(polygon);
                break;
            }
        } catch (Exception e) {
            logger.error("[AerospikeGeoDao] query polygon which contains point:{} occur exception:", coordinate, e);
        }
        return geoPolygon;
    }

}