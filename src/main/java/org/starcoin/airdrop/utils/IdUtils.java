package org.starcoin.airdrop.utils;

import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.starcoin.airdrop.data.model.StarcoinEvent;


public class IdUtils {

    /**
     * Generate eventId by event's properties.
     *
     * @param e starcoin event.
     * @return eventId.
     */
    public static String generateEventId(StarcoinEvent e) {
        String s = "{" +
                "blockHash='" + e.getBlockHash() + '\'' +
                //", blockNumber=" + blockNumber +
                //", transactionHash='" + e.getTransactionHash() + '\'' +
                ", transactionIndex=" + e.getTransactionIndex() +
                ", eventKey='" + e.getEventKey() + '\'' +
                ", eventSequenceNumber=" + e.getEventSequenceNumber() +
                //", typeTag='" + typeTag + '\'' +
                //", data='" + data + '\'' +
                //", status='" + status + '\'' +
                '}';
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return Hex.toHexString(digestSHA3.digest(s.getBytes()));
    }

//    public static String generateLogId(EthereumLog e) {
//        String s = "{" +
//                "blockHash='" + e.getBlockHash() + '\'' +
//                ", transactionIndex=" + e.getTransactionIndex() +
//                ", logIndex=" + e.getLogIndex() +
//                '}';
//        Keccak.Digest256 digest256 = new Keccak.Digest256();
//        return Hex.toHexString(digest256.digest(s.getBytes()));
//    }

}
