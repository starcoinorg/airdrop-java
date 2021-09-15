package org.starcoin.airdrop.taskservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;
import org.starcoin.airdrop.service.StarcoinVoteChangedEventService;
import org.starcoin.airdrop.service.VoteRewardService;

import java.util.List;

@Service
public class VoteRewardProcessTaskService {

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private StarcoinVoteChangedEventService starcoinVoteChangedEventService;

    @Autowired
    private VoteRewardService voteRewardService;

    @Autowired
    private StarcoinEventRepository starcoinEventRepository;

    @Scheduled(fixedDelayString = "${airdrop.vote-reward-process-task-service.fixed-delay}")
    public void task() {
        List<VoteRewardProcess> voteRewardProcesses = voteRewardProcessRepository.findByStatusEquals(VoteRewardProcess.STATUS_CREATED);
        for (VoteRewardProcess v : voteRewardProcesses) {
            updateStatusProcessing(v);
            // ------------------------------
            // start processing...
            //todo deactive event statuses by proposal Id.
            starcoinVoteChangedEventService.findESEventsAndSave(v.getProposalId(), v.getProposer(), v.getVoteStartTimestamp(), v.getVoteEndTimestamp());
            List<StarcoinVoteChangedEvent> events = starcoinEventRepository.findStarcoinVoteChangedEventsByProposalIdOrderByVoteTimestamp(v.getProposalId());
            //todo deactive vote reward records by proposal Id.
            voteRewardService.addOrUpdateVoteRewards(v.getProposalId(), events);
            voteRewardService.calculateRewords(v.getProposalId(), v.getVoteEndTimestamp());
            // ------------------------------
            updateVoteRewardProcessStatusProcessed(v.getProcessId());
        }
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
