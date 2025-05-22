package com.camunda.loan.worker;

import com.camunda.loan.serviceloan.Loan;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LoanDecisionWorker {
    public static final String VARIABLE_LOAN_RISK_ACCEPTANCE = "loanRiskAcceptance";
    public static final String VARIABLE_MESSAGE = "message";
    public static final String VARIABLE_STATUS_LOAN = "statusLoan";
    public static final String VARIABLE_LOAN_RISK_ACCEPTANCE_V_GREEN = "green";
    public static final String VARIABLE_LOAN_RISK_ACCEPTANCE_V_YELLOW = "yellow";
    public static final String VARIABLE_LOAN_RISK_ACCEPTANCE_V_RED = "red";
    Logger logger = LoggerFactory.getLogger(LoanDecisionWorker.class.getName());

    @JobWorker(type = "loan-decision")
    public Map<String, Object> loanDecision(JobClient jobClient,
                                            @Variable(name = "age") Integer age,
                                            @Variable(name = "creditScore") Integer creditScore,
                                            @Variable(name = "amount") Integer amount
    ) {
        long begin = System.currentTimeMillis();
        String acceptance = VARIABLE_LOAN_RISK_ACCEPTANCE_V_RED;
        String message = "";
        if (amount < 500) {
            acceptance = VARIABLE_LOAN_RISK_ACCEPTANCE_V_GREEN;
            message = "Under $500, loan from any customer is accepted";
            // we need more time to validate this
        } else if (creditScore < 500) {
            acceptance = VARIABLE_LOAN_RISK_ACCEPTANCE_V_RED;

            message = "Not possible to give a loan";
        } else if (creditScore >= 800 && amount < 15000) {
            acceptance = VARIABLE_LOAN_RISK_ACCEPTANCE_V_GREEN;
            message = "Good customer, medium loan accepted";
        } else {

            message = "Medium customer, need to review the application";
            // we need more time to decide here
            try {
                Thread.sleep(1000 * 2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            acceptance = VARIABLE_LOAN_RISK_ACCEPTANCE_V_YELLOW;
        }

        if (amount > 10000) {

            // we need much more time
            try {
                Thread.sleep(1000 * 15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            message += "...deep review..";

        }

        // We lie a litle: we set the acceptance here
        Loan.STATUSLOAN statusLoan;
        if (acceptance.equals(VARIABLE_LOAN_RISK_ACCEPTANCE_V_YELLOW)) {
            statusLoan = Loan.STATUSLOAN.REVIEW;
        } else if (acceptance.equals(VARIABLE_LOAN_RISK_ACCEPTANCE_V_RED)) {
            statusLoan = Loan.STATUSLOAN.REJECTED;
        } else {
            statusLoan = Loan.STATUSLOAN.ACCEPTED;
        }

        logger.info("LoanDecisionWorker [{}] timeForDecision {} ms message: {}", acceptance, System.currentTimeMillis() - begin, message);
        return Map.of(VARIABLE_LOAN_RISK_ACCEPTANCE, acceptance,
                VARIABLE_STATUS_LOAN, statusLoan.name(),
                VARIABLE_MESSAGE, message);
    }
}
