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
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleTree;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.starcoin.airdrop.data.model.VoteRewardProcess.MAX_NAME_LENGTH;

@Service
public class VoteRewardProcessService {
    public static final Long NO_AIRDROP_ID = -1L;
    public static final long CLAIM_REWARD_TIME_LIMIT_MILLISECONDS = 14 * 24L * 60 * 60 * 1000;

    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessService.class);

    //todo config???
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
    private AirdropRecordService airdropRecordService;

    @Autowired
    private AirdropMerkleDistributionService airdropMerkleDistributionService;

    @Autowired
    private StarcoinProposalService starcoinProposalService;

    public VoteRewardProcess getVoteRewardProcess(Long processId) {
        return voteRewardProcessRepository.findById(processId).orElse(null);
    }

    public VoteRewardProcess createVoteRewardProcess(VoteRewardProcess src) {
        if (src.getName() == null || src.getName().isEmpty())
            throw new IllegalArgumentException("Process name is null.");
        if (src.getChainId() == null) throw new IllegalArgumentException("Chain Id is null.");
        if (src.getVoteStartTimestamp() == null)
            throw new IllegalArgumentException("Start time is null.");
        if (src.getVoteEndTimestamp() == null)
            throw new IllegalArgumentException("End time is null.");
        if (src.getName().length() > MAX_NAME_LENGTH)
            throw new IllegalArgumentException("Name is too long.");
        if (src.getProposalProcessSeqNumber() == null) {
            throw new IllegalArgumentException("Proposal Process Sequence Number is null.");
        }
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
        try {
            starcoinVoteChangedEventService.findESEventsAndSave(v.getProposalId(), v.getProposer(), v.getVoteStartTimestamp(), v.getVoteEndTimestamp());
        } catch (RuntimeException runtimeException) {
            updateVoteRewardProcessingMessage(v.getProcessId(), "Pull elasticsearch events or save events error.");
            throw runtimeException;
        }
        List<StarcoinVoteChangedEvent> events = starcoinEventRepository.findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(v.getProposalId());
        voteRewardRepository.deactiveVoteRewardsByProposalId(v.getProposalId());
        processVoteRewards(v, events);
        boolean onChain = v.getOnChainDisabled() == null || !v.getOnChainDisabled(); // default is on-chain.
        Date claimRewardStartTime = new Date(v.getVoteEndTimestamp());//new Date(v.getVoteStartTimestamp());
        Date claimRewardEndTime = new Date(v.getVoteEndTimestamp() + CLAIM_REWARD_TIME_LIMIT_MILLISECONDS);
        Long airdropId = onChain
                ? airdropProjectService.addProject(v.getChainId(), v.getName(), claimRewardStartTime, claimRewardEndTime)
                : NO_AIRDROP_ID;
        ApiMerkleTree apiMerkleTree;
        if (onChain) {
            // 在奖励发放之前，检查 privateKey 对应 address，STC 的 balance 应该大于应发放奖励的总额
            airdropMerkleDistributionService.assertOwnerAccountHasSufficientBalance(v.getProcessId());
            apiMerkleTree = airdropMerkleDistributionService.createAirdropMerkleTreeAndUpdateOnChain(v.getProcessId(), airdropId);
            airdropProjectService.updateProject(airdropId, apiMerkleTree.getOwnerAddress(), apiMerkleTree.getRoot());
            airdropRecordService.addAirdropRecords(apiMerkleTree);
        } else {
            apiMerkleTree = airdropMerkleDistributionService.createAirdropMerkleTreeAndSave(v.getProcessId(), airdropId);
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("Airdrop MerkleTree proof created and saved. Root hash: " + apiMerkleTree.getRoot());
        }
        // ------------------------------
        updateVoteRewardProcessStatusProcessed(v.getProcessId());
    }

    private void processVoteRewards(VoteRewardProcess v, List<StarcoinVoteChangedEvent> events) {
        voteRewardService.addOrUpdateVoteRewards(v.getProposalId(), events);
        voteRewardService.calculateRewords(v.getProposalId(), v.getVoteEndTimestamp());
        BigInteger totalRewardAmount = voteRewardRepository.sumTotalRewardAmountByProposalId(v.getProposalId());
        if (totalRewardAmount.compareTo(TOTAL_REWARD_AMOUNT_LIMIT) > 0) {
            LOG.info("Calculated total reward amount exceed limit. " + totalRewardAmount + " > " + TOTAL_REWARD_AMOUNT_LIMIT);
            voteRewardService.adjustRewardsUnderLimit(v.getProposalId(), totalRewardAmount, TOTAL_REWARD_AMOUNT_LIMIT);
            LOG.info("Adjusted rewards under total amount limit: " + TOTAL_REWARD_AMOUNT_LIMIT);
        }
    }

    public void updateVoteRewardProcessStatusError(Long processId, String message) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find by process by Id: " + processId));
        v.setStatusError(message);
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
    }

    private void updateVoteRewardProcessingMessage(Long processId, String message) {
        VoteRewardProcess v = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find by process by Id: " + processId));
        v.setMessage(message);
        v.setUpdatedBy("admin");
        v.setUpdatedAt(System.currentTimeMillis());
        voteRewardProcessRepository.save(v);
        voteRewardProcessRepository.flush();
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

    public VoteRewardProcess createVoteRewardProcessByProposalId(String proposalId, boolean onChainDisabled) {
        if (!onChainDisabled) {
            BigInteger maxOnChainProposalId = voteRewardProcessRepository.getMaxOnChainProposalId();
            //System.out.println(maxOnChainProposalId);
            if (maxOnChainProposalId != null && maxOnChainProposalId.compareTo(new BigInteger(proposalId)) >= 0) {
                String msg = "Proposal #" + proposalId + " already has process.";
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
        }
        StarcoinProposalService.Proposal proposal = starcoinProposalService.getProposalByIdOnChain(proposalId);
        VoteRewardProcess voteRewardProcess = starcoinProposalService.createVoteRewardProcess(proposal, onChainDisabled);
        //System.out.println(voteRewardProcess);
        return createVoteRewardProcess(voteRewardProcess);
    }

}
