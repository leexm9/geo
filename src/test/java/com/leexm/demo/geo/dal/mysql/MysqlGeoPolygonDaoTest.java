package com.leexm.demo.geo.dal.mysql;

import com.leexm.demo.geo.dal.mysql.dao.MysqlGeoPolygonDao;
import com.leexm.demo.geo.dal.mysql.object.GeoPolygon;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author leexm
 * @date 2019-09-02 22:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MysqlGeoPolygonDaoTest {

    @Autowired
    private MysqlGeoPolygonDao geoPolygonDao;

    @Test
    public void testInsert() {
        GeoPolygon area = new GeoPolygon();
        area.setName("test");
        area.setDetail("测试使用");
        List<Point> list = new ArrayList<>();
        list.add(new Point(120.0226283199,30.2849990890));
        list.add(new Point(120.0213089777,30.2887521288));
        list.add(new Point(120.0255358344,30.2900299763));
        list.add(new Point(120.0270164138,30.2860923793));
        list.add(new Point(120.0226283199,30.2849990890));
        Polygon polygon = new Polygon(list);
        List<Polygon> polygons = Arrays.asList(polygon);
        area.setRegional(polygons);

        geoPolygonDao.insert(area);
        System.out.println(area.getId());

        // 这个点在海创园范围内
        Point point = new Point(120.023, 30.2863);
        int rs = geoPolygonDao.containsPoint(area.getId(), point);
        Assert.assertEquals(1, rs);
    }

    @Test
    public void testQueryByName() {
        GeoPolygon geoPolygon = geoPolygonDao.queryByName("海创园");
        Assert.assertNotNull(geoPolygon);
        System.out.println("================");
        System.out.println(geoPolygon);
        System.out.println("================");
    }

    @Test
    public void testContainsPoint() {
        // 这个点在海创园范围内
        Point point = new Point(120.023, 30.2863);
        int rs = geoPolygonDao.containsPoint(1, point); // 第一条记录是海创园
        Assert.assertEquals(1, rs);

        // 这个点不在海川园范围内
        point = new Point(120.035, 30.2855);
        rs = geoPolygonDao.containsPoint(1, point);
        Assert.assertEquals(0, rs);
    }

    @Test
    public void testQueryByPoints() {
        // 这个点在海创园范围内
        Point point = new Point(120.023, 30.2863);
        GeoPolygon polygon = geoPolygonDao.queryByPoint(point);
        Assert.assertEquals("海创园", polygon.getName());
        System.out.println(polygon);

        // 这个点不在海川园范围内
        point = new Point(120.035, 30.2855);
        polygon = geoPolygonDao.queryByPoint(point);
        Assert.assertEquals("阿里巴巴", polygon.getName());
        System.out.println(polygon);
    }

}