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