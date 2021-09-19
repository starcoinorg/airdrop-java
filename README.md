# Airdrop Java Services

以下举例说明的 URL，在使用时候请将其中的 `http://localhost:8787` 替换为实际的 BASE_URL。

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

在处理流程执行完成之后（状态为 PROCESSED），即可以导出奖励 CSV 文件。

需要导出投票奖励 CSV 文件时，可以使用 HTTP GET 请求以下 URL：

```url
http://localhost:8787/v1/exportRewardCsv?processId={processId}
```

通过输入 airdrop Id 与 root hash 作为参数，revoke 链上的空投记录：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/revokeOnChain?airdropId=12&root=0xbcc6b34299c01419d978fbd9ea8c61f37e6bc5e3e4e6c14b917946733bcc87b2"
```

或者可以通过 process Id 来 revoke 链上的空投记录：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/revokeOnChainByProcessId?processId=8"
```

更多 API 描述见：

```
http://localhost:8787/swagger-ui/index.html
```

