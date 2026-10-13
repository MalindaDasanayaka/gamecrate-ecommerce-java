-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: game_crate_db
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `game_crate_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `game_crate_db` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci */;

USE `game_crate_db`;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `line_1` varchar(45) NOT NULL,
  `line_2` varchar(45) NOT NULL,
  `city_id` int(11) NOT NULL,
  `postal_code` varchar(5) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_address_city1_idx` (`city_id`),
  KEY `fk_address_user1_idx` (`user_id`),
  CONSTRAINT `fk_address_city1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_address_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,'No. 42, Galle Road','Kollupitiya',4,'60181',1),(2,'No. 15, Temple Road','Colombo 03',4,'60161',1),(3,'No. 256, Bakers Lane','Kandy Road',1,'60152',2),(4,'2nd Street','Galle Road',1,'60253',4),(5,'2nd Lane','Kandy Road',1,'60151',5),(7,'Homagama road','Colombo',1,'60020',9),(8,'Homagama road','Colombo',1,'60020',9),(9,'Kurunegala','Kandy',4,'60450',11);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin@gamecrate.lk','Admin@GameCrate2026');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cart_user1_idx` (`user_id`),
  KEY `fk_cart_product1_idx` (`product_id`),
  CONSTRAINT `fk_cart_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_cart_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=181 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'Consoles'),(2,'Games');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `city`
--

DROP TABLE IF EXISTS `city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `city`
--

