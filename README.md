# timeline-mq
[![GitHub license](https://img.shields.io/github/license/mathcoder23/timeline-mq)](https://github.com/mathcoder23/timeline-mq/blob/main/LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/mathcoder23/timeline-mq)](https://github.com/mathcoder23/timeline-mq/stargazers)
[![Maven Central](https://img.shields.io/maven-central/v/com.pettyfox.timeline/timeline-mq-starter-redis.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.pettyfox.timeline/timeline-mq-starter-redis)

基于Java实现的。Timeline模式朋友圈、微博、消息推送、feed流、im通讯的抽象库，提供数据流间的分发功能、分发失败后重试，将抽象层、存储层分离提高系统灵活性、以及各种存储层的具体实现。

它的本质是一款消息队列，但是消息队列又过于抽象，因此我们将具体的某些业务进行抽离、加工、设计，转换为一类以消息队列为核心的业务实现方法。
# 状态
> 项目开发中，处于不稳定状态

# 功能特点
- 轻量级MQ，小到仅需要去下jar包便可以使用。或轻量级服务间消息队列通讯(无中间件、点对点通讯)。
- 专有化的消息队列。旨在解决某类特有消息队列模型下的业务流处理方法。

# 数据同步
## 场景描述1
在仓库A中用户1的信息实时的同步到仓库B、C...中。当用户1的信息在A仓中被修改、删除时也会实时同步。
数据流程如下：
1. 仓A新建用户1，数据同步至仓B，数据同步至仓C...

2. 仓A修改用户1，数据同步至仓B，数据同步至仓C...

3. 仓A删除用户1，数据同步至仓B，数据同步至仓C...

4. 仓A创建用户2，数据同步至仓B，数据同步至仓C...

上述数据流微观描述：仓A作为数据生成者。仓B、仓C...作为数据消费者。
- 数据单向流动
- 数据存在顺序性
- 生产者和消费者的数据被消息代理持久化，解耦生产或消费时的高可用处理逻辑。
- 生产者和消费者关系解耦，实时的增删生产者与消费者关系

# 使用指南
## Maven集成
```xml
<dependency>
    <groupId>com.pettyfox.timeline</groupId>
    <artifactId>timeline-mq-starter-redis</artifactId>
    <version>1.0.3</version>
</dependency>
```
