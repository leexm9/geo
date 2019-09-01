package com.leexm.demo.geo.dal.redis;

import com.leexm.demo.geo.dal.mongo.dao.MongoGeoDao;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mysql.dao.MysqlGeoDao;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import com.leexm.demo.geo.dal.redis.dao.RedisGeoDao;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author leexm
 * @date 2019-09-01 16:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisGeoDaoTest {

    @Autowired
    private RedisGeoDao redisGeoDao;

    @Autowired
    private MysqlGeoDao mysqlGeoDao;

    @Autowired
    private MongoGeoDao mongoGeoDao;

    /**
     * 导入数据使用
     */
    @Test
    @Ignore
    public void testInsertPoint() {
        for (long i = 1; i <= 9; i++) {
            GeoPoint geoPoint = mysqlGeoDao.queryById(i);
            MongoGeoPoint mongoPoint = mongoGeoDao.queryByName(geoPoint.getName());
            redisGeoDao.insertPoint(mongoPoint);
        }
    }

    @Test
    public void testQueryWithinRadius() {
        List<MongoGeoPoint> points = redisGeoDao.queryWithinRadius(120.023, 30.2863, 97);
        System.out.println("===============");
        System.out.println(points);
        System.out.println("===============");
    }

}
