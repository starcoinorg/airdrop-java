# Airdrop Java Services

创建投票奖励处理流程：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":0,"voteStartTimestamp":0,"voteEndTimestamp":1623142000000}' \
"http://localhost:8787/v1/voteRewardProcesses/"
```

返回的结果中有流程 Id（processId）。

查看处理流程：

```url
http://localhost:8787/v1/voteRewardProcesses/{processId}
```

在处理流程已经完成之后（状态为 PROCCESED），可以导出奖励 CSV 文件。

导出奖励 CSV 文件，请求 URL：

```url
http://localhost:8787/v1/exportRwardCsv?proposalId=0
```
