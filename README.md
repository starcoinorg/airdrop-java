# Airdrop Java Services

以下举例说明的 URL，在使用时候请将其中的 `http://localhost:8787` 替换为实际的 BASE_URL，并且输入正确的参数值。

## API 说明

### 创建投票奖励处理流程

创建投票奖励处理流程时，需要注意填写正确的投票开始和结束时间戳。时间戳的值应为 epoch milliseconds。

示例：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":29,"proposer":"0x0000000000000000000000000a550c18","name":"TEST-barnard-101","chainId":251,"voteStartTimestamp":1609602653000,"voteEndTimestamp":1631807453000}' \
"http://localhost:8787/v1/voteRewardProcesses/"
```

参数说明：

* proposalId：提案 Id。
* proposer：提案账号地址。
* name：流程名称（也用作空投项目名称）。必须有唯一性，避免重复处理。
* chainId：链 Id。比如 barnard 测试链的 Id 是 251。
* voteStartTimestamp：投票开始时间。用于过滤需要处理的投票事件，加快处理速度。
* voteEndTimestamp：投票结束时间（很重要）。用于计算奖励金额。
* onChainDisabled：设置为 true（示例见下）时，则处理流程不包括将空投奖励数据提交上链的操作。操作者可以导出空投数据 JSON 文件（导出方法见下）后手动上链。

投票开始与结束时间参数也可以使用 ISO 8601 格式的日期时间字符串。字符串中需要存在时区指示信息，比如 `Z` 表示 UTC 时间/零时区。 例子：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":29,"proposer":"0x0000000000000000000000000a550c18","name":"TEST-barnard-112","chainId":251,"voteStartDateTime":"2021-01-02T15:50:53Z","voteEndDateTime":"2021-09-16T15:50:53Z","onChainDisabled":true}' \
"http://localhost:8787/v1/voteRewardProcesses/"
```

参数说明：

* voteStartDateTime：投票结束时间字符串。必须是 ISO 8601 格式。
* voteEndDateTime：投票结束时间字符串。必须是 ISO 8601 格式。
* onChainDisabled：设置为 true 则处理流程不包括上链操作。

如果创建流程成功，在返回的结果中存在流程 Id（`processId`）。

### 查看处理流程

查看流程的处理状态：

```url
http://localhost:8787/v1/voteRewardProcesses/{processId}
```

在处理流程执行完成之后（状态为 PROCESSED），即可以导出奖励 CSV 文件以及空投 JSON 文件。

### 导出投票奖励 CSV 文件

需要导出投票奖励 CSV 文件时，可以使用 HTTP GET 请求以下 URL：

```url
http://localhost:8787/v1/exportRewardCsv?processId={processId}
```

### 导出空投信息 JSON 文件

可以导出生成的奖励空投信息 JSON 文件，通过 HTTP GET 方法请求以下 URL：

```url
http://localhost:8787/v1/exportAirdropJson?processId={processId}
```

### Revoke 已上链的空投

通过输入 airdrop Id 与空投数据的 root hash 作为参数，revoke 已上链的空投。 例子（假设 airdrop Id 为 12）：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/revokeOnChain?airdropId=12&root=0xbcc6b34299c01419d978fbd9ea8c61f37e6bc5e3e4e6c14b917946733bcc87b2"
```

查询参数说明：

* airdropId：空投 Id（空投项目 Id）。
* root：空投数据的根哈希值（root hash）。

也可以通过 process Id 来 revoke 链上的空投记录。例子（假设 process Id 为 8）：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/revokeOnChainByProcessId?processId=8"
```

查询参数说明：

* processId：处理流程 Id。


### 更多 API 描述

见 Swagger UI：

```
http://localhost:8787/swagger-ui/index.html
```


## 参考信息

参考链接：

* ISO 8601：https://baike.baidu.com/item/ISO%208601/3910715

