package org.starcoin.airdrop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.starcoin.airdrop.config.StarcoinChainConfig;
import org.starcoin.airdrop.data.model.VoteRewardProcess;

import java.util.List;

@Service
public class StarcoinProposalService {

    @Value("${starcoin.get-last-proposal-list-url}")
    private String getLastProposalListUrl;

    @Value("${starcoin.vote-reward-name-prefix-format}")
    private String voteRewardNamePrefixFormat;


    @Value("${starcoin.vote-reward-name-en-prefix-format}")
    private String voteRewardNameEnPrefixFormat;

    @Autowired
    private StarcoinChainConfig.ChainSettings chainSettings;

    @Autowired
    private RestTemplate restTemplate;

    private static boolean indicatesSuccess(ProposalPageResult result) {
        //"code": "SUCCESS",
        return "SUCCESS".equalsIgnoreCase(result.code);
    }

    /**
     * New VoteRewardProcess instance using proposal properties. Like this:
     * {
     * "proposalId": 29,
     * "proposer": "0x0000000000000000000000000a550c18",
     * "name": "TEST-barnard-101",
     * "chainId": 251,
     * "voteStartTimestamp": 1609602653000,
     * "voteEndTimestamp": 1631807453000,
     * "onChainDisabled": true
     * }
     *
     * @param proposal proposal info.
     * @return created VoteRewardProcess.
     */
    public VoteRewardProcess newVoteRewardProcess(Proposal proposal, boolean onChainDisabled) {
        long proposalProcessSeqNumber = onChainDisabled ? System.currentTimeMillis() : VoteRewardProcess.DEFAULT_PROPOSAL_PROCESS_SEQ_NUMBER;
        VoteRewardProcess voteRewardProcess = new VoteRewardProcess();
        voteRewardProcess.setProposalId(Long.parseLong(proposal.idOnChain));
        voteRewardProcess.setProposer(proposal.creator);
        String testNamePrefix = "TEST_" + proposalProcessSeqNumber + "_";
        String name = (onChainDisabled ? testNamePrefix : "")
                + String.format(this.voteRewardNamePrefixFormat, proposal.idOnChain) + proposal.title;
        if (name.length() > VoteRewardProcess.MAX_NAME_LENGTH) {
            name = name.substring(0, VoteRewardProcess.MAX_NAME_LENGTH - 3) + "...";
        }
        String nameEn = (onChainDisabled ? testNamePrefix : "")
                + String.format(this.voteRewardNameEnPrefixFormat, proposal.idOnChain) + proposal.titleEn;
        if (nameEn.length() > VoteRewardProcess.MAX_NAME_EN_LENGTH) {
            nameEn = nameEn.substring(0, VoteRewardProcess.MAX_NAME_LENGTH - 3) + "...";
        }
        voteRewardProcess.setName(name);
        voteRewardProcess.setNameEn(nameEn);
        voteRewardProcess.setChainId(this.chainSettings.getChainId());
        voteRewardProcess.setVoteStartTimestamp(proposal.onChainStartTime);
        voteRewardProcess.setVoteEndTimestamp(proposal.onChainEndTime);
        voteRewardProcess.setOnChainDisabled(onChainDisabled);
        voteRewardProcess.setProposalProcessSeqNumber(proposalProcessSeqNumber);
        return voteRewardProcess;
    }

    public Proposal getProposalByIdOnChain(String idOnChain) {
        ProposalPageResult pageResult = getLastProposalList();
        if (!indicatesSuccess(pageResult)) {
            throw new RuntimeException("Get last proposal list error." + pageResult);
        }
        Proposal proposal = pageResult.data.list.stream().filter(p -> idOnChain.equals(p.idOnChain)).findFirst().orElse(null);
        if (proposal == null) {
            throw new RuntimeException("Cannot get proposal by id(on-chain): " + idOnChain);
        }
        if (!this.chainSettings.getNetwork().equalsIgnoreCase(proposal.network)) {
            throw new RuntimeException("Find proposal not in same network. " + this.chainSettings.getNetwork() + " <> " + proposal.network);
        }
        return proposal;
    }

