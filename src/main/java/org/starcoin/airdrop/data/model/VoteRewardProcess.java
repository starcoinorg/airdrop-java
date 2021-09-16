package org.starcoin.airdrop.data.model;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "UniqueName", columnNames = {"name"})})
public class VoteRewardProcess {
    public static final int CHAIN_ID_MAIN = 1;//1: 'main'
    public static final int CHAIN_ID_PROXIMA = 2;//2: 'proxima'
    public static final int CHAIN_ID_BARNARD = 251;//251: 'barnard'
    public static final int CHAIN_ID_HALLEY = 251;//253: 'halley'

    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_PROCESSED = "PROCESSED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long processId;

    @Column(name = "proposal_id", nullable = false)
    private Long proposalId;

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

    @Version
    private Long version;

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
        return STATUS_PROCESSING.equalsIgnoreCase(this.getStatus())
                || STATUS_CREATED.equalsIgnoreCase(this.getStatus());
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
        this.message = message;
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
}
