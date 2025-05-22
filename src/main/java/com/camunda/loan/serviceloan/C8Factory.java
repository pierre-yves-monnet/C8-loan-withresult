package com.camunda.loan.serviceloan;

import io.camunda.common.auth.*;
import io.camunda.executewithresult.executor.WithResultAPI;
import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.CamundaTaskListClientBuilder;
import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Configuration
@Component
public class C8Factory {
    Logger logger = LoggerFactory.getLogger(C8Factory.class.getName());
    ZeebeClient zeebeClient;
    WithResultAPI withResultAPI;
    private CamundaTaskListClient taskClient;
    @Value("${camunda.client.mode}")
    private String mode;
    @Value("${camunda.client.tasklist.rest-address}")
    private String taskListUrl;
    @Value("${camunda.client.tasklist.user-name}")
    private String taskListUserName;
    @Value("${camunda.client.tasklist.user-password:}")
    private String taskListUserPassword;

    C8Factory(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostConstruct
    public void init() {
        connectionTaskList();
    }

    public ZeebeClient getZeebeClient() {
        return zeebeClient;
    }

    public CamundaTaskListClient getTaskListClient() {
        return taskClient;
    }

    private boolean connectionTaskList() {
        try {
            logger.info("------------------ Connection to taskList Url[{}] user[{}] password[{}]", taskListUrl, taskListUserName, taskListUserPassword);

            CamundaTaskListClientBuilder taskListBuilder = CamundaTaskListClient.builder();
            SimpleConfig simpleConf = new SimpleConfig();
            simpleConf.addProduct(Product.TASKLIST,
                    new SimpleCredential(taskListUrl, taskListUserName, taskListUserPassword));
            Authentication auth = SimpleAuthentication.builder().withSimpleConfig(simpleConf).build();

            taskListBuilder.taskListUrl(taskListUrl).authentication(auth).cookieExpiration(Duration.ofSeconds(500));
            logger.info("Connection to TaskList");
            taskClient = taskListBuilder.build();
            logger.info("Connection with success to TaskList");
            return true;
        } catch (Exception e) {
            logger.error("------------------ Connection error to taskList {}", e);
            return false;
        }

    }
}
