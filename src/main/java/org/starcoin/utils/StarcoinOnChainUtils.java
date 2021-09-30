package org.starcoin.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.jsonrpc.client.JSONRPC2SessionException;
import org.starcoin.types.AccountAddress;
import org.starcoin.types.ChainId;
import org.starcoin.types.RawUserTransaction;
import org.starcoin.types.TransactionPayload;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StarcoinOnChainUtils {
    public static final String METHOD_TXPOOL_SUBMIT_HEX_TRANSACTION = "txpool.submit_hex_transaction";

    public static final String GAS_TOKEN_CODE_STC = "0x1::STC::STC";

    private static final Logger LOG = LoggerFactory.getLogger(StarcoinOnChainUtils.class);

    public static BigInteger getLatestBlockNumber(JSONRPC2Session jsonRpcSession) throws JSONRPC2SessionException, JsonProcessingException {
        String method = "chain.info";
        Map<String, Object> resultMap = new JsonRpcClient(jsonRpcSession).sendJsonRpc(
                method, Collections.emptyList(), new TypeReference<Map<String, Object>>() {
                });
        return new BigInteger(((Map<String, Object>) resultMap.get("head")).get("number").toString());
    }

    public static String submitHexTransaction(JSONRPC2Session jsonRpcSession, byte[] signedMessage) {
        String hexValue = HexUtils.toHexString(signedMessage);
        if (LOG.isDebugEnabled())
            LOG.debug("Signed transaction: " + hexValue);
        List<Object> params = Collections.singletonList(hexValue);
        Object result = new JsonRpcClient(jsonRpcSession).sendJsonRpc(METHOD_TXPOOL_SUBMIT_HEX_TRANSACTION, params);
        if (!(result instanceof String)) {
            throw new RuntimeException("Send JSON RPC error. Unknown result type, result string is: " + result);
        }
        return (String) result;
    }

    public static BigInteger getGasPrice(JSONRPC2Session jsonRpcSession) {
        String r = new JsonRpcClient(jsonRpcSession).sendJsonRpc("txpool.gas_price",
                Collections.emptyList(), String.class);
        return new BigInteger(r);
    }

    public static Long getNowSeconds(JSONRPC2Session jsonRpcSession) {
        Map<String, Object> m = new JsonRpcClient(jsonRpcSession).sendJsonRpc("node.info",
                Collections.emptyList(), new TypeReference<Map<String, Object>>() {
                });
        return Long.valueOf(m.get("now_seconds").toString());
    }

    @SuppressWarnings("unchecked")
    public static BigInteger getAccountSequenceNumber(JSONRPC2Session jsonRpcSession, String accountAddress) {
        Map<String, Object> m = new JsonRpcClient(jsonRpcSession).sendJsonRpc("contract.get_resource",
                Arrays.asList(accountAddress, "0x1::Account::Account"), new TypeReference<Map<String, Object>>() {
                });
        if (m == null) {
            String msg = "Get null account resource by address: " + accountAddress;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
        List<Object> resourceItem = (List<Object>) ((List<Object>) m.get("value")).stream()
                .filter(item -> item instanceof List && "sequence_number".equals(((List) item).get(0)))
                .findFirst().orElseThrow(() -> new RuntimeException("Account resource item 'sequence_number' does NOT exist."));
        return new BigInteger(((Map<String, Object>) resourceItem.get(1)).get("U64").toString());
    }

    @SuppressWarnings("unchecked")
    public static BigInteger getAccountStcBalance(JSONRPC2Session jsonRpcSession, String accountAddress) {
        Map<String, Object> m = new JsonRpcClient(jsonRpcSession).sendJsonRpc("contract.get_resource",
                Arrays.asList(accountAddress, "0x1::Account::Balance<0x1::STC::STC>"), new TypeReference<Map<String, Object>>() {
                });
        if (m == null) {
            String msg = "Get null account resource by address: " + accountAddress;
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
        List<Object> resourceItem = (List<Object>) ((List<Object>) m.get("value")).stream()
                .filter(item -> item instanceof List && "token".equals(((List) item).get(0)))
                .findFirst().orElseThrow(() -> new RuntimeException("Account resource item 'token' does NOT exist."));
        Map<String, Object> tokenStruct = (Map<String, Object>) ((Map<String, Object>) resourceItem.get(1)).get("Struct");
        List<Object> tokenStructValue = (List<Object>) ((List<Object>) tokenStruct.get("value")).stream()
                .filter(item -> item instanceof List && "value".equals(((List) item).get(0)))
                .findFirst().orElseThrow(() -> new RuntimeException("Token struct item 'value' does NOT exist."));
        return new BigInteger(((Map<String, Object>) tokenStructValue.get(1)).get("U128").toString());
/**
 * {
 *   "jsonrpc": "2.0",
 *   "result": {
 *     "abilities": 8,
 *     "type_": "0x00000000000000000000000000000001::Account::Balance<0x00000000000000000000000000000001::STC::STC>",
 *     "value": [
 *       [
 *         "token",
 *         {
 *           "Struct": {
 *             "abilities": 4,
 *             "type_": "0x00000000000000000000000000000001::Token::Token<0x00000000000000000000000000000001::STC::STC>",
 *             "value": [
 *               [
 *                 "value",
 *                 {
 *                   "U128": "8873161509829392"
 *                 }
 *               ]
 *             ]
 *           }
 *         }
 *       ]
 *     ]
 *   },
 *   "id": 101
 * }
 */
    }

    public static RawUserTransaction createRawUserTransaction(Integer chainId, AccountAddress accountAddress,
                                                              BigInteger accountSeqNumber,
                                                              TransactionPayload payload,
                                                              BigInteger gasPrice, BigInteger gasLimit,
                                                              Long expirationTimestampSecs) {
        ChainId chainIdObj = new ChainId(chainId.byteValue());
        return new RawUserTransaction(accountAddress, accountSeqNumber.longValue(), payload,
                gasLimit.longValue(), gasPrice.longValue(), GAS_TOKEN_CODE_STC,
                expirationTimestampSecs,//getNowSeconds() + 43200,
                chainIdObj);
    }

    public static Map<String, Object> getJsonRpcDryRunParam(Integer chainId, BigInteger gasUnitPrice, BigInteger maxGasAmount,
                                                            String senderAddress, String senderPublicKey, BigInteger accountSeqNumber,
                                                            String code, List<String> type_args, List<String> args) {
        DryRunParam p = new DryRunParam();
        p.chain_id = chainId & 0xFFL;
        p.gas_unit_price = gasUnitPrice;
        p.sender = senderAddress;
        p.sender_public_key = senderPublicKey;
        p.sequence_number = accountSeqNumber;
        p.max_gas_amount = maxGasAmount;//BigInteger.valueOf(10000000);
        DryRunParam.Script s = new DryRunParam.Script();
        s.code = code;
        s.type_args = type_args;
        s.args = args;
        p.script = s;
        return new ObjectMapper().convertValue(p, new TypeReference<Map<String, Object>>() {
        });
    }

    public static OnChainTransaction getOnChainTransaction(JSONRPC2Session jsonRpcSession, String transactionHash) {
        OnChainTransaction onChainTransaction = new JsonRpcClient(jsonRpcSession)
                .sendJsonRpc("chain.get_transaction", Collections.singletonList(transactionHash), OnChainTransaction.class);
        return onChainTransaction;
    }

    /**
     * {
     * "block_hash": "0x1222ccf9adf8783f304e8af46c8911aaa3291e036b9179017df025160f6bf303",
     * "block_number": "127889",
     * "transaction_hash": "0x66434cfe8055fca8b7fd6233ddb138a74a75f47c94c61b9c39e2b89be88b6ec3",
     * "transaction_index": 5,
     * "block_metadata": null,
     * "user_transaction": {
     * "transaction_hash": "0x66434cfe8055fca8b7fd6233ddb138a74a75f47c94c61b9c39e2b89be88b6ec3",
     * "raw_txn": {
     * "sender": "0xd347389cea8711cd1715e9590b6f89fe",
     * "sequence_number": "74308",
     * "payload": "0x02000000000000000000000000000000010f5472616e73666572536372697074730c706565725f746f5f7065657201070000000000000000000000000000000103535443035354430003100000000000000000000000000a550c18010010e8030000000000000000000000000000",
     * "max_gas_amount": "40000000",
     * "gas_unit_price": "1",
     * "gas_token_code": "0x1::STC::STC",
     * "expiration_timestamp_secs": "1618229397",
     * "chain_id": 251
     * },
     * "authenticator": {
     * "Ed25519": {
     * "public_key": "0x24aa8e87584d8bb64089c698505c0af2bc448bf1f51332d313951a737771b859",
     * "signature": "0x1fc8f1f44e31f443f647a45d6f56f6142b9be7a6714975e128fcf4d91f011fe0ebad3daff018dce0dce4835190bbf653fe1c5bcc3e061bfa39842b2d39eed20f"
     * }
     * }
     * }
     * }
     */
    public static class OnChainTransaction {
        public String block_hash;
        public String block_number;
        public String transaction_hash;
        public Long transaction_index;
        public Object block_metadata;
        public UserTransaction user_transaction;

        public static class UserTransaction {
            public String transaction_hash;
            public RawTransaction raw_txn;
            public Authenticator authenticator;

            public static class RawTransaction {
                public String sender;
                public String sequence_number;
                public String payload;
                public String max_gas_amount;
                public String gas_unit_price;
                public String gas_token_code;
                public String expiration_timestamp_secs;
                public Long chain_id;
            }

            public static class Authenticator {
                public Map<String, Object> Ed25519;
                // {
                //    "public_key": "0x24aa8e87584d8bb64089c698505c0af2bc448bf1f51332d313951a737771b859",
                //    "signature": "0x1fc8f1f44e31f443f647a45d6f56f6142b9be7a6714975e128fcf4d91f011fe0ebad3daff018dce0dce4835190bbf653fe1c5bcc3e061bfa39842b2d39eed20f"
                //}
            }
        }
    }

    /**
     * {
     * "block_hash": "0x1222ccf9adf8783f304e8af46c8911aaa3291e036b9179017df025160f6bf303",
     * "block_number": "127889",
     * "transaction_hash": "0x66434cfe8055fca8b7fd6233ddb138a74a75f47c94c61b9c39e2b89be88b6ec3",
     * "transaction_index": 5,
     * "state_root_hash": "0x3e125869b8b0a56c15e7751d86094deab57dc2e693fc0d64bc0436b93640ed75",
     * "event_root_hash": "0xff8294c24cc2dabc47b2b6f7200e91afaafcb99469b2e42f14b3299457379248",
     * "gas_used": "124191",
     * "status": "Executed"
     * }
     */
    private static class OnChainTransactionInfo {
        @JsonProperty
        public String block_hash;
        @JsonProperty
        public String block_number;
        @JsonProperty
        public String transaction_hash;
        @JsonProperty
        public Long transaction_index;
        @JsonProperty
        public String state_root_hash;
        @JsonProperty
        public String event_root_hash;
        @JsonProperty
        public String gas_used;
        @JsonProperty
        public String status;
    }

    /**
     * {
     * "chain_id": 1,
     * "gas_unit_price": 1,
     * "sender": "0x82e35b34096F32C42061717C06e44A59",
     * "sender_public_key": "0x671f257c6c31231bb272fb67e3090b1f6218010a2e7e31e677ce56924ae12074",
     * "sequence_number": 0,
     * "max_gas_amount": 10000000,
     * "script": {
     * "code": "0x1::TransferScripts::peer_to_peer",
     * "type_args": ["0x1::STC::STC"],
     * "args": ["0x621500bf2b4aad17a690cb24f9a225c6", "x\"\"", "1000000000u128"]
     * }
     * }
     */
    private static class DryRunParam {
        @JsonProperty
        Long chain_id;
        @JsonProperty
        BigInteger gas_unit_price;
        @JsonProperty
        String sender;
        @JsonProperty
        String sender_public_key;
        @JsonProperty
        BigInteger sequence_number;
        @JsonProperty
        BigInteger max_gas_amount;
        @JsonProperty
        Script script;

        static class Script {
            @JsonProperty
            String code;
            @JsonProperty
            List<String> type_args;
            @JsonProperty
            List<String> args;
        }
    }

}
