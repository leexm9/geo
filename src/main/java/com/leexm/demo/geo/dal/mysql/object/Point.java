package com.leexm.demo.geo.dal.mysql.object;

import com.leexm.demo.geo.util.JsonUtils;

/**
 * @author leexm
 * @date 2019-08-19 00:56
 */
public class Point extends org.springframework.data.geo.Point {

    public Point() {
        super(0, 0);
    }

    public Point(double x, double y) {
        super(x, y);
    }

    @Override
    public String toString() {
        return JsonUtils.obj2String(this);
    }
}
