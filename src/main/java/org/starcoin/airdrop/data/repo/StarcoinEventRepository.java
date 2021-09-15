package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.StarcoinEvent;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;

import java.util.List;

public interface StarcoinEventRepository extends JpaRepository<StarcoinEvent, String> {

    List<StarcoinVoteChangedEvent> findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(Long proposalId);

}
