-- MySQL dump 10.13  Distrib 8.0.25, for macos11 (x86_64)
--
-- Host: 127.0.0.1    Database: airdrop
-- ------------------------------------------------------
-- Server version	5.7.34

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
-- Table structure for table `starcoin_event`
--

-- DROP TABLE IF EXISTS `starcoin_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `starcoin_event` (
  `event_type` varchar(31) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_id` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `block_hash` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `block_number` decimal(50,0) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `data` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `event_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_sequence_number` decimal(50,0) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_hash` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_index` decimal(50,0) NOT NULL,
  `type_tag` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `is_agree_vote` bit(1) DEFAULT NULL,
  `proposal_id` bigint(20) DEFAULT NULL,
  `proposer` varchar(34) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vote_amount` decimal(50,0) DEFAULT NULL,
  `vote_timestamp` bigint(20) DEFAULT NULL,
  `voter` varchar(34) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  UNIQUE KEY `UniqueStarcoinEvent` (`block_hash`,`transaction_index`,`event_key`,`event_sequence_number`),
  KEY `StcEvnStatusCreatedAt` (`status`,`created_at`),
  KEY `StcEvnStatusUpdatedAt` (`status`,`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vote_reward`
--

-- DROP TABLE IF EXISTS `vote_reward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vote_reward` (
  `event_id` varchar(66) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proposal_id` bigint(20) DEFAULT NULL,
  `reward_amount` decimal(50,0) DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `vote_amount` decimal(50,0) DEFAULT NULL,
  `vote_timestamp` bigint(20) DEFAULT NULL,
  `voter` varchar(34) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vote_added_amount` decimal(50,0) DEFAULT NULL,
  `reward_vote_amount` decimal(50,0) DEFAULT NULL,
  `deactived` bit(1) NOT NULL,
  PRIMARY KEY (`event_id`),
  KEY `IdxPrpIdVoterTime` (`proposal_id`,`voter`,`vote_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `vote_reward_process`
--

-- DROP TABLE IF EXISTS `vote_reward_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vote_reward_process` (
  `process_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proposal_id` bigint(20) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `vote_end_timestamp` bigint(20) DEFAULT NULL,
  `vote_start_timestamp` bigint(20) DEFAULT NULL,
  `proposer` varchar(34) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`process_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-16  0:41:32
