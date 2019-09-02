/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : geo

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 03/09/2019 00:08:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for geo_point
-- ----------------------------
DROP TABLE IF EXISTS `geo_point`;
CREATE TABLE `geo_point` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `detail` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '详情',
  `lng` decimal(14,11) DEFAULT NULL COMMENT '经度',
  `lat` decimal(14,11) DEFAULT NULL COMMENT '纬度',
  `coordinate` point NOT NULL COMMENT '坐标',
  `geohash` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci GENERATED ALWAYS AS (st_geohash(`coordinate`,8)) VIRTUAL COMMENT 'geohash编码',
  PRIMARY KEY (`id`),
  SPATIAL KEY `idx_point` (`coordinate`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='地理位置信息';

-- ----------------------------
-- Records of geo_point
-- ----------------------------
BEGIN;
INSERT INTO `geo_point` VALUES (1, '4号楼', '未来科技城海创园4号楼', 120.02500000000, 30.28730000000, ST_GeomFromText('POINT(120.025 30.2873)'), DEFAULT);
INSERT INTO `geo_point` VALUES (2, '18号楼', '未来科技城海创园18号楼', 120.02300000000, 30.28630000000, ST_GeomFromText('POINT(120.023 30.2863)'), DEFAULT);
INSERT INTO `geo_point` VALUES (3, '星巴克', '星巴克', 120.02400000000, 30.28650000000, ST_GeomFromText('POINT(120.024 30.2865)'), DEFAULT);
INSERT INTO `geo_point` VALUES (4, '印象城', '西溪印象城', 120.05700000000, 30.25320000000, ST_GeomFromText('POINT(120.057 30.2532)'), DEFAULT);
INSERT INTO `geo_point` VALUES (5, '农商行', '海创园农商行', 120.02500000000, 30.28790000000, ST_GeomFromText('POINT(120.025 30.2879)'), DEFAULT);
INSERT INTO `geo_point` VALUES (6, '西城时代', '西城时代', 119.95900000000, 30.27080000000, ST_GeomFromText('POINT(119.959 30.2708)'), DEFAULT);
INSERT INTO `geo_point` VALUES (7, '工商银行', '工商银行海创园网点', 120.02400000000, 30.28630000000, ST_GeomFromText('POINT(120.024 30.2863)'), DEFAULT);
INSERT INTO `geo_point` VALUES (8, '阿里巴巴-7号楼', '阿里巴巴西溪园区', 120.03500000000, 30.28550000000, ST_GeomFromText('POINT(120.035 30.2855)'), DEFAULT);
INSERT INTO `geo_point` VALUES (9, '19号楼', '未来科技城海创园19号楼', 120.02400000000, 30.28650000000, ST_GeomFromText('POINT(120.024 30.2865)'), DEFAULT);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
