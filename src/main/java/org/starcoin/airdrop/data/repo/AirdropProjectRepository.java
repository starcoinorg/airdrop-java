package org.starcoin.airdrop.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.starcoin.airdrop.data.model.AirdropProject;

import java.util.List;

public interface AirdropProjectRepository extends JpaRepository<AirdropProject, Long> {

    List<AirdropProject> findByName(String name);

}
