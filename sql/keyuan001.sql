-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: 192.168.239.100    Database: keyuan
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `detail`
--

DROP TABLE IF EXISTS `detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情页主键',
  `detail_name` varchar(5) DEFAULT NULL COMMENT '详情名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品的详情页';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detail`
--

LOCK TABLES `detail` WRITE;
/*!40000 ALTER TABLE `detail` DISABLE KEYS */;
INSERT INTO `detail` VALUES (1,'大中小'),(2,'大中'),(3,'');
/*!40000 ALTER TABLE `detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detail_price`
--

DROP TABLE IF EXISTS `detail_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detail_price` (
  `id` bigint NOT NULL COMMENT '价格表id',
  `detail_name` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '详情名称',
  `price` tinyint DEFAULT NULL COMMENT '价格',
  `detail_id` bigint DEFAULT NULL COMMENT '详情id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detail_price`
--

LOCK TABLES `detail_price` WRITE;
/*!40000 ALTER TABLE `detail_price` DISABLE KEYS */;
/*!40000 ALTER TABLE `detail_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `good`
--

DROP TABLE IF EXISTS `good`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `good` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `good_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `good_price` int NOT NULL COMMENT '商品价格',
  `type_id` int NOT NULL COMMENT '商品类别',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '商品照片',
  `soleNum` bigint DEFAULT NULL COMMENT '月销量',
  `is_detail` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '是否需要选规格,需要为1,不需要为2',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_gn_gp` (`good_name`(4),`good_price`),
  KEY `type_id` (`type_id`),
  CONSTRAINT `type_id` FOREIGN KEY (`type_id`) REFERENCES `good_type` (`type_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `good`
--

LOCK TABLES `good` WRITE;
/*!40000 ALTER TABLE `good` DISABLE KEYS */;
INSERT INTO `good` VALUES (1,'牛肉汤粉',15,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(2,'牛杂汤粉',15,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(3,'牛腩汤粉',15,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(4,'牛丸汤粉',15,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(5,'猪杂汤粉',8,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(6,'廋肉汤粉',8,3,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(7,'腌面+汤',18,7,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(8,'牛腩饭',18,8,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(9,'牛肉酸菜饭',18,8,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(10,'鸡蛋肠粉',5,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(11,'火腿肠粉',5,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(12,'鸡蛋米粉',5,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(13,'鸡蛋蒸面',5,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(14,'鸡蛋肉肠粉',6,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(15,'鸡蛋肉米粉',6,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(16,'鸡蛋肉蒸面',6,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(17,'腊肠鸡蛋肠粉',7,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(18,'猪肝鸡蛋肠',7,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(19,'斋肠粉',4,4,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(20,'凉拌皮蛋',15,6,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(21,'凉拌青瓜',15,6,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(22,'凉拌木耳',18,6,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(23,'凉拌牛腩',35,6,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL),(24,'牛肉炒河粉',15,5,'image/383d7264bada4561935e0c9eba0c10d9.png',0,NULL);
/*!40000 ALTER TABLE `good` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `good_type`
--

DROP TABLE IF EXISTS `good_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `good_type` (
  `type_id` int NOT NULL AUTO_INCREMENT COMMENT '主键 类型id',
  `type_name` varchar(255) DEFAULT NULL COMMENT '类型名称',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `good_type`
--

LOCK TABLES `good_type` WRITE;
/*!40000 ALTER TABLE `good_type` DISABLE KEYS */;
INSERT INTO `good_type` VALUES (1,'点过'),(2,'热销'),(3,'汤粉面'),(4,'早餐类'),(5,'炒粉炒面'),(6,'凉拌小食'),(7,'腌面食'),(8,'套餐主食'),(9,'饮料区');
/*!40000 ALTER TABLE `good_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!50001 DROP VIEW IF EXISTS `goods`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `goods` AS SELECT 
 1 AS `id`,
 1 AS `good_price`,
 1 AS `type_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `goods`
--

/*!50001 DROP VIEW IF EXISTS `goods`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`%` SQL SECURITY DEFINER */
/*!50001 VIEW `goods` AS select `good`.`id` AS `id`,`good`.`good_price` AS `good_price`,`good_type`.`type_name` AS `type_name` from (`good` left join `good_type` on((`good`.`type_id` = `good_type`.`type_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-03-14  0:19:03
