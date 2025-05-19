package com.camunda.loan.worker;

import io.camunda.executewithresult.worker.DelayWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoanNotificationRejectedWorker {
    Logger logger = LoggerFactory.getLogger(LoanNotificationRejectedWorker.class.getName());

    @JobWorker(type = "loan-notification-rejected")
    public Map<String,Object> loanNotificationRejected(JobClient jobClient, @Variable(name="loanId") String loanId) {
        logger.info("Loan [{}] is rejected",loanId);
        return Map.of("messageCustomer", "Sorry, your loan is not accepted at this moment");
    }
}
