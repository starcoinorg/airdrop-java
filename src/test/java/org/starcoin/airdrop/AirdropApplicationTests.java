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
import org.starcoin.airdrop.service.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    AirdropProjectService airdropProjectService;

    @Autowired
    MerkleTreeService merkleTreeService;

    @Test
    void contextLoads() {
        merkleTreeService.createAirdropMerkleTreeAndUpdateOnChain(8L, 0L);
        if (true) return;

        Long prjId = airdropProjectService.addProject("Test prj. " + System.currentTimeMillis(), new Date(), new Date());
        System.out.println(prjId);
        if (true) return;

        List<Map<String, Object>> sumRewards = voteRewardRepository.sumRewardAmountGroupByVoter(0L);
        for (Map<String, Object> m : sumRewards) {
            System.out.println(m.get("voter") + "\t" + m.get("reward_amount"));
        }
        if (true) return;

        ZonedDateTime voteEndTime = ZonedDateTime.of(2021, 6, 16, 0, 0, 0, 0, ZoneId.of("Asia/Shanghai"));
        voteRewardService.calculateRewords(0L, voteEndTime.toInstant().toEpochMilli());
        if (true) return;

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tomorrow = now.plusYears(1);
        BigInteger reward = VoteRewardService.getRewordAmount(BigInteger.valueOf(1000000000), now.toInstant().toEpochMilli(), tomorrow.toInstant().toEpochMilli());
        System.out.println(reward);
        if (true) return;

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