    public ProposalPageResult getLastProposalList() {
        ProposalPageResult result = restTemplate.getForObject(getLastProposalListUrl, ProposalPageResult.class);
        //System.out.println(result);
        return result;
    }

    public static class ProposalPageResult {
        public String code;
        public String message;
        public Data data;

        @Override
        public String toString() {
            return "ProposalPageResult{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }

        public static class Data {
            public Integer totalPage;
            public Integer currentPage;
            public Integer totalElements;
            public List<Proposal> list;

            @Override
            public String toString() {
                return "Data{" +
                        "totalPage=" + totalPage +
                        ", currentPage=" + currentPage +
                        ", totalElements=" + totalElements +
                        ", list=" + list +
                        '}';
            }
        }
    }

    public static class Proposal {
        public Long id;
        public String network;
        public String idOnChain;
        public String title;
        public String titleEn;
        public String description;
        public String descriptionEn;
        public String creator;
        public Long againstVotes;
        public Long forVotes;
        public Long quorumVotes;
        public Long onChainStartTime;
        public Long onChainEndTime;
        public String link;
        public String typeArgs1;
        public Long endTime;
        public Integer status;
        public Long createdAt;
        public Long updatedAt;
        public Long deletedAt;

        @Override
        public String toString() {
            return "Proposal{" +
                    "id=" + id +
                    ", network='" + network + '\'' +
                    ", idOnChain=" + idOnChain +
                    ", title='" + title + '\'' +
                    ", titleEn='" + titleEn + '\'' +
                    ", description='" + description + '\'' +
                    ", descriptionEn='" + descriptionEn + '\'' +
                    ", creator='" + creator + '\'' +
                    ", againstVotes=" + againstVotes +
                    ", forVotes=" + forVotes +
                    ", quorumVotes=" + quorumVotes +
                    ", onChainStartTime=" + onChainStartTime +
                    ", onChainEndTime=" + onChainEndTime +
                    ", link='" + link + '\'' +
                    ", typeArgs1='" + typeArgs1 + '\'' +
                    ", endTime=" + endTime +
                    ", status=" + status +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    ", deletedAt=" + deletedAt +
                    '}';
        }
    }
    /**
     * {
     *   "code": "SUCCESS",
     *   "message": "SUCCESS",
     *   "data": {
     *     "totalPage": 1,
     *     "currentPage": 0,
     *     "totalElements": 7,
     *     "list": [
     *       {
     *         "id": 6,
     *         "network": "main",
     *         "idOnChain": "6",
     *         "title": "将链上 Move 字节码版本配置升级到 v3",
     *         "titleEn": "Upgrade the on-chain configuration of Move bytecode version to v3",
     *         "description": "本提案提议将将链上 Move 字节码版本配置升级到 v3。\n\n\n\n\n\n\n\n升级提案信息\n\n\n\n提案交易: https://stcscan.io/main/transactions/detail/0x3442232605e31ef8bc43fbdd80447938c0e467ce9e3b5870ed0374a97a778eee\n\n\n\n提案发起人：0xbfc6a0138b5a596c8080303c32160119\n\n\n\n提案 id: 6",
     *         "descriptionEn": "This proposal will upgrade the on-chain configuration of Move bytecode version to v3.\n\n\n\n\n\n\n\nUpgrade Proposal Information\n\n\n\nProposal Transaction: https://stcscan.io/main/transactions/detail/0x3442232605e31ef8bc43fbdd80447938c0e467ce9e3b5870ed0374a97a778eee\n\n\n\nProposer: 0xbfc6a0138b5a596c8080303c32160119\n\n\n\nProposal id: 6",
     *         "creator": "0xbfc6a0138b5a596c8080303c32160119",
     *         "againstVotes": 802000000000,
     *         "forVotes": 8184292970000000,
     *         "link": "https://github.com/starcoinorg/starcoin/discussions/2913",
     *         "typeArgs1": "0x1::OnChainConfigDao::OnChainConfigUpdate<0x1::LanguageVersion::LanguageVersion>",
     *         "endTime": 1633056057653,
     *         "status": 7,
     *         "createdAt": null,
     *         "updatedAt": null,
     *         "deletedAt": null
     *       }
     *     ]
     *   }
     * }
     */
}
