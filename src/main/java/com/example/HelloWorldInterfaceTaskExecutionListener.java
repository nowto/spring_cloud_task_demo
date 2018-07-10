package com.example;

import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

/**
 * 任务执行生命周期的监听器，可以针对某个生命周期做些事情
 *
 * 如果一个事件的某个监听器抛出了异常，则后续该事件的其他监听器将不会执行
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
