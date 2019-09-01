package com.leexm.demo.geo.dal.redis.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.redis.JedisClient;
import com.leexm.demo.geo.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author leexm
 * @date 2019-09-01 15:46
 */
@Component("redisGeoDao")
public class RedisGeoDao {

    private static final Logger logger = LoggerFactory.getLogger(RedisGeoDao.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    /** 经纬度存储 key */
    private static final String POINT = "geo_point";

    /** 存储地点的详细信息 */
    private static final String LOCATION = "location^^";

    @Autowired
    private JedisClient jedisClient;

    public boolean insertPoint(MongoGeoPoint point) {
        if (point == null || point.getCoordinate() == null) {
            return false;
        }
        boolean flag = true;
        try (Jedis jedis = jedisClient.getClient()) {
            Transaction tran = jedis.multi();
            // 这里将地点的信息分开存储：地点自身信息存储一条，地点 geo hash 信息存储一条，使用 mongo 生产的 id 进行关联，这个关联值，也可以自己生成
            // 这样方便信息的变更和删除：经纬度不变的话，地点自身信息可修改；也可根据关联的 id 来删除 redis 中 geo hash 的数据
            tran.set(LOCATION + point.getId(), JsonUtils.obj2String(point));
            tran.geoadd(POINT, point.getLng(), point.getLat(), point.getId());
            tran.exec();
        } catch (Exception e) {
            flag = false;
            logger.error("[RedisGeoDao] insert point:{} occur exception:", point, e);
        }
        return flag;
    }

    /**
     * 取 ({lng} {lat}) 为圆心， 半径 {radius} 范围内的地址
     * @param radius
     * @return
     */
    public List<MongoGeoPoint> queryWithinRadius(double lng, double lat, double radius) {
        // 查询条件
        GeoRadiusParam radiusParam = GeoRadiusParam.geoRadiusParam();
        radiusParam
                .withCoord()        // 返回元素的经纬度
                .withDist()         // 返回查到的点距给定经纬度的距离
                .sortAscending()    // 按照距离排序：升序
                .count(4);          // 取结果：可以指定结果数量
        List<GeoRadiusResponse> responses = null;
        try (Jedis jedis = jedisClient.getClient()) {
            responses = jedisClient.getClient().georadius(POINT, lng, lat, radius, GeoUnit.M, radiusParam);
        } catch (Exception e) {
            logger.error("[RedisGeoDao] query point occur exception:", e);
        }
        if (CollectionUtils.isEmpty(responses)) {
            return null;
        }
        List<MongoGeoPoint> points = new ArrayList<>();
        for (GeoRadiusResponse response : responses) {
            String id = response.getMemberByString();
            String str = jedisClient.getClient().get(LOCATION + id);
            points.add(convert2MongoPoint(str));
        }
        return points;
    }

    private MongoGeoPoint convert2MongoPoint(String str) {
        try {
            JsonNode root = mapper.readTree(str);
            MongoGeoPoint point = new MongoGeoPoint();
            point.setId(root.path("id").textValue());
            point.setName(root.path("name").textValue());
            point.setDetail(root.path("detail").textValue());
            GeoJsonPoint geoJsonPoint = new GeoJsonPoint(root.path("lng").doubleValue(), root.path("lat").doubleValue());
            point.setCoordinate(geoJsonPoint);
            return point;
        } catch (IOException e) {
            logger.error("[RedisGeoDao] convert:{} to mongo point error:", str, e);
        }
        return null;
    }

}
