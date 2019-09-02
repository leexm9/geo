package com.leexm.demo.geo.dal.mongo.dao;

import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 项目层次计较简单，也没有复杂的调用关系，就不再按接口、实现类方式构建了
 *
 * @author leexm
 * @date 2019-09-01 00:55
 */
@Component("mongoGeoDao")
public class MongoGeoDao {

    private static final Logger logger = LoggerFactory.getLogger(MongoGeoDao.class);

    private static final String INDEX_FIELD = "coordinate";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 新增地址信息
     * @param geoPoint
     * @return
     */
    public boolean insertPoint(MongoGeoPoint geoPoint) {
        if (geoPoint == null || geoPoint.getCoordinate() == null) {
            return false;
        }

        boolean flag = true;
        try {
            mongoTemplate.save(geoPoint);
        } catch (Exception e) {
            flag = false;
            logger.error("[MongoGeoDao] insert point:{} occur exception:", geoPoint, e);
        }
        return flag;
    }

    /**
     * 根据名称取地址信息
     * @param
     * @return
     */
    public MongoGeoPoint queryById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        MongoGeoPoint point = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        try {
            point = mongoTemplate.findOne(query, MongoGeoPoint.class);
        } catch (Exception e) {
            logger.error("[MongoGeoDao] query point, id:{} occur exception:", id, e);
        }
        return point;
    }

    /**
     * 根据名称取地址信息
     * @param name
     * @return
     */
    public MongoGeoPoint queryByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        MongoGeoPoint point = null;
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        try {
            point = mongoTemplate.findOne(query, MongoGeoPoint.class);
        } catch (Exception e) {
            logger.error("[MongoGeoDao] query point, name:{} occur exception:", name, e);
        }
        return point;
    }

    /**
     * 取距 {name} {distance}范围内的地点集合
     * @param name
     * @param radius, 单位：米
     * @return
     */
    public List<MongoGeoPoint> nearWithin(String name, double radius) {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("name is blank.");
        }
        MongoGeoPoint center = this.queryByName(name);
        if (center == null) {
            return null;
        }
        List<MongoGeoPoint> points = this.queryWithinRadius(center.getLng(), center.getLat(), radius);
        points.remove(center);
        return points;
    }

    /**
     * 取 ({lng} {lat}) 为圆心， 半径 {radius} 范围内的地址
     * @param radius
     * @return
     */
    public List<MongoGeoPoint> queryWithinRadius(double lng, double lat, double radius) {
        List<MongoGeoPoint> points = null;

        // 单位km
        double radius2 = radius / 1000.0;
        Distance distance = new Distance(radius2, Metrics.KILOMETERS);
        Point point = new Point(lng, lat);
        Circle circle = new Circle(point, distance);
        Query query = Query.query(Criteria.where(INDEX_FIELD).withinSphere(circle));
        try {
            points = mongoTemplate.find(query, MongoGeoPoint.class);
        } catch (Exception e) {
            logger.error("[MongoGeoDao] query points, lng:{}, lat:{} occur exception:", lng, lat, e);
        }
        return points;
    }

    /**
     * 查找距给定坐标点 ({lng} {lat}) 范围在 [minDistance, maxDistance) 内的点
     * 可以利用这个方法，实现渐变范围查找
     * @param lng
     * @param lat
     * @param minDistance
     * @param maxDistance
     * @return
     */
    public List<MongoGeoPoint> queryWithinRadiusSorted(double lng, double lat, double minDistance, double maxDistance) {
        List<MongoGeoPoint> points = null;

        // 单位km
        Distance radius1 = new Distance(minDistance / 1000.0, Metrics.KILOMETERS);
        Distance radius2 = new Distance(maxDistance / 1000.0, Metrics.KILOMETERS);
        Point point = new Point(lng, lat);
        Query query = Query.query(Criteria.where(INDEX_FIELD).nearSphere(point)
                .minDistance(radius1.getNormalizedValue())
                .maxDistance(radius2.getNormalizedValue()));
        try {
            points = mongoTemplate.find(query, MongoGeoPoint.class);
        } catch (Exception e) {
            logger.error("[MongoGeoDao] query sorted points, lng:{}, lat:{} occur exception:", lng, lat, e);
        }
        return points;
    }

}
