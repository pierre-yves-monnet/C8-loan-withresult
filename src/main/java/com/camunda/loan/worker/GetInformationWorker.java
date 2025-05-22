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
    public static final String VARIABLE_AGE = "age";
    public static final String VARIABLE_CREDIT_SCORE = "creditScore";
    public static final String VARIABLE_LOAN_ID = "loanId";
    public static final String VARIABLE_LAST_NAME = "lastName";
    public static final String VARIABLE_LOAN = "loan";
    Logger logger = LoggerFactory.getLogger(LoanNotificationRejectedWorker.class.getName());


    String[] listActors = {
            "DeNiro", "Pacino", "Streep", "Hanks", "Washington", "Pitt", "Clooney", "DiCaprio", "Damon", "Norton",
            "Cruise", "Downey", "Johansson", "Lawrence", "McConaughey", "Reynolds", "Roberts", "Aniston", "Gosling", "Chastain",
            "Cassel", "Binoche", "Reno", "Deneuve", "Cotillard", "Bellucci", "Tautou", "Canet", "Ulliel", "Kassovitz",
            "Depardieu", "Huppert", "Lhermitte", "Auteuil", "Adjani", "Belmondo", "Delon", "Marceau", "Cluzet", "Bohringer",
            "Hardy", "Ardant", "Bouquet", "Lonsdale", "Noiret", "Blier", "Villeret", "Signoret", "Montand", "Trintignant"
    };
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
            resultMap.put(VARIABLE_AGE, firstSSN % 50 + 20);
            resultMap.put(VARIABLE_CREDIT_SCORE, Math.max(lastSSN % 1000,300));
            resultMap.put(VARIABLE_LOAN_ID, String.valueOf(activatedJob.getProcessInstanceKey()));
            resultMap.put(VARIABLE_LAST_NAME, listActors[(int) (Math.random() * listActors.length)]);
            resultMap.put(VARIABLE_LOAN, "HOLD");
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
