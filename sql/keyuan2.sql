/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.239.100_3306
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : 192.168.239.100:3306
 Source Schema         : keyuan2

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 07/06/2023 18:21:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for good_scale
-- ----------------------------
DROP TABLE IF EXISTS `good_scale`;
CREATE TABLE `good_scale`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '规格主键',
  `scale` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `price` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '钱数',
  `good_id` bigint(0) NULL DEFAULT NULL COMMENT '商品id',
  `shop_id` bigint(0) NULL DEFAULT NULL COMMENT '商店id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of good_scale
-- ----------------------------
INSERT INTO `good_scale` VALUES (7, '小,中', '10,15', 19, NULL);
INSERT INTO `good_scale` VALUES (10, '小,中', '10,15', 1, NULL);
INSERT INTO `good_scale` VALUES (12, '小,中', '10,15', 2, NULL);
INSERT INTO `good_scale` VALUES (13, '小,中', '10,15', 3, NULL);
INSERT INTO `good_scale` VALUES (14, '小,中', '10,15', 4, NULL);
INSERT INTO `good_scale` VALUES (15, '小,中', '10,15', 5, NULL);

-- ----------------------------
-- Table structure for good_snake
-- ----------------------------
DROP TABLE IF EXISTS `good_snake`;
CREATE TABLE `good_snake`  (
  `snake_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shop_id` bigint(0) NULL DEFAULT NULL COMMENT '商店的id',
  `snake_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小食名称',
  `snake_money` decimal(10, 0) NULL DEFAULT NULL COMMENT '小食价格',
  PRIMARY KEY (`snake_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of good_snake
-- ----------------------------
INSERT INTO `good_snake` VALUES (1, 1, '凉拌皮蛋', 15);
INSERT INTO `good_snake` VALUES (2, 1, '凉拌青瓜', 15);
INSERT INTO `good_snake` VALUES (3, 1, '凉拌海带', 15);
INSERT INTO `good_snake` VALUES (4, 1, '凉拌木耳', 18);
INSERT INTO `good_snake` VALUES (5, 1, '凉拌牛肉', 35);
INSERT INTO `good_snake` VALUES (6, 1, '凉拌牛腩', 35);
INSERT INTO `good_snake` VALUES (77, 10, '小食1', 100);
INSERT INTO `good_snake` VALUES (78, 10, '小食2', 100);
INSERT INTO `good_snake` VALUES (79, 10, '小食3', 100);

-- ----------------------------
-- Table structure for shop_type
-- ----------------------------
DROP TABLE IF EXISTS `shop_type`;
CREATE TABLE `shop_type`  (
  `id` bigint(0) NOT NULL COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shop_type
-- ----------------------------
INSERT INTO `shop_type` VALUES (1, '汉堡');

-- ----------------------------
-- Table structure for t_good
-- ----------------------------
DROP TABLE IF EXISTS `t_good`;
CREATE TABLE `t_good`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `food_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食品名称',
  `food_price` decimal(10, 0) NOT NULL COMMENT '初始价格',
  `food_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '食品类型',
  `snake_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '小食id',
  `hasScale` tinyint(0) NULL DEFAULT NULL COMMENT '是否有规格,有则是1,没有则是0',
  `optional_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可选项名字',
  `flavor` int(0) NULL DEFAULT 0 COMMENT '可选项id,当为1 表示有口味要求 辣,不辣,微辣,0则是没有口味',
  `soleNum` bigint(0) NULL DEFAULT 0 COMMENT '销量',
  `shop_id` bigint(0) NULL DEFAULT NULL COMMENT '商家id',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间,这里需要一个月清空一次销量',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '用来进行逻辑删除功效',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_good
-- ----------------------------
INSERT INTO `t_good` VALUES (1, '牛肉汤粉面', 15, '汤粉类', '1,2,3', 1, '', NULL, 1, 1, NULL, '2023-06-08 02:01:59', NULL, 1);
INSERT INTO `t_good` VALUES (2, '牛杂汤粉面', 15, '汤粉类', NULL, 1, NULL, NULL, 1, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (3, '牛腩汤粉面', 15, '汤粉类', NULL, 1, NULL, NULL, 1, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (4, '牛丸汤粉面', 15, '汤粉类', NULL, 1, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (5, '猪杂汤粉面', 8, '汤粉类', NULL, 1, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (6, '廋肉汤粉面', 8, '汤粉类', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (7, '腌面+汤', 15, '腌面食', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, '', 0);
INSERT INTO `t_good` VALUES (8, '牛肉抄粉面', 15, '炒粉炒面类', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (9, '牛肉酸菜饭', 18, '套餐主食', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (10, '牛腩饭', 18, '套餐主食', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (11, '鸡蛋肠粉', 5, '早餐类', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (12, '火腿肠粉', 5, '早餐类', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (13, '鸡蛋米粉', 5, '早餐类', NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (14, '食品名称', 10, 'textType', NULL, NULL, '[米粉, 肠粉, 炒粉]', 0, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (15, '食品名称2', 10, 'textType', NULL, NULL, '米粉,肠粉,炒粉', 0, 0, 1, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (17, '食品名称3', 10, 'textType', NULL, NULL, '米粉,肠粉,炒粉', 0, 0, 10, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (18, '食品名称4', 10, 'textType', NULL, 1, '米粉,肠粉,炒粉', 0, 0, 10, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (19, '食品名称6', 10, 'textType', '77,78,79', NULL, '米粉,肠粉,炒粉', 0, 0, 10, NULL, NULL, NULL, 0);
INSERT INTO `t_good` VALUES (20, '食品名称7', 10, 'textType', '77,78,79', NULL, '米粉,肠粉,炒粉', 0, 0, 10, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `order_id` bigint(0) NOT NULL COMMENT '订单编号:全局唯一id',
  `order_number` bigint(0) NULL DEFAULT NULL COMMENT '取单号:每日生成一个',
  `user_id` bigint(0) NULL DEFAULT NULL,
  `good_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品的id',
  `shop_id` bigint(0) NULL DEFAULT NULL COMMENT '商店id',
  `table_id` tinyint(0) NULL DEFAULT NULL COMMENT '桌号',
  `use_type` tinyint(0) NULL DEFAULT NULL COMMENT '就餐方式 1则是堂食  0 则是打包',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '下单时间',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `refund_time` datetime(0) NULL DEFAULT NULL COMMENT '退款时间',
  `pay_money` decimal(10, 0) NULL DEFAULT NULL COMMENT '支付金额',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `order_status` int(0) NOT NULL COMMENT '订单状态,333则是失败 400则是成功 300则是等待中',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES (-47951381419524092, 2421, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 300);
INSERT INTO `t_order` VALUES (-47933810708316155, 2422, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 300);
INSERT INTO `t_order` VALUES (1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 300);
INSERT INTO `t_order` VALUES (9069811287982081, 4580, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47907967890096115, 2430, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47909982229757940, 2429, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47911399568965621, 2428, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47913869175160822, 2427, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47918426135461879, 2426, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47919310898724856, 2425, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 300);
INSERT INTO `t_order` VALUES (47920333100941305, 2424, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);
INSERT INTO `t_order` VALUES (47933544420343802, 2423, NULL, '1', 1, 1, 0, '2023-05-23 16:13:58', NULL, NULL, 30, 'test', 400);

-- ----------------------------
-- Table structure for t_shop
-- ----------------------------
DROP TABLE IF EXISTS `t_shop`;
CREATE TABLE `t_shop`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键,商家id',
  `type_id` bigint(0) NULL DEFAULT NULL COMMENT '商店类型',
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商店名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商店地址',
  `use_id` bigint(0) NULL DEFAULT NULL COMMENT '商家id',
  `shop_function` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开通的功能',
  `open_hour` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业时间',
  `x` double NULL DEFAULT NULL COMMENT '经度',
  `y` double NULL DEFAULT NULL COMMENT '维度',
  `sold` bigint(0) NULL DEFAULT NULL COMMENT '销量',
  `avg_price` decimal(10, 0) NOT NULL COMMENT '平均销量',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `image` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商家照片',
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商圈',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_shop
-- ----------------------------
INSERT INTO `t_shop` VALUES (1, 1, '华莱士', 'xxxxx', NULL, '外卖定制', '2023-10-23 23:21:11', 11.3, 11.4, 100, 15, '2023-05-29 03:37:27', NULL, NULL, '广州');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户微信名称',
  `shop_id` bigint(0) NULL DEFAULT NULL COMMENT '商店id',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话,可以为null,商家需要用到',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `create_Time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商业圈',
  `x` double NULL DEFAULT NULL COMMENT '经度',
  `y` double NULL DEFAULT NULL COMMENT '维度',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录token',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
