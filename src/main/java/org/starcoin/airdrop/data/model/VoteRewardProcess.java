package org.starcoin.airdrop.data.model;

import javax.persistence.*;

@Entity
public class VoteRewardProcess {
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

    @Column
    private String description;

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
}
