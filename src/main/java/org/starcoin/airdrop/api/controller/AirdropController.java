package org.starcoin.airdrop.api.controller;

import com.opencsv.CSVWriter;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.service.AirdropMerkleDistributionService;
import org.starcoin.airdrop.service.VoteRewardProcessService;
import org.starcoin.airdrop.service.VoteRewardService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = {"Airdrop Java RESTful API"})
@RestController
@RequestMapping("v1")
public class AirdropController {

    @Resource
    private VoteRewardService voteRewardService;

    @Resource
    private VoteRewardProcessService voteRewardProcessService;

    @Resource
    private AirdropMerkleDistributionService airdropMerkleDistributionService;

    @GetMapping("voteRewardProcesses/{processId}")
    public VoteRewardProcess getVoteRewardProcess(@PathVariable("processId") Long processId) {
        return voteRewardProcessService.getVoteRewardProcess(processId);
    }

    /**
     * Revoke on-chain.
     *
     * @param airdropId airdrop Id.
     * @param root      root hash.
     * @return transaction hash.
     */
    @PostMapping("revokeOnChain")
    public String revokeOnChain(@RequestParam("airdropId") Long airdropId, @RequestParam("root") String root) {
        return airdropMerkleDistributionService.revokeOnChain(airdropId, root);
    }

    /**
     * Revoke on-chain.
     *
     * @param processId process Id.
     * @return transaction hash.
     */
    @PostMapping("revokeOnChainByProcessId")
    public String revokeOnChain(@RequestParam("processId") Long processId) {
        return airdropMerkleDistributionService.revokeOnChain(processId);
    }

    @PostMapping("voteRewardProcesses")
    public VoteRewardProcess postVoteRewardProcess(@RequestBody VoteRewardProcess voteRewardProcess) {
        return voteRewardProcessService.createVoteRewardProcess(voteRewardProcess);
    }

    @GetMapping("exportRewardCsv")
    public void exportRewardCsv(HttpServletResponse response,
                                @RequestParam("processId") Long processId
    ) throws IOException {
        VoteRewardProcess voteRewardProcess = voteRewardProcessService.findByIdOrElseThrow(processId);
        if (voteRewardProcess.isProcessing()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Long proposalId = voteRewardProcess.getProposalId();

        // set file name and content type
        String filename = "rewards.csv";
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"");
        Writer writer = response.getWriter();
        // create a csv writer
        // header record
        String[] headerRecord = {"voter", "reward_amount"};
        // create a csv writer
        CSVWriter csvWriter = new CSVWriter(writer);
//                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
//                .withQuoteChar(CSVWriter.NO_QUOTE_CHARACTER)
//                .withEscapeChar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
//                .withLineEnd(CSVWriter.DEFAULT_LINE_END)
//                .build();
        List<Map<String, Object>> rewards = voteRewardService.sumRewardAmountGroupByVoter(proposalId);
        rewards.stream().map(m -> {
            List<String> cells = new ArrayList<>();
            for (String h : headerRecord) {
                cells.add(m.get(h).toString());
            }
            return cells.toArray(new String[0]);
        }).forEach(r -> csvWriter.writeNext(r));
        csvWriter.close();
        writer.close();
    }

}
