package com.camunda.loan.worker;

import com.camunda.loan.serviceloan.Loan;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoanNotificationAcceptedWorker {
    Logger logger = LoggerFactory.getLogger(LoanNotificationRejectedWorker.class.getName());

    @JobWorker(type = "loan-notification-accepted")
    public Map<String,Object> loanNotificationRejected(JobClient jobClient, @Variable(name="loanId") String loanId) {
        logger.info("Loan [{}] is accepted", loanId);
        return Map.of("messageCustomer", "Congratulations! Your loan was approved.",
                LoanDecisionWorker.VARIABLE_STATUS_LOAN, Loan.STATUSLOAN.ACCEPTED.name());
    }

}
