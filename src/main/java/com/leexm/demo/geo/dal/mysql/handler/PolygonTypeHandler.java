package com.leexm.demo.geo.dal.mysql.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leexm
 * @date 2019-09-02 00:29
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class PolygonTypeHandler extends BaseTypeHandler<List<Polygon>> {

    private static final String PLOYGON_FORMAT = "POLYGON(%s)";

    private static final Pattern PLOYGON = Pattern.compile("[(](.*?)[)]");
    private static final Pattern POINT = Pattern.compile("(\\d+)\\.(\\d+) (\\d+)\\.(\\d+)");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Polygon> parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder builder = new StringBuilder();
        for (Polygon polygon : parameter) {
            builder.append("(");
            List<Point> points = polygon.getPoints();
            for (Point point : points) {
                builder.append(point.getX()).append(" ").append(point.getY()).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("),");
        }
        builder.deleteCharAt(builder.length() - 1);
        String text = String.format(PLOYGON_FORMAT, builder.toString());
        ps.setObject(i, text);
    }

    @Override
    public List<Polygon> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String text = rs.getString(columnName);
        return convert2Polygon(text);
    }

    @Override
    public List<Polygon> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String text = rs.getString(columnIndex);
        return convert2Polygon(text);
    }

    @Override
    public List<Polygon> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String text = cs.getString(columnIndex);
        return convert2Polygon(text);
    }

    private List<Polygon> convert2Polygon(String text) {
        if (StringUtils.isBlank(text) || !StringUtils.startsWith(text, "POLYGON")) {
            return null;
        }
        List<Polygon> polygons = new ArrayList<>();
        Matcher matcher = PLOYGON.matcher(text);
        while (matcher.find()) {
            String item = matcher.group();
            List<Point> points = new ArrayList<>();
            Matcher matcher1 = POINT.matcher(item);
            while (matcher1.find()) {
                String[] strs = StringUtils.split(matcher1.group(), " ");
                Point point = new Point(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]));
                points.add(point);
            }
            Polygon polygon = new Polygon(points);
            polygons.add(polygon);
        }
        return polygons;
    }

}