LOCK TABLES `city` WRITE;
/*!40000 ALTER TABLE `city` DISABLE KEYS */;
INSERT INTO `city` VALUES (1,'Colombo'),(2,'Gampaha'),(3,'Kaluthara'),(4,'Kurunegala'),(5,'Puththalama'),(6,'Kandy'),(7,'Matale'),(8,'Nuwara Eliya'),(9,'Galle'),(10,'Matara'),(11,'Hambanthota'),(12,'Kegalle'),(13,'Rathnapura'),(14,'Bandarawela');
/*!40000 ALTER TABLE `city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_type`
--

DROP TABLE IF EXISTS `delivery_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `delivery_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_type`
--

LOCK TABLES `delivery_type` WRITE;
/*!40000 ALTER TABLE `delivery_type` DISABLE KEYS */;
INSERT INTO `delivery_type` VALUES (1,'Within Kurunegala',100),(2,'Out of Kurunegala',250);
/*!40000 ALTER TABLE `delivery_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genre`
--

DROP TABLE IF EXISTS `genre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genre` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genre`
--

LOCK TABLES `genre` WRITE;
/*!40000 ALTER TABLE `genre` DISABLE KEYS */;
INSERT INTO `genre` VALUES (1,'Not Applicable'),(2,'Action'),(3,'Adventure'),(4,'Educational'),(5,'Fighting'),(6,'First-Person Shooter'),(7,'Horror'),(8,'Puzzle'),(9,'Racing'),(10,'Role-Playing Game'),(11,'Simulation'),(12,'Sports'),(13,'Strategy'),(14,'Survival');
/*!40000 ALTER TABLE `genre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL,
  `orders_id` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_order_items_product1_idx` (`product_id`),
  KEY `fk_order_items_orders1_idx` (`orders_id`),
  CONSTRAINT `fk_order_items_orders1` FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_order_items_product1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,12,3,'50095500'),(2,1,1,'50095500'),(3,15,2,'50095500'),(4,16,1,'50095500'),(5,17,1,'50095500'),(6,18,5,'50095500'),(7,13,2,'28526915'),(8,16,1,'28526915'),(9,4,1,'11544397'),(10,4,1,'26787803'),(11,17,2,'67388783'),(12,20,1,'67388783'),(13,10,1,'37486812'),(14,9,1,'37486812'),(15,11,1,'37486812'),(16,25,2,'37486812'),(17,24,1,'37486812'),(18,23,1,'57192411'),(19,21,2,'57192411'),(20,6,1,'25701238'),(21,20,1,'25701238'),(22,22,1,'25701238'),(23,5,1,'19841050'),(24,15,1,'19841050'),(25,3,1,'93220767'),(26,1,1,'26724690'),(27,7,1,'95496612'),(28,8,1,'61396167'),(29,1,1,'14810407'),(30,17,2,'14810407'),(31,18,1,'14810407'),(32,4,1,'56774976'),(33,13,1,'56774976'),(34,15,1,'56774976'),(35,1,1,'33170689'),(36,19,1,'33170689'),(37,18,1,'33170689'),(38,17,1,'33170689'),(39,16,1,'33170689'),(40,4,1,'35953490'),(41,15,1,'35953490'),(42,14,1,'35953490'),(43,13,1,'35953490'),(44,12,1,'35953490'),(45,6,1,'21409430'),(46,23,1,'21409430'),(47,22,1,'21409430'),(48,21,1,'21409430'),(49,20,1,'21409430'),(50,26,1,'13247414'),(51,25,1,'13247414'),(52,24,1,'13247414'),(53,26,1,'56928454'),(54,25,1,'56928454'),(55,24,1,'56928454'),(56,1,1,'23542025'),(57,17,4,'23542025'),(60,1,1,'18485250'),(61,4,1,'26702679'),(62,13,2,'26702679'),(63,22,2,'25113896');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status`
--

DROP TABLE IF EXISTS `order_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status`
--

LOCK TABLES `order_status` WRITE;
/*!40000 ALTER TABLE `order_status` DISABLE KEYS */;
INSERT INTO `order_status` VALUES (1,'Paid'),(2,'Processing'),(3,'Out for Delivery'),(4,'Delivered');
/*!40000 ALTER TABLE `order_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `id` varchar(10) NOT NULL,
  `user_id` int(11) NOT NULL,
  `address_id` int(11) NOT NULL,
  `delivery_type_id` int(11) NOT NULL,
  `order_status_id` int(11) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_orders_user1_idx` (`user_id`),
  KEY `fk_orders_address1_idx` (`address_id`),
  KEY `fk_orders_delivery_type1_idx` (`delivery_type_id`),
  KEY `fk_orders_order_status1_idx` (`order_status_id`),
  CONSTRAINT `fk_orders_address1` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_orders_delivery_type1` FOREIGN KEY (`delivery_type_id`) REFERENCES `delivery_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_orders_order_status1` FOREIGN KEY (`order_status_id`) REFERENCES `order_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_orders_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('11544397',2,3,2,4,'2025-09-29 17:28:54'),('13247414',1,1,1,1,'2025-10-02 12:47:09'),('14810407',1,1,1,3,'2025-10-01 09:42:56'),('18485250',9,7,2,1,'2025-10-06 22:53:37'),('19841050',1,1,1,4,'2025-09-30 15:02:31'),('21409430',1,1,1,1,'2025-10-02 09:49:25'),('23542025',5,5,2,1,'2025-10-04 11:35:10'),('25113896',11,9,1,1,'2026-01-22 18:43:21'),('25701238',1,1,1,4,'2025-09-30 14:16:56'),('26472915',9,8,2,4,'2025-10-06 22:53:59'),('26702679',1,1,1,1,'2025-10-18 13:55:37'),('26724690',1,1,1,4,'2025-09-30 15:16:50'),('26787803',1,2,1,4,'2025-09-29 17:48:02'),('28526915',2,3,2,4,'2025-09-29 17:13:00'),('33170689',1,1,1,1,'2025-10-01 15:35:28'),('35953490',1,1,1,1,'2025-10-02 09:37:26'),('37486812',1,1,1,4,'2025-09-30 11:57:19'),('50095500',1,1,1,4,'2025-09-28 16:55:56'),('56774976',4,4,2,2,'2025-10-01 12:12:47'),('56928454',1,2,1,1,'2025-10-04 09:36:58'),('57192411',1,1,1,4,'2025-09-30 12:11:22'),('61396167',1,1,1,4,'2025-09-30 15:24:47'),('67388783',1,1,1,4,'2025-09-29 18:11:15'),('93220767',1,1,1,4,'2025-09-30 15:13:37'),('95496612',1,1,1,4,'2025-09-30 15:21:05');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `platform`
--

DROP TABLE IF EXISTS `platform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `platform` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `platform`
--

LOCK TABLES `platform` WRITE;
/*!40000 ALTER TABLE `platform` DISABLE KEYS */;
INSERT INTO `platform` VALUES (1,'PlayStation 4'),(2,'PlayStation 5'),(3,'Xbox'),(4,'Nintendo Switch'),(5,'Nintendo Switch 2');
/*!40000 ALTER TABLE `platform` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `category_id` int(11) NOT NULL,
  `platform_id` int(11) NOT NULL,
  `genre_id` int(11) NOT NULL,
  `description` text NOT NULL,
  `qty` int(11) NOT NULL,
  `price` double NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `product_status_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_product_category1_idx` (`category_id`),
  KEY `fk_product_platform1_idx` (`platform_id`),
  KEY `fk_product_genre1_idx` (`genre_id`),
  KEY `fk_product_product_status1_idx` (`product_status_id`),
  CONSTRAINT `fk_product_category1` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_genre1` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_platform1` FOREIGN KEY (`platform_id`) REFERENCES `platform` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_product_status1` FOREIGN KEY (`product_status_id`) REFERENCES `product_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'PlayStation 4 (Standard)',1,1,1,'The PlayStation 4 Standard Edition is the console that redefined gaming for a generation. With a robust library of over 2,000 titles and seamless online multiplayer through PS Plus, itâs an affordable yet powerful gaming solution. Enjoy HD graphics, cinematic storytelling, and access to popular franchises like God of War, The Last of Us, and Uncharted.',17,800,'2025-09-24 12:44:03','2025-09-28 08:34:59',1),(2,'PlayStation 4 Slim',1,1,1,'The PS4 Slim is a sleek, energy-efficient version of the original PS4, offering the same performance in a more compact shell. It features a 500GB or 1TB hard drive, support for HDR gaming, and built-in Wi-Fi and Bluetooth. Its modern design and quiet cooling system make it perfect for living rooms and bedrooms.',22,750,'2025-09-24 12:45:32','2025-09-28 09:12:58',1),(3,'PlayStation 4 Pro',1,1,1,'The PS4 Pro delivers an enhanced gaming experience with 4K upscaling, smoother frame rates, and improved visual fidelity. Itâs ideal for gamers with 4K TVs who want to enjoy ultra-high-definition gaming and streaming. The PS4 Pro also improves performance in compatible PS4 titles, offering faster load times and richer graphics.',23,1100,'2025-09-24 12:47:57','2025-09-28 09:15:09',1),(4,'PlayStation 5 (Standard)',1,2,1,'Experience lightning-fast loading with an ultra-high speed SSD, deeper immersion with support for haptic feedback, adaptive triggers, and 3D Audio, and an all-new generation of incredible PlayStation games. The PS5 Standard Edition includes a 4K UHD Blu-ray disc drive, allowing you to enjoy both digital and physical copies of your favorite games and movies. With a sleek futuristic design and support for ray tracing and up to 120fps gameplay, the PlayStation 5 delivers cutting-edge performance, visuals, and responsiveness.',18,1800,'2025-09-24 12:49:32','2025-09-29 15:50:19',1),(5,'PlayStation 5 Slim',1,2,1,'The PlayStation 5 Slim offers the same powerhouse performance in a more compact, streamlined form factor. Featuring a 1TB SSD, 4K UHD Blu-ray disc drive, and support for ray tracing, HDR, and lightning-fast load times, the PS5 Slim is built for modern gamers who want style and power. Its slimmer, lighter design makes it easier to fit into entertainment setups, while maintaining full compatibility with PS5 games and accessories.',19,1750,'2025-09-24 12:52:09','2025-09-29 16:24:08',1),(6,'Xbox Series X',1,3,1,'The Xbox Series X is the most powerful console ever created by Microsoft. Equipped with a custom 1TB NVMe SSD, 12 teraflops of GPU power, and support for up to 8K resolution and 120fps gameplay, the Series X delivers unmatched performance and speed. With quick resume, ray tracing, and full backward compatibility across Xbox One, Xbox 360, and original Xbox titles, itâs the ultimate console for serious gamers.',20,1850,'2025-09-24 12:53:31','2025-09-29 16:50:17',1),(7,'Xbox Series S',1,3,1,'The Xbox Series S is an all-digital next-gen console designed for performance and affordability. Despite its smaller size, it supports fast load times, 1440p resolution up to 120fps, and full access to Xbox Game Pass. With its sleek white design and seamless cloud gaming capabilities, itâs perfect for casual and digital-first gamers.',18,1700,'2025-09-24 12:55:40','2025-10-01 09:14:49',1),(8,'Nintendo Switch (Original)',1,4,1,'The Nintendo Switch is the revolutionary hybrid console that allows you to play anytime, anywhere. Whether docked to your TV, placed in tabletop mode, or taken on the go as a handheld device, the Switch adapts to your lifestyle. Featuring a 6.2-inch touchscreen, detachable Joy-Con controllers, and a vast library of exclusive titles like Zelda: Breath of the Wild and Super Mario Odyssey, the original Switch is a fan favorite.',26,750,'2025-09-24 12:57:25','2025-10-01 15:50:15',1),(9,'Nintendo Switch Lite',1,4,1,'The Nintendo Switch Lite is a lightweight, handheld-only version of the original Switch, built for gaming on the move. Itâs compact, durable, and available in vibrant colors. While it doesnât support docking to a TV, it plays the full library of Nintendo Switch games that support handheld mode, making it perfect for younger gamers or anyone who wants a dedicated portable device.',20,650,'2025-09-24 12:58:41','2025-10-02 09:15:00',1),(10,'Nintendo Switch OLED',1,4,1,'The Nintendo Switch OLED Model features a stunning 7-inch OLED screen that delivers vivid colors and sharper contrast, perfect for handheld and tabletop gaming. It includes an improved kickstand, enhanced audio, and 64GB of internal storage. The included dock also has a built-in LAN port for more stable online play. Itâs the premium way to enjoy Nintendoâs hybrid gaming experience.',18,950,'2025-09-24 12:59:51','2025-10-02 09:15:39',1),(11,'Nintendo Switch 2',1,5,1,'The Nintendo Switch 2 is expected to be the next-generation hybrid console from Nintendo. Rumored features include a larger OLED or mini-LED screen, enhanced graphics performance, backward compatibility, and support for advanced haptic feedback. Stay tuned for updates as the gaming world awaits official announcements.',15,1200,'2025-09-24 13:01:24','2025-10-02 09:56:07',1),(12,'Marvelâs SpiderâMan 2 (PS5)',2,2,2,'Swing through a sprawling New York City as both Peter Parker and Miles Morales in this exhilarating action-adventure sequel. With lightning-fast traversal, iconic villains like Venom and Kraven, and dynamic storytelling that interweaves the two heroesâ journeys, Spider-Man 2 offers an electrifying experienceâcomplete with stunning graphics, refined combat, and emotional depth.',30,210,'2025-09-27 15:24:00','2025-10-02 13:18:53',1),(13,'Elden Ring (PS5)',2,2,10,'From the creators of Dark Souls, this action RPG invites you into a hauntingly beautiful open world filled with secrets, danger, and epic bosses. Customize your character, forge alliances, and carve your own path through an intricately connected realm. Every step is a discoveryâevery victory, hard-earned.',33,180,'2025-09-27 15:26:33','2025-09-30 15:50:09',1),(14,'God of War Ragnarok (PS5)',2,2,2,'Join Kratos and his son Atreus in this mythic journey through Norse realms on the brink of prophecy. Expect brutal combat, breathtaking set-pieces, and deeply emotional storytelling. As Ragnarok looms, the father-and-son duo must face gods, monsters, and their own destinies in an unforgettable epic.',35,210,'2025-09-27 15:28:57','2025-09-30 15:52:08',1),(15,'Horizon Forbidden West (PS5)',2,2,3,'Explore lush post-apocalyptic landscapes as Aloy in this thrilling sequel. Battle colossal machines, uncover hidden secrets, and forge alliances to survive the Forbidden West. Features breathtaking environments and immersive storytelling.',25,165,'2025-09-27 15:31:21','2025-09-30 15:56:07',1),(16,'The Last of Us Part II â Remastered (PS4)',2,1,7,'The critically-acclaimed sequel returns with smoother visuals and enhanced performance. Ellieâs journey becomes a powerful story of loss, revenge, and resilience in a violent post-apocalyptic America. Its haunting imagery and intense gameplay make it an emotional powerhouse.',42,120,'2025-09-27 15:34:12','2025-09-30 16:04:16',1),(17,'Unchartedâ¯4: A Thiefâs End (PS4)',2,1,13,'Follow adventurer Nathan Drake on his final globe-spanning escapade. Expect swashbuckling treasure hunts, cinematic moments, and witty banter, with set-pieces ripped straight from blockbuster films. A polished and emotionally satisfying chapter in the beloved franchise.',30,150,'2025-09-27 15:37:02','2025-09-30 16:05:55',1),(18,'Bloodborne (PS4)',2,1,10,'From the creators of Dark Souls, this gothic action RPG delivers brutal, fast-paced combat and terrifying world design in a gothic horror setting. Risk and reward go hand-in-hand as you explore Yharnamâs nightmarish streets in a test of skill and courage.',26,200,'2025-09-27 15:39:10','2025-09-30 16:09:05',1),(19,'Horizon Zero Dawn (PS4)',2,1,3,'Play as Aloy, a skilled hunter in a world overrun by robotic beasts. Traverse lush forests, climb rocky cliffs, and uncover ancient mysteries. Rich storytelling and satisfying combat loops make this one of the most memorable open-world adventures on the platform.',31,250,'2025-09-27 15:41:03','2025-09-30 16:11:14',1),(20,'Halo Infinite (Xbox)',2,3,6,'Master Chief returns for a vast sci-fi journey on Zeta Halo. The campaign balances epic storytelling with freedom of approach, supplemented by a reimagined, addictive multiplayer featuring classic returns and new mechanics. A welcome next-gen boost to a legendary franchise.',27,170,'2025-09-27 15:42:52','2025-09-30 16:17:23',1),(21,'Red Dead Redemptionâ¯2 (Xbox)',2,3,2,'A sprawling Wild West epic where every detail feels lived-inâfrom sweeping landscapes to layered character relationships. Step into the boots of Arthur Morgan and the Van der Linde gang in a slow-burning tale of loyalty, survival, and tragedy.',32,185,'2025-09-27 15:44:22','2025-09-30 16:22:00',1),(22,'Alan Wakeâ¯2 (Xbox)',2,3,7,'This long-awaited sequel dives deeper into psychological horror with dual protagonists, supernatural prophecies, and a cinematic tone that blurs reality. With a gripping story, tense atmosphere, and inventive storytelling, itâs a standout thriller experience.',34,180,'2025-09-27 15:46:31','2025-09-30 16:50:09',1),(23,'Doom: Eternal (Xbox)',2,3,14,'Enter a medieval era of demon-slaying violence in this brutal Doom expansion. Armed with a sword and shotgun, storm gothic castles and demon-infested dungeons in fast-paced combat dripping with grit, gore, and adrenaline.',28,150,'2025-09-27 15:47:50','2025-09-30 17:12:59',1),(24,'Legend of Zelda Breath of the Wild (NS1)',2,4,10,'An open-world masterpiece where exploration and wonder reign. Climb any mountain, solve environmental puzzles, and face ancient threats at your own pace. A game that redefined adventure in gaming.',22,185,'2025-09-27 15:50:13','2025-09-30 17:14:31',1),(25,'Super Mario Odyssey (NS1)',2,4,3,'Marioâs grandest 3D adventure yet takes him around the globeâliterally. With his cap Cappy, Mario gains new movement abilities, hilarious transformations, and endless creative levels in this joyful platformer for all ages.',23,175,'2025-09-27 15:52:06','2025-09-30 17:21:01',1),(26,'Mario Kart 8 Deluxe (NS1)',2,4,9,'The ultimate kart racer loaded with tracks, characters, weapons, and modes. Drift, boost, and competeâor team up with friends locally or online to reign supreme on Rainbow Road.',23,160,'2025-09-27 15:53:36','2025-09-30 17:25:11',1),(27,'Black Myth: Wukong',2,2,2,'Black Myth: Wukong is an action RPG rooted in Chinese mythology. You shall set out as the Destined One to venture into the challenges and marvels ahead, to uncover the obscured truth beneath the veil of a glorious legend from the past.',30,250,'2025-09-30 11:37:54',NULL,1);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_status`
--

DROP TABLE IF EXISTS `product_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_status`
--

LOCK TABLES `product_status` WRITE;
/*!40000 ALTER TABLE `product_status` DISABLE KEYS */;
INSERT INTO `product_status` VALUES (1,'Active'),(2,'Discontinued');
/*!40000 ALTER TABLE `product_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `mobile` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(20) NOT NULL,
  `verification` varchar(10) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_status_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_user_status_idx` (`user_status_id`),
  CONSTRAINT `fk_user_user_status` FOREIGN KEY (`user_status_id`) REFERENCES `user_status` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'Malinda','Dasanayaka','0771234567','customer@gamecrate.lk','Customer@2026','Verified','2025-09-22 10:10:23','2025-09-24 09:24:26',1),(2,'Sahan','Gunasekara','0778185778','sahan@example.com','Sahan@1234','Verified','2025-09-22 16:31:18','2025-09-25 15:44:35',1),(3,'Yasiru','Samuditha','0767253744','yasiru@example.com','Yasiru@1234','Verified','2025-09-22 17:35:03','2025-09-25 09:32:31',1),(4,'Malinda','Dasanayaka','0772345628','malinda.demo@example.com','Demo@1234','Verified','2025-09-26 16:11:16',NULL,1),(5,'Kavindu','Dilshan','0778354288','kavindu@example.com','Ravid@2357','Verified','2025-09-30 11:30:43','2025-09-30 11:39:52',1),(9,'Alex','Morgan','0771324565','alex@example.com','Alex@1234','Verified','2025-10-06 22:51:47',NULL,1),(10,'janitha','akash','0762739091','janitha@example.com','jaNitha@$1','650892','2026-01-22 18:38:59',NULL,1),(11,'janitha','akash','0762739091','david@example.com','jaNitha@$1','Verified','2026-01-22 18:40:31',NULL,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_status`
--

DROP TABLE IF EXISTS `user_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_status`
--

LOCK TABLES `user_status` WRITE;
/*!40000 ALTER TABLE `user_status` DISABLE KEYS */;
INSERT INTO `user_status` VALUES (1,'Active'),(2,'Blocked');
/*!40000 ALTER TABLE `user_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'game_crate_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-13 18:34:50
