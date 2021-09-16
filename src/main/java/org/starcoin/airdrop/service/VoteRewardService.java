package org.starcoin.airdrop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.model.VoteReward;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoteRewardService {
    private static final Logger LOG = LoggerFactory.getLogger(VoteRewardService.class);

    private static final BigDecimal REWARD_APY = new BigDecimal("0.15");

    private static final long YEAR_SECONDS = 60L * 60 * 24 * 365;

    @Autowired
    private VoteRewardRepository voteRewardRepository;

    public static BigInteger getRewordAmount(BigInteger voteAmount, long voteTimestamp, long voteEndTimestamp) {
        return new BigDecimal(voteAmount)
                .multiply(REWARD_APY)
                .multiply(BigDecimal.valueOf((voteEndTimestamp - voteTimestamp) / 1000)
                        .divide(BigDecimal.valueOf(YEAR_SECONDS), 10, RoundingMode.HALF_UP))
                .toBigInteger();
    }

    public void calculateRewords(long proposalId, long voteEndTimestamp) {
        List<VoteReward> voteRewards = voteRewardRepository.findByProposalIdAndDeactivedIsFalse(proposalId);
        for (VoteReward v : voteRewards) {
            if (v.getDeactived()) {
                continue;
            }
            BigInteger rewardAmount = getRewordAmount(v.getRewardVoteAmount(), v.getVoteTimestamp(), voteEndTimestamp);
            v.setRewardAmount(rewardAmount.compareTo(BigInteger.ZERO) > 0 ? rewardAmount : BigInteger.ZERO);
            v.setUpdatedAt(System.currentTimeMillis());
            v.setUpdatedBy("admin");
            voteRewardRepository.save(v);
        }
    }

    public void addOrUpdateVoteRewards(long proposalId, List<StarcoinVoteChangedEvent> voteChangedEvents) {
        Map<String, BigInteger> voterLastUpdateAmountMap = new HashMap<>();
        for (StarcoinVoteChangedEvent e : voteChangedEvents) {
            if (proposalId != e.getProposalId()) {
                throw new IllegalArgumentException("ProposalId NOT equals e.getProposalId()");
            }
            // ignore deactived events
            if (e.isDeactived()) {
                continue;
            }
            VoteReward v = voteRewardRepository.findById(e.getEventId()).orElse(null);
            if (v == null) {
                v = new VoteReward();
                v.setEventId(e.getEventId());
                v.setProposalId(e.getProposalId());
                v.setVoter(e.getVoter());
                v.setVoteTimestamp(e.getVoteTimestamp());
                v.setCreatedAt(System.currentTimeMillis());
                v.setCreatedBy("admin");
                v.setUpdatedAt(v.getCreatedAt());
                v.setUpdatedBy(v.getCreatedBy());
            } else {
                //LOG.debug("Update vote reward: " + e.getEventId());
                v.setUpdatedAt(System.currentTimeMillis());
                v.setUpdatedBy("admin");
            }
            v.setDeactived(false); // active it!
            v.setVoteAmount(e.getVoteAmount());
            v.setVoteAddedAmount(getVoteAddedAmount(voterLastUpdateAmountMap, e.getVoter(), e.getVoteAmount()));
            v.setRewardVoteAmount(v.getVoteAddedAmount()); // default rewardVoteAmount is voteAddedAmount
            voteRewardRepository.save(v);
        }

        // 一旦发生取出投票抵押的情况（即使只取出一小部分），之前投票（抵押本可以获得的）奖励都清零，
        // 需要从取出发生的时间点开始，以取出后的抵押余额，重新计算可获得的投票奖励。
        List<VoteReward> negativeAddedVotes = voteRewardRepository.findByProposalIdAndVoteAddedAmountLessThanOrderByVoteTimestamp(proposalId, BigInteger.ZERO);
        for (VoteReward n : negativeAddedVotes) {
            List<VoteReward> voterVotes = voteRewardRepository.findByProposalIdAndVoterOrderByVoteTimestamp(proposalId, n.getVoter());
            for (VoteReward v : voterVotes) {
                if (v.getVoteTimestamp().compareTo(n.getVoteTimestamp()) < 0) {
                    v.setRewardVoteAmount(BigInteger.ZERO);
                    voteRewardRepository.save(v);
                } else if (v.getVoteTimestamp().compareTo(n.getVoteTimestamp()) == 0) {
                    v.setRewardVoteAmount(v.getVoteAmount());
                    voteRewardRepository.save(v);
                }
            }
        }
    }

    /**
     * calculate vote amount added.
     */
    private BigInteger getVoteAddedAmount(Map<String, BigInteger> voterLastUpdateAmountMap, String voter, BigInteger amount) {
        if (voterLastUpdateAmountMap.containsKey(voter)) {
            BigInteger added = amount.subtract(voterLastUpdateAmountMap.get(voter));
            voterLastUpdateAmountMap.put(voter, amount);
            return added;
        } else {
            voterLastUpdateAmountMap.put(voter, amount);
            return amount;
        }
    }

    public List<Map<String, Object>> sumRewardAmountGroupByVoter(Long proposalId) {
        return voteRewardRepository.sumRewardAmountGroupByVoter(proposalId);
    }

    @Transactional
    public void adjustRewardsUnderLimit(Long proposalId, BigInteger currentTotalRewardAmount, BigInteger totalRewardAmountLimit) {
        List<VoteReward> voteRewards = voteRewardRepository.findByProposalIdAndDeactivedIsFalse(proposalId);
        for (VoteReward v : voteRewards) {
            if (v.getDeactived()) {
                continue;
            }
            BigInteger rewardAmount = v.getRewardAmount().multiply(totalRewardAmountLimit).divide(currentTotalRewardAmount);
            v.setRewardAmount(rewardAmount);
            v.setUpdatedAt(System.currentTimeMillis());
            v.setUpdatedBy("admin");
            voteRewardRepository.save(v);
        }
    }
}
