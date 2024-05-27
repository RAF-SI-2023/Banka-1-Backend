/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- set myusername and mypassword to root and root


DROP DATABASE IF EXISTS bank_marketservice;
DROP DATABASE IF EXISTS bank_userservice;
DROP DATABASE IF EXISTS bank_test;

CREATE DATABASE IF NOT EXISTS bank_marketservice;
CREATE DATABASE IF NOT EXISTS bank_userservice;
CREATE DATABASE IF NOT EXISTS bank_test;

-- CREATE USER 'root'@'%' IDENTIFIED BY 'root';
-- GRANT ALL PRIVILEGES ON bank_marketservice.* TO 'root'@'%';
-- GRANT ALL PRIVILEGES ON bank_userservice.* TO 'root'@'%';

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
-- START TRANSACTION;

USE bank_userservice;


-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bank_userservice
-- ------------------------------------------------------
-- Server version	8.3.0

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
-- Table structure for table `bank_account`
--

DROP TABLE IF EXISTS `bank_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bank_account` (
                                `account_status` bit(1) DEFAULT NULL,
                                `account_type` tinyint DEFAULT NULL,
                                `available_balance` double DEFAULT NULL,
                                `balance` double DEFAULT NULL,
                                `maintenance_cost` double DEFAULT NULL,
                                `company_id` bigint DEFAULT NULL,
                                `created_by_agent_id` bigint DEFAULT NULL,
                                `creation_date` bigint DEFAULT NULL,
                                `currency_id` bigint DEFAULT NULL,
                                `customer_id` bigint DEFAULT NULL,
                                `expiration_date` bigint DEFAULT NULL,
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `account_name` varchar(255) DEFAULT NULL,
                                `account_number` varchar(255) DEFAULT NULL,
                                `subtype_of_account` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKgm6p0ej8mhbakybacuneanald` (`company_id`),
                                KEY `FKb0rqy46m451rdbnhil6kkvlve` (`currency_id`),
                                KEY `FKj818ht4ban0c4uw4bmsbf3jme` (`customer_id`),
                                CONSTRAINT `FKb0rqy46m451rdbnhil6kkvlve` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
                                CONSTRAINT `FKgm6p0ej8mhbakybacuneanald` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
                                CONSTRAINT `FKj818ht4ban0c4uw4bmsbf3jme` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_account`
--

LOCK TABLES `bank_account` WRITE;
/*!40000 ALTER TABLE `bank_account` DISABLE KEYS */;
-- INSERT INTO `bank_account` VALUES (_binary '\0',2,1000000,1000000,NULL,1,NULL,1714003200,10000,NULL,1871769600,9,'Banks account','131242095807818250',NULL),(_binary '\0',2,1000000,1000000,NULL,1,NULL,1714003200,10001,NULL,1871769600,10,'Banks account','131242095807818251',NULL),(_binary '\0',2,1000010,1000010,NULL,1,NULL,1714003200,10002,NULL,1871769600,11,'Banks account','131242095807818251',NULL),(_binary '',1,8990,10990,100,NULL,100,1710959528,10000,101,2710959528,100,'test12345','1234567890','subtest'),(_binary '',1,11000,13000,100,NULL,100,1710959558,10001,101,2710959528,101,'test1','0987654321','subtest'),(_binary '\0',1,1000,1000,100,NULL,100,1714348800,10000,101,1872115200,236,'My Account','131242014608610358','PERSONAL'),(_binary '\0',2,1000,1000,NULL,1,100,1714348800,10000,NULL,1872115200,237,'BUSINESS Account','131242031194361386',NULL),(_binary '',1,0,0,1000,NULL,100,1714348800,10000,1,1872115200,238,NULL,'131242059348804610','');
/*!40000 ALTER TABLE `bank_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `capital`
--

DROP TABLE IF EXISTS `capital`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `capital` (
                           `listing_type` tinyint DEFAULT NULL,
                           `reserved` double DEFAULT NULL,
                           `total` double DEFAULT NULL,
                           `bank_account_id` bigint DEFAULT NULL,
                           `currency_id` bigint DEFAULT NULL,
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `listing_id` bigint DEFAULT NULL,
                           `ticker` varchar(255) DEFAULT NULL,
                           `public_total` int DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `UK_81xcswplhpkvu5wbbfjgs0n03` (`bank_account_id`),
                           UNIQUE KEY `UK_dm0tv6yd7escujrfu33k2ubo3` (`currency_id`),
                           CONSTRAINT `FK1v2xrn4lbxfx4e5jjsjnlgjg1` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`),
                           CONSTRAINT `FKbxbn82a8uhg0qnlx64suq72cr` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=246 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `capital`
--

LOCK TABLES `capital` WRITE;
/*!40000 ALTER TABLE `capital` DISABLE KEYS */;
/*!40000 ALTER TABLE `capital` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `card` (
                        `card_limit` int DEFAULT NULL,
                        `cvv` varchar(3) DEFAULT NULL,
                        `is_activated` bit(1) DEFAULT NULL,
                        `creation_date` bigint DEFAULT NULL,
                        `expiration_date` bigint DEFAULT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `card_number` varchar(16) DEFAULT NULL,
                        `account_number` varchar(255) DEFAULT NULL,
                        `card_name` varchar(255) DEFAULT NULL,
                        `card_type` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_by1nk98m2hq5onhl68bo09sc1` (`card_number`)
) ENGINE=InnoDB AUTO_INCREMENT=477 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
-- INSERT INTO `card` VALUES (1000,'735',_binary '',1714348800,1872115200,471,'4766332386092836','131242014608610358','VisaCard','VISA'),(10000,'578',_binary '',1714348800,1872115200,472,'8023854887494803','131242014608610358','MasterCard','MASTER'),(1000,'051',_binary '',1714348800,1872115200,473,'1820225461629711','131242031194361386','VisaCard','VISA'),(10000,'100',_binary '',1714348800,1872115200,474,'2856942271346172','131242031194361386','MasterCard','MASTER'),(1000,'959',_binary '',1714348800,1872115200,475,'2111089126264314','131242059348804610','VisaCard','VISA'),(10000,'623',_binary '',1714348800,1872115200,476,'7146780045183909','131242059348804610','MasterCard','MASTER');
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `company` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `company_name` varchar(255) DEFAULT NULL,
                           `fax_number` varchar(255) DEFAULT NULL,
                           `id_number` varchar(255) DEFAULT NULL,
                           `job_id` varchar(255) DEFAULT NULL,
                           `pib` varchar(255) DEFAULT NULL,
                           `registration_number` varchar(255) DEFAULT NULL,
                           `telephone_number` varchar(255) DEFAULT NULL,
                           `adress` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
-- INSERT INTO `company` VALUES (1,'Banka1','test','test','test','test','test','test',NULL);
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `amount` double DEFAULT NULL,
                            `bank_approval` bit(1) DEFAULT NULL,
                            `comment` varchar(255) DEFAULT NULL,
                            `creation_date` bigint DEFAULT NULL,
                            `price` double DEFAULT NULL,
                            `realization_date` bigint DEFAULT NULL,
                            `reference_number` varchar(255) DEFAULT NULL,
                            `seller_approval` bit(1) DEFAULT NULL,
                            `ticker` varchar(255) DEFAULT NULL,
                            `buyer_id` bigint NOT NULL,
                            `seller_id` bigint NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKr59ftb1p1a3j8iagy9asg0jyx` (`buyer_id`),
                            KEY `FKxyn6dm8rtua1xjw26nlygfqt` (`seller_id`),
                            CONSTRAINT `FKr59ftb1p1a3j8iagy9asg0jyx` FOREIGN KEY (`buyer_id`) REFERENCES `bank_account` (`id`),
                            CONSTRAINT `FKxyn6dm8rtua1xjw26nlygfqt` FOREIGN KEY (`seller_id`) REFERENCES `bank_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

LOCK TABLES `contract` WRITE;
/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `currency` (
                            `active` bit(1) DEFAULT NULL,
                            `fromrsd` double DEFAULT NULL,
                            `torsd` double DEFAULT NULL,
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `country` varchar(255) DEFAULT NULL,
                            `currency_code` varchar(255) DEFAULT NULL,
                            `currency_desc` varchar(255) DEFAULT NULL,
                            `currency_name` varchar(255) DEFAULT NULL,
                            `currency_symbol` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
-- INSERT INTO `currency` VALUES (_binary '',1,1,10000,'TEST','TST','TEST','TEST','TST'),(_binary '',1,1,10001,'TEST1','TSS','TEST1','TEST1','TST1'),(_binary '',1,1,10002,'RSD','RSD','RSD','RSD','RSD');
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
                            `active` bit(1) NOT NULL,
                            `company_id` bigint DEFAULT NULL,
                            `date_of_birth` bigint DEFAULT NULL,
                            `user_id` bigint NOT NULL,
                            `activation_token` varchar(255) DEFAULT NULL,
                            `address` varchar(255) DEFAULT NULL,
                            `email` varchar(255) DEFAULT NULL,
                            `first_name` varchar(255) DEFAULT NULL,
                            `gender` varchar(255) DEFAULT NULL,
                            `jmbg` varchar(255) DEFAULT NULL,
                            `last_name` varchar(255) DEFAULT NULL,
                            `password` varchar(255) DEFAULT NULL,
                            `phone_number` varchar(255) DEFAULT NULL,
                            `reset_password_token` varchar(255) DEFAULT NULL,
                            `single_use_code` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `UK_dwk6cx0afu8bs9o4t536v1j5v` (`email`),
                            UNIQUE KEY `UK_74noy3hdjlok2p75kut6hc7q9` (`jmbg`),
                            KEY `FKcc6lvs1hfb70cc5rjbyq0m8is` (`company_id`),
                            CONSTRAINT `FKcc6lvs1hfb70cc5rjbyq0m8is` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
-- INSERT INTO `customer` VALUES (_binary '',NULL,1000159200000,1,NULL,'Bulevar Kralja Aleksandra 73','petarpetrovic@gmail.rs','Milan','M','1508213802838','Petrovic','$2a$10$B73HKCR5.81FloKSVWcRX.WXIuzhqRMUeVlE.42YeJHtzJBDt4bVW','0651234567',NULL,NULL),(_binary '',NULL,NULL,101,NULL,NULL,'user@test.com','petar',NULL,'412325124','petrovic','$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze','1111111111',NULL,NULL),(_binary '',NULL,NULL,102,NULL,NULL,'user123@test.com','mika',NULL,'215412512','mikic','$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze','22222324',NULL,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
                            `active` bit(1) NOT NULL,
                            `limit_now` double DEFAULT NULL,
                            `orderlimit` double DEFAULT NULL,
                            `require_approval` bit(1) DEFAULT NULL,
                            `company_id` bigint DEFAULT NULL,
                            `user_id` bigint NOT NULL,
                            `activation_token` varchar(255) DEFAULT NULL,
                            `email` varchar(255) DEFAULT NULL,
                            `first_name` varchar(255) DEFAULT NULL,
                            `jmbg` varchar(255) DEFAULT NULL,
                            `last_name` varchar(255) DEFAULT NULL,
                            `password` varchar(255) DEFAULT NULL,
                            `phone_number` varchar(255) DEFAULT NULL,
                            `position` varchar(255) DEFAULT NULL,
                            `reset_password_token` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`user_id`),
                            UNIQUE KEY `UK_fopic1oh5oln2khj8eat6ino0` (`email`),
                            UNIQUE KEY `UK_cd34h7mb4lrr65book7qaa8sj` (`jmbg`),
                            KEY `FK5v50ed2bjh60n1gc7ifuxmgf4` (`company_id`),
                            CONSTRAINT `FK5v50ed2bjh60n1gc7ifuxmgf4` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
-- INSERT INTO `employee` VALUES (_binary '',0,300,_binary '',1,2,NULL,'drugizaposleni@gmail.rs','Aleksa','0254203482','Aleksic','$2a$10$lGAEDc6sKo3GkFzpjqf/ZOsnRWvBR1F.JgnagDyLYHNdp0vCUJ0k6','512312321','agent',NULL),(_binary '',NULL,NULL,NULL,NULL,100,NULL,'admin@admin.com','admin','admin','admin','$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze','1234567890','admin',NULL);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequences`
--

DROP TABLE IF EXISTS `hibernate_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequences` (
                                       `next_val` bigint DEFAULT NULL,
                                       `sequence_name` varchar(255) NOT NULL,
                                       PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequences`
--

LOCK TABLES `hibernate_sequences` WRITE;
/*!40000 ALTER TABLE `hibernate_sequences` DISABLE KEYS */;
-- INSERT INTO `hibernate_sequences` VALUES (100,'default');
/*!40000 ALTER TABLE `hibernate_sequences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan`
--

DROP TABLE IF EXISTS `loan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan` (
                        `effective_interest_rate` double DEFAULT NULL,
                        `installment_amount` double DEFAULT NULL,
                        `loan_amount` double DEFAULT NULL,
                        `loan_type` tinyint DEFAULT NULL,
                        `nominal_interest_rate` double DEFAULT NULL,
                        `remaining_debt` double DEFAULT NULL,
                        `repayment_period` int DEFAULT NULL,
                        `agreement_date` bigint DEFAULT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `maturity_date` bigint DEFAULT NULL,
                        `next_installment_date` bigint DEFAULT NULL,
                        `account_number` varchar(255) NOT NULL,
                        `currency` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan`
--

LOCK TABLES `loan` WRITE;
/*!40000 ALTER TABLE `loan` DISABLE KEYS */;
-- INSERT INTO `loan` VALUES (0,0,0,0,0,0,0,0,100,0,0,'1234567890','TST');
/*!40000 ALTER TABLE `loan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loan_request`
--

DROP TABLE IF EXISTS `loan_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `loan_request` (
                                `loan_amount` double DEFAULT NULL,
                                `loan_type` tinyint DEFAULT NULL,
                                `monthly_income_amount` double DEFAULT NULL,
                                `permanent_employee` bit(1) DEFAULT NULL,
                                `status` tinyint DEFAULT NULL,
                                `employment_period` bigint DEFAULT NULL,
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `loan_term` bigint DEFAULT NULL,
                                `account_number` varchar(255) DEFAULT NULL,
                                `branch_office` varchar(255) DEFAULT NULL,
                                `currency` varchar(255) DEFAULT NULL,
                                `loan_purpose` varchar(255) DEFAULT NULL,
                                `monthly_income_currency` varchar(255) DEFAULT NULL,
                                `phone_number` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loan_request`
--

LOCK TABLES `loan_request` WRITE;
/*!40000 ALTER TABLE `loan_request` DISABLE KEYS */;
-- INSERT INTO `loan_request` VALUES (1000,0,428048,_binary '',0,50,2,24,'1234567890','test','TST','test','TST','1111111111');
/*!40000 ALTER TABLE `loan_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `market_order`
--

DROP TABLE IF EXISTS `market_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `market_order` (
                                `all_or_none` bit(1) DEFAULT NULL,
                                `fee` double DEFAULT NULL,
                                `limit_value` double DEFAULT NULL,
                                `listing_type` tinyint DEFAULT NULL,
                                `order_type` tinyint DEFAULT NULL,
                                `price` double DEFAULT NULL,
                                `status` tinyint DEFAULT NULL,
                                `stop_value` double DEFAULT NULL,
                                `approved_by_user_id` bigint(20) DEFAULT NULL,
                                `contract_size` bigint(20) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `last_modified_date` bigint(20) DEFAULT NULL,
                                `owner_user_id` bigint(20) DEFAULT NULL,
                                `processed_number` bigint(20) DEFAULT NULL,
                                `stock_id` bigint(20) DEFAULT NULL,
                                `timestamp` bigint(20) DEFAULT NULL,
                                `currentAmount` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `market_order`
--

LOCK TABLES `market_order` WRITE;
/*!40000 ALTER TABLE `market_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `market_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
                           `amount` double DEFAULT NULL,
                           `commission_fee` double DEFAULT NULL,
                           `status` tinyint DEFAULT NULL,
                           `date_of_payment` bigint DEFAULT NULL,
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `sender_bankaccount_id` bigint DEFAULT NULL,
                           `channel` varchar(255) DEFAULT NULL,
                           `model` varchar(255) DEFAULT NULL,
                           `payment_code` varchar(255) DEFAULT NULL,
                           `payment_purpose` varchar(255) DEFAULT NULL,
                           `recipient_account_number` varchar(255) DEFAULT NULL,
                           `recipient_name` varchar(255) DEFAULT NULL,
                           `reference_number` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKjk674co7a4x2ie6wl4bpjjtp9` (`sender_bankaccount_id`),
                           CONSTRAINT `FKjk674co7a4x2ie6wl4bpjjtp9` FOREIGN KEY (`sender_bankaccount_id`) REFERENCES `bank_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
-- INSERT INTO `payment` VALUES (100,2,2,1714348800,1,100,'web','test','test','test','0987654321','test','test');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_recipient`
--

DROP TABLE IF EXISTS `payment_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_recipient` (
                                     `customer_id` bigint DEFAULT NULL,
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `first_name` varchar(255) DEFAULT NULL,
                                     `last_name` varchar(255) DEFAULT NULL,
                                     `recipient_account_number` varchar(255) DEFAULT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `FK50onfwsjlj9k1n40pjdg4l20a` (`customer_id`),
                                     CONSTRAINT `FK50onfwsjlj9k1n40pjdg4l20a` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_recipient`
--

LOCK TABLES `payment_recipient` WRITE;
/*!40000 ALTER TABLE `payment_recipient` DISABLE KEYS */;
-- INSERT INTO `payment_recipient` VALUES (101,2,'mika','aleksic','1234567777');
/*!40000 ALTER TABLE `payment_recipient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
                              `permission_id` bigint NOT NULL AUTO_INCREMENT,
                              `description` varchar(255) DEFAULT NULL,
                              `name` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
-- INSERT INTO `permission` VALUES (2,'can_manage_users','can_manage_users'),(3,'readUser','readUser'),(4,'addUser','addUser'),(5,'modifyUser','modifyUser'),(6,'deleteUser','deleteUser'),(7,'manageLoans','manageLoans'),(8,'manageLoanRequests','manageLoanRequests'),(9,'modifyCustomer','modifyCustomer'),(10,'manageOrderRequests','manageOrderRequests');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
                               `buy` double DEFAULT NULL,
                               `reserve_used` double DEFAULT NULL,
                               `reserved` double DEFAULT NULL,
                               `sell` double DEFAULT NULL,
                               `bank_account_id` bigint DEFAULT NULL,
                               `currency_id` bigint DEFAULT NULL,
                               `date_time` bigint DEFAULT NULL,
                               `employee_user_id` bigint DEFAULT NULL,
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `market_order_id` bigint DEFAULT NULL,
                               `description` varchar(255) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FKec44dj1u86xnsku7ld84pirje` (`bank_account_id`),
                               KEY `FKlcx7g8g7x4fyns9k6vesu3n9n` (`currency_id`),
                               KEY `FKibviys28drl3pswtdq2tymkq4` (`employee_user_id`),
                               KEY `FKoyyk0hp6i1ct6cg73m4ftwbd6` (`market_order_id`),
                               CONSTRAINT `FKec44dj1u86xnsku7ld84pirje` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`),
                               CONSTRAINT `FKibviys28drl3pswtdq2tymkq4` FOREIGN KEY (`employee_user_id`) REFERENCES `employee` (`user_id`),
                               CONSTRAINT `FKlcx7g8g7x4fyns9k6vesu3n9n` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
                               CONSTRAINT `FKoyyk0hp6i1ct6cg73m4ftwbd6` FOREIGN KEY (`market_order_id`) REFERENCES `market_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer`
--

DROP TABLE IF EXISTS `transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer` (
                            `amount` double DEFAULT NULL,
                            `commission` double DEFAULT NULL,
                            `converted_amount` double DEFAULT NULL,
                            `exchange_rate` double DEFAULT NULL,
                            `status` tinyint DEFAULT NULL,
                            `currency_from_id` bigint DEFAULT NULL,
                            `currency_to_id` bigint DEFAULT NULL,
                            `date_of_payment` bigint DEFAULT NULL,
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `recipient_bankaccount_id` bigint DEFAULT NULL,
                            `sender_bankaccount_id` bigint DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FKp6gfwwxhthg8yqecmun7dm39t` (`currency_from_id`),
                            KEY `FKnv9ogynj0kyvrag3x20ich804` (`currency_to_id`),
                            KEY `FK6yx3ielvsf9s8rqd7mivfaomt` (`recipient_bankaccount_id`),
                            KEY `FK39vrk1o3aexxp4fv2qg97di1l` (`sender_bankaccount_id`),
                            CONSTRAINT `FK39vrk1o3aexxp4fv2qg97di1l` FOREIGN KEY (`sender_bankaccount_id`) REFERENCES `bank_account` (`id`),
                            CONSTRAINT `FK6yx3ielvsf9s8rqd7mivfaomt` FOREIGN KEY (`recipient_bankaccount_id`) REFERENCES `bank_account` (`id`),
                            CONSTRAINT `FKnv9ogynj0kyvrag3x20ich804` FOREIGN KEY (`currency_to_id`) REFERENCES `currency` (`id`),
                            CONSTRAINT `FKp6gfwwxhthg8yqecmun7dm39t` FOREIGN KEY (`currency_from_id`) REFERENCES `currency` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer`
--

LOCK TABLES `transfer` WRITE;
/*!40000 ALTER TABLE `transfer` DISABLE KEYS */;
-- INSERT INTO `transfer` VALUES (1000,10,1000,1,0,10000,10001,1714348800,1,101,100);
/*!40000 ALTER TABLE `transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permissions_customer`
--

DROP TABLE IF EXISTS `user_permissions_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permissions_customer` (
                                             `permission_id` bigint NOT NULL,
                                             `user_id` bigint NOT NULL,
                                             PRIMARY KEY (`permission_id`,`user_id`),
                                             KEY `FK6xxlag2b3hjgv0qxrword9560` (`user_id`),
                                             CONSTRAINT `FK6xxlag2b3hjgv0qxrword9560` FOREIGN KEY (`user_id`) REFERENCES `customer` (`user_id`),
                                             CONSTRAINT `FK7ocumjmox0nk1eo2vsu771c07` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permissions_customer`
--

LOCK TABLES `user_permissions_customer` WRITE;
/*!40000 ALTER TABLE `user_permissions_customer` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_permissions_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_permissions_employee`
--

DROP TABLE IF EXISTS `user_permissions_employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_permissions_employee` (
                                             `permission_id` bigint NOT NULL,
                                             `user_id` bigint NOT NULL,
                                             PRIMARY KEY (`permission_id`,`user_id`),
                                             KEY `FKjv5retfeiq32vnojwuqqqt9ol` (`user_id`),
                                             CONSTRAINT `FKjv5retfeiq32vnojwuqqqt9ol` FOREIGN KEY (`user_id`) REFERENCES `employee` (`user_id`),
                                             CONSTRAINT `FKrp0i2e3sql1jemh151ahjqwvb` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_permissions_employee`
--

LOCK TABLES `user_permissions_employee` WRITE;
/*!40000 ALTER TABLE `user_permissions_employee` DISABLE KEYS */;
-- INSERT INTO `user_permissions_employee` VALUES (3,2),(4,2),(5,2),(6,2),(7,2),(8,2),(9,2),(2,100),(3,100),(4,100),(5,100),(6,100),(7,100),(8,100),(9,100),(10,100);
/*!40000 ALTER TABLE `user_permissions_employee` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-29 20:35:43


INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (2, 'can_manage_users', 'can_manage_users');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (3, 'readUser', 'readUser');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (4, 'addUser', 'addUser');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (5, 'modifyUser', 'modifyUser');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (6, 'deleteUser', 'deleteUser');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (7, 'manageLoans', 'manageLoans');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (8, 'manageLoanRequests', 'manageLoanRequests');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (9, 'modifyCustomer', 'modifyCustomer');

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
    (10, 'manageOrderRequests', 'manageOrderRequests');


INSERT INTO `employee` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`, `position`) VALUES
    (100, b'1', 'admin@admin.com', 'admin', 'admin', 'admin', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '1234567890', 'admin');

INSERT INTO `customer` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (101, b'1', 'user@test.com', 'petar', '412325124', 'petrovic', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '1111111111');

INSERT INTO `customer` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (102, b'1', 'user123@test.com', 'mika', '215412512', 'mikic', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '22222324');

INSERT INTO `customer` (`user_id`, `active`, `company_id`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (103, b'1', 1, 'pravno_lice@test.com', 'petar1', '712325127', 'petrovic1', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '33333333');

INSERT INTO `customer` (`user_id`, `active`, `company_id`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (104, b'1', 2, 'pravno_lice_buyer@test.com', 'miroslav', '65656565', 'lazanski', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '63333663');

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 2);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 3);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 4);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 5);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 6);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 7);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 8);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 9);

INSERT INTO `user_permissions_employee` (`user_id`, `permission_id`) VALUES
    (100, 10);

INSERT INTO `company` (`id`, `company_name`, `fax_number`, `id_number`, `job_id`, `pib`, `registration_number`, `telephone_number`) VALUES
    (1, 'Banka1', 'test', 'test', 'test', 'test', 'test', 'test');

INSERT INTO `company` (`id`, `company_name`, `fax_number`, `id_number`, `job_id`, `pib`, `registration_number`, `telephone_number`) VALUES
    (2, 'Banka1_test2', 'test2', 'test2', 'test2', 'test2', 'test2', 'test2');

INSERT INTO `currency` (`active`, `id`, `country`, `currency_code`, `currency_desc`, `currency_name`, `currency_symbol`, `toRSD`, `fromRSD`) VALUES
    (b'1', 10000, 'TEST', 'TST', 'TEST', 'TEST', 'TST', 1.0, 1.0);

INSERT INTO `currency` (`active`, `id`, `country`, `currency_code`, `currency_desc`, `currency_name`, `currency_symbol`, `toRSD`, `fromRSD`) VALUES
    (b'1', 10001, 'TEST1', 'TSS', 'TEST1', 'TEST1', 'TST1', 1.0, 1.0);

INSERT INTO `currency` (`active`, `id`, `country`, `currency_code`, `currency_desc`, `currency_name`, `currency_symbol`, `toRSD`, `fromRSD`) VALUES
    (b'1', 10002, 'RSD', 'RSD', 'RSD', 'RSD', 'RSD', 1.0, 1.0);


INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10000, 101, 2710959528, 100, 'test', '1234567890', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959558, 10001, 101, 2710959528, 101, 'test1', '0987654321', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 1, 100, 1710959558, 10001, NULL, 2710959528, 102, 'test1', '7151517151', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 2, 100, 1710959558, 10001, NULL, 2710959528, 103, 'test1', '1515151717', 'subtest');

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10000, NULL, 1871769600, 9, 'Banks account', '131242095807818250', NULL);

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10001, NULL, 1871769600, 10, 'Banks account', '131242095807818251', NULL);

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10002, NULL, 1871769600, 11, 'Banks account', '131242095807818251', NULL);

INSERT INTO `loan` (account_number, currency, effective_interest_rate, installment_amount, loan_amount, loan_type, nominal_interest_rate, remaining_debt, repayment_period, agreement_date, id, maturity_date, next_installment_date)
    VALUES ('1234567890', 'TST', 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0, 0, 100, 0, 0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, currency_id, id, listing_id, ticker, public_total)
    VALUES (0, 0.0, 30.0, 100, NULL, 1001, 100003, 'testticker', 0.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, currency_id, id, listing_id, ticker, public_total)
    VALUES (2, 0.0, 36.0, 100, NULL, 1002, 100001, 'testticker', 5.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, currency_id, id, listing_id, ticker, public_total)
    VALUES (1, 0.0, 35.0, 102, NULL, 1003, 100002, 'testticker', 3.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, currency_id, id, listing_id, ticker, public_total)
    VALUES (2, 0.0, 35.0, 102, NULL, 1004, 100001, 'testticker', 0.0);
-- INSERT INTO `foreign_currency_accounts` (`id`, `account_maintenance`, `account_number`, `account_status`, `available_balance`, `balance`, `created_by_agent_id`, `creation_date`, `currency`, `default_currency`, `expiration_date`, `owner_id`, `subtype_of_account`, `type_of_account`) VALUES
--  (100, 100.0, '123456789', 'active', 1000.0, 1200.0, 100, 1710959528, 'CD1', 'CD1', 2710959528, 102, 'subtest', 'test');

-- COMMIT;

USE bank_marketservice;

SET time_zone = "+00:00";

CREATE TABLE `country` (
                           `id` bigint(20) NOT NULL,
                           `isocode` varchar(255) DEFAULT NULL,
                           `close_time` time(6) DEFAULT NULL,
                           `open_time` time(6) DEFAULT NULL,
                           `timezone_offset` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `currency`
--

CREATE TABLE `currency` (
                            `id` bigint(20) NOT NULL,
                            `currency_code` varchar(255) DEFAULT NULL,
                            `currency_name` varchar(255) DEFAULT NULL,
                            `currency_symbol` varchar(255) DEFAULT NULL,
                            `polity` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `example_model`
--

CREATE TABLE `example_model` (
                                 `example_id` bigint(20) NOT NULL,
                                 `value` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `exchange`
--

CREATE TABLE `exchange` (
                            `id` bigint(20) NOT NULL,
                            `currency` varchar(255) DEFAULT NULL,
                            `exchange_acronym` varchar(255) DEFAULT NULL,
                            `exchange_name` varchar(255) DEFAULT NULL,
                            `mic_code` varchar(255) DEFAULT NULL,
                            `country_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `holiday`
--

CREATE TABLE `holiday` (
                           `id` bigint(20) NOT NULL,
                           `date` date DEFAULT NULL,
                           `country_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inflations`
--

CREATE TABLE `inflations` (
                              `id` bigint(20) NOT NULL,
                              `inflation_rate` float DEFAULT NULL,
                              `year` int(11) DEFAULT NULL,
                              `currency_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_forex`
--

CREATE TABLE `listing_forex` (
                                 `listing_id` bigint(20) NOT NULL,
                                 `exchange` varchar(255) DEFAULT NULL,
                                 `high` double DEFAULT NULL,
                                 `last_refresh` int(11) DEFAULT NULL,
                                 `listing_type` varchar(255) DEFAULT NULL,
                                 `low` double DEFAULT NULL,
                                 `name` varchar(255) DEFAULT NULL,
                                 `price` double DEFAULT NULL,
                                 `price_change` double DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL,
                                 `volume` int(11) DEFAULT NULL,
                                 `base_currency` varchar(255) DEFAULT NULL,
                                 `quote_currency` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_future`
--

CREATE TABLE `listing_future` (
                                  `listing_id` bigint(20) NOT NULL,
                                  `exchange` varchar(255) DEFAULT NULL,
                                  `high` double DEFAULT NULL,
                                  `last_refresh` int(11) DEFAULT NULL,
                                  `listing_type` varchar(255) DEFAULT NULL,
                                  `low` double DEFAULT NULL,
                                  `name` varchar(255) DEFAULT NULL,
                                  `price` double DEFAULT NULL,
                                  `price_change` double DEFAULT NULL,
                                  `ticker` varchar(255) DEFAULT NULL,
                                  `volume` int(11) DEFAULT NULL,
                                  `contract_size` int(11) DEFAULT NULL,
                                  `contract_unit` varchar(255) DEFAULT NULL,
                                  `open_interest` int(11) DEFAULT NULL,
                                  `settlement_date` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_history`
--

CREATE TABLE `listing_history` (
                                   `listing_history_id` bigint(20) NOT NULL,
                                   `changed` double DEFAULT NULL,
                                   `date` bigint(20) NOT NULL,
                                   `high` double DEFAULT NULL,
                                   `low` double DEFAULT NULL,
                                   `price` double DEFAULT NULL,
                                   `ticker` varchar(255) DEFAULT NULL,
                                   `volume` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_stock`
--

CREATE TABLE `listing_stock` (
                                 `listing_id` bigint(20) NOT NULL,
                                 `exchange` varchar(255) DEFAULT NULL,
                                 `high` double DEFAULT NULL,
                                 `last_refresh` int(11) DEFAULT NULL,
                                 `listing_type` varchar(255) DEFAULT NULL,
                                 `low` double DEFAULT NULL,
                                 `name` varchar(255) DEFAULT NULL,
                                 `price` double DEFAULT NULL,
                                 `price_change` double DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL,
                                 `volume` int(11) DEFAULT NULL,
                                 `dividend_yield` double DEFAULT NULL,
                                 `outstanding_shares` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `options_model`
--

CREATE TABLE `options_model` (
                                 `id` bigint(20) NOT NULL,
                                 `currency` varchar(255) DEFAULT NULL,
                                 `expiration_date` bigint(20) DEFAULT NULL,
                                 `implied_volatility` double DEFAULT NULL,
                                 `open_interest` int(11) DEFAULT NULL,
                                 `option_type` varchar(255) DEFAULT NULL,
                                 `strike_price` double DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `country`
--
ALTER TABLE `country`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `currency`
--
ALTER TABLE `currency`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_7n17ygajjchsso2n0lyxrsyif` (`currency_code`);

--
-- Indexes for table `example_model`
--
ALTER TABLE `example_model`
    ADD PRIMARY KEY (`example_id`),
  ADD UNIQUE KEY `UK_jpr61htfa7ostvbbkxiqos5gq` (`value`);

--
-- Indexes for table `exchange`
--
ALTER TABLE `exchange`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKjfclu6m1y6tc1q2md77q1raqs` (`country_id`);

--
-- Indexes for table `holiday`
--
ALTER TABLE `holiday`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKm5yhb55jjkwtbox6ghsl6jx10` (`country_id`);

--
-- Indexes for table `inflations`
--
ALTER TABLE `inflations`
    ADD PRIMARY KEY (`id`),
  ADD KEY `FKkq6hejum61olie75euponp3as` (`currency_id`);

--
-- Indexes for table `listing_forex`
--
ALTER TABLE `listing_forex`
    ADD PRIMARY KEY (`listing_id`);

--
-- Indexes for table `listing_future`
--
ALTER TABLE `listing_future`
    ADD PRIMARY KEY (`listing_id`);

--
-- Indexes for table `listing_history`
--
ALTER TABLE `listing_history`
    ADD PRIMARY KEY (`listing_history_id`);

--
-- Indexes for table `listing_stock`
--
ALTER TABLE `listing_stock`
    ADD PRIMARY KEY (`listing_id`);

--
-- Indexes for table `options_model`
--
ALTER TABLE `options_model`
    ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `country`
--
ALTER TABLE `country`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `currency`
--
ALTER TABLE `currency`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `example_model`
--
ALTER TABLE `example_model`
    MODIFY `example_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `exchange`
--
ALTER TABLE `exchange`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `holiday`
--
ALTER TABLE `holiday`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inflations`
--
ALTER TABLE `inflations`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `listing_forex`
--
ALTER TABLE `listing_forex`
    MODIFY `listing_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `listing_future`
--
ALTER TABLE `listing_future`
    MODIFY `listing_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `listing_history`
--
ALTER TABLE `listing_history`
    MODIFY `listing_history_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `listing_stock`
--
ALTER TABLE `listing_stock`
    MODIFY `listing_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `options_model`
--
ALTER TABLE `options_model`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `exchange`
--
ALTER TABLE `exchange`
    ADD CONSTRAINT `FKjfclu6m1y6tc1q2md77q1raqs` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`);

--
-- Constraints for table `holiday`
--
ALTER TABLE `holiday`
    ADD CONSTRAINT `FKm5yhb55jjkwtbox6ghsl6jx10` FOREIGN KEY (`country_id`) REFERENCES `country` (`id`);

--
-- Constraints for table `inflations`
--
ALTER TABLE `inflations`
    ADD CONSTRAINT `FKkq6hejum61olie75euponp3as` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`);

-- DATA

INSERT INTO `country` (`id`, `isocode`, `close_time`, `open_time`, `timezone_offset`) VALUES
    (100000, 'TEST', '17:00:00.000000', '09:00:00.000000', 0);


INSERT INTO `currency` (`id`, `currency_code`, `currency_name`, `currency_symbol`, `polity`) VALUES
    (100000, 'CD1', 'test1', 'testsym1', 'testpolity1');

INSERT INTO `currency` (`id`, `currency_code`, `currency_name`, `currency_symbol`, `polity`) VALUES
    (100001, 'CD2', 'test2', 'testsym2', 'testpolity2');

INSERT INTO `inflations` (`id`, `inflation_rate`, `year`, `currency_id`) VALUES
        (100000, 3.57, 2024, 100000);

INSERT INTO `listing_forex` (`listing_id`, `exchange`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `base_currency`, `quote_currency`) VALUES
(100001, 'oanda', '0.85381', '1710929683', 'testforex', '0.85376', 'Oanda CD1/CD2', '0.8538', '0.8538', 'CD1/CD2', '0', 'CD1', 'CD2');

INSERT INTO `listing_future` (`listing_id`, `exchange`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `contract_size`, `contract_unit`, `open_interest`, `settlement_date`) VALUES
    (100002, 'test_exchange', '46.895', '1710929671', 'Future', '45.1708', 'testfuture', '46.49', '0.97', 'testticker', '3839039', '100', 'testunit', '0', '1713484800');

INSERT INTO `listing_stock` (`listing_id`, `exchange`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `dividend_yield`, `outstanding_shares`) VALUES
    (100003, 'test_exchange', '46.895', '1710929671', 'Stock', '45.1708', 'teststock', '46.49', '0.97', 'testticker', '3839039', '0', '295999000');


INSERT INTO `options_model` (`id`, `currency`, `expiration_date`, `implied_volatility`, `open_interest`, `option_type`, `strike_price`, `ticker`) VALUES
    (100000, 'CD1', '1713484800', '0.000010000000000000003', '0', 'CALL', '35', 'testticker');

INSERT INTO `listing_history` (`listing_history_id`, `changed`, `date`, `high`, `low`, `price`, `ticker`, `volume`) VALUES
    (100000, '1.240000000000002', '1710806400', '46.895', '45.1708', '46.49', 'testticker', '3839039');

 INSERT INTO `exchange` (`id`, `currency`, `exchange_acronym`, `exchange_name`, `mic_code`, `country_id`) VALUES
     (100000, 'CD1', 'test_acronym', 'test_exchange', 'test_code', 100000);

INSERT INTO `listing_stock` (`listing_id`, `exchange`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `dividend_yield`, `outstanding_shares`) VALUES
    (100000, 'test_exchange', '46.895', '1710929671', 'Stock', '45.1708', 'teststock', '46.49', '0.97', 'testticker', '3839039', '0', '295999000');

INSERT INTO `holiday` (`id`, `date`, `country_id`) VALUES
    (100000, '2020-03-01', 100000);
