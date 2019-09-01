package com.leexm.demo.geo.dal.mysql.dao;

import com.leexm.demo.geo.dal.mysql.object.GeoPoint;
import com.leexm.demo.geo.dal.mysql.object.Point;

import java.util.List;

/**
 * @author leexm
 * @date 2019-08-18 18:30
 */
public interface MysqlGeoDao {

    /**
     * 根据名称取地点
     * @param name
     * @return
     */
    GeoPoint queryByName(String name);

    /**
     * 根据主键取地点
     * @param id
     * @return
     */
    GeoPoint queryById(Long id);

    /**
     * 插入新的地址
     * @param geoPoint
     * @return
     */
    int insert(GeoPoint geoPoint);

    /**
     * 取距 {name} {distance}范围内的地点集合
     * @param name
     * @param radius, 单位：米
     * @return
     */
    List<GeoPoint> nearWithin(String name, double radius);

    /**
     * 取 {coordinate} 为圆心， 半径 {radius} 范围内的点
     * @param coordinate
     * @param radius
     * @return
     */
    List<GeoPoint> queryWithinRadius(Point coordinate, double radius);

}
