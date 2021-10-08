ALTER TABLE `vote_reward_process`
add COLUMN `proposal_process_seq_number` bigint(20);

UPDATE `vote_reward_process`
SET
    proposal_process_seq_number = NOW() + process_id
WHERE
    proposal_process_seq_number IS NULL
        AND process_id != - 11111111;

alter table  `vote_reward_process`
ADD UNIQUE INDEX `UniqueProposalAndSeqNum` (`proposal_id`, `proposal_process_seq_number`);
