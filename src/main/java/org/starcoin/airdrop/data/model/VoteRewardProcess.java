package org.starcoin.airdrop.data.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueName", columnNames = {"name"}),
        @UniqueConstraint(name = "UniqueProposalAndSeqNum", columnNames = {"proposal_id", "proposal_process_seq_number"})
})
public class VoteRewardProcess {

    public static final Long DEFAULT_PROPOSAL_PROCESS_SEQ_NUMBER = 0L;

    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_NAME_EN_LENGTH = 100;

    public static final int MAX_MESSAGE_LENGTH = 255;

    public static final int CHAIN_ID_MAIN = 1;//1: 'main'
    public static final int CHAIN_ID_PROXIMA = 2;//2: 'proxima'
    public static final int CHAIN_ID_BARNARD = 251;//251: 'barnard'
    public static final int CHAIN_ID_HALLEY = 253;//253: 'halley'

    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_ERROR = "ERROR";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    @Column(name = "proposal_id", nullable = false)
    private Long proposalId;

    @Column(name = "proposal_process_seq_number")
    private Long proposalProcessSeqNumber;

    @Column(length = 34, nullable = false)
    private String proposer;

    @Column
    private Long voteStartTimestamp;

    @Column
    private Long voteEndTimestamp;

    @Column(length = 20)
    private String status = STATUS_CREATED;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @Column(length = 100)
    private String name;


    @Column(length = 100)
    private String nameEn;

    @Column(length = 255)
    private String description;

    @Column(length = 255)
    private String message;

    @Column
    private Integer chainId;

    @Lob
    @Column(columnDefinition = "LongText")
    @Basic(fetch = FetchType.LAZY)
    private String airdropJson;

    /**
     * Create airdrop on-chain transaction hash.
     */
    @Column(length = 66)
    private String onChainTransactionHash;

    @Column(length = 66)
    private String revokeOnChainTransactionHash;

    @Column
    private Boolean onChainDisabled;

    @Version
    private Long version;

    public Boolean getOnChainDisabled() {
        return onChainDisabled;
    }

    public void setOnChainDisabled(Boolean onChainDisabled) {
        this.onChainDisabled = onChainDisabled;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Long getVoteStartTimestamp() {
        return voteStartTimestamp;
    }

    public void setVoteStartTimestamp(Long voteStartTimestamp) {
        this.voteStartTimestamp = voteStartTimestamp;
    }

    public Long getVoteEndTimestamp() {
        return voteEndTimestamp;
    }

    public void setVoteEndTimestamp(Long voteEndTimestamp) {
        this.voteEndTimestamp = voteEndTimestamp;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public void processing() {
        this.setStatus(STATUS_PROCESSING);
    }

    public void processed() {
        this.setStatus(STATUS_PROCESSED);
    }

    public boolean isProcessing() {
        return STATUS_PROCESSING.equalsIgnoreCase(this.getStatus());//|| STATUS_CREATED.equalsIgnoreCase(this.getStatus());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null
                : (message.length() > MAX_MESSAGE_LENGTH ? message.substring(0, MAX_MESSAGE_LENGTH - 3) + "..." : message);
    }

    public String getAirdropJson() {
        return airdropJson;
    }

    public void setAirdropJson(String airdropJson) {
        this.airdropJson = airdropJson;
    }

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnChainTransactionHash() {
        return onChainTransactionHash;
    }

    public void setOnChainTransactionHash(String onChainTransactionHash) {
        this.onChainTransactionHash = onChainTransactionHash;
    }

    public String getRevokeOnChainTransactionHash() {
        return revokeOnChainTransactionHash;
    }

    public void setRevokeOnChainTransactionHash(String revokeOnChainTransactionHash) {
        this.revokeOnChainTransactionHash = revokeOnChainTransactionHash;
    }

    /**
     * Set status to error and message.
     *
     * @param message error message.
     */
    public void setStatusError(String message) {
        this.setStatus(STATUS_ERROR);
        this.setMessage(this.getMessage() != null && !this.getMessage().isEmpty()
                ? this.getMessage() + " " + message // if this.message is not null, append it.
                : message);
    }

    public void reset() {
        if (!STATUS_ERROR.equalsIgnoreCase(this.status)) {
            throw new RuntimeException("Process current status is NOT 'ERROR'.");
        }
        this.setStatus(STATUS_CREATED);
    }

    public Long getProposalProcessSeqNumber() {
        return proposalProcessSeqNumber;
    }

    public void setProposalProcessSeqNumber(Long proposalProcessSeqNumber) {
        this.proposalProcessSeqNumber = proposalProcessSeqNumber;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    @Override
    public String toString() {
        return "VoteRewardProcess{" +
                "processId=" + processId +
                ", proposalId=" + proposalId +
                ", proposalProcessSeqNumber=" + proposalProcessSeqNumber +
                ", proposer='" + proposer + '\'' +
                ", voteStartTimestamp=" + voteStartTimestamp +
                ", voteEndTimestamp=" + voteEndTimestamp +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", description='" + description + '\'' +
                ", message='" + message + '\'' +
                ", chainId=" + chainId +
                ", airdropJson='" + airdropJson + '\'' +
                ", onChainTransactionHash='" + onChainTransactionHash + '\'' +
                ", revokeOnChainTransactionHash='" + revokeOnChainTransactionHash + '\'' +
                ", onChainDisabled=" + onChainDisabled +
                ", version=" + version +
                '}';
    }

}
