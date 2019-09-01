package com.leexm.demo.geo.dal.mysql.handler;

import com.leexm.demo.geo.dal.mysql.object.Point;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author leexm
 * @date 2019-08-19 00:44
 */
@MappedTypes(Point.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PointTypeHandler extends BaseTypeHandler<Point> {

    private static final String POINT_FORMAT = "POINT(%f %f)";

    /**
     * 把Java类型参数转换为对应的数据库类型
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Point parameter, JdbcType jdbcType) throws SQLException {
        String pointText = String.format(POINT_FORMAT, parameter.getX(), parameter.getY());
        ps.setObject(i, pointText);
    }

    /**
     * 获取数据结果集时把数据库类型转换为对应的Java类型
     * POINT(120.023 30.2863)
     */
    @Override
    public Point getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String text = rs.getString(columnName);
        return string2Point(text);
    }

    /**
     * 通过字段位置获取字段数据时把数据库类型转换为对应的Java类型
     */
    @Override
    public Point getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String text = rs.getString(columnIndex);
        return string2Point(text);
    }

    /**
     * 调用存储过程后把数据库类型的数据转换为对应的Java类型
     */
    @Override
    public Point getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String text = cs.getString(columnIndex);
        return string2Point(text);
    }

    private Point string2Point(String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        int i = StringUtils.indexOf(string, "(");
        if (i == -1) {
            return null;
        }
        string = StringUtils.substring(string, i + 1, string.length() - 1);
        String[] items = StringUtils.split(string, " ");
        if (items.length != 2) {
            return null;
        }
        return new Point(Double.parseDouble(items[0]), Double.parseDouble(items[1]));
    }

    public static void main(String[] args) {
        String yxy = String.format(POINT_FORMAT, 1.4, 3.3);
        System.out.println(yxy);
    }

}
