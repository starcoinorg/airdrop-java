package org.starcoin.airdrop.taskservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;
import org.starcoin.airdrop.service.StarcoinVoteChangedEventService;
import org.starcoin.airdrop.service.VoteRewardProcessService;
import org.starcoin.airdrop.service.VoteRewardService;

import java.util.List;

@Service
public class VoteRewardProcessTaskService {
    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardProcessTaskService.class);

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

    @Autowired
    private VoteRewardProcessService voteRewardProcessService;

    @Scheduled(fixedDelayString = "${airdrop.vote-reward-process-task-service.fixed-delay}")
    public void task() {
        List<VoteRewardProcess> voteRewardProcesses = voteRewardProcessRepository.findByStatusEquals(VoteRewardProcess.STATUS_CREATED);
        for (VoteRewardProcess v : voteRewardProcesses) {
            // start processing...
            if (LOG.isDebugEnabled()) {
                LOG.debug("Start processing vote rewards... ProcessId: " + v.getProcessId());
            }
            voteRewardProcessService.process(v);
            // ------------------------------
            if (LOG.isDebugEnabled()) {
                LOG.debug("End of processing vote rewards. ProcessId: " + v.getProcessId());
            }
        }
    }

}
