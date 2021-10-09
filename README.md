# Airdrop Java Services

以下举例说明的 URL，在使用时候请将其中的 `http://localhost:8787` 替换为实际的 BASE_URL，并且输入正确的参数值。

## API 说明

### 创建投票奖励处理流程

创建投票奖励处理流程时，需要注意填写正确的投票开始和结束时间戳（timestamp）。时间戳的值应为 epoch milliseconds。

示例：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":29,"proposer":"0x0000000000000000000000000a550c18","name":"TEST-barnard-101","chainId":251,"voteStartTimestamp":1609602653000,"voteEndTimestamp":1631807453000,"onChainDisabled":true}' \
"http://localhost:8787/v1/airdrops/voteRewardProcesses/"
```

参数说明：

* proposalId：提案 Id。
* proposer：提案账号地址。
* name：流程名称（也用作空投项目名称）。必须有唯一性，避免重复处理。
* chainId：链 Id。比如 barnard 测试链的 Id 是 251。
* voteStartTimestamp：投票开始的时间戳。用于过滤需要处理的投票事件，加快处理速度。
* voteEndTimestamp：投票结束的时间戳（很重要）。用于计算奖励金额。
* onChainDisabled：如果设置为 true （默认值为 true），则处理流程不包括将空投奖励数据提交上链的操作。不过此时操作者可以导出空投数据 JSON 文件（导出方法见下）后，使用文件中的参数信息手动调用链上的合约上链。

投票开始与结束时间参数也可以使用 ISO 8601 格式的日期时间字符串，代替时间戳。字符串中需要存在时区指示信息，比如 `Z` 表示 UTC 时间/零时区，`+08:00` 表示东八区/北京时间。

这是一个例子（注意，这里开始时间与结束时间的参数名称与使用时间戳的时候不一样，并且这里指定的时间值是东八区时间/北京时间）：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{"proposalId":5,"proposer":"0x3aaf92261930d73cdb24e0cc17e2807a","name":"TEST-main-5-2","chainId":1,"voteStartDateTime":"2021-09-09T14:50:00+08:00","voteEndDateTime":"2021-09-16T15:50:53+08:00","onChainDisabled":true}' \
"http://localhost:8787/v1/airdrops/voteRewardProcesses/"
```

参数说明：

* voteStartDateTime：投票结束时间字符串。必须是 ISO 8601 格式。注意，中国用户在 Web 前端页面上看到时间描述很**可能是北京时间**
  （比如：https://poll.starcoin.org/polls/detail/5?network=main）。
* voteEndDateTime：投票结束时间字符串。必须是 ISO 8601 格式。
* onChainDisabled：设置为 true 则处理流程不包括提交空投数据上链的操作。上面的示例设置为 true。

如果创建流程成功，在返回的结果中存在流程 Id（`processId`）。

#### 通过提案 Id 创建投票奖励处理流程

一个更简单的创建奖励处理流程的方法，通过 POST 提案 Id 创建处理流程：

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{}' "http://localhost:8787/v1/airdrops/createVoteRewardProcess?proposalId=6&onChainDisabled=true"
```

参数说明：

* proposalId：提案 Id。注意：这里的提案 Id 指的是链上的 Id（`idOnChain`）。
* onChainDisabled：设置为 true 则处理流程不包括提交空投数据上链的操作。如果不填写，默认值为 true。

注意：对于一个 proposalId，只允许创建一个执行上链操作（即 `onChainDisabled=false`）的处理流程，但可以创建多个不执行上链操作（`onChainDisabled=true`）的处理流程。

对于一个 proposalId，可以先创建不执行上链操作的处理流程，导出 CSV 和 JSON 文件，查看生成的奖励信息，确认无误后再创建会执行上链操作的处理流程（这样的流程只能创建一次，尝试重复创建会失败）。

### 查看处理流程

查看流程的处理状态：

```url
http://localhost:8787/v1/airdrops/voteRewardProcesses/{processId}
```

在处理流程执行完成之后（状态为 PROCESSED），即可以导出奖励 CSV 文件以及空投 JSON 文件。

### 导出投票奖励 CSV 文件

需要导出投票奖励 CSV 文件时，可以使用 HTTP GET 请求以下 URL：

```url
http://localhost:8787/v1/airdrops/exportRewardCsv?processId={processId}
```

### 导出空投信息 JSON 文件

可以导出生成的奖励空投信息 JSON 文件，通过 HTTP GET 方法请求以下 URL：

```url
http://localhost:8787/v1/airdrops/exportAirdropJson?processId={processId}
```

### Revoke 已上链的空投

通过输入 airdrop Id 与空投数据的 root hash 作为参数，revoke 已上链的空投。 例子（假设 airdrop Id 为 12）：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/airdrops/revokeOnChain?airdropId=12&root=0xbcc6b34299c01419d978fbd9ea8c61f37e6bc5e3e4e6c14b917946733bcc87b2"
```

查询参数说明：

* airdropId：空投 Id（空投项目 Id）。
* root：空投数据的根哈希值（root hash）。

也可以通过 process Id 来 revoke 链上的空投记录。例子（假设 process Id 为 8）：

```shell
curl -H "Content-Type: application/json" -X POST \
"http://localhost:8787/v1/airdrops/revokeOnChainByProcessId?processId=8"
```

查询参数说明：

* processId：处理流程 Id。

### 重置处理流程

重置出错的处理流程，使其可以被重新处理。

```shell
curl -H "Content-Type: application/json" -X POST \
-d '{}' "http://localhost:8787/v1/airdrops/resetVoteRewardProcess?processId=25"
```

查询参数说明：

* processId：处理流程 Id。

注意：该接口需要在排查出之前流程处理出错的原因（比如链上账户余额不足），解决问题（比如给链上账户充值）之后再调用。

### 更多 API 描述

见 Swagger UI：

```
http://localhost:8787/airdrop-api-doc/swagger-ui/index.html
```

## 参考信息

参考链接：

* ISO 8601：https://baike.baidu.com/item/ISO%208601/3910715

