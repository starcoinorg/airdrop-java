package org.starcoin.airdrop.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.jsonrpc.client.JSONRPC2SessionException;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class StarcoinRpcUtils {

    public static BigInteger getLatestBlockNumber(JSONRPC2Session jsonRpcSession) throws JSONRPC2SessionException, JsonProcessingException {
        String method = "chain.info";
        Map<String, Object> resultMap = new JsonRpcClient(jsonRpcSession).sendJsonRpc(
                method, Collections.emptyList(), new TypeReference<Map<String, Object>>() {
                });
        return new BigInteger(((Map<String, Object>) resultMap.get("head")).get("number").toString());
    }

}
