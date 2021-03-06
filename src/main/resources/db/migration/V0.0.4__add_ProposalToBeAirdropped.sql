CREATE TABLE `proposal_to_be_airdropped` (
  `proposal_id` bigint(20) NOT NULL,
  `against_votes` bigint(20) DEFAULT NULL,
  `created_at` bigint(20) NOT NULL,
  `created_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deactived` bit(1) NOT NULL,
  `description` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description_en` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_time` bigint(20) DEFAULT NULL,
  `for_votes` bigint(20) DEFAULT NULL,
  `link` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `network` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `on_chain_end_time` bigint(20) DEFAULT NULL,
  `on_chain_start_time` bigint(20) DEFAULT NULL,
  `quorum_votes` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title_en` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type_args1` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` bigint(20) NOT NULL,
  `updated_by` varchar(70) COLLATE utf8mb4_unicode_ci NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`proposal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
