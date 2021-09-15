package org.starcoin.airdrop.api.controller;

import com.opencsv.CSVWriter;
import io.swagger.annotations.Api;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.starcoin.airdrop.data.model.VoteRewardProcess;
import org.starcoin.airdrop.data.repo.VoteRewardRepository;
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
    private VoteRewardRepository voteRewardRepository;//todo remove this

    @Resource
    private VoteRewardProcessService voteRewardProcessService;

    @GetMapping("voteRewardProcesses/{processId}")
    public VoteRewardProcess getVoteRewardProcess(@PathVariable("processId") Long processId) {
        return voteRewardProcessService.getVoteRewardProcess(processId);
    }

    @PostMapping("voteRewardProcesses")
    public VoteRewardProcess postVoteRewardProcess(@RequestBody VoteRewardProcess voteRewardProcess) {
        return voteRewardProcessService.createVoteRewardProcess(voteRewardProcess);
    }

    @GetMapping("exportRewardCsv")
    public void exportRewardCsv(HttpServletResponse response,
                                @RequestParam("proposalId") Long proposalId
    ) throws IOException {
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
        List<Map<String, Object>> rewards = voteRewardRepository.sumRewardAmountGroupByVoter(proposalId);
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
