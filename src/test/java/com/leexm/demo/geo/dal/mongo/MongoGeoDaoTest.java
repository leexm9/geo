package com.leexm.demo.geo.dal.mongo;

import com.leexm.demo.geo.dal.mongo.dao.MongoGeoDao;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPoint;
import com.leexm.demo.geo.dal.mongo.object.MongoGeoPolygon;
import com.leexm.demo.geo.dal.mysql.dao.MysqlGeoPointDao;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import com.leexm.demo.geo.util.PointUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
    private MysqlGeoPointDao mysqlGeoPointDao;

    /**
     * 导入数据，只需要执行一次即可
     */
    @Ignore
    @Test
    public void insertPoint() {
        for (long i = 1; i <= 9; i++) {
            GeoPoint geoPoint = mysqlGeoPointDao.queryById(i);
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

    /**
     * 导入数据并测试 insert
     */
    @Ignore
    @Test
    public void testInsertPolygon() {
        MongoGeoPolygon geoPolygon = new MongoGeoPolygon();
        geoPolygon.setName("海创园");
        geoPolygon.setDetail("杭州未来科技城");
        List<Point> points = new ArrayList<>();
        points.add(new Point(120.0226283199,30.2849990890));
        points.add(new Point(120.0213089777,30.2887521288));
        points.add(new Point(120.0255358344,30.2900299763));
        points.add(new Point(120.0270164138,30.2860923793));
        points.add(new Point(120.0226283199,30.2849990890));
        GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(points);
        geoPolygon.setRegional(geoJsonPolygon);

        boolean flag = mongoGeoDao.insertPolygon(geoPolygon);
        Assert.assertTrue(flag);

        geoPolygon = new MongoGeoPolygon();
        geoPolygon.setName("阿里巴巴");
        geoPolygon.setDetail("杭州阿里巴巴西溪园区");
        points = new ArrayList<>();
        points.add(new Point(120.0274133808,30.2853789625));
        points.add(new Point(120.0340805100,30.2873510540));
        points.add(new Point(120.0344882058,30.2864616115));
        points.add(new Point(120.0355503605,30.2867025030));
        points.add(new Point(120.0361404465,30.2848124156));
        points.add(new Point(120.0374815510,30.2850069851));
        points.add(new Point(120.0379536198,30.2834689491));
        points.add(new Point(120.0332651185,30.2831075997));
        points.add(new Point(120.0284540779,30.2821175623));
        points.add(new Point(120.0274133808,30.2853789625));
        geoJsonPolygon = new GeoJsonPolygon(points);
        geoPolygon.setRegional(geoJsonPolygon);

        flag = mongoGeoDao.insertPolygon(geoPolygon);
        Assert.assertTrue(flag);
    }

    @Test
    public void testQueryPolygonByName() {
        MongoGeoPolygon polygon = mongoGeoDao.queryPolygonByName("阿里巴巴");
        System.out.println("================");
        System.out.println(polygon);
        System.out.println("================");
    }

    @Test
    public void testContainsPoint() {
        // 这个点在海创园范围内
        Point point = new Point(120.023, 30.2863);
        boolean flag = mongoGeoDao.containsPoint("海创园", point);
        Assert.assertTrue(flag);

        point = new Point(120.035, 30.2855);
        flag = mongoGeoDao.containsPoint("海创园", point);
        Assert.assertFalse(flag);
    }

    @Test
    public void testQueryPolygonByPoint() {
        // 这个点在海创园范围内
        Point point = new Point(120.023, 30.2863);
        MongoGeoPolygon geoPolygon = mongoGeoDao.queryPolygonByPoint(point);
        Assert.assertEquals("海创园", geoPolygon.getName());

        // 这个点不在海川园范围内
        point = new Point(120.035, 30.2855);
        geoPolygon = mongoGeoDao.queryPolygonByPoint(point);
        Assert.assertEquals("阿里巴巴", geoPolygon.getName());
    }

}
