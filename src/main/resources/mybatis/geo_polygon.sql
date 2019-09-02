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

 Date: 03/09/2019 00:08:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for geo_polygon
-- ----------------------------
DROP TABLE IF EXISTS `geo_polygon`;
CREATE TABLE `geo_polygon` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(40) NOT NULL COMMENT '名称',
  `detail` varchar(200) DEFAULT NULL COMMENT '详情',
  `regional` polygon NOT NULL COMMENT '地理信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of geo_polygon
-- ----------------------------
BEGIN;
INSERT INTO `geo_polygon` VALUES (1, '海创园', '杭州未来科技城', ST_GeomFromText('POLYGON((120.023 30.285, 120.021 30.2888, 120.026 30.29, 120.027 30.2861, 120.023 30.285))'));
INSERT INTO `geo_polygon` VALUES (2, '阿里巴巴', '杭州阿里巴巴西溪园区', ST_GeomFromText('POLYGON((120.027 30.2854, 120.034 30.2874, 120.034 30.2865, 120.036 30.2867, 120.036 30.2848, 120.037 30.285, 120.038 30.2835, 120.033 30.2831, 120.028 30.2821, 120.027 30.2854))'));
INSERT INTO `geo_polygon` VALUES (5, 'test', '测试使用', ST_GeomFromText('POLYGON((120.023 30.285, 120.021 30.2888, 120.026 30.29, 120.027 30.2861, 120.023 30.285))'));
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
