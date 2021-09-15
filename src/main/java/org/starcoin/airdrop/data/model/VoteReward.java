package org.starcoin.airdrop.data.model;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(indexes = {
        @Index(name = "IdxPrpIdVoterTime", columnList = "proposal_id, voter, vote_timestamp")
})
public class VoteReward {

    @Id
    @Column(length = 66, nullable = false)
    private String eventId;

    @Column(name = "proposal_id")
    private Long proposalId;

    @Column(name = "voter", length = 34)
    private String voter;

    @Column(precision = 50, scale = 0)
    private BigInteger rewardAmount;

//    @Column(length = 34)
//    private String proposer;

    @Column(precision = 50, scale = 0)
    private BigInteger voteAddedAmount;

    @Column(precision = 50, scale = 0)
    private BigInteger voteAmount;

    @Column(precision = 50, scale = 0)
    private BigInteger rewardVoteAmount;

//    @Column
//    private Boolean isAgreeVote;

    @Column(name = "vote_timestamp")
    private Long voteTimestamp;

    @Column(length = 70, nullable = false)
    private String createdBy;

    @Column(length = 70, nullable = false)
    private String updatedBy;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    /**
     * 是否已不再使用。
     */
    @Column(nullable = false)
    private Boolean deactived = false;

    @Version
    private Long version;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public BigInteger getVoteAddedAmount() {
        return voteAddedAmount;
    }

    public void setVoteAddedAmount(BigInteger voteAddedAmount) {
        this.voteAddedAmount = voteAddedAmount;
    }

    public Long getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(Long voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }

    public BigInteger getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigInteger rewardAmount) {
        this.rewardAmount = rewardAmount;
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

    public BigInteger getVoteAmount() {
        return voteAmount;
    }

    public void setVoteAmount(BigInteger voteAmount) {
        this.voteAmount = voteAmount;
    }

    public BigInteger getRewardVoteAmount() {
        return rewardVoteAmount;
    }

    public void setRewardVoteAmount(BigInteger rewardVoteAmount) {
        this.rewardVoteAmount = rewardVoteAmount;
    }

    public Boolean getDeactived() {
        return deactived;
    }

    public void setDeactived(Boolean deactived) {
        this.deactived = deactived;
    }
}
