package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.airdrop.data.model.VoteReward;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface VoteRewardRepository extends JpaRepository<VoteReward, String> {

    VoteReward findFirstByProposalIdAndVoterOrderByVoteTimestampDesc(Long proposalId, String voter);

    List<VoteReward> findByProposalIdAndVoterOrderByVoteTimestamp(Long proposalId, String voter);

    List<VoteReward> findByProposalIdAndVoteAddedAmountLessThanOrderByVoteTimestamp(Long proposalId, BigInteger voteAddedAmountLessThan);

    List<VoteReward> findByProposalIdAndDeactivedIsFalse(Long proposalId);

    @Query(value = "SELECT \n" +
            "    v.voter, SUM(v.reward_amount) AS reward_amount\n" +
            "FROM\n" +
            "    vote_reward v WHERE proposal_id = :proposalId\n" +
            "GROUP BY v.voter;", nativeQuery = true)
    List<Map<String, Object>> sumRewardAmountGroupByVoter(Long proposalId);
}
