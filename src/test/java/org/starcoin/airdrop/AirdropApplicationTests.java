package org.starcoin.airdrop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.airdrop.data.model.AirdropProject;
import org.starcoin.airdrop.data.model.AirdropRecord;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.model.VoteReward;
import org.starcoin.airdrop.data.repo.AirdropProjectRepository;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;
import org.starcoin.airdrop.service.ElasticSearchService;
import org.starcoin.airdrop.service.StarcoinVoteChangedEventService;
import org.starcoin.airdrop.service.VoteRewardService;

import java.util.List;

@SpringBootTest
class AirdropApplicationTests {

    @Autowired
    AirdropProjectRepository airdropProjectRepository;

    @Autowired
    AirdropRecordRepository airdropRecordRepository;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    StarcoinVoteChangedEventService starcoinVoteChangedEventService;

    @Autowired
    StarcoinEventRepository starcoinEventRepository;

    @Autowired
    VoteRewardRepository voteRewardRepository;

    @Autowired
    VoteRewardService voteRewardService;

    @Test
    void contextLoads() {
        long proposalId = 0L;
        List<StarcoinVoteChangedEvent> events = starcoinEventRepository.findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(proposalId);
        voteRewardService.addOrUpdateVoteRewards(proposalId, events);
//        System.out.println(events);
//        VoteReward voteReward = voteRewardRepository.findFirstByProposalIdAndVoterOrderByVoteTimestampDesc(0L, "");
//        System.out.println(voteReward);
//        List<VoteReward> voteRewards = voteRewardRepository.findByProposalIdAndVoterOrderByVoteTimestamp(0L, "");
//        System.out.println(voteRewards);

        if (true) return;
        List<AirdropProject> airdropProjects = airdropProjectRepository.findAll();
        System.out.println(airdropProjects.size());

        List<AirdropRecord> airdropRecords = airdropRecordRepository.findAll();
        System.out.println(airdropRecords.size());

//		try {
//			List<ElasticSearchService.TransactionVoteChangedEvent> list = elasticSearchService
//					.findTransactionEventsByProposalIdAndProposer(0L,
//							"0xb2aa52f94db4516c5beecef363af850a",
//							0, Long.MAX_VALUE);
//			System.out.println(list.size());
//		} catch (IOException exception) {
//			exception.printStackTrace();
//		} catch (DeserializationError deserializationError) {
//			deserializationError.printStackTrace();
//		}

        starcoinVoteChangedEventService.findESEventsAndSave(0L,
                "0xb2aa52f94db4516c5beecef363af850a",
                0, Long.MAX_VALUE);

    }

}
