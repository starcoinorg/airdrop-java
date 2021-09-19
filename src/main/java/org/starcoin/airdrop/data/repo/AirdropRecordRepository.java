package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.AirdropRecord;


public interface AirdropRecordRepository extends JpaRepository<AirdropRecord, Long> {

}
