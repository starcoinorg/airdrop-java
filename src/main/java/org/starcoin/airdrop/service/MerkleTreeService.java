package org.starcoin.airdrop.service;

import com.alibaba.fastjson.JSON;
import com.novi.serde.SerializationError;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.VoteRewardProcessRepository;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;
import org.starcoin.airdrop.utils.StarcoinAccountAddressUtils;
import org.starcoin.airdrop.utils.StarcoinTransactionPayloadUtils;
import org.starcoin.jsonrpc.client.JSONRPC2Session;
import org.starcoin.types.Ed25519PrivateKey;
import org.starcoin.types.RawUserTransaction;
import org.starcoin.types.SignedUserTransaction;
import org.starcoin.types.TransactionPayload;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.SignatureUtils;
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleProof;
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleTree;
import studio.wormhole.quark.command.alma.airdrop.CSVRecord;
import studio.wormhole.quark.command.alma.airdrop.MerkleTreeHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.starcoin.airdrop.utils.StarcoinOnChainUtils.*;

@Service
public class MerkleTreeService {
    public static final long TRANSACTION_EXPIRATION_SECONDS = 2 * 60 * 60L; // two hours?
    private static final Logger LOG = LoggerFactory.getLogger(MerkleTreeService.class);
    private static final BigInteger DEFAULT_GAS_LIMIT = BigInteger.valueOf(10000000);
    private final String airdropOwnerPrivateKey;

    private final String airdropOwnerAddress;
    private final String jsonRpcUrl;
    private final JSONRPC2Session jsonRpcSession;
    @Autowired
    private VoteRewardRepository voteRewardRepository;
    @Autowired
    private VoteRewardProcessRepository voteRewardProcessRepository;
    @Value("${starcoin.chain-id}")
    private Integer chainId;
    @Value("${starcoin.airdrop.function-address}")
    private String airdropFunctionAddress;
    @Value("${starcoin.airdrop.token-type}")
    private String airdropTokenType;

    public MerkleTreeService(@Value("${starcoin.airdrop.owner-private-key}") String airdropOwnerPrivateKey,
                             @Value("${starcoin.json-rpc-url}") String jsonRpcUrl) throws MalformedURLException {
        this.jsonRpcUrl = jsonRpcUrl;
        this.jsonRpcSession = new JSONRPC2Session(new URL(this.jsonRpcUrl));
        this.airdropOwnerPrivateKey = airdropOwnerPrivateKey;
        Ed25519PrivateKey privateKey = SignatureUtils.strToPrivateKey(this.airdropOwnerPrivateKey);
        //Ed25519PublicKey publicKey = SignatureUtils.getPublicKey(privateKey);
        this.airdropOwnerAddress = StarcoinAccountAddressUtils.getAddressFromPrivateKey(privateKey);
        LOG.info("Airdrop owner address: " + this.airdropOwnerAddress);
    }

    public String getJsonRpcUrl() {
        return jsonRpcUrl;
    }

