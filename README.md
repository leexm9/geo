## GEO

前段时间优化公司的地理位置查询服务时，测试了几种提供地理信息功能的存储，涉及 Mysql、redis、MongoDB和 Aerospike；另一方面，绝大部分的使用场景没有类似的对gis这块的要求，所以特地记录下这几种存储在 gis 方面的使用方法。同时，在项目期间学习了地理信息hash算法，也一并记录再此。涉及地理信息算法主要是 geohash 和 S2 算法，算法说明见 *地理信息算法说明* 一文。

---

注意相关库的版本对地理信息定位的支持，低版本上是不支持。

- mysql 相关语句

  mysql 对这块的支持，详细的可以查看[官方文档，](https://dev.mysql.com/doc/refman/5.7/en/spatial-analysis-functions.html)本项目中只实现其中的部分功能。

  本项目中涉及的数据参考  mybatis 文件下sql 文件：坐标点文件 geo_point.sql，地理区域文件 geo_polygon。

  - 对坐标点的相关操作

    ~~~sql
    # 建表语句
    CREATE TABLE `geo_point` (
      `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
      `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
      `detail` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '详情',
      `lng` decimal(14,11) DEFAULT NULL COMMENT '经度',
      `lat` decimal(14,11) DEFAULT NULL COMMENT '纬度',
      `coordinate` coordinate NOT NULL COMMENT '坐标',
      `geohash` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci GENERATED ALWAYS AS (st_geohash(`coordinate`,8)) VIRTUAL COMMENT 'geohash编码',
      PRIMARY KEY (`id`),
      SPATIAL KEY `idx_point` (`coordinate`)
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='地理位置信息';
    
    # insert geohash 是根据 coordinate 计算来的使用 default
    insert into `geo_point` values (null, '4号楼', '未来科技城海创园4号楼', 120.025, 30.2873, ST_GeomFromText('POINT(120.025 30.2873)'), default);
    
    # 计算两个地点的距离，取整
    select floor(ST_distance_sphere((select coordinate from geo_point where name = '西城时代'), coordinate)) distance from geo_point where name = '18号楼';
    
    # 距离 18 号楼小于 500m 范围的建筑物
    select name from geo_point where ST_distance_sphere((select coordinate from geo_point where name = '18号楼'), coordinate) < 500 and name != '18号楼';
    
    # 距离给定坐标 500m 范围内的建筑物
    select name from geo_point where ST_distance_sphere(ST_GeomFromText('POINT(120.023 30.2863)'), coordinate) < 500;
    
    # 距离 18 号楼小于 500m 范围的建筑物并排序
    select name, floor(ST_distance_sphere((select coordinate from geo_point where name = '18号楼'), coordinate)) distance, ST_astext(coordinate) coordinate from geo_point where ST_distance_sphere((select coordinate from geo_point where name = '18号楼'), coordinate) < 500 and name != '18号楼' order by distance asc;
    ~~~

  - 对区域的相关操作

    ~~~sql
    # 建表语句
    CREATE TABLE `geo_polygon` (
      `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
      `name` varchar(40) NOT NULL COMMENT '名称',
      `detail` varchar(200) DEFAULT NULL COMMENT '详情',
      `regional` polygon NOT NULL COMMENT '地理信息',
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
    
    # insert 语句
    INSERT INTO `geo_polygon` VALUES (NULL, '阿里巴巴', '杭州阿里巴巴西溪园区', NULL, ST_GeomFromText('POLYGON((120.0274133808 30.2853789625, 120.0340805100 30.2873510540, 120.0344882058 30.2864616115, 120.0355503605 30.2867025030, 120.0361404465 30.2848124156, 120.0374815510 30.2850069851, 120.0379536198 30.2834689491, 120.0332651185 30.2831075997, 120.0284540779 30.2821175623, 120.0274133808 30.2853789625))'));
    
    # 判断点是否在指定区域，1：包含，0：不包含
    SELECT ST_within(ST_GeomFromText('POINT(120.023 30.2863)'), regional) from geo_polygon where `name`='海创园';
    
    # 取包含指定的点的区域
    select `name` from geo_polygon where ST_contains(regional, ST_GeomFromText('POINT(120.035 30.2855)')) = 1;
    ~~~

- mongodb 相关

  mongo 对这块的支持，详细的可以查看[官方文档，](mysql 对这块的支持，详细的可以查看[官方文档，](https://dev.mysql.com/doc/refman/5.7/en/spatial-analysis-functions.html)本项目中只实现其中的部分功能。)本项目中只实现其中的部分功能。

  注意：对 collection 中的 geo 相关的 field 需要建立索引，mongo 支持两种类型：一种是平面类型，2d；一种是球面类型，2dsphere。

~~~
# mongo 对地理信息字段建立 2dsphere 索引
db.geo_point.createIndex({"coordinate":"2dsphere"})
db.geo_polygon.createIndex({"coordinate":"2dsphere"})
~~~

- redis 相关

  redis 对地理信息定位的支持——支持坐标点，详细的可以参考[说明文档](http://redisdoc.com/geo/geoadd.html)。
