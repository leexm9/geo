package com.leexm.demo.geo.dal.mysql.object;

import com.leexm.demo.geo.util.JsonUtils;
import org.springframework.data.geo.Polygon;

import java.util.List;

/**
 * 表示地理区域
 * 区域可以是：封闭区域、中间有空洞的区域、有"飞地"的区域
 *
 * @author leexm
 * @date 2019-09-02 00:01
 */
public class GeoPolygon {

    /** 主键 */
    private Long id;

    /** 名称 */
    private String name;

    /** 详情 */
    private String detail;

    /** 地理区域坐标集 */
    private List<Polygon> regional;

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

    public List<Polygon> getRegional() {
        return regional;
    }

    public void setRegional(List<Polygon> regional) {
        this.regional = regional;
    }

    @Override
    public String toString() {
        return JsonUtils.obj2String(this);
    }

}
