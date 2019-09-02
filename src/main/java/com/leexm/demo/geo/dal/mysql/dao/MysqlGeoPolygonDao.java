package com.leexm.demo.geo.dal.mysql.dao;

import com.leexm.demo.geo.dal.mysql.object.GeoPolygon;
import org.springframework.data.geo.Point;

/**
 * 操作 Polygon 类型
 *
 * @author leexm
 * @date 2019-09-01 23:59
 */
public interface MysqlGeoPolygonDao {

    /**
     * 插入地理区域
     *
     * @param geoPolygon
     * @return
     */
    int insert(GeoPolygon geoPolygon);

    /**
     * 根据名称取地理区域
     *
     * @param name
     * @return
     */
    GeoPolygon queryByName(String name);

    /**
     * 判断指定地理区域是否包含指定坐标
     *
     * @param id 区域主键
     * @param coordinate 点坐标
     * @return 1:点在区域内，0:点在区域外
     */
    int containsPoint(long id, Point coordinate);

    /**
     * 查找指定坐标所在的地理区域
     *
     * @param coordinate 点坐标
     * @return
     */
    GeoPolygon queryByPoint(Point coordinate);

}
