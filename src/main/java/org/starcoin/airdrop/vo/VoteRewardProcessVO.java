package org.starcoin.airdrop.vo;

import org.starcoin.airdrop.data.model.VoteRewardProcess;

import java.time.ZonedDateTime;

public class VoteRewardProcessVO extends VoteRewardProcess {

    private ZonedDateTime voteStartDateTime;

    private ZonedDateTime voteEndDateTime;

    @Override
    public Long getVoteStartTimestamp() {
        if (super.getVoteStartTimestamp() == null && voteStartDateTime != null) {
            return voteStartDateTime.toInstant().toEpochMilli();
        }
        return super.getVoteStartTimestamp();
    }

    @Override
    public Long getVoteEndTimestamp() {
        if (super.getVoteEndTimestamp() == null && voteEndDateTime != null) {
            return voteEndDateTime.toInstant().toEpochMilli();
        }
        return super.getVoteEndTimestamp();
    }

    public ZonedDateTime getVoteStartDateTime() {
        return voteStartDateTime;
    }

    public void setVoteStartDateTime(ZonedDateTime voteStartDateTime) {
        this.voteStartDateTime = voteStartDateTime;
    }

    public ZonedDateTime getVoteEndDateTime() {
        return voteEndDateTime;
    }

    public void setVoteEndDateTime(ZonedDateTime voteEndDateTime) {
        this.voteEndDateTime = voteEndDateTime;
    }
}
