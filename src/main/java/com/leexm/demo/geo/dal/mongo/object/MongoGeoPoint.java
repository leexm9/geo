package com.leexm.demo.geo.dal.mongo.object;

import com.leexm.demo.geo.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

/**
 * @author leexm
 * @date 2019-09-01 09:53
 */
@Document(collection = "geo_point")
public class MongoGeoPoint {

    /**
     * mongo 生成的主键
     */
    @Id
    private String id;

    /**
     * 地点名称
     */
    @Field
    private String name;

    /**
     * 地点详情
     */
    @Field
    private String detail;

    /**
     * 经度
     */
    @Field
    private Double lng;

    /**
     * 纬度
     */
    @Field
    private Double lat;

    /**
     * mongo 要求的经纬度坐标对象
     * 需要在 mongo 中给该字段建立 2dsphere 索引
     */
    @Field
    private GeoJsonPoint coordinate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public GeoJsonPoint getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(GeoJsonPoint coordinate) {
        this.coordinate = coordinate;

        // 经纬度赋值
        this.setLng(coordinate.getX());
        this.setLat(coordinate.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MongoGeoPoint that = (MongoGeoPoint) o;
        return StringUtils.equals(this.id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, detail, lng, lat, coordinate);
    }

    @Override
    public String toString() {
        return JsonUtils.obj2String(this);
    }
}
