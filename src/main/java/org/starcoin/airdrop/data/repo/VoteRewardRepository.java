package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.VoteReward;

import java.util.List;

public interface VoteRewardRepository extends JpaRepository<VoteReward, String> {

    VoteReward findFirstByProposalIdAndVoterOrderByVoteTimestampDesc(Long proposalId, String voter);

    List<VoteReward> findByProposalIdAndVoterOrderByVoteTimestamp(Long proposalId, String voter);

}
