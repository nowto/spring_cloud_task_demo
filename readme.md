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
> 所以有必要先介绍一下：spring cloud task使用CommandLineRunner或ApplicationRunner定义任务的业务逻辑（以下统称为runner），但spring cloud task的task和runner不是对等的，并不是一个runner是一个task，而是整个Application是一个task。而一个Application可以定义任意多个runner。

> 更为有趣的是，runner接口定义在spring boot中，不在spring cloud task。即便只引入spring boot依赖，而不引入spring cloud task，启动spring boot，runner也能够得到执行。

> 可见runner的执行是spring boot完成的，那么spring cloud task的工作并不是执行runner，而是：

Spring Cloud Task的优势：
1. 提供了Task全生命周期的管理。(启动时要做什么，结束时要做什么，抛异常了怎么办；开始时间，结束时间，是否正常结束...)
2. 生命周期任务执行情况写入数据库，供之后分析，可追踪
3. 更容易集成Spring Security、Spring Actuator等Spring框架
4. 更容易集成已存在的Spring Batch工作

# Spring Cloud Task没有的能力
1. Spring Cloud Task没有调度自身任务的功能，要任务得到执行，需要手动执行，或使用调度框架或消息驱动框架，例如Spring Cloud Sream，Spring Cloud Deployer，Quartz，cron。
2. 任务与任务之间是独立的，没有编排能力

