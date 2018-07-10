package com.example;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.net.UnknownHostException;
import java.sql.SQLException;

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
     * 这里使用CommandLineRunner举例，有了@EnableTask注解，容器中的'*Runner'Bean会被spring cloud task发现并作为任务执行
     *
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
     *
     * @see org.springframework.cloud.task.repository.TaskExecution
     */
    public static class HelloWorldCommandLineRunner implements CommandLineRunner {

        /**
         * 任务执行体
         *
         * @param args task的参数，即启动SpringBoot时传奇给jvm的参数，就是main方法的参数
         * @throws Exception 任务抛出的异常，可以被spring cloud task映射为退出码(exit code)，无任何异常，退出码为0，有异常根据
         *                   ExitCodeExceptionMapper这一接口对异常和退出码做映射
         */
        @Override
        public void run(String... args) throws Exception {

            System.out.println(this.getClass().getName()+ "运行了");
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