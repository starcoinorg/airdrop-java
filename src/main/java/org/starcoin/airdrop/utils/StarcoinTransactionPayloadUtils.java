package org.starcoin.airdrop.utils;

import com.novi.bcs.BcsSerializer;
import com.novi.serde.Bytes;
import com.novi.serde.Int128;
import com.novi.serde.SerializationError;
import com.novi.serde.Unsigned;
import org.starcoin.types.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class StarcoinTransactionPayloadUtils {

    /**
     * curl --location --request POST 'https://barnard-seed.starcoin.org' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{
     * "id":101,
     * "jsonrpc":"2.0",
     * "method":"contract.resolve_function",
     * "params":["0xb987F1aB0D7879b2aB421b98f96eFb44::MerkleDistributorScript::create"]
     * }'
     */
    public static TransactionPayload encodeMerkleDistributorScriptCreateFunction(String tokenType,
                                                                                 Long airdropId,
                                                                                 String root,
                                                                                 BigInteger amount,
                                                                                 Integer proofsSize) {
        // todo encodeMerkleDistributorScriptCreateFunction...
        //        chainService.call_function(apiMerkleTree.getFunctionAddress() + "::MerkleDistributorScript::create",
        //                Lists.newArrayList(apiMerkleTree.getTokenType()),
        //                Lists.newArrayList(apiMerkleTree.getAirDropId() + "", apiMerkleTree.getRoot(), amount.toString(), apiMerkleTree.getProofs().size() + "")
        //        );
        return null;
    }


    // MerkleDistributorScript::create
    public static TransactionPayload encode_withdraw_from_ethereum_chain_script_function(TypeTag token_type,
                                                                                         String from,
                                                                                         AccountAddress to,
                                                                                         BigInteger amount,
                                                                                         int from_chain) {
        ScriptFunction.Builder script_function_builder = new ScriptFunction.Builder();
        script_function_builder.ty_args = java.util.Arrays.asList(token_type);
        //(signer: signer, from: vector<u8>, to: address, amount: u128, from_chain: u8)
        script_function_builder.args = java.util.Arrays.asList(
                encode_u8vector_argument(new Bytes(Numeric.hexStringToByteArray(from))),
                encode_address_argument(to),
                encode_u128_argument(amount),
                encode_u8_argument((byte) from_chain));
        script_function_builder.function = new Identifier("withdraw_from_ethereum_chain");
        script_function_builder.module = new ModuleId(
                AccountAddress.valueOf(Numeric.hexStringToByteArray("0x569AB535990a17Ac9Afd1bc57Faec683")),
                new Identifier("BifrostScripts"));

        TransactionPayload.ScriptFunction.Builder builder = new TransactionPayload.ScriptFunction.Builder();
        builder.value = script_function_builder.build();
        return builder.build();
    }


    private static Bytes encode_u8_argument(@Unsigned Byte arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u8(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8");
        }
    }

    private static Bytes encode_u128_argument(@Unsigned @Int128 BigInteger arg) {
        try {

            BcsSerializer s = new BcsSerializer();
            s.serialize_u128(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u128");
        }
    }

    private static Bytes encode_address_argument(AccountAddress arg) {
        try {

            return Bytes.valueOf(arg.bcsSerialize());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type address");
        }
    }

    private static Bytes encode_u8vector_argument(Bytes arg) {
        try {
            BcsSerializer s = new BcsSerializer();
            s.serialize_bytes(arg);
            return Bytes.valueOf(s.get_bytes());

        } catch (SerializationError e) {
            throw new IllegalArgumentException("Unable to serialize argument of type u8vector");
        }
    }

    private static Boolean decode_bool_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.Bool)) {
            throw new IllegalArgumentException("Was expecting a Bool argument");
        }
        return ((TransactionArgument.Bool) arg).value;
    }

    private static @Unsigned Byte decode_u8_argument(TransactionArgument arg) {
        if (!(arg instanceof TransactionArgument.U8)) {
            throw new IllegalArgumentException("Was expecting a U8 argument");
        }
        return ((TransactionArgument.U8) arg).value;
    }

}
