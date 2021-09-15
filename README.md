# Airdrop Java Services

以下举例说明的 URL，在使用时候请将 `http://localhost:8787` 替换为实际的 BASE_URL。 

创建投票奖励处理流程（注意填写正确的投票开始时间和结束时间，时间值应为 epoch milliseconds）：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":0,"proposer":"0xb2aa52f94db4516c5beecef363af850a","voteStartTimestamp":1620000000000,"voteEndTimestamp":1623744459999}' \
"http://localhost:8787/v1/voteRewardProcesses/"
```

返回的结果中有流程 Id（processId）。

查看处理流程：

```url
http://localhost:8787/v1/voteRewardProcesses/{processId}
```

在处理流程执行完成之后（状态为 PROCCESED），即可以导出奖励 CSV 文件。

导出投票奖励 CSV 文件，请求以下 URL：

```url
http://localhost:8787/v1/exportRewardCsv?processId={processId}
```

