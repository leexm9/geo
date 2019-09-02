package com.leexm.demo.geo.dal.mongo.object;

import com.leexm.demo.geo.util.JsonUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author leexm
 * @date 2019-09-03 00:19
 */
@Document(collection = "geo_polygon")
public class MongoGeoPolygon {

    /**
     * mongo 生成的主键
     */
    @Id
    private String id;

    /**
     * 区域名称
     */
    @Field
    private String name;

    /**
     * 区域详情
     */
    @Field
    private String detail;

    /**
     * 地理信息数据
     */
    @Field
    private GeoJsonPolygon regional;

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

    public GeoJsonPolygon getRegional() {
        return regional;
    }

    public void setRegional(GeoJsonPolygon regional) {
        this.regional = regional;
    }

    @Override
    public String toString() {
        return JsonUtils.obj2String(this);
    }
}
