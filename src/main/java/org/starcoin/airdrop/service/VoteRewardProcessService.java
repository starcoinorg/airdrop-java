package org.starcoin.airdrop.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;

@Service
public class VoteRewardProcessService {

    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;

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
}
