package com.leexm.demo.geo.dal.mysql;

import com.leexm.demo.geo.dal.mysql.dao.MysqlGeoPointDao;
import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author leexm
 * @date 2019-08-18 18:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlGeoPointDaoTest {

    @Autowired
    private MysqlGeoPointDao geoPointDAO;

    @Test
    public void testQueryByName() {
        GeoPoint geoPoint = geoPointDAO.queryByName("18号楼");
        Assert.assertNotNull(geoPoint);
        System.out.println(geoPoint);
    }

    @Test
    public void testQueryById() {
        GeoPoint point = geoPointDAO.queryById(1L);
        Assert.assertNotNull(point);
        System.out.println(point);
    }

    @Test
    public void testInsert() {
        GeoPoint geoPoint = new GeoPoint();
        geoPoint.setName("机场");
        geoPoint.setDetail("北京东城区");
        Point coordinate = new Point(123.197, 45.8846);
        geoPoint.setCoordinate(coordinate);
        geoPointDAO.insert(geoPoint);
        Assert.assertNotNull(geoPoint.getId());
    }

    @Test
    public void testNearWith() {
        List<GeoPoint> points = geoPointDAO.nearWithin("18号楼", 500.0);
        System.out.println(points);
    }

    @Test
    public void testQueryWithinRadius() {
        Point coordinate = new Point(120.023, 30.2863);
        List<GeoPoint> points = geoPointDAO.queryWithinRadius(coordinate, 50);
        Assert.assertNotNull(points);
        System.out.println(points);
    }

}