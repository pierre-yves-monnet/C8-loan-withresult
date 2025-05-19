package com.camunda.loan.worker;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeepReviewWorker {
    Logger logger = LoggerFactory.getLogger(DeepReviewWorker.class.getName());

    @JobWorker(type = "loan-deep-review")
    public Map<String,Object> deepReview(JobClient jobClient,
                                             @Variable(name = "amount") Integer amount) {
        logger.info("Deep review started amount [{}]", amount);
        if (amount <= 5000) {
            return Map.of("loanAcceptance", "green", "rewiewNeeded", Boolean.FALSE);
        }
        return Map.of("loanAcceptance", "red", "rewiewNeeded", Boolean.FALSE);
    }
}
