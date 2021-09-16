package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.starcoin.airdrop.data.model.VoteReward;

import javax.transaction.Transactional;
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
            "GROUP BY v.voter HAVING reward_amount > 0", nativeQuery = true)
    List<Map<String, Object>> sumRewardAmountGroupByVoter(Long proposalId);

    @Query(value = "SELECT \n" +
            "    SUM(r.reward_amount) AS total_reward_amount\n" +
            "FROM\n" +
            "    vote_reward r\n" +
            "WHERE\n" +
            "    r.proposal_id = :proposalId", nativeQuery = true)
    BigInteger sumTotalRewardAmountByProposalId(Long proposalId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE vote_reward \n" +
            "SET \n" +
            "    deactived = TRUE, reward_amount = 0 \n" +
            "WHERE \n" +
            "    proposal_id = :proposalId", nativeQuery = true)
    void deactiveVoteRewardsByProposalId(@Param("proposalId") Long proposalId);
}
