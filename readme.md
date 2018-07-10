# SHORT LIVED MICROSERVICES
spring cloud task官方的的Overview是这样描述的:
> Spring Cloud Task allows a user to develop and run **short lived 
microservices** using Spring Cloud and run them locally, in the cloud, 
even on Spring Cloud Data Flow. Just add @EnableTask and run your app as a Spring Boot app (single application context).


从Overview可以看出，spring cloud task是用于创建短生命周期的微服务。这里关键是理解什么是短生命周期微服务，以下简称为短命微服务。所谓短命微服务是相对长命微服务来讲的。所谓长命微服务，即是指需要一直处于执行中、没有外力进程不会终止的微服务，需要一直处于监听状态，等待请求；或者说，长命微服务是指，如果程序运行正常，我们是不希望会终止的服务。而短命微服务，主要完成的是一个或几个任务，每个任务都有固定的步骤，而且会结束，不会一直占有线程/进程；当所有任务都结束，整个程序就结束了；或者说，短命微服务是指，程序在开始的时候，我们就期待它能够结束的服务。p.s. **short**在我的理解，其更重要的意思是说它会终止；第二，才是执行时间短，而且这个短也是相对长命微服务来说的。

那么Spring Cloud Task的出现，就是来解决Short Lived Microservice这种问题的。

可以举几个例子。
1. 定时执行的任务，比如每天夜里跑一次，跑完就可以停掉的任务：数据备份，垃圾清理，月末数据汇总，费率调整...
2. 临时的任务，需要用时就跑一次，比如数据分析

[Spring Cloud Task的Github仓库](https://github.com/spring-cloud/spring-cloud-task)的Description为：
> ![sdfsadf](http://docs.trustchain.com/download/attachments/2326915/spring-cloud-task-github.png)

> Short lived microservices with **Spring Batch**

Spring Batch是Spring的一个批处理框架。而批处理作业的一个原则就是**避免停不下来**，同时也是非交互性质的。(从这里也能看出Spring Cloud Task与Spring Batch配合使用，效果更佳。

**批处理简介**：在大型企业中，由于业务复杂、数据量大、数据格式不同、数据交互格式繁杂，并非所有的操作都能通过交互界面进行处理。而有一些操作需要定期读取大批量的数据，然后进行一系列的后续处理。这样的过程就是“批处理”。请见：[《Spring Batch批处理框架介绍》](https://juejin.im/entry/597e69a8f265da3e2b32f3f3)[《spring batch 在大型企业中的最佳实践》](https://juejin.im/entry/587838378d6d810058720a56) )

# 为什么使用Spring Cloud Task
所谓任务，无非是一段程序，写在main方法中不也能执行吗？那么Spring Cloud Task的优势在哪？
Spring Cloud Task的优势：
1. 提供了Task全生命周期的管理。(启动时要做什么，结束时要做什么，抛异常了怎么办；开始时间，结束时间，是否正常结束...)
2. 生命周期任务执行情况写入数据库，供之后分析，可追踪
3. 更容易集成Spring Security、Spring Actuator等Spring框架
4. 更容易集成已存在的Spring Batch工作

# Spring Cloud Task没有的能力
1. Spring Cloud Task没有调度自身任务的功能，要任务得到执行，需要手动执行，或使用调度框架或消息驱动框架，例如Spring Cloud Sream，Spring Cloud Deployer，Quartz，cron。
2. 任务与任务之间是独立的，没有编排能力


