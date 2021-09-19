package org.starcoin.airdrop.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.AirdropRecord;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleTree;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;

@Service
public class AirdropRecordService {

    @Autowired
    private AirdropRecordRepository airdropRecordRepository;

    @Transactional
    public void addAirdropRecords(ApiMerkleTree apiMerkleTree) {
        apiMerkleTree.getProofs().forEach(r -> {
            addAirdropRecord(apiMerkleTree.getAirDropId(),
                    r.getAddress(), r.getAmount(), (int) r.getIndex(), JSON.toJSONString(r.getProof()));
        });
    }

    @Transactional
    public void addAirdropRecord(Long airdropId, String address, BigInteger amount, Integer index, String proof) {
        AirdropRecord r = new AirdropRecord();
        r.setAirdropId(airdropId);
        r.setAddress(address.toLowerCase());
        r.setAmount(amount);
        r.setIdx(index);
        r.setProof(proof);
        r.setStatus(0);
        r.setCreatedAt(new Date());
        r.setUpdatedAt(r.getCreatedAt());
        airdropRecordRepository.save(r);
    }
    //  const data = {
    //    airDropId: Number(reward.airDropId),
    //    ownerAddress: reward.ownerAddress,
    //    root: reward.root,
    //    chainId: reward.chainId,
    //    address: proof.address.toLowerCase(),
    //    idx: Number(proof.index),
    //    amount: Number(proof.amount),
    //    proof: proof.proof,
    //    status: '0',
    //  }
    // `insert into airdrop_records
    // (id, address, amount, idx, proof, status, airdrop_id)
    // values
    // (null, '${data.address}', ${data.amount}, ${data.idx},
    // '${JSON.stringify(data.proof)}',
    // '${data.status}', ${data.airDropId});`

}
