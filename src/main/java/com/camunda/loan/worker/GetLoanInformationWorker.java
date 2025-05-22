package com.camunda.loan.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class GetLoanInformationWorker {
    public static final String VARIABLE_LOAN_INFO = "loanInfo";
    Logger logger = LoggerFactory.getLogger(GetLoanInformationWorker.class.getName());


    String[] listMessages = {
            "Still waiting for approval",
            "Approved with no reserve",
            "Rejected with no reserve",
            "Pending additional documents",
            "On hold due to verification",
            "Disbursed",
            "Cancelled by applicant",
            "Not valid anymore",
            "Awaiting final review",
            "Expired due to inactivity"
    };

    @JobWorker(type = "get-loan-information")
    public Map<String, Object> getInformation(JobClient jobClient,
                                              ActivatedJob activatedJob,
                                              @Variable(name = "loanId") String loanId) {
        logger.debug("GetLoanInformation");
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String message = "";
            if (activatedJob.getVariablesAsMap().containsKey("reviewDecision")) {
                Boolean reviewDecision = (Boolean) activatedJob.getVariable("reviewDecision");
                message += "Reviewer " + (reviewDecision ? "Approved" : "Rejected");
            }
            message += " Internal decision:" + listMessages[(int) (Math.random() * listMessages.length)];
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            message += " at " + LocalDateTime.now().format(formatter);

            resultMap.put(VARIABLE_LOAN_INFO, message);
            logger.info("GetLoanInformation: Loan Id[{}]  Status: [{}]", loanId, resultMap.get(VARIABLE_LOAN_INFO));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            return resultMap;
        } catch (Exception e) {
            logger.error("GetLoanInformation: loanId[{}]", loanId, e);
            return resultMap;

        }
    }
}
