package org.starcoin.airdrop;

import com.novi.serde.DeserializationError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.airdrop.data.model.AirdropProject;
import org.starcoin.airdrop.data.model.AirdropRecord;
import org.starcoin.airdrop.data.repo.AirdropProjectRepository;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;
import org.starcoin.airdrop.service.ElasticSearchService;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class AirdropApplicationTests {

	@Autowired
	AirdropProjectRepository airdropProjectRepository;

	@Autowired
	AirdropRecordRepository airdropRecordRepository;

	@Autowired
	ElasticSearchService elasticSearchService;

	@Test
	void contextLoads() {
		List<AirdropProject> airdropProjects = airdropProjectRepository.findAll();
		System.out.println(airdropProjects.size());

		List<AirdropRecord> airdropRecords = airdropRecordRepository.findAll();
		System.out.println(airdropRecords.size());

		try {
			List<ElasticSearchService.TransactionVoteChangedEvent> list = elasticSearchService.getTransactionEventsByProposalIdAndProposer(0L, "0xb2aa52f94db4516c5beecef363af850a");
			System.out.println(list.size());
		} catch (IOException exception) {
			exception.printStackTrace();
		} catch (DeserializationError deserializationError) {
			deserializationError.printStackTrace();
		}
	}

}
