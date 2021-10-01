package org.starcoin.airdrop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.AirdropProject;
import org.starcoin.airdrop.data.repo.AirdropProjectRepository;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class AirdropProjectService {

    @Autowired
    private AirdropProjectRepository airdropProjectRepository;

    /**
     * Add airdrop project.
     *
     * @param chainId        chain Id.
     * @param name           project name(process name).
     * @param claimStartTime claim reward started at.
     * @param claimEndTime   claim reward ended at.
     * @return Airdrop Id.(project Id.)
     */
    @Transactional
    public Long addProject(Integer chainId, String name, Date claimStartTime, Date claimEndTime) {
        AirdropProject existedPrj = airdropProjectRepository.findFirstByName(name);
        if (existedPrj != null) {
            throw new RuntimeException("Project name existed.");
        }
        // INSERT INTO `airdrop.airdrop_projects`
        // (id, name, token, token_symbol, token_precision, start_at, end_at)
        // VALUES
        // (null,'投票#4奖励-Starcoin Move 合约标准库升级到 V6 版本','0x1::STC::STC', 'STC', 9, '2021-09-26 23:00:00','2021-09-20 23:59:59')
        AirdropProject p = new AirdropProject();
        p.setName(name);
        p.setStartAt(claimStartTime);
        p.setEndAt(claimEndTime);
        p.setNetworkVersion(chainId);
        //p.setToken("0x1::STC::STC"); // default token type.
        p.setCreateAt(new Date());
        p.setUpdateAt(p.getCreateAt());
        airdropProjectRepository.save(p);
        airdropProjectRepository.flush();
        return p.getId();
    }

    /**
     * Update project info.
     *
     * @param airdropId    Airdrop Id.(project Id.)
     * @param ownerAddress On-chain owner address.
     * @param rootHash     merkle tree root hash.
     */
    @Transactional
    public void updateProject(Long airdropId, String ownerAddress, String rootHash) { // , Integer chainId
        AirdropProject p = airdropProjectRepository.findById(airdropId).orElseThrow(() -> new RuntimeException("Cannot find project by Id: " + airdropId));
        // `update airdrop_projects set
        // owner_address='${reward.ownerAddress}',
        // root='${reward.root}',
        // network_version='${reward.chainId}'
        // where id = ${reward.airDropId};`
        p.setOwnerAddress(ownerAddress);
        p.setRoot(rootHash);
        //p.setNetworkVersion(chainId);
        p.setUpdateAt(new Date());
        airdropProjectRepository.save(p);
        airdropProjectRepository.flush();
    }

}
