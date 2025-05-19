package com.camunda.loan.worker;

import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoanDecisionWorker {
    Logger logger = LoggerFactory.getLogger(LoanDecisionWorker.class.getName());

    @JobWorker(type = "loan-decision")
    public Map<String, Object> loanDecision(JobClient jobClient,
                                            @Variable(name = "age") Integer age,
                                            @Variable(name = "creditScore") Integer creditScore,
                                            @Variable(name = "amount") Integer amount
    ) {
        long begin = System.currentTimeMillis();
        String acceptance = "red";
        String message = "";
        if (amount < 500) {
            acceptance = "green";
            message = "Under $500, loan from any customer is accepted";
            // we need more time to validate this
        } else if (creditScore < 500) {
            acceptance = "red";
            message = "Not possible to give a loan";
        } else if (creditScore >= 800 && amount < 15000) {
            acceptance = "green";
            message = "Good customer, medium loan accepted";
        } else {

            message = "Medium customer, need to review the application";
            // we need more time to decide here
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            acceptance = "yellow";
        }

        if (amount > 10000) {

            // we need much more time
            try {
                Thread.sleep(1000 * 15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            message = "Very border line customer, deep review recommended";

        }


        logger.info("LoanDecisionWorker [{}] timeForDecision {} ms message: {}", acceptance, System.currentTimeMillis() - begin, message);
        return Map.of("loanAcceptance", acceptance, "message", message);
    }
}
