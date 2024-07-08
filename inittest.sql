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

CREATE TABLE `bankotcstock` (
                                `amount` int(11) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bank_account`
--

CREATE TABLE `bank_account` (
                                `account_status` bit(1) DEFAULT NULL,
                                `account_type` tinyint(4) DEFAULT NULL,
                                `available_balance` double DEFAULT NULL,
                                `balance` double DEFAULT NULL,
                                `maintenance_cost` double DEFAULT NULL,
                                `company_id` bigint(20) DEFAULT NULL,
                                `created_by_agent_id` bigint(20) DEFAULT NULL,
                                `creation_date` bigint(20) DEFAULT NULL,
                                `currency_id` bigint(20) DEFAULT NULL,
                                `customer_id` bigint(20) DEFAULT NULL,
                                `expiration_date` bigint(20) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `account_name` varchar(255) DEFAULT NULL,
                                `account_number` varchar(255) DEFAULT NULL,
                                `subtype_of_account` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `capital`
--

CREATE TABLE `capital` (
                           `average_buying_price` double DEFAULT NULL,
                           `listing_type` tinyint(4) DEFAULT NULL,
                           `public_total` double DEFAULT NULL,
                           `reserved` double DEFAULT NULL,
                           `total` double DEFAULT NULL,
                           `bank_account_id` bigint(20) DEFAULT NULL,
                           `id` bigint(20) NOT NULL,
                           `last_modified` datetime(6) DEFAULT NULL,
                           `listing_id` bigint(20) DEFAULT NULL,
                           `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `card`
--

CREATE TABLE `card` (
                        `card_limit` int(11) DEFAULT NULL,
                        `cvv` varchar(3) DEFAULT NULL,
                        `is_activated` bit(1) DEFAULT NULL,
                        `bankaccount_id` bigint(20) DEFAULT NULL,
                        `creation_date` bigint(20) DEFAULT NULL,
                        `expiration_date` bigint(20) DEFAULT NULL,
                        `id` bigint(20) NOT NULL,
                        `card_number` varchar(16) DEFAULT NULL,
                        `account_number` varchar(255) DEFAULT NULL,
                        `card_name` varchar(255) DEFAULT NULL,
                        `card_type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `company`
--

CREATE TABLE `company` (
                           `id` bigint(20) NOT NULL,
                           `adress` varchar(255) DEFAULT NULL,
                           `company_name` varchar(255) DEFAULT NULL,
                           `fax_number` varchar(255) DEFAULT NULL,
                           `id_number` varchar(255) DEFAULT NULL,
                           `job_id` varchar(255) DEFAULT NULL,
                           `pib` varchar(255) DEFAULT NULL,
                           `registration_number` varchar(255) DEFAULT NULL,
                           `telephone_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `contract`
--

CREATE TABLE `contract` (
                            `amount` double DEFAULT NULL,
                            `bank_approval` bit(1) DEFAULT NULL,
                            `price` double DEFAULT NULL,
                            `seller_approval` bit(1) DEFAULT NULL,
                            `buyer_id` bigint(20) NOT NULL,
                            `creation_date` bigint(20) DEFAULT NULL,
                            `id` bigint(20) NOT NULL,
                            `listing_id` bigint(20) DEFAULT NULL,
                            `realization_date` bigint(20) DEFAULT NULL,
                            `seller_id` bigint(20) NOT NULL,
                            `comment` varchar(255) DEFAULT NULL,
                            `reference_number` varchar(255) DEFAULT NULL,
                            `ticker` varchar(255) DEFAULT NULL,
                            `listing_type` enum('STOCK','FUTURE','FOREX','OPTIONS') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `currency`
--

CREATE TABLE `currency` (
                            `active` bit(1) DEFAULT NULL,
                            `fromrsd` double DEFAULT NULL,
                            `torsd` double DEFAULT NULL,
                            `id` bigint(20) NOT NULL,
                            `country` varchar(255) DEFAULT NULL,
                            `currency_code` varchar(255) DEFAULT NULL,
                            `currency_desc` varchar(255) DEFAULT NULL,
                            `currency_name` varchar(255) DEFAULT NULL,
                            `currency_symbol` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
                            `active` bit(1) NOT NULL,
                            `company_id` bigint(20) DEFAULT NULL,
                            `date_of_birth` bigint(20) DEFAULT NULL,
                            `user_id` bigint(20) NOT NULL,
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
                            `single_use_code` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
                            `active` bit(1) NOT NULL,
                            `limit_now` double DEFAULT NULL,
                            `orderlimit` double DEFAULT NULL,
                            `require_approval` bit(1) DEFAULT NULL,
                            `company_id` bigint(20) DEFAULT NULL,
                            `user_id` bigint(20) NOT NULL,
                            `activation_token` varchar(255) DEFAULT NULL,
                            `email` varchar(255) DEFAULT NULL,
                            `first_name` varchar(255) DEFAULT NULL,
                            `jmbg` varchar(255) DEFAULT NULL,
                            `last_name` varchar(255) DEFAULT NULL,
                            `password` varchar(255) DEFAULT NULL,
                            `phone_number` varchar(255) DEFAULT NULL,
                            `position` varchar(255) DEFAULT NULL,
                            `reset_password_token` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hibernate_sequences`
--

CREATE TABLE `hibernate_sequences` (
                                       `next_val` bigint(20) DEFAULT NULL,
                                       `sequence_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `loan`
--

CREATE TABLE `loan` (
                        `effective_interest_rate` double DEFAULT NULL,
                        `installment_amount` double DEFAULT NULL,
                        `loan_amount` double DEFAULT NULL,
                        `loan_type` tinyint(4) DEFAULT NULL,
                        `nominal_interest_rate` double DEFAULT NULL,
                        `remaining_debt` double DEFAULT NULL,
                        `repayment_period` int(11) DEFAULT NULL,
                        `agreement_date` bigint(20) DEFAULT NULL,
                        `id` bigint(20) NOT NULL,
                        `maturity_date` bigint(20) DEFAULT NULL,
                        `next_installment_date` bigint(20) DEFAULT NULL,
                        `account_number` varchar(255) NOT NULL,
                        `currency` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `loan_request`
--

CREATE TABLE `loan_request` (
                                `loan_amount` double DEFAULT NULL,
                                `loan_type` tinyint(4) DEFAULT NULL,
                                `monthly_income_amount` double DEFAULT NULL,
                                `permanent_employee` bit(1) DEFAULT NULL,
                                `status` tinyint(4) DEFAULT NULL,
                                `employment_period` bigint(20) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `loan_term` bigint(20) DEFAULT NULL,
                                `account_number` varchar(255) DEFAULT NULL,
                                `branch_office` varchar(255) DEFAULT NULL,
                                `currency` varchar(255) DEFAULT NULL,
                                `loan_purpose` varchar(255) DEFAULT NULL,
                                `monthly_income_currency` varchar(255) DEFAULT NULL,
                                `phone_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `margin_account`
--

CREATE TABLE `margin_account` (
                                  `balance` double DEFAULT NULL,
                                  `loan_value` double DEFAULT NULL,
                                  `maintenance_margin` double DEFAULT NULL,
                                  `margin_call_level` int(11) NOT NULL,
                                  `bank_account_id` bigint(20) DEFAULT NULL,
                                  `currency_id` bigint(20) DEFAULT NULL,
                                  `id` bigint(20) NOT NULL,
                                  `listing_type` enum('STOCK','FUTURE','FOREX','OPTIONS') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `margin_transaction`
--

CREATE TABLE `margin_transaction` (
                                      `capital_amount` double DEFAULT NULL,
                                      `deposit` double DEFAULT NULL,
                                      `interest` double DEFAULT NULL,
                                      `loan_value` double DEFAULT NULL,
                                      `maintenance_margin` double DEFAULT NULL,
                                      `currency_id` bigint(20) DEFAULT NULL,
                                      `customer_user_id` bigint(20) DEFAULT NULL,
                                      `date_time` datetime(6) DEFAULT NULL,
                                      `id` bigint(20) NOT NULL,
                                      `order_id` bigint(20) DEFAULT NULL,
                                      `description` varchar(255) DEFAULT NULL,
                                      `transaction_type` enum('DEPOSIT','WITHDRAWAL') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `market_order`
--

CREATE TABLE `market_order` (
                                `all_or_none` bit(1) DEFAULT NULL,
                                `fee` double DEFAULT NULL,
                                `is_margin` bit(1) DEFAULT NULL,
                                `limit_value` double DEFAULT NULL,
                                `listing_type` tinyint(4) DEFAULT NULL,
                                `order_type` tinyint(4) DEFAULT NULL,
                                `price` double DEFAULT NULL,
                                `status` tinyint(4) DEFAULT NULL,
                                `stop_value` double DEFAULT NULL,
                                `version` int(11) DEFAULT NULL,
                                `approved_by_user_id` bigint(20) DEFAULT NULL,
                                `contract_size` bigint(20) DEFAULT NULL,
                                `current_amount` bigint(20) DEFAULT NULL,
                                `customer_user_id` bigint(20) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `listing_id` bigint(20) DEFAULT NULL,
                                `owner_user_id` bigint(20) DEFAULT NULL,
                                `processed_number` bigint(20) DEFAULT NULL,
                                `timestamp` bigint(20) DEFAULT NULL,
                                `updated_at` datetime(6) DEFAULT NULL,
                                `bank_account_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `my_offer`
--

CREATE TABLE `my_offer` (
                            `amount` int(11) DEFAULT NULL,
                            `offer_status` tinyint(4) DEFAULT NULL,
                            `price` double DEFAULT NULL,
                            `my_offer_id` bigint(20) NOT NULL,
                            `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `my_stock`
--

CREATE TABLE `my_stock` (
                            `amount` int(11) DEFAULT NULL,
                            `minimum_price` double DEFAULT NULL,
                            `private_amount` int(11) DEFAULT NULL,
                            `public_amount` int(11) DEFAULT NULL,
                            `company_id` bigint(20) DEFAULT NULL,
                            `my_stock_id` bigint(20) NOT NULL,
                            `user_id` bigint(20) DEFAULT NULL,
                            `currency_mark` varchar(255) DEFAULT NULL,
                            `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `offer`
--

CREATE TABLE `offer` (
                         `amount` int(11) DEFAULT NULL,
                         `price` double DEFAULT NULL,
                         `id_bank` bigint(20) DEFAULT NULL,
                         `offer_id` bigint(20) NOT NULL,
                         `ticker` varchar(255) DEFAULT NULL,
                         `offer_status` enum('PROCESSING','ACCEPTED','DECLINED','FINISHED_ACCEPTED','FINISHED_DECLINED') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

CREATE TABLE `payment` (
                           `amount` double DEFAULT NULL,
                           `commission_fee` double DEFAULT NULL,
                           `status` tinyint(4) DEFAULT NULL,
                           `date_of_payment` bigint(20) DEFAULT NULL,
                           `id` bigint(20) NOT NULL,
                           `sender_bankaccount_id` bigint(20) DEFAULT NULL,
                           `channel` varchar(255) DEFAULT NULL,
                           `model` varchar(255) DEFAULT NULL,
                           `payment_code` varchar(255) DEFAULT NULL,
                           `payment_purpose` varchar(255) DEFAULT NULL,
                           `recipient_account_number` varchar(255) DEFAULT NULL,
                           `recipient_name` varchar(255) DEFAULT NULL,
                           `reference_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_recipient`
--

CREATE TABLE `payment_recipient` (
                                     `customer_id` bigint(20) DEFAULT NULL,
                                     `id` bigint(20) NOT NULL,
                                     `first_name` varchar(255) DEFAULT NULL,
                                     `last_name` varchar(255) DEFAULT NULL,
                                     `recipient_account_number` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `permission`
--

CREATE TABLE `permission` (
                              `permission_id` bigint(20) NOT NULL,
                              `description` varchar(255) DEFAULT NULL,
                              `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stock_profit`
--

CREATE TABLE `stock_profit` (
                                `transaction_profit` double DEFAULT NULL,
                                `date_time` bigint(20) DEFAULT NULL,
                                `employee_user_id` bigint(20) DEFAULT NULL,
                                `id` bigint(20) NOT NULL,
                                `transaction_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
                               `buy` double DEFAULT NULL,
                               `reserve_used` double DEFAULT NULL,
                               `reserved` double DEFAULT NULL,
                               `sell` double DEFAULT NULL,
                               `bank_account_id` bigint(20) DEFAULT NULL,
                               `currency_id` bigint(20) DEFAULT NULL,
                               `date_time` bigint(20) DEFAULT NULL,
                               `employee_user_id` bigint(20) DEFAULT NULL,
                               `id` bigint(20) NOT NULL,
                               `market_order_id` bigint(20) DEFAULT NULL,
                               `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `transfer`
--

CREATE TABLE `transfer` (
                            `amount` double DEFAULT NULL,
                            `commission` double DEFAULT NULL,
                            `converted_amount` double DEFAULT NULL,
                            `exchange_rate` double DEFAULT NULL,
                            `status` tinyint(4) DEFAULT NULL,
                            `currency_from_id` bigint(20) DEFAULT NULL,
                            `currency_to_id` bigint(20) DEFAULT NULL,
                            `date_of_payment` bigint(20) DEFAULT NULL,
                            `id` bigint(20) NOT NULL,
                            `recipient_bankaccount_id` bigint(20) DEFAULT NULL,
                            `sender_bankaccount_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_permissions_customer`
--

CREATE TABLE `user_permissions_customer` (
                                             `permission_id` bigint(20) NOT NULL,
                                             `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_permissions_employee`
--

CREATE TABLE `user_permissions_employee` (
                                             `permission_id` bigint(20) NOT NULL,
                                             `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bankotcstock`
--
ALTER TABLE `bankotcstock`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `bank_account`
--
ALTER TABLE `bank_account`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKgm6p0ej8mhbakybacuneanald` (`company_id`),
    ADD KEY `FKb0rqy46m451rdbnhil6kkvlve` (`currency_id`),
    ADD KEY `FKj818ht4ban0c4uw4bmsbf3jme` (`customer_id`);

--
-- Indexes for table `capital`
--
ALTER TABLE `capital`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK1v2xrn4lbxfx4e5jjsjnlgjg1` (`bank_account_id`);

--
-- Indexes for table `card`
--
ALTER TABLE `card`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `UK_by1nk98m2hq5onhl68bo09sc1` (`card_number`),
    ADD KEY `FKq9omn7q09296yvaq6bxo2onr1` (`bankaccount_id`);

--
-- Indexes for table `company`
--
ALTER TABLE `company`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `contract`
--
ALTER TABLE `contract`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKr59ftb1p1a3j8iagy9asg0jyx` (`buyer_id`),
    ADD KEY `FKxyn6dm8rtua1xjw26nlygfqt` (`seller_id`);

--
-- Indexes for table `currency`
--
ALTER TABLE `currency`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
    ADD PRIMARY KEY (`user_id`),
    ADD UNIQUE KEY `UK_dwk6cx0afu8bs9o4t536v1j5v` (`email`),
    ADD UNIQUE KEY `UK_74noy3hdjlok2p75kut6hc7q9` (`jmbg`),
    ADD KEY `FKcc6lvs1hfb70cc5rjbyq0m8is` (`company_id`);

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
    ADD PRIMARY KEY (`user_id`),
    ADD UNIQUE KEY `UK_fopic1oh5oln2khj8eat6ino0` (`email`),
    ADD UNIQUE KEY `UK_cd34h7mb4lrr65book7qaa8sj` (`jmbg`),
    ADD KEY `FK5v50ed2bjh60n1gc7ifuxmgf4` (`company_id`);

--
-- Indexes for table `hibernate_sequences`
--
ALTER TABLE `hibernate_sequences`
    ADD PRIMARY KEY (`sequence_name`);

--
-- Indexes for table `loan`
--
ALTER TABLE `loan`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `loan_request`
--
ALTER TABLE `loan_request`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `margin_account`
--
ALTER TABLE `margin_account`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK72td69o3pi62qp1kgx79s53da` (`currency_id`),
    ADD KEY `FKgxlvua9mavo11sibxrd3ow9jp` (`bank_account_id`);

--
-- Indexes for table `margin_transaction`
--
ALTER TABLE `margin_transaction`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKln9jscvm1o17aucjhqy7i2ski` (`currency_id`),
    ADD KEY `FKmhb4pom4venny2wu6b2w0laeh` (`customer_user_id`),
    ADD KEY `FKqmgf1n6q4yesk90cofdx9lxxj` (`order_id`);

--
-- Indexes for table `market_order`
--
ALTER TABLE `market_order`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKsdr43k6rsa6xpwvqx08qnv4d6` (`approved_by_user_id`),
    ADD KEY `FKo2nupunxxk3ieyulld41yi0dh` (`customer_user_id`),
    ADD KEY `FKiov4aqi2dlbwuy2abajdyhng` (`owner_user_id`);

--
-- Indexes for table `my_offer`
--
ALTER TABLE `my_offer`
    ADD PRIMARY KEY (`my_offer_id`);

--
-- Indexes for table `my_stock`
--
ALTER TABLE `my_stock`
    ADD PRIMARY KEY (`my_stock_id`);

--
-- Indexes for table `offer`
--
ALTER TABLE `offer`
    ADD PRIMARY KEY (`offer_id`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKjk674co7a4x2ie6wl4bpjjtp9` (`sender_bankaccount_id`);

--
-- Indexes for table `payment_recipient`
--
ALTER TABLE `payment_recipient`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FK50onfwsjlj9k1n40pjdg4l20a` (`customer_id`);

--
-- Indexes for table `permission`
--
ALTER TABLE `permission`
    ADD PRIMARY KEY (`permission_id`);

--
-- Indexes for table `stock_profit`
--
ALTER TABLE `stock_profit`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `UK_206w8cksdwb7m56rqd3w8rnw6` (`transaction_id`),
    ADD KEY `FKmsu75gi491a1lfvr3rkbbbo0y` (`employee_user_id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKec44dj1u86xnsku7ld84pirje` (`bank_account_id`),
    ADD KEY `FKlcx7g8g7x4fyns9k6vesu3n9n` (`currency_id`),
    ADD KEY `FKibviys28drl3pswtdq2tymkq4` (`employee_user_id`),
    ADD KEY `FKoyyk0hp6i1ct6cg73m4ftwbd6` (`market_order_id`);

--
-- Indexes for table `transfer`
--
ALTER TABLE `transfer`
    ADD PRIMARY KEY (`id`),
    ADD KEY `FKp6gfwwxhthg8yqecmun7dm39t` (`currency_from_id`),
    ADD KEY `FKnv9ogynj0kyvrag3x20ich804` (`currency_to_id`),
    ADD KEY `FK6yx3ielvsf9s8rqd7mivfaomt` (`recipient_bankaccount_id`),
    ADD KEY `FK39vrk1o3aexxp4fv2qg97di1l` (`sender_bankaccount_id`);

--
-- Indexes for table `user_permissions_customer`
--
ALTER TABLE `user_permissions_customer`
    ADD PRIMARY KEY (`permission_id`,`user_id`),
    ADD KEY `FK6xxlag2b3hjgv0qxrword9560` (`user_id`);

--
-- Indexes for table `user_permissions_employee`
--
ALTER TABLE `user_permissions_employee`
    ADD PRIMARY KEY (`permission_id`,`user_id`),
    ADD KEY `FKjv5retfeiq32vnojwuqqqt9ol` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bankotcstock`
--
ALTER TABLE `bankotcstock`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bank_account`
--
ALTER TABLE `bank_account`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `capital`
--
ALTER TABLE `capital`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `card`
--
ALTER TABLE `card`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `company`
--
ALTER TABLE `company`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `contract`
--
ALTER TABLE `contract`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `currency`
--
ALTER TABLE `currency`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `loan`
--
ALTER TABLE `loan`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `loan_request`
--
ALTER TABLE `loan_request`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `margin_account`
--
ALTER TABLE `margin_account`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `margin_transaction`
--
ALTER TABLE `margin_transaction`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `market_order`
--
ALTER TABLE `market_order`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `my_offer`
--
ALTER TABLE `my_offer`
    MODIFY `my_offer_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `my_stock`
--
ALTER TABLE `my_stock`
    MODIFY `my_stock_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `offer`
--
ALTER TABLE `offer`
    MODIFY `offer_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment`
--
ALTER TABLE `payment`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_recipient`
--
ALTER TABLE `payment_recipient`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `permission`
--
ALTER TABLE `permission`
    MODIFY `permission_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stock_profit`
--
ALTER TABLE `stock_profit`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transfer`
--
ALTER TABLE `transfer`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bank_account`
--
ALTER TABLE `bank_account`
    ADD CONSTRAINT `FKb0rqy46m451rdbnhil6kkvlve` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    ADD CONSTRAINT `FKgm6p0ej8mhbakybacuneanald` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`),
    ADD CONSTRAINT `FKj818ht4ban0c4uw4bmsbf3jme` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`);

--
-- Constraints for table `capital`
--
ALTER TABLE `capital`
    ADD CONSTRAINT `FK1v2xrn4lbxfx4e5jjsjnlgjg1` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`);

--
-- Constraints for table `card`
--
ALTER TABLE `card`
    ADD CONSTRAINT `FKq9omn7q09296yvaq6bxo2onr1` FOREIGN KEY (`bankaccount_id`) REFERENCES `bank_account` (`id`);

--
-- Constraints for table `contract`
--
ALTER TABLE `contract`
    ADD CONSTRAINT `FKr59ftb1p1a3j8iagy9asg0jyx` FOREIGN KEY (`buyer_id`) REFERENCES `bank_account` (`id`),
    ADD CONSTRAINT `FKxyn6dm8rtua1xjw26nlygfqt` FOREIGN KEY (`seller_id`) REFERENCES `bank_account` (`id`);

--
-- Constraints for table `customer`
--
ALTER TABLE `customer`
    ADD CONSTRAINT `FKcc6lvs1hfb70cc5rjbyq0m8is` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`);

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
    ADD CONSTRAINT `FK5v50ed2bjh60n1gc7ifuxmgf4` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`);

--
-- Constraints for table `margin_account`
--
ALTER TABLE `margin_account`
    ADD CONSTRAINT `FK72td69o3pi62qp1kgx79s53da` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    ADD CONSTRAINT `FKgxlvua9mavo11sibxrd3ow9jp` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`);

--
-- Constraints for table `margin_transaction`
--
ALTER TABLE `margin_transaction`
    ADD CONSTRAINT `FKln9jscvm1o17aucjhqy7i2ski` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    ADD CONSTRAINT `FKmhb4pom4venny2wu6b2w0laeh` FOREIGN KEY (`customer_user_id`) REFERENCES `margin_account` (`id`),
    ADD CONSTRAINT `FKqmgf1n6q4yesk90cofdx9lxxj` FOREIGN KEY (`order_id`) REFERENCES `market_order` (`id`);

--
-- Constraints for table `market_order`
--
ALTER TABLE `market_order`
    ADD CONSTRAINT `FKiov4aqi2dlbwuy2abajdyhng` FOREIGN KEY (`owner_user_id`) REFERENCES `employee` (`user_id`),
    ADD CONSTRAINT `FKo2nupunxxk3ieyulld41yi0dh` FOREIGN KEY (`customer_user_id`) REFERENCES `customer` (`user_id`),
    ADD CONSTRAINT `FKsdr43k6rsa6xpwvqx08qnv4d6` FOREIGN KEY (`approved_by_user_id`) REFERENCES `employee` (`user_id`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
    ADD CONSTRAINT `FKjk674co7a4x2ie6wl4bpjjtp9` FOREIGN KEY (`sender_bankaccount_id`) REFERENCES `bank_account` (`id`);

--
-- Constraints for table `payment_recipient`
--
ALTER TABLE `payment_recipient`
    ADD CONSTRAINT `FK50onfwsjlj9k1n40pjdg4l20a` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`user_id`);

--
-- Constraints for table `stock_profit`
--
ALTER TABLE `stock_profit`
    ADD CONSTRAINT `FK4jo7urfx1tlll2110bgjxc3ls` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`id`),
    ADD CONSTRAINT `FKmsu75gi491a1lfvr3rkbbbo0y` FOREIGN KEY (`employee_user_id`) REFERENCES `employee` (`user_id`);

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
    ADD CONSTRAINT `FKec44dj1u86xnsku7ld84pirje` FOREIGN KEY (`bank_account_id`) REFERENCES `bank_account` (`id`),
    ADD CONSTRAINT `FKibviys28drl3pswtdq2tymkq4` FOREIGN KEY (`employee_user_id`) REFERENCES `employee` (`user_id`),
    ADD CONSTRAINT `FKlcx7g8g7x4fyns9k6vesu3n9n` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
    ADD CONSTRAINT `FKoyyk0hp6i1ct6cg73m4ftwbd6` FOREIGN KEY (`market_order_id`) REFERENCES `market_order` (`id`);

--
-- Constraints for table `transfer`
--
ALTER TABLE `transfer`
    ADD CONSTRAINT `FK39vrk1o3aexxp4fv2qg97di1l` FOREIGN KEY (`sender_bankaccount_id`) REFERENCES `bank_account` (`id`),
    ADD CONSTRAINT `FK6yx3ielvsf9s8rqd7mivfaomt` FOREIGN KEY (`recipient_bankaccount_id`) REFERENCES `bank_account` (`id`),
    ADD CONSTRAINT `FKnv9ogynj0kyvrag3x20ich804` FOREIGN KEY (`currency_to_id`) REFERENCES `currency` (`id`),
    ADD CONSTRAINT `FKp6gfwwxhthg8yqecmun7dm39t` FOREIGN KEY (`currency_from_id`) REFERENCES `currency` (`id`);

--
-- Constraints for table `user_permissions_customer`
--
ALTER TABLE `user_permissions_customer`
    ADD CONSTRAINT `FK6xxlag2b3hjgv0qxrword9560` FOREIGN KEY (`user_id`) REFERENCES `customer` (`user_id`),
    ADD CONSTRAINT `FK7ocumjmox0nk1eo2vsu771c07` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`);

--
-- Constraints for table `user_permissions_employee`
--
ALTER TABLE `user_permissions_employee`
    ADD CONSTRAINT `FKjv5retfeiq32vnojwuqqqt9ol` FOREIGN KEY (`user_id`) REFERENCES `employee` (`user_id`),
    ADD CONSTRAINT `FKrp0i2e3sql1jemh151ahjqwvb` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`);


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
    (105, b'1', 3, 'pravno_lice1@test.com', 'petar1', '7123251271', 'petrovic1', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '33333333');

INSERT INTO `customer` (`user_id`, `active`, `company_id`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (104, b'1', 2, 'pravno_lice_buyer@test.com', 'miroslav', '656565652', 'lazanski', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '63333663');

INSERT INTO `customer` (`user_id`, `active`, `company_id`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`) VALUES
    (106, b'1', 4, 'pravno_lice_buyer1@test.com', 'miroslav', '656565651', 'lazanski', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '63333663');

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

INSERT INTO `company` (`id`, `company_name`, `fax_number`, `id_number`, `job_id`, `pib`, `registration_number`, `telephone_number`) VALUES
    (3, 'Banka1_test3', 'test3', 'test3', 'test3', 'test3', 'test3', 'test3');

INSERT INTO `company` (`id`, `company_name`, `fax_number`, `id_number`, `job_id`, `pib`, `registration_number`, `telephone_number`) VALUES
    (4, 'Banka1_test4', 'test4', 'test4', 'test4', 'test4', 'test4', 'test4');

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
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959558, 10002, 101, 2710959528, 1010, 'test1', '09876543212', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 1, 100, 1710959558, 10001, NULL, 2710959528, 102, 'test1', '7151517151', 'subtest');

# INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
# VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 1, 100, 1710959558, 10002, NULL, 2710959528, 1002123, 'test1', '7151517151123', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 2, 100, 1710959558, 10001, NULL, 2710959528, 103, 'test1', '1515151717', 'subtest');

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10000, NULL, 1871769600, 9, 'Banks account', '131242095807818250', NULL);

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10001, NULL, 1871769600, 10, 'Banks account', '131242095807818251', NULL);

INSERT INTO `bank_account` (`account_status`, `account_type`, `available_balance`, `balance`, `maintenance_cost`, `company_id`, `created_by_agent_id`, `creation_date`, `currency_id`, `customer_id`, `expiration_date`, `id`, `account_name`, `account_number`, `subtype_of_account`)
VALUES (b'0', 2, 1000000, 1000000, NULL, 1, NULL, 1714003200, 10002, NULL, 1871769600, 11, 'Banks account', '131242095807818251', NULL);

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10002, 102, 2710959528, 1051, 'test', '12345678902', 'subtest');

# INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
# VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10002, 101, 2710959528, 1052, 'test', '12345678909', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10002, 104, 2710959528, 1053, 'test', '12345678908', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10002, 103, 2710959528, 1054, 'test', '12345678905', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 3, 100, 1710959558, 10002, NULL, 2710959528, 12345, 'test1', '7151517151124', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 12000.0, 14000.0, 100.0, 4, 100, 1710959558, 10002, NULL, 2710959528, 123456, 'test1', '7151517151125', 'subtest');

INSERT INTO `bank_account` (account_status, account_type, available_balance, balance, maintenance_cost, company_id, created_by_agent_id, creation_date, currency_id, customer_id, expiration_date, id, account_name, account_number, subtype_of_account)
VALUES (b'1', 1, 10000.0, 12000.0, 100.0, NULL, 100, 1710959528, 10002, 106, 2710959528, 1053123, 'test', '12345678908123', 'subtest');

INSERT INTO `loan` (account_number, currency, effective_interest_rate, installment_amount, loan_amount, loan_type, nominal_interest_rate, remaining_debt, repayment_period, agreement_date, id, maturity_date, next_installment_date)
    VALUES ('1234567890', 'TST', 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0, 0, 100, 0, 0);

# INSERT INTO `contract` (amount, bank_approval, price, seller_approval, buyer_id, creation_date, id, listing_id, realization_date, seller_id, comment, reference_number, ticker, listing_type) VALUES
#     (100.0, 1, 100.0, 1, 1051, 1710959528, 6, 100001, 1710959528, 102, 'test', '1234567890', 'testticker', 0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
    VALUES (0, 0.0, 30.0, 100, 1001, 100003, 'testticker', 0.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
    VALUES (2, 0.0, 36.0, 100, 1002, 100001, 'testticker', 5.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 1051, 1051, 100001, 'testticker', 3.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 1052, 1052, 100001, 'testticker', 3.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
    VALUES (3, 0.0, 10000.0, 1054, 1054, 100001, 'testticker', 10.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 1053, 1053, 100001, 'testticker', 3.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 102, 1055, 100001, 'testticker', 3.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 12345, 1056, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (0, 0.0, 10000.0, 12345, 10570, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (1, 0.0, 10000.0, 12345, 10571, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (2, 0.0, 10000.0, 12345, 10572, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (3, 0.0, 10000.0, 1010, 10561, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (0, 0.0, 10000.0, 1010, 105701, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (1, 0.0, 10000.0, 1010, 105711, 100001, 'testticker', 155.0);

INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
VALUES (2, 0.0, 10000.0, 1010, 105721, 100001, 'testticker', 155.0);

# INSERT INTO `capital` (listing_type, reserved, total, bank_account_id, id, listing_id, ticker, public_total)
# VALUES (4, 0.0, 10000.0, 12345, 10574, 100001, 'testticker', 3.0);

-- INSERT INTO `foreign_currency_accounts` (`id`, `account_maintenance`, `account_number`, `account_status`, `available_balance`, `balance`, `created_by_agent_id`, `creation_date`, `currency`, `default_currency`, `expiration_date`, `owner_id`, `subtype_of_account`, `type_of_account`) VALUES
--  (100, 100.0, '123456789', 'active', 1000.0, 1200.0, 100, 1710959528, 'CD1', 'CD1', 2710959528, 102, 'subtest', 'test');

-- COMMIT;

USE bank_marketservice;

CREATE TABLE `bankotcstock` (
                                `id` bigint(20) NOT NULL,
                                `amount` int(11) DEFAULT NULL,
                                `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `country`
--

CREATE TABLE `country` (
                           `close_time` time(6) DEFAULT NULL,
                           `open_time` time(6) DEFAULT NULL,
                           `timezone_offset` int(11) NOT NULL,
                           `id` bigint(20) NOT NULL,
                           `isocode` varchar(255) DEFAULT NULL
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
                            `country_id` bigint(20) DEFAULT NULL,
                            `id` bigint(20) NOT NULL,
                            `currency` varchar(255) DEFAULT NULL,
                            `exchange_acronym` varchar(255) DEFAULT NULL,
                            `exchange_name` varchar(255) DEFAULT NULL,
                            `mic_code` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `holiday`
--

CREATE TABLE `holiday` (
                           `date` date DEFAULT NULL,
                           `country_id` bigint(20) DEFAULT NULL,
                           `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `inflations`
--

CREATE TABLE `inflations` (
                              `inflation_rate` float DEFAULT NULL,
                              `year` int(11) DEFAULT NULL,
                              `currency_id` bigint(20) DEFAULT NULL,
                              `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_forex`
--

CREATE TABLE `listing_forex` (
                                 `high` double DEFAULT NULL,
                                 `last_refresh` int(11) DEFAULT NULL,
                                 `low` double DEFAULT NULL,
                                 `price` double DEFAULT NULL,
                                 `price_change` double DEFAULT NULL,
                                 `volume` int(11) DEFAULT NULL,
                                 `listing_id` bigint(20) NOT NULL,
                                 `base_currency` varchar(255) DEFAULT NULL,
                                 `exchange_name` varchar(255) DEFAULT NULL,
                                 `listing_type` varchar(255) DEFAULT NULL,
                                 `name` varchar(255) DEFAULT NULL,
                                 `quote_currency` varchar(255) DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_future`
--

CREATE TABLE `listing_future` (
                                  `contract_size` int(11) DEFAULT NULL,
                                  `high` double DEFAULT NULL,
                                  `last_refresh` int(11) DEFAULT NULL,
                                  `low` double DEFAULT NULL,
                                  `open_interest` int(11) DEFAULT NULL,
                                  `price` double DEFAULT NULL,
                                  `price_change` double DEFAULT NULL,
                                  `settlement_date` int(11) DEFAULT NULL,
                                  `volume` int(11) DEFAULT NULL,
                                  `listing_id` bigint(20) NOT NULL,
                                  `alternative_ticker` varchar(255) DEFAULT NULL,
                                  `contract_unit` varchar(255) DEFAULT NULL,
                                  `exchange_name` varchar(255) DEFAULT NULL,
                                  `listing_type` varchar(255) DEFAULT NULL,
                                  `name` varchar(255) DEFAULT NULL,
                                  `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_history`
--

CREATE TABLE `listing_history` (
                                   `changed` double DEFAULT NULL,
                                   `high` double DEFAULT NULL,
                                   `low` double DEFAULT NULL,
                                   `price` double DEFAULT NULL,
                                   `volume` int(11) DEFAULT NULL,
                                   `date` bigint(20) NOT NULL,
                                   `listing_history_id` bigint(20) NOT NULL,
                                   `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `listing_stock`
--

CREATE TABLE `listing_stock` (
                                 `dividend_yield` double DEFAULT NULL,
                                 `high` double DEFAULT NULL,
                                 `last_refresh` int(11) DEFAULT NULL,
                                 `low` double DEFAULT NULL,
                                 `outstanding_shares` int(11) DEFAULT NULL,
                                 `price` double DEFAULT NULL,
                                 `price_change` double DEFAULT NULL,
                                 `volume` int(11) DEFAULT NULL,
                                 `exchange_id` bigint(20) DEFAULT NULL,
                                 `listing_id` bigint(20) NOT NULL,
                                 `exchange_name` varchar(255) DEFAULT NULL,
                                 `listing_type` varchar(255) DEFAULT NULL,
                                 `name` varchar(255) DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `my_offer`
--

CREATE TABLE `my_offer` (
                            `my_offer_id` bigint(20) NOT NULL,
                            `amount` int(11) DEFAULT NULL,
                            `offer_status` tinyint(4) DEFAULT NULL,
                            `price` double DEFAULT NULL,
                            `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `my_stock`
--

CREATE TABLE `my_stock` (
                            `my_stock_id` bigint(20) NOT NULL,
                            `amount` int(11) DEFAULT NULL,
                            `company_id` bigint(20) DEFAULT NULL,
                            `currency_mark` varchar(255) DEFAULT NULL,
                            `minimum_price` double DEFAULT NULL,
                            `private_amount` int(11) DEFAULT NULL,
                            `public_amount` int(11) DEFAULT NULL,
                            `ticker` varchar(255) DEFAULT NULL,
                            `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `offer`
--

CREATE TABLE `offer` (
                         `offer_id` bigint(20) NOT NULL,
                         `amount` int(11) DEFAULT NULL,
                         `id_bank` bigint(20) DEFAULT NULL,
                         `offer_status` enum('PROCESSING','ACCEPTED','DECLINED','FINISHED_ACCEPTED','FINISHED_DECLINED') DEFAULT NULL,
                         `price` double DEFAULT NULL,
                         `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `options_model`
--

CREATE TABLE `options_model` (
                                 `high` double DEFAULT NULL,
                                 `implied_volatility` double DEFAULT NULL,
                                 `last_refresh` int(11) DEFAULT NULL,
                                 `low` double DEFAULT NULL,
                                 `open_interest` int(11) DEFAULT NULL,
                                 `price` double DEFAULT NULL,
                                 `price_change` double DEFAULT NULL,
                                 `strike_price` double DEFAULT NULL,
                                 `volume` int(11) DEFAULT NULL,
                                 `expiration_date` bigint(20) DEFAULT NULL,
                                 `listing_id` bigint(20) NOT NULL,
                                 `currency` varchar(255) DEFAULT NULL,
                                 `exchange_name` varchar(255) DEFAULT NULL,
                                 `listing_type` varchar(255) DEFAULT NULL,
                                 `name` varchar(255) DEFAULT NULL,
                                 `option_type` varchar(255) DEFAULT NULL,
                                 `ticker` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `bankotcstock`
--
ALTER TABLE `bankotcstock`
    ADD PRIMARY KEY (`id`);

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
    ADD PRIMARY KEY (`listing_id`),
    ADD KEY `FKbp64aqm4q9t14047ydx0l1gjm` (`exchange_id`);

--
-- Indexes for table `my_offer`
--
ALTER TABLE `my_offer`
    ADD PRIMARY KEY (`my_offer_id`);

--
-- Indexes for table `my_stock`
--
ALTER TABLE `my_stock`
    ADD PRIMARY KEY (`my_stock_id`);

--
-- Indexes for table `offer`
--
ALTER TABLE `offer`
    ADD PRIMARY KEY (`offer_id`);

--
-- Indexes for table `options_model`
--
ALTER TABLE `options_model`
    ADD PRIMARY KEY (`listing_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `bankotcstock`
--
ALTER TABLE `bankotcstock`
    MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

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
-- AUTO_INCREMENT for table `my_offer`
--
ALTER TABLE `my_offer`
    MODIFY `my_offer_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `my_stock`
--
ALTER TABLE `my_stock`
    MODIFY `my_stock_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `offer`
--
ALTER TABLE `offer`
    MODIFY `offer_id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `options_model`
--
ALTER TABLE `options_model`
    MODIFY `listing_id` bigint(20) NOT NULL AUTO_INCREMENT;

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

--
-- Constraints for table `listing_stock`
--
ALTER TABLE `listing_stock`
    ADD CONSTRAINT `FKbp64aqm4q9t14047ydx0l1gjm` FOREIGN KEY (`exchange_id`) REFERENCES `exchange` (`id`);

-- DATA

INSERT INTO `country` (`id`, `isocode`, `close_time`, `open_time`, `timezone_offset`) VALUES
    (100000, 'TEST', '17:00:00.000000', '09:00:00.000000', 0);


INSERT INTO `currency` (`id`, `currency_code`, `currency_name`, `currency_symbol`, `polity`) VALUES
    (100000, 'CD1', 'test1', 'testsym1', 'testpolity1');

INSERT INTO `currency` (`id`, `currency_code`, `currency_name`, `currency_symbol`, `polity`) VALUES
    (100001, 'CD2', 'test2', 'testsym2', 'testpolity2');

INSERT INTO `inflations` (`id`, `inflation_rate`, `year`, `currency_id`) VALUES
        (100000, 3.57, 2024, 100000);

INSERT INTO `listing_forex` (`listing_id`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `base_currency`, `quote_currency`) VALUES
(100001, '0.85381', '1710929683', 'testforex', '0.85376', 'Oanda CD1/CD2', '0.8538', '0.8538', 'CD1/CD2', '0', 'CD1', 'CD2');

INSERT INTO `listing_future` (`listing_id`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `contract_size`, `contract_unit`, `open_interest`, `settlement_date`) VALUES
    (100002, '46.895', '1710929671', 'Future', '45.1708', 'testfuture', '46.49', '0.97', 'testticker', '3839039', '100', 'testunit', '0', '1713484800');

INSERT INTO `listing_stock` (`listing_id`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `dividend_yield`, `outstanding_shares`) VALUES
    (100003, '46.895', '1710929671', 'Stock', '45.1708', 'teststock', '46.49', '0.97', 'testticker', '3839039', '0', '295999000');


INSERT INTO `options_model` (`currency`, `expiration_date`, `implied_volatility`, `open_interest`, `option_type`, `strike_price`, `ticker`, `listing_id`) VALUES
    ('CD1', '1713484800', '0.000010000000000000003', '0', 'CALL', '35', 'testticker', 100001);

INSERT INTO `listing_history` (`listing_history_id`, `changed`, `date`, `high`, `low`, `price`, `ticker`, `volume`) VALUES
    (100000, '1.240000000000002', '1710806400', '46.895', '45.1708', '46.49', 'testticker', '3839039');

 INSERT INTO `exchange` (`id`, `currency`, `exchange_acronym`, `exchange_name`, `mic_code`, `country_id`) VALUES
     (100000, 'CD1', 'test_acronym', 'test_exchange', 'test_code', 100000);

INSERT INTO `listing_stock` (`listing_id`, `high`, `last_refresh`, `listing_type`, `low`, `name`, `price`, `price_change`, `ticker`, `volume`, `dividend_yield`, `outstanding_shares`) VALUES
    (100000, '46.895', '1710929671', 'Stock', '45.1708', 'teststock', '46.49', '0.97', 'testticker', '3839039', '0', '295999000');

INSERT INTO `holiday` (`id`, `date`, `country_id`) VALUES
    (100000, '2020-03-01', 100000);
