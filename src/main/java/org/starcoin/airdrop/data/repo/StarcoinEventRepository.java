package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.StarcoinEvent;

public interface StarcoinEventRepository extends JpaRepository<StarcoinEvent, String> {

}
