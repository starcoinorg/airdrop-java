package org.starcoin.airdrop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class VoteRewardProcessService {
    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessService.class);

    private static final BigInteger TOTAL_REWARD_AMOUNT_LIMIT = BigInteger.valueOf(20000L).multiply(BigInteger.TEN.pow(9));

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private StarcoinVoteChangedEventService starcoinVoteChangedEventService;

    @Autowired
    private VoteRewardService voteRewardService;

    @Autowired
    private StarcoinEventRepository starcoinEventRepository;

    @Autowired
    private VoteRewardRepository voteRewardRepository;

    @Autowired
    private AirdropProjectService airdropProjectService;

    @Autowired
    private MerkleTreeService merkleTreeService;

    public VoteRewardProcess getVoteRewardProcess(Long processId) {
        return voteRewardProcessRepository.findById(processId).orElse(null);
    }

    public VoteRewardProcess createVoteRewardProcess(VoteRewardProcess src) {
        VoteRewardProcess v = new VoteRewardProcess();
        BeanUtils.copyProperties(src, v);
        v.setProcessId(null);
        v.setVersion(null);
        v.setCreatedAt(System.currentTimeMillis());
        v.setCreatedBy("admin");
        v.setUpdatedAt(v.getCreatedAt());
        v.setUpdatedBy(v.getCreatedBy());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
        return v;
    }

    public VoteRewardProcess findByIdOrElseThrow(Long processId) {
        return voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
    }

    public void process(VoteRewardProcess v) {
        if (!VoteRewardProcess.STATUS_CREATED.equalsIgnoreCase(v.getStatus())) {
            throw new IllegalArgumentException("VoteRewardProcess status error. ProcessId: " + v.getProcessId());
        }
        updateStatusProcessing(v);
        // ------------------------------
        starcoinEventRepository.deactiveEventsByProposalId(v.getProposalId());
        starcoinVoteChangedEventService.findESEventsAndSave(v.getProposalId(), v.getProposer(), v.getVoteStartTimestamp(), v.getVoteEndTimestamp());
        List<StarcoinVoteChangedEvent> events = starcoinEventRepository.findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(v.getProposalId());
        voteRewardRepository.deactiveVoteRewardsByProposalId(v.getProposalId());
        voteRewardService.addOrUpdateVoteRewards(v.getProposalId(), events);
        voteRewardService.calculateRewords(v.getProposalId(), v.getVoteEndTimestamp());
        BigInteger totalRewardAmount = voteRewardRepository.sumTotalRewardAmountByProposalId(v.getProposalId());
        if (totalRewardAmount.compareTo(TOTAL_REWARD_AMOUNT_LIMIT) > 0) {
            LOG.info("Calculated total reward amount exceed limit. " + totalRewardAmount + " > " + TOTAL_REWARD_AMOUNT_LIMIT);
            voteRewardService.adjustRewardsUnderLimit(v.getProposalId(), totalRewardAmount, TOTAL_REWARD_AMOUNT_LIMIT);
            LOG.info("Adjusted rewards under total amount limit: " + TOTAL_REWARD_AMOUNT_LIMIT);
        }
        Long projId = airdropProjectService.addProject(v.getName(), new Date(v.getVoteStartTimestamp()), new Date(v.getVoteEndTimestamp()));
        merkleTreeService.createAirdropMerkleTreeAndUpdateOnChain(v.getProcessId(), projId);
        // ------------------------------
        updateVoteRewardProcessStatusProcessed(v.getProcessId());
    }

    private void updateVoteRewardProcessStatusProcessed(Long processId) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find by process by Id: " + processId));
        v.processed();
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    private void updateStatusProcessing(VoteRewardProcess v) {
        v.processing();
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

}