    public ApiMerkleTree createAirdropMerkleTreeAndUpdateOnChain(Long processId, Long airdropId) {
        ApiMerkleTree apiMerkleTree = createAirdropMerkleTree(processId, airdropId);
        BigInteger amount = apiMerkleTree.getProofs().stream().map(ApiMerkleProof::getAmount)
                .reduce(BigInteger::add).orElseThrow(() -> new RuntimeException("Null amount."));
        //        chainService.call_function(apiMerkleTree.getFunctionAddress() + "::MerkleDistributorScript::create",
        //                Lists.newArrayList(apiMerkleTree.getTokenType()),
        //                Lists.newArrayList(apiMerkleTree.getAirDropId() + "", apiMerkleTree.getRoot(),
        //                amount.toString(), apiMerkleTree.getProofs().size() + "")
        //        );
        TransactionPayload transactionPayload = StarcoinTransactionPayloadUtils
                .encodeMerkleDistributorScriptCreateFunction(airdropFunctionAddress,
                        apiMerkleTree.getTokenType(), apiMerkleTree.getAirDropId(), apiMerkleTree.getRoot(),
                        amount, (long) apiMerkleTree.getProofs().size()
                );

        RawUserTransaction rawUserTransaction = createRawUserTransaction(
                this.chainId,
                AccountAddressUtils.create(this.airdropOwnerAddress),
                getAccountSequenceNumber(this.jsonRpcSession, this.airdropOwnerAddress),
                transactionPayload,
                getGasPrice(this.jsonRpcSession),
                DEFAULT_GAS_LIMIT,
                getNowSeconds(this.jsonRpcSession) + TRANSACTION_EXPIRATION_SECONDS);
        // ///////////////////////////////////////
        Ed25519PrivateKey ed25519PrivateKey = SignatureUtils.strToPrivateKey(this.airdropOwnerPrivateKey);
        SignedUserTransaction signedUserTransaction = SignatureUtils.signTxn(ed25519PrivateKey,
                rawUserTransaction);
        byte[] signedMessage = new byte[0];
        try {
            signedMessage = signedUserTransaction.bcsSerialize();
        } catch (SerializationError error) {
            LOG.error("Serialize signedUserTransaction error.", error);
            throw new RuntimeException(error);
        }
        String transactionHash = submitHexTransaction(this.jsonRpcSession, signedMessage);
        LOG.info("Submit MerkleDistributorScript-create transaction on-chain. Transaction hash: " + transactionHash);
        return apiMerkleTree;
    }

    private ApiMerkleTree createAirdropMerkleTree(Long processId, Long airdropId) {
        VoteRewardProcess process = voteRewardProcessRepository.findById(processId).orElseThrow(() -> new RuntimeException("Cannot find process by Id: " + processId));
        if (!chainId.equals(process.getChainId())) {
            throw new RuntimeException("Wrong chain Id. Must be: " + chainId);
        }
        Long proposalId = process.getProposalId();
        //        ChainService chainService = new ChainService(ChainAccount.builder()
        //                .privateKey(privateKeyStr)
        //                .build(), chainId);
        //        CsvToBean<CSVRecord> csvToBean = new CsvToBeanBuilder(
        //                new InputStreamReader(new FileInputStream(file)))
        //                .withType(CSVRecord.class)
        //                .withIgnoreLeadingWhiteSpace(true)
        //                .build();
        if (airdropId == null) {
            throw new IllegalArgumentException("Airdrop Id is null.");
        }
        // List<CSVRecord> records = Lists.newArrayList(csvToBean.iterator());
        List<Map<String, Object>> rs = voteRewardRepository.sumRewardAmountGroupByVoter(proposalId);
        List<CSVRecord> records = rs.stream().map(r -> {
            CSVRecord csvRecord = new CSVRecord();
            csvRecord.setAddress((String) r.get("voter"));
            csvRecord.setAmount(((BigDecimal) r.get("reward_amount")).toBigInteger());
            return csvRecord;
        }).collect(Collectors.toList());

        ApiMerkleTree apiMerkleTree = MerkleTreeHelper.merkleTree(airdropId, records);

        apiMerkleTree.setAirDropId(airdropId);
        if (StringUtils.isEmpty(airdropFunctionAddress)) {
            throw new RuntimeException("Function address is null.");
        }
        apiMerkleTree.setFunctionAddress(airdropFunctionAddress);
        if (StringUtils.isEmpty(airdropTokenType)) {
            throw new RuntimeException("Token type is null.");
        }
        apiMerkleTree.setTokenType(airdropTokenType);
        apiMerkleTree.setOwnerAddress(airdropOwnerAddress);

        apiMerkleTree.setChainId(chainId);
        String json = JSON.toJSONString(apiMerkleTree, true);
        //System.out.println(json);
        process.setAirdropJson(json);
        voteRewardProcessRepository.save(process);
        voteRewardProcessRepository.flush();
        return apiMerkleTree;
    }

}
