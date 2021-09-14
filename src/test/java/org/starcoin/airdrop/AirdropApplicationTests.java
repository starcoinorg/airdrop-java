package org.starcoin.airdrop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.airdrop.data.model.AirdropProject;
import org.starcoin.airdrop.data.model.AirdropRecord;
import org.starcoin.airdrop.data.repo.AirdropProjectRepository;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;

import java.util.List;

@SpringBootTest
class AirdropApplicationTests {

	@Autowired
	AirdropProjectRepository airdropProjectRepository;

	@Autowired
	AirdropRecordRepository airdropRecordRepository;

	@Test
	void contextLoads() {
		List<AirdropProject> airdropProjects = airdropProjectRepository.findAll();
		System.out.println(airdropProjects.size());

		List<AirdropRecord> airdropRecords = airdropRecordRepository.findAll();
		System.out.println(airdropRecords.size());
	}

}