# DEMO创建
1. 创建SpingBoot项目
2. MAVEN依赖
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

        <modelVersion>4.0.0</modelVersion>

        <groupId>com.example</groupId>
        <artifactId>taskdemo</artifactId>
        <packaging>jar</packaging>
        <version>0.0.1-SNAPSHOT</version>

        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>1.5.2.RELEASE</version>
        </parent>

        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter</artifactId>
            </dependency>
            <!-- spring cloud task 依赖 -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-task</artifactId>
                <version>1.2.3.RELEASE</version>
            </dependency>
            <!-- 生命周期状态会写入数据库，所以需要数据库相关依赖 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jpa</artifactId>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
            </dependency>
        </dependencies>

        <!-- 引入spring cloud 依赖管理 -->
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-dependencies</artifactId>
                    <version>Finchley.RELEASE</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>

        <!-- spring boot插件，提供spring-boot:run等目标 -->
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </project>
    ```
1. 配置application.properties
    ```properties
    # task日志级别，开发阶段可以设为debug
    logging.level.org.springframework.cloud.task=DEBUG
    # 应用名称，默认任务会使用应用名称作为任务名
    spring.application.name=helloWorld

    # 如果需要将任务执行周期状态记录数据库，须配置数据源，否则只是记录到内存的一个Map
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.url=jdbc:mysql:///test
    #spring.jpa.database=MySQL
    #spring.jpa.show-sql=true
    #spring.jpa.generate-ddl=true
    #spring.jpa.hibernate.ddl-auto=update

    # 任务表表名前缀，可以不配，默认"TASK_"，如果需要同一数据库中多个任务表，必须配置该项
    # spring.cloud.task.tablePrefix=TASKDEMO_

    # 配置是让spring创建任务表，还是自己创建。为true是spring创建。默认为true
    spring.cloud.task.initialize.enable=true

    # 配置任务名称 默认是spring.application.name
    spring.cloud.task.name=helloworld

    # 用来配置同名task是否可以并发执行。如果为true，拒绝并发任务将会失败，失败信息为"Task with name "xxxx" is already running."
    # 默认值：false
    # 要使该配置生效，需添加如下依赖
    #<dependency>
    #    <groupId>org.springframework.integration</groupId>
    #    <artifactId>spring-integration-core</artifactId>
    #</dependency>
    #<dependency>
    #    <groupId>org.springframework.integration</groupId>
    #    <artifactId>spring-integration-jdbc</artifactId>
    #</dependency>
    # spring.cloud.task.singleInstanceEnabled=false
    ```

1. 编写Application
    ```java

    /**
     * SampleTask
     *
     * @EnableTask 注解开启SpringBoot对spring cloud task的支持
     */
    @SpringBootApplication
    @EnableTask
    public class SampleTask {

        public static void main(String[] args) {
            SpringApplication.run(SampleTask.class, args);
        }

        /**
         * 在spring cloud task中task的执行体，是CommandLineRunner或ApplicationRunner接口的实现，
         * 类似于Thread的执行体是Runnable接口的实现
         * <p>
         * 这里使用CommandLineRunner举例
         * Runner的执行顺序默认按照注册的顺序，如果希望不按照注册的顺序，可以加@Order注解
         * @return CommandLineRunner
         */
            @Bean
            @Order(2)
            public CommandLineRunner commandLineRunner() {
                return new CommandLineRunner() {
                    @Override
                    public void run(String... args) throws Exception {
                        System.out.println("CommandLineRunner运行了");
                    }
                };
            }

            @Bean
            @Order(1)
            public ApplicationRunner secondRunner() {
                return new ApplicationRunner() {
                    @Override
                    public void run(ApplicationArguments args) throws Exception {
                        System.out.println("ApplicationRunner运行了");
                    }
                };
            }

        /**
         * task 的实现
         * 任务的状态会被spring cloud task做记录
         * 每个任务包含如下这么多状态：
         * executionId: 执行id
         * parentExecutionId: 父task执行id
         * exitCode： 退出码
         * taskName: 任务名称
         * startTime： 开始时间
         * endTime： 结束时间
         * exitMessage： 退出消息
         * //TODO externalExecutionId： 暂时不懂
         * errorMessage： 错误消息
         * arguments： 参数
         * @see org.springframework.cloud.task.repository.TaskExecution
         */
        public static cleass HelloWorldCommandLineRunner implements CommandLineRunner {

            /**
             * 任务执行体
             *
             * @param args task的参数，即启动SpringBoot时传奇给jvm的参数，就是main方法的参数
             * @throws Exception 任务抛出的异常，可以被spring cloud task映射为退出码(exit code)，无任何异常，退出码为0，有异常根据
             *                   ExitCodeExceptionMapper这一接口对异常和退出码做映射
             */
            @Override
            public void run(String... args) throws Exception {

                System.out.println("Hello World!");
            }

        }

        /**
         * 将异常映射为退出码
         * NullPointerException
         * UnknownHostException
         * SqlException
         * 分别映射为1,2,3
         * 其他都为255
         * @return
         */
        @Bean
        public ExitCodeExceptionMapper exitCodeExceptionMapper() {
            return new ExitCodeExceptionMapper() {
                @Override
                public int getExitCode(Throwable exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof NullPointerException) {
                        return 1;
                    }
                    if (cause instanceof UnknownHostException) {
                        return 2;
                    }
                    if (cause instanceof SQLException) {
                        return 3;
                    }
                    return 255;
                }
            };
        }
    }
    ```
1. 可以监听task生命周期事件，有两种方式
    - 实现TaskExecutionListener
        ```java
        /**
         * 任务执行生命周期的监听器，可以针对某个生命周期做些事情
         *
         * 如果一个事件的某个监听器抛出了异常，则后续该事件的其他监听器将不会执行
         * TaskExecution会被存储数据库
         */
        @Component
        public class HelloWorldInterfaceTaskExecutionListener implements TaskExecutionListener {
            /**
             * 任务就要启动前
             * @param taskExecution
             */
            @Override
            public void onTaskStartup(TaskExecution taskExecution) {
                System.out.println("interface: task startup");
            }

            /**
             * 任务将要结束前
             * @param taskExecution
             */
            @Override
            public void onTaskEnd(TaskExecution taskExecution) {
                taskExecution.setExitMessage(taskExecution.getTaskName() + "完成了");
                System.out.println("interface: task end");
            }

            /**
             * 任务发生了未捕获的异常
             * @param taskExecution
             * @param throwable
             */
            @Override
            public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
                taskExecution.setErrorMessage(taskExecution.getTaskName() + "失败了： " + throwable.getMessage());
                System.out.println("interface: task failed");
            }
        }
        ```
    - 使用注解
        ```java
        /**
         * 任务执行生命周期的监听器
         * 与 <code>HelloWorldInterfaceTaskExecutionListener</code>
         * 完成功能相同，只不过是以注解的方式
         */
        @Component
        public class HelloWorldAnnotationTaskExecutionListener {
            /**
             * 任务就要启动前
             * @param taskExecution
             */
            @BeforeTask
            public void beforeTask(TaskExecution taskExecution) {
                System.out.println("annotation: before task");
            }

            /**
             * 任务将要结束前
             * @param taskExecution
             */
            @AfterTask
            public void afterTask(TaskExecution taskExecution) {
                System.out.println("annotation: after task");
            }

            /**
             * 任务发生了未捕获的异常
             * @param taskExecution
             * @param throwable
             */
            @FailedTask
            public void failedTask(TaskExecution taskExecution, Throwable throwable) {
                System.out.println("annotation: failed task");
            }
        }
        ```
2. 运行
    `mvn spring-boot:run` 或执行main方法
    输出日志：
    
    ![sdf](/log.png)
    
    从日志也可以看出，并不是每个runner的开始和结束之前打印监听器的监听语句，而是所有runner被视为一个task。    