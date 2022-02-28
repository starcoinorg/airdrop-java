package org.starcoin.airdrop.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.ProposalToBeAirdropped;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.ProposalToBeAirdroppedRepository;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;
import org.starcoin.airdrop.service.StarcoinProposalService;

import static org.starcoin.airdrop.service.StarcoinProposalService.Proposal.*;

@Service
public class ProposalToBeAirdroppedTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(ProposalToBeAirdroppedTaskService.class);

    private static final long ONLY_AIRDROP_PROPOSAL_ENDED_AFTER = 1645772782916L;
    private static final long ONLY_AIRDROP_PROPOSAL_ID_AFTER = 9L;

    @Autowired
    private ProposalToBeAirdroppedRepository proposalToBeAirdroppedRepository;

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private StarcoinProposalService starcoinProposalService;

    @Scheduled(fixedDelayString = "${airdrop.proposal-to-be-airdropped-task-service.fixed-delay}")
    public void task() {
        StarcoinProposalService.ProposalPageResult proposalPageResult = starcoinProposalService.getLastProposalList();
        if (proposalPageResult == null || proposalPageResult.data == null || proposalPageResult.data.list == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Start processing proposals to be airdropped... Got null list...");
                return;
            }
        }
        for (StarcoinProposalService.Proposal p : proposalPageResult.data.list) {
            Long proposalId = Long.parseLong(p.idOnChain);
            if (null == p.status
                    || PROPOSAL_STATE_PENDING == p.status
                    || PROPOSAL_STATE_ACTIVE == p.status) {
                if (LOG.isDebugEnabled()) {
                    String msg = "Status of Proposal #" + p.idOnChain + " is " + p.status + ", ignore creating reward airdrop.";
                    LOG.debug(msg);
                }
                continue;
            }
            if (p.onChainEndTime == null) {
                continue;
            }
            if (p.onChainEndTime <= ONLY_AIRDROP_PROPOSAL_ENDED_AFTER) {
                continue;
            }
            if (proposalId <= ONLY_AIRDROP_PROPOSAL_ID_AFTER) {
                continue;
            }
            if (PROPOSAL_STATE_EXTRACTED != p.status && p.onChainEndTime < System.currentTimeMillis()) {
                continue;
            }

            ProposalToBeAirdropped toBeAirdropped = proposalToBeAirdroppedRepository.findById(proposalId).orElse(null);
            if (toBeAirdropped != null) {
                continue;
            }
            VoteRewardProcess voteRewardProcess = voteRewardProcessRepository.findFirstByProposalIdAndOnChainDisabled(proposalId, false);
            if (voteRewardProcess != null) {
                continue;
            }
            toBeAirdropped = toProposalToBeAirdropped(proposalId, p);
            proposalToBeAirdroppedRepository.save(toBeAirdropped);
            proposalToBeAirdroppedRepository.flush();
        }
    }

    private ProposalToBeAirdropped toProposalToBeAirdropped(Long proposalId, StarcoinProposalService.Proposal p) {
        ProposalToBeAirdropped toBeAirdropped = new ProposalToBeAirdropped();
        toBeAirdropped.setProposalId(proposalId);
        toBeAirdropped.setAgainstVotes(p.againstVotes);
        toBeAirdropped.setCreator(p.creator);
        toBeAirdropped.setStatus(p.status);
        toBeAirdropped.setDescription(p.description);
        toBeAirdropped.setCreatedAt(p.createdAt);
        toBeAirdropped.setDescriptionEn(p.descriptionEn);
        toBeAirdropped.setEndTime(p.endTime);
        toBeAirdropped.setForVotes(p.forVotes);
        toBeAirdropped.setLink(p.link);
        toBeAirdropped.setNetwork(p.network);
        toBeAirdropped.setTitle(p.title);
        toBeAirdropped.setOnChainStartTime(p.onChainStartTime);
        toBeAirdropped.setOnChainEndTime(p.onChainEndTime);
        toBeAirdropped.setTypeArgs1(p.typeArgs1);
        toBeAirdropped.setQuorumVotes(p.quorumVotes);

        toBeAirdropped.setCreatedBy("admin");
        toBeAirdropped.setCreatedAt(System.currentTimeMillis());
        toBeAirdropped.setUpdatedBy("admin");
        toBeAirdropped.setUpdatedAt(System.currentTimeMillis());
        return toBeAirdropped;
    }

}
