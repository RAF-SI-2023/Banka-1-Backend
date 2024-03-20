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

SET time_zone = "+00:00";

-- INSERT INTO A VALUES B;

CREATE TABLE `permission` (
                              `permission_id` bigint(20) NOT NULL,
                              `description` varchar(255) DEFAULT NULL,
                              `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `permission`
--

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
                        `user_id` bigint(20) NOT NULL,
                        `active` bit(1) DEFAULT NULL,
                        `email` varchar(255) DEFAULT NULL,
                        `first_name` varchar(255) DEFAULT NULL,
                        `jmbg` varchar(255) DEFAULT NULL,
                        `last_name` varchar(255) DEFAULT NULL,
                        `password` varchar(255) DEFAULT NULL,
                        `phone_number` varchar(255) DEFAULT NULL,
                        `position` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

-- --------------------------------------------------------

--
-- Table structure for table `user_permissions`
--

CREATE TABLE `user_permissions` (
                                    `user_id` bigint(20) NOT NULL,
                                    `permission_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_permissions`
--


--
-- Indexes for dumped tables
--

--
-- Indexes for table `permission`
--
ALTER TABLE `permission`
    ADD PRIMARY KEY (`permission_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  ADD UNIQUE KEY `UK_foa7nm38hlyhwqgbw0ujd3tt4` (`jmbg`);

--
-- Indexes for table `user_permissions`
--
ALTER TABLE `user_permissions`
    ADD PRIMARY KEY (`user_id`,`permission_id`),
  ADD KEY `FKmyy1imx646s9c8usrmsfu9f51` (`permission_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `permission`
--
ALTER TABLE `permission`
    MODIFY `permission_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
    MODIFY `user_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_permissions`
--
ALTER TABLE `user_permissions`
    ADD CONSTRAINT `FK79uqaq5t8qjak65ldagkoo7yr` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `FKmyy1imx646s9c8usrmsfu9f51` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`);

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

INSERT INTO `user` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`, `position`) VALUES
    (100, b'1', 'admin@admin.com', 'admin', 'admin', 'admin', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '1234567890', 'admin');

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 2);

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 3);

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 4);

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 5);

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 6);

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
(NULL, 'oanda', '0.85381', '1710929683', 'testforex', '0.85376', 'Oanda CD1/CD2', '0.8538', '0.8538', 'CD1/CD2', '0', 'CD1', 'CD2');


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