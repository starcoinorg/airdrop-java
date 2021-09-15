package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.VoteRewardProcess;

import java.util.List;

public interface VoteRewardProcessRepository extends JpaRepository<VoteRewardProcess, Long> {

    List<VoteRewardProcess> findByStatusEquals(String status);

    List<VoteRewardProcess> findByStatusIn(String[] statuses);

}
