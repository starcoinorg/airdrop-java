package org.starcoin.airdrop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.repo.AirdropRecordRepository;

import javax.transaction.Transactional;

@Service
public class AirdropRecordService {

    @Autowired
    private AirdropRecordRepository airdropRecordRepository;

    @Transactional
    public void addRecord() {
        // `insert into airdrop_records
        // (id, address, amount, idx, proof, status, airdrop_id)
        // values
        // (null, '${data.address}', ${data.amount}, ${data.idx},
        // '${JSON.stringify(data.proof)}',
        // '${data.status}', ${data.airDropId});`
        //todo
    }

}
