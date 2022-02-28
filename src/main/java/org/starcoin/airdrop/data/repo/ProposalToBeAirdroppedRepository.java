package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.starcoin.airdrop.data.model.ProposalToBeAirdropped;

import java.util.List;

public interface ProposalToBeAirdroppedRepository extends JpaRepository<ProposalToBeAirdropped, Long> {

    List<ProposalToBeAirdropped> findByStatusEquals(String status);

    List<ProposalToBeAirdropped> findByStatusIn(String[] statuses);

    @Query(value = "SELECT \n" +
            "    t.*\n" +
            "FROM\n" +
            "    proposal_to_be_airdropped t\n" +
            "        LEFT JOIN\n" +
            "    vote_reward_process p ON t.proposal_id = p.proposal_id\n" +
            "        AND p.on_chain_disabled = FALSE\n" +
            "WHERE\n" +
            "    p.proposal_id IS NULL;", nativeQuery = true)
    List<ProposalToBeAirdropped> findProposalsNotAirdropped();
}
