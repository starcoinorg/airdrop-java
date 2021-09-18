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
     * @param chainId   chain Id.
     * @param name      prject name(process name).
     * @param startTime vote started at.
     * @param endTime   vote ended at.
     * @return Project Id.
     */
    @Transactional
    public Long addProject(Integer chainId, String name, Date startTime, Date endTime) {
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
        p.setStartAt(startTime);
        p.setEndAt(endTime);
        p.setNetworkVersion(chainId);
        //p.setToken("0x1::STC::STC");
        p.setCreateAt(new Date());
        p.setUpdateAt(p.getCreateAt());
        airdropProjectRepository.save(p);
        airdropProjectRepository.flush();
        return p.getId();
    }

    /**
     * Update project info.
     *
     * @param id           project Id.
     * @param ownerAddress On-chain owner address.
     * @param root         merkle tree root.
     */
    @Transactional
    public void updateProject(Long id, String ownerAddress, String root) { // , Integer chainId
        AirdropProject p = airdropProjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find project by Id: " + id));
        // `update airdrop_projects set
        // owner_address='${reward.ownerAddress}',
        // root='${reward.root}',
        // network_version='${reward.chainId}'
        // where id = ${reward.airDropId};`
        p.setOwnerAddress(ownerAddress);
        p.setRoot(root);
        //p.setNetworkVersion(chainId);
        p.setUpdateAt(new Date());
        airdropProjectRepository.save(p);
        airdropProjectRepository.flush();
    }

}
