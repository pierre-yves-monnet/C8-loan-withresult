package com.camunda.loan.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GetInformationWorker {
    Logger logger = LoggerFactory.getLogger(LoanNotificationRejectedWorker.class.getName());

    @JobWorker(type = "get-information")
    public Map<String, Object> getInformation(JobClient jobClient,
                                              ActivatedJob activatedJob,
                                              @Variable(name = "ssn") String ssn) {
        logger.debug("Get information-start");
        // SSN is "123-45-6789"
        try {
            int firstSSN = Integer.parseInt(ssn.substring(0, 3));
            int lastSSN = Integer.parseInt(ssn.substring(7, 7+4));
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("age", firstSSN % 50 + 20);
            resultMap.put("creditScore", Math.max(lastSSN % 1000,300));
            resultMap.put("loanId", String.valueOf(activatedJob.getProcessInstanceKey()));
            logger.info("GetInformation Loan Id[{}] ssn[{}] age: {} creditScore: {}",
                    resultMap.get("loanId"),
                    ssn,
                    resultMap.get("age"),
                    resultMap.get("creditScore"));
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
            }

            return resultMap;
        } catch (Exception e) {
            logger.error("Get information ssn[{}]", ssn, e);

            throw new ZeebeBpmnError("SSN_INVALID", "The SSN[" + ssn + "] does not follow the pattern 111-11-1111", Map.of("errorCode", "SSN_INVALID"));

        }
    }
}
