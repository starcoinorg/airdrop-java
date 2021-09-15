package org.starcoin.airdrop.service;

import com.novi.serde.DeserializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.starcoin.airdrop.data.model.StarcoinVoteChangedEvent;
import org.starcoin.airdrop.data.repo.StarcoinEventRepository;
import org.starcoin.airdrop.utils.IdUtils;

import java.io.IOException;
import java.util.List;

@Service
public class StarcoinVoteChangedEventService {
    private static final Logger LOG = LoggerFactory.getLogger(StarcoinVoteChangedEventService.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private StarcoinEventRepository starcoinEventRepository;

    public void findESEventsAndSave(Long proposalId, String proposer, long fromTimestamp, long toTimestamp) {
        List<ElasticSearchService.TransactionVoteChangedEvent> esEvents;
        try {
            esEvents = elasticSearchService.findTransactionEventsByProposalIdAndProposer(proposalId, proposer, fromTimestamp, toTimestamp);
        } catch (IOException exception) {
            LOG.error("Find ES events error.", exception);
            return;
        } catch (DeserializationError deserializationError) {
            LOG.error("Find ES events error.", deserializationError);
            return;
        }
        for (ElasticSearchService.TransactionVoteChangedEvent es : esEvents) {
            StarcoinVoteChangedEvent trg = new StarcoinVoteChangedEvent();
            ElasticSearchService.copyProperties(es, trg);
            trg.setEventId(IdUtils.generateEventId(trg));
            trg.setCreatedAt(System.currentTimeMillis());
            trg.setCreatedBy("admin");
            trg.setUpdatedAt(trg.getCreatedAt());
            trg.setUpdatedBy(trg.getCreatedBy());
            try {
                starcoinEventRepository.save(trg);
            } catch (org.springframework.dao.DataIntegrityViolationException dataIntegrityViolationException) {
                LOG.info("Insert violated record.", dataIntegrityViolationException);
                //continue;
            }
        }
    }

}
