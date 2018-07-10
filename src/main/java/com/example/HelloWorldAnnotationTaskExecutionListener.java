package com.example;

import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;
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
