package org.starcoin.airdrop.service;

import com.alibaba.fastjson.JSON;
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
import org.starcoin.types.Ed25519PrivateKey;
import org.starcoin.utils.SignatureUtils;
import studio.wormhole.quark.command.alma.airdrop.ApiMerkleTree;
import studio.wormhole.quark.command.alma.airdrop.CSVRecord;
import studio.wormhole.quark.command.alma.airdrop.MerkleTreeHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MerkleTreeService {
    private static final Logger LOG = LoggerFactory.getLogger(MerkleTreeService.class);

    private final String airdropOwnerPrivateKey;

    private final String airdropOwnerAddress;

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

    public MerkleTreeService(@Value("${starcoin.airdrop.owner-private-key}") String airdropOwnerPrivateKey) {
        this.airdropOwnerPrivateKey = airdropOwnerPrivateKey;
        Ed25519PrivateKey privateKey = SignatureUtils.strToPrivateKey(this.airdropOwnerPrivateKey);
        //Ed25519PublicKey publicKey = SignatureUtils.getPublicKey(privateKey);
        this.airdropOwnerAddress = StarcoinAccountAddressUtils.getAddressFromPrivateKey(privateKey);
        LOG.info("Airdrop owner address: " + this.airdropOwnerAddress);
    }

    public void createAirdropMerkleTreeAndUpdateOnChain(Long processId, Long airdropId) {
        ApiMerkleTree apiMerkleTree = createAirdropMerkleTree(processId, airdropId);
        BigInteger amount = apiMerkleTree.getProofs().stream().map(s -> s.getAmount())
                .reduce((bigInteger, bigInteger2) -> bigInteger.add(bigInteger2)).get();
        //todo update on-chain.
        //        chainService.call_function(apiMerkleTree.getFunctionAddress() + "::MerkleDistributorScript::create",
        //                Lists.newArrayList(apiMerkleTree.getTokenType()),
        //                Lists.newArrayList(apiMerkleTree.getAirDropId() + "", apiMerkleTree.getRoot(), amount.toString(), apiMerkleTree.getProofs().size() + "")
        //        );

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