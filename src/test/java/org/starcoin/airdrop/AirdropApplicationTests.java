package org.starcoin.airdrop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.starcoin.airdrop.data.model.AirdropProject;
import org.starcoin.airdrop.data.model.AirdropRecord;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.repo.AirdropProjectRepository;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;
import org.starcoin.airdrop.service.*;

import java.math.BigInteger;
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
    AirdropMerkleDistributionService airdropMerkleDistributionService;

    //
    // A test proposal in barnard network:
    // proposalId = 29
    // proposer = 0x0000000000000000000000000a550c18
    //
    // To distribute vote rewards, called this function:
    // 0xb987f1ab0d7879b2ab421b98f96efb44::MerkleDistributorScript::create
    // Submitted transaction:
    // https://stcscan.io/barnard/transactions/detail/0x56b16b40e22929112be338a2bda160f29ee0c48dd7e3992090da7d585ced8866
    //
    // Created airdrop(reward) json file:
    //{
    //    "airDropId": 11,
    //    "chainId": 251,
    //    "functionAddress": "0xb987F1aB0D7879b2aB421b98f96eFb44",
    //    "ownerAddress": "0xccf1adedf0ba6f9bdb9a6905173a5d72",
    //    "proofs": [
    //        {
    //            "address": "0x0000000000000000000000000a550c18",
    //            "amount": 19999999999999,
    //            "index": 0,
    //            "proof": [
    //                "0xf9b34a781533306d40793cad67a30e4791c655bbbf3ed76eabfe998f4d2f38f7"
    //            ]
    //        }
    //    ],
    //    "root": "0x98d2a629ed623aba9c219c946ef063845d878fe0a265701bdce4d104d857d522",
    //    "tokenType": "0x00000000000000000000000000000001::STC::STC"
    //}
    //

    @Test
    void contextLoads() {
        airdropMerkleDistributionService.revokeOnChain(8L);
        if (true) return;

        airdropMerkleDistributionService.createAirdropMerkleTreeAndUpdateOnChain(8L, 0L);
        if (true) return;

        Long prjId = airdropProjectService.addProject(1, "Test prj. " + System.currentTimeMillis(), new Date(), new Date());
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
