-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: doan_langthang
-- ------------------------------------------------------
-- Server version	8.0.22

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(100) COLLATE utf8_bin NOT NULL,
  `password` varchar(64) COLLATE utf8_bin NOT NULL,
  `role` enum('ROLE_MEMBER','ROLE_ADMIN') COLLATE utf8_bin NOT NULL DEFAULT 'ROLE_MEMBER',
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `avatar_link` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `gender` enum('MALE','FEMALE','UNKNOWN') COLLATE utf8_bin DEFAULT 'UNKNOWN',
  `date_of_birth` timestamp NULL DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  `about` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `occupation` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `last_login` timestamp NULL DEFAULT NULL,
  `login_count` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,'trinhvideo123@gmail.com','illusion','ROLE_MEMBER','Do Van Trinh',NULL,'MALE',NULL,1,NULL,NULL,NULL,0),(3,'trinhdvtdut@gmail.com','aloaloalo','ROLE_MEMBER','trinhdvt',NULL,'UNKNOWN',NULL,1,NULL,NULL,NULL,NULL),(4,'trinhdvt2@gmail.com','sasdads','ROLE_MEMBER','rinhdvt2',NULL,'UNKNOWN',NULL,1,NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmarked_post`
--

DROP TABLE IF EXISTS `bookmarked_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmarked_post` (
  `account_id` int unsigned NOT NULL,
  `post_id` int unsigned NOT NULL,
  `bookmarked_date` timestamp NOT NULL,
  PRIMARY KEY (`account_id`,`post_id`),
  KEY `user_bookmark_idx` (`account_id`),
  KEY `post_bookmark_idx` (`post_id`),
  CONSTRAINT `post_bookmark` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_bookmark` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmarked_post`
--

LOCK TABLES `bookmarked_post` WRITE;
/*!40000 ALTER TABLE `bookmarked_post` DISABLE KEYS */;
INSERT INTO `bookmarked_post` VALUES (1,1,'2021-04-22 09:46:11'),(3,1,'2021-04-22 17:37:37'),(4,1,'2021-04-22 17:40:58');
/*!40000 ALTER TABLE `bookmarked_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `post_id` int unsigned NOT NULL,
  `account_id` int unsigned NOT NULL,
  `content` varchar(255) COLLATE utf8_bin NOT NULL,
  `comment_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_comment_idx` (`account_id`),
  KEY `post_comment_idx` (`post_id`),
  CONSTRAINT `comment_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,1,'Comment 1','2021-04-22 20:53:32'),(2,1,3,'Comment 1 / 3','2021-04-22 20:53:43'),(3,1,1,'Comment 2','2021-04-22 20:53:53');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `comment_id` int unsigned NOT NULL,
  `account_id` int unsigned NOT NULL,
  PRIMARY KEY (`comment_id`,`account_id`),
  KEY `like_comment_idx` (`comment_id`),
  KEY `user_like_comment_idx` (`account_id`),
  CONSTRAINT `like_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_like_comment` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
INSERT INTO `comment_like` VALUES (1,1),(2,1),(3,1);
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `external_account`
--

DROP TABLE IF EXISTS `external_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `external_account` (
  `account_id` int unsigned NOT NULL,
  `fb_link` char(100) COLLATE utf8_bin DEFAULT NULL,
  `instagram_link` char(100) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  CONSTRAINT `acc_external_acc` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `external_account`
--

LOCK TABLES `external_account` WRITE;
/*!40000 ALTER TABLE `external_account` DISABLE KEYS */;
INSERT INTO `external_account` VALUES (1,'fb','insta');
/*!40000 ALTER TABLE `external_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `following_relationship`
--

DROP TABLE IF EXISTS `following_relationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `following_relationship` (
  `account_id` int unsigned NOT NULL,
  `following_account_id` int unsigned NOT NULL,
  `following_date` timestamp NOT NULL,
  PRIMARY KEY (`account_id`,`following_account_id`),
  KEY `acc_to_acc2_idx` (`following_account_id`),
  CONSTRAINT `acc_to_acc` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `acc_to_acc2` FOREIGN KEY (`following_account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `following_relationship`
--

LOCK TABLES `following_relationship` WRITE;
/*!40000 ALTER TABLE `following_relationship` DISABLE KEYS */;
INSERT INTO `following_relationship` VALUES (1,3,'2021-04-17 09:02:53'),(1,4,'2021-04-17 10:14:43');
/*!40000 ALTER TABLE `following_relationship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notify`
--

DROP TABLE IF EXISTS `notify`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notify` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int unsigned NOT NULL,
  `content` text COLLATE utf8_bin,
  `notify_date` timestamp NULL DEFAULT NULL,
  `is_seen` tinyint(1) NOT NULL DEFAULT '0',
  `post_id` int unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `notify_user_idx` (`account_id`),
  KEY `notify_post_idx` (`post_id`),
  CONSTRAINT `notify_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `notify_user` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notify`
--

LOCK TABLES `notify` WRITE;
/*!40000 ALTER TABLE `notify` DISABLE KEYS */;
INSERT INTO `notify` VALUES (1,1,'Day la 1 notify','2021-04-17 16:54:04',0,NULL),(2,1,'Day la 1 notify co post_id','2021-04-25 15:26:09',0,1);
/*!40000 ALTER TABLE `notify` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int unsigned NOT NULL,
  `title` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `content` text COLLATE utf8_bin,
  `published_date` timestamp NULL DEFAULT NULL,
  `last_modified` timestamp NULL DEFAULT NULL,
  `post_thumbnail` char(100) COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_post_idx` (`account_id`),
  CONSTRAINT `account_post` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,1,'Test thu post repository','Khong co gi trong nay ca, lam on cuu toi','2021-04-16 15:06:24','2021-04-16 15:06:28',NULL,1);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_category`
--

DROP TABLE IF EXISTS `post_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_category` (
  `post_id` int unsigned NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`post_id`,`category_id`),
  KEY `post_categories_idx` (`post_id`),
  KEY `categories_post_idx` (`category_id`),
  CONSTRAINT `categories_post` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `post_categories` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_category`
--

LOCK TABLES `post_category` WRITE;
/*!40000 ALTER TABLE `post_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_report`
--

DROP TABLE IF EXISTS `post_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_id` int unsigned NOT NULL,
  `post_id` int unsigned NOT NULL,
  `reported_date` timestamp NULL DEFAULT NULL,
  `content` text COLLATE utf8_bin,
  `is_solved` tinyint(1) DEFAULT '0',
  `decision` text COLLATE utf8_bin,
  PRIMARY KEY (`id`),
  KEY `user_report_idx` (`account_id`),
  KEY `posrt_report_idx` (`post_id`),
  CONSTRAINT `account_report` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_report` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_report`
--

LOCK TABLES `post_report` WRITE;
/*!40000 ALTER TABLE `post_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_stats`
--

DROP TABLE IF EXISTS `post_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_stats` (
  `post_id` int unsigned NOT NULL,
  `view_count` int DEFAULT '0',
  `bookmark_count` int DEFAULT '0',
  `comment_count` int DEFAULT '0',
  PRIMARY KEY (`post_id`),
  CONSTRAINT `stats_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_stats`
--

LOCK TABLES `post_stats` WRITE;
/*!40000 ALTER TABLE `post_stats` DISABLE KEYS */;
INSERT INTO `post_stats` VALUES (1,1,1,0);
/*!40000 ALTER TABLE `post_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_tag`
--

DROP TABLE IF EXISTS `post_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_tag` (
  `post_id` int unsigned NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`post_id`,`tag_id`),
  KEY `tag_post_idx` (`tag_id`),
  CONSTRAINT `post_tag` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `tag_post` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_tag`
--

LOCK TABLES `post_tag` WRITE;
/*!40000 ALTER TABLE `post_tag` DISABLE KEYS */;
INSERT INTO `post_tag` VALUES (1,1),(1,2);
/*!40000 ALTER TABLE `post_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_name_UNIQUE` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'Da-nang'),(2,'Hue');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-30 22:37:56
