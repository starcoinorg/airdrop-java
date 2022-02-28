package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.airdrop.data.model.VoteRewardProcess;

import java.math.BigInteger;
import java.util.List;

public interface VoteRewardProcessRepository extends JpaRepository<VoteRewardProcess, Long> {

    List<VoteRewardProcess> findByStatusEquals(String status);

    List<VoteRewardProcess> findByStatusIn(String[] statuses);

    @Query(value = "SELECT \n" +
            "    p.proposal_id\n" +
            "FROM\n" +
            "    vote_reward_process p\n" +
            "WHERE\n" +
            "    p.on_chain_disabled = false\n" +
            "ORDER BY p.proposal_id DESC\n" +
            "LIMIT 1;", nativeQuery = true)
    BigInteger getMaxOnChainProposalId();

    VoteRewardProcess findFirstByProposalIdAndOnChainDisabled(Long proposalId, boolean onChainDisabled);

}
