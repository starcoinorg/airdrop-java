package org.starcoin.airdrop.data.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigInteger;

@Entity
@DiscriminatorValue("VoteChangedEvent")
public class StarcoinVoteChangedEvent extends StarcoinEvent {

    @Column
    private Long proposalId;

    @Column(length = 34)
    private String proposer;

    @Column(length = 34)
    private String voter;

    @Column(precision = 50, scale = 0)
    private BigInteger voteAmount;

    @Column
    private Boolean isAgreeVote;

    @Column
    private Long voteTimestamp;

    public Long getProposalId() {
        return this.proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public String getProposer() {
        return this.proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
    }

    public String getVoter() {
        return this.voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }

    public BigInteger getVoteAmount() {
        return this.voteAmount;
    }

    public void setVoteAmount(BigInteger voteAmount) {
        this.voteAmount = voteAmount;
    }

    public Long getTimestamp() {
        return this.voteTimestamp;
    }

    public Boolean getAgreeVote() {
        return isAgreeVote;
    }

    public void setAgreeVote(Boolean agreeVote) {
        isAgreeVote = agreeVote;
    }

    public Long getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(Long voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }

    @Override
    public String toString() {
        return "StarcoinVoteChangedEvent{" +
                "proposalId=" + proposalId +
                ", proposer='" + proposer + '\'' +
                ", voter='" + voter + '\'' +
                ", voteAmount=" + voteAmount +
                ", isAgreeVote=" + isAgreeVote +
                ", voteTimestamp=" + voteTimestamp +
                '}' +
                " is " + super.toString();
    }
}
