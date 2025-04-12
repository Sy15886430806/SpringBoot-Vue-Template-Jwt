/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50736 (5.7.36-log)
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50736 (5.7.36-log)
 File Encoding         : 65001

 Date: 11/04/2025 22:41:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for db_account
-- ----------------------------
DROP TABLE IF EXISTS `db_account`;
CREATE TABLE `db_account`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `register_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_name`(`username`) USING BTREE,
  UNIQUE INDEX `unique_email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of db_account
-- ----------------------------
INSERT INTO `db_account` VALUES (1, 'YiXiMi2025', '$2a$10$ChFQzWgS5rWq3jB5aTO.PeBCDsXG4iDIoMJh5k.FUA42.lE02zDkO', 'YiXiMi2025@163.com', 'user', '2025-04-10 11:08:44');
INSERT INTO `db_account` VALUES (4, '管理员', '$2a$10$c3iS5XAb5bdutLJOrlNoZewN42mKZ8hnqfkt8R9eoFl2N0bOVJF6K', 'Y1Suuu@163.com', 'user', '2025-04-11 05:18:28');
INSERT INTO `db_account` VALUES (5, '一粟', '$2a$10$Q8qGVceEeSU0psKI37r/hOLEpEFuHM5Ozz6xEVaSqlADjvgSh7s52', '3362187436@qq.com', 'user', '2025-04-11 18:42:09');

SET FOREIGN_KEY_CHECKS = 1;
