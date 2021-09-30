package org.starcoin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.starcoin.jsonrpc.JSONRPC2Request;
import org.starcoin.jsonrpc.JSONRPC2Response;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.jsonrpc.client.JSONRPC2SessionException;

import java.util.List;
import java.util.function.Function;

public class JsonRpcClient {

    private final JSONRPC2Session jsonRpcSession;

    public JsonRpcClient(JSONRPC2Session jsonRpcSession) {
        this.jsonRpcSession = jsonRpcSession;
    }

    public <T> T sendJsonRpc(String method, List<Object> params, Class<T> resultType) {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj == null ? null
                    : getObjectMapper().convertValue(resultObj, resultType);
        });
    }

    public Object sendJsonRpc(String method, List<Object> params) {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj;
        });
    }

    public <T> T sendJsonRpc(String method, List<Object> params, TypeReference<T> resultType) {
        return sendJsonRpc(method, params, (resultObj) -> {
            return resultObj == null ? null
                    : getObjectMapper().convertValue(resultObj, resultType);
        });
    }

    private <T> T sendJsonRpc(String method, List<Object> params, Function<Object, T> readResult) {
        JSONRPC2Request request = new JSONRPC2Request(method, params, System.currentTimeMillis());
        JSONRPC2Response response = null;
        try {
            response = jsonRpcSession.send(request);
        } catch (JSONRPC2SessionException e) {
            throw new RuntimeException("JSON RPC send error.", e);
        }
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
        //todo catch SEQUENCE_NUMBER_TOO_OLD error???
        String msg = "JSON RPC error. response error: '" + response.getError() + "'. Request: " + request;
        throw new RuntimeException(msg);
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
