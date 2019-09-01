package com.leexm.demo.geo.dal.mongo;

import com.leexm.demo.geo.dal.mongo.dao.MongoGeoDao;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mysql.dao.MysqlGeoDao;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import com.leexm.demo.geo.util.PointUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author leexm
 * @date 2019-09-01 10:58
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoGeoDaoTest {

    @Autowired
    private MongoGeoDao mongoGeoDao;

    @Autowired
    private MysqlGeoDao mysqlGeoDao;

    /**
     * 导入数据，只需要执行一次即可
     */
    @Test
    @Ignore
    public void insertPoint() {
        for (long i = 1; i <= 9; i++) {
            GeoPoint geoPoint = mysqlGeoDao.queryById(i);
            mongoGeoDao.insertPoint(PointUtils.point2MongoPoint(geoPoint));
        }
    }

    @Test
    public void testQueryById() {
        MongoGeoPoint mongoGeoPoint = mongoGeoDao.queryById("5d6b36e1b92fb61aabfa399a");
        System.out.println("=================");
        System.out.println(mongoGeoPoint);
        System.out.println("=================");
        Assert.assertNotNull(mongoGeoPoint);
    }

    @Test
    public void testQueryByName() {
        MongoGeoPoint mongoGeoPoint = mongoGeoDao.queryByName("18号楼");
        System.out.println("=================");
        System.out.println(mongoGeoPoint);
        System.out.println("=================");
        Assert.assertNotNull(mongoGeoPoint);
    }

    @Test
    public void testQueryWithinRadius() {
        List<MongoGeoPoint> mongoGeoPoints = mongoGeoDao.queryWithinRadius(120.023, 30.2863, 500);
        System.out.println("=================");
        System.out.println(mongoGeoPoints);
        System.out.println("=================");
    }

    @Test
    public void testNearWithin() {
        List<MongoGeoPoint> mongoGeoPoints = mongoGeoDao.nearWithin("18号楼", 98);
        System.out.println("=================");
        System.out.println(mongoGeoPoints);
        System.out.println("=================");
    }

    @Test
    public void testQueryWithinRadiusSorted() {
        List<MongoGeoPoint> mongoGeoPoints = mongoGeoDao.queryWithinRadiusSorted(120.023, 30.2863, 100, 500);
        System.out.println("=================");
        System.out.println(mongoGeoPoints);
        System.out.println("=================");
    }

}
