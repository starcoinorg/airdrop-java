package org.starcoin.airdrop.service;

import com.alibaba.fastjson.JSON;
import com.novi.serde.DeserializationError;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.utils.HexUtils;
import org.starcoin.bean.EventFull;
import org.starcoin.types.event.VoteChangedEvent;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchService {
    public static final String TRANSACTION_EVENT_INDEX = "txn_events";

    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchService.class);

    private final RestHighLevelClient client;

    private final String transactionEventIndexPrefix;

    @Autowired
    public ElasticSearchService(RestHighLevelClient client,
                                @Value("${elasticsearch.transaction-event-index-prefix}") String transactionEventIndexPrefix) {
        this.client = client;
        this.transactionEventIndexPrefix = transactionEventIndexPrefix;
    }

    public static void copyProperties(TransactionVoteChangedEvent src, StarcoinVoteChangedEvent trg) {
        trg.setBlockHash(src.event.getBlockHash());
        trg.setBlockNumber(new BigInteger(src.event.getBlockNumber()));
        trg.setTransactionHash(src.event.getTransactionHash());
        trg.setTransactionIndex(BigInteger.valueOf(src.event.getTransactionIndex()));
        trg.setEventKey(src.event.getEventKey());
        trg.setEventSequenceNumber(new BigInteger(src.event.getEventSeqNumber()));
        trg.setTypeTag(src.event.getTypeTag());
        trg.setData(src.event.getData());
        trg.setProposalId(src.getProposalId());
        trg.setProposer(src.getProposer());
        trg.setVoter(src.getVoter());
        trg.setVoteAmount(src.getVoteAmount());
        trg.setAgreeVote(src.getAgree());
        trg.setVoteTimestamp(src.getTimestamp());
    }

    public List<TransactionVoteChangedEvent> findTransactionEventsByProposalIdAndProposer(Long proposalId,
                                                                                          String proposer,
                                                                                          long fromTimestamp,
                                                                                          long toTimestamp) throws IOException, DeserializationError {
        String transactionEventIndex = transactionEventIndexPrefix
                + (transactionEventIndexPrefix.endsWith(".") ? "" : ".") + TRANSACTION_EVENT_INDEX;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.matchQuery("tag_name", "VoteChangedEvent"));
        boolQuery.must(QueryBuilders.rangeQuery("timestamp").from(fromTimestamp).to(toTimestamp));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.size(50);
        SearchRequest searchRequest = new SearchRequest(transactionEventIndex);
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));
        searchRequest.scroll(scroll).source(searchSourceBuilder);
        List<TransactionVoteChangedEvent> result = new ArrayList<>();
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        while (searchResponse.getHits() != null && searchResponse.getHits().getHits().length > 0) {
            result.addAll(filterAndConvertSearchHits(searchResponse, proposalId, proposer));
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        if (LOG.isDebugEnabled()) {
            LOG.debug("RestHighLevelClient clearScroll: " + clearScrollResponse.isSucceeded());
        }
        return result;
    }

    private List<TransactionVoteChangedEvent> filterAndConvertSearchHits(SearchResponse searchResponse,
                                                                         Long proposalId, String proposerStr) throws DeserializationError {
        SearchHit[] searchHit = searchResponse.getHits().getHits();
        List<TransactionVoteChangedEvent> transactions = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            EventFull event = JSON.parseObject(hit.getSourceAsString(), EventFull.class);
            byte[] voteBytes = HexUtils.hexToByteArray(event.getData());
            VoteChangedEvent data = VoteChangedEvent.bcsDeserialize(voteBytes);
            // byte[] proposerBytes = CommonUtils.hexToByteArray(proposerStr);
            // AccountAddress proposer = AccountAddress.bcsDeserialize(proposerBytes);
            if (!data.proposal_id.equals(proposalId) || !HexUtils.byteListToHexWithPrefix(data.proposer.value).equalsIgnoreCase(proposerStr)) {//!data.proposer.equals(proposer)) {
                continue;
            }
            transactions.add(new TransactionVoteChangedEvent(event, data));
        }
        return transactions;
    }

    public static class TransactionVoteChangedEvent {
        private final EventFull event;
        private final VoteChangedEvent voteChangedEvent;

        public TransactionVoteChangedEvent(EventFull event, VoteChangedEvent voteChangedEvent) {
            this.event = event;
            this.voteChangedEvent = voteChangedEvent;
        }

        public Long getProposalId() {
            return this.voteChangedEvent.proposal_id;
        }

        public String getProposer() {
            return HexUtils.byteListToHexWithPrefix(this.voteChangedEvent.proposer.value);
        }

        public String getVoter() {
            return HexUtils.byteListToHexWithPrefix(this.voteChangedEvent.voter.value);
        }

        public BigInteger getVoteAmount() {
            return this.voteChangedEvent.vote;
        }

        public Boolean getAgree() {
            return this.voteChangedEvent.agree;
        }

        public Long getTimestamp() {
            return this.event.getTimestamp();
        }

        @Override
        public String toString() {
            return "TransactionVoteChangedEvent{" +
                    "event=" + event +
                    ", voteChangedEvent=" + voteChangedEvent +
                    '}';
        }
    }
}
