package com.leexm.demo.geo.dal.mysql.object;

import com.leexm.demo.geo.util.JsonUtils;

/**
 * 表示地点
 *
 * @author leexm
 * @date 2019-08-17 22:44
 */
public class GeoPoint {

    /** 主键 */
    private Long id;

    /** 名称 */
    private String name;

    /** 详细地址 */
    private String detail;

    /** 经度 */
    private Double lng;

    /** 纬度 */
    private Double lat;

    /** 坐标 */
    private Point coordinate;

    public GeoPoint() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Point coordinate) {
        this.coordinate = coordinate;
        this.lng = coordinate.getX();
        this.lat = coordinate.getY();
    }

    @Override
    public String toString() {
        return JsonUtils.obj2String(this);
    }

}
