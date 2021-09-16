package org.starcoin.airdrop.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.util.List;
import java.util.function.Function;

public class JsonRpcClient {

    private final JSONRPC2Session jsonRpcSession;

    public JsonRpcClient(JSONRPC2Session jsonRpcSession) {
        this.jsonRpcSession = jsonRpcSession;
    }

    public <T> T sendJsonRpc(String method, List<Object> params, Class<T> resultType)
            throws JSONRPC2SessionException, JsonProcessingException {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj == null ? null
                    : getObjectMapper().convertValue(resultObj, resultType);
        });
    }

    public Object sendJsonRpc(String method, List<Object> params)
            throws JSONRPC2SessionException, JsonProcessingException {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj;
        });
    }

    public <T> T sendJsonRpc(String method, List<Object> params, TypeReference<T> resultType)
            throws JSONRPC2SessionException, JsonProcessingException {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj == null ? null
                    : getObjectMapper().convertValue(resultObj, resultType);
        });
    }

    private <T> T sendJsonRpc(String method, List<Object> params, Function<Object, T> readResult)
            throws JSONRPC2SessionException {
        JSONRPC2Request request = new JSONRPC2Request(method, params, System.currentTimeMillis());
        JSONRPC2Response response = jsonRpcSession.send(request);
        if (response.indicatesSuccess()) {
            Object result = response.getResult();
            //if (result != null) {
            return readResult.apply(result);
            //} else {
            //    return null;
            //}
        }
        /**
         * {
         *   "code": -49998,
         *   "data": {
         *     "Discard": {
         *       "status_code": "3",
         *       "status_code_name": "SEQUENCE_NUMBER_TOO_OLD"
         *     }
         *   },
         *   "message": "Transaction error (Call txn err: Transaction execution error (Execution error: status SEQUENCE_NUMBER_TOO_OLD of type Validation)..)"
         * }
         */
        // catch SEQUENCE_NUMBER_TOO_OLD error???
        throw new RuntimeException("JSON RPC response error: " + response.getError());
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
