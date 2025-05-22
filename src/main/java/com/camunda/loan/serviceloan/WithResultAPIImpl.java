package com.camunda.loan.serviceloan;

import com.camunda.loan.worker.GetInformationWorker;
import com.camunda.loan.worker.LoanDecisionWorker;
import io.camunda.executewithresult.executor.ExecuteWithResult;
import io.camunda.executewithresult.executor.ResultWorker;
import io.camunda.executewithresult.executor.WithResultAPI;
import io.camunda.tasklist.dto.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class WithResultAPIImpl {
    Logger logger = LoggerFactory.getLogger(WithResultAPIImpl.class.getName());

    C8Factory c8Factory;
    WithResultAPI withResultAPI;

    WithResultAPIImpl(C8Factory c8Factory) {
        this.c8Factory = c8Factory;
        withResultAPI = new WithResultAPI(c8Factory.getZeebeClient(), c8Factory.getTaskListClient(), true, ResultWorker.WorkerImplementation.HOST);
    }

    protected void createProcessInstanceWithResult(Loan loan) {
        try {
            ExecuteWithResult executeWithResult = withResultAPI.processInstanceWithResult("CranberryApplication",
                    loan.getMap(), "cranberry-with-result", Duration.ofSeconds(5)).join();

            loan.statusRequest = executeWithResult.timeOut ? Loan.STATUSREQUEST.IN_PROGRESS : Loan.STATUSREQUEST.COMPLETED;
            loan.loanId = String.valueOf(executeWithResult.processInstanceKey == null ? "" : executeWithResult.processInstanceKey);
            if (!executeWithResult.timeOut) {

                loan.statusLoan = Loan.STATUSLOAN.valueOf((String) executeWithResult.processVariables.getOrDefault(LoanDecisionWorker.VARIABLE_STATUS_LOAN, null));
                loan.loanRiskAcceptance = (String) executeWithResult.processVariables.getOrDefault(LoanDecisionWorker.VARIABLE_LOAN_RISK_ACCEPTANCE, null);
                loan.age = (Integer) executeWithResult.processVariables.getOrDefault(GetInformationWorker.VARIABLE_AGE, null);
                loan.creditScore = (Integer) executeWithResult.processVariables.getOrDefault(GetInformationWorker.VARIABLE_CREDIT_SCORE, null);
                loan.lastName = (String) executeWithResult.processVariables.getOrDefault(GetInformationWorker.VARIABLE_LAST_NAME, null);
                loan.messageCustomer = (String) executeWithResult.processVariables.getOrDefault("messageCustomer", null);
                loan.dateAcceptance = new Date();
            }
        } catch (Exception e) {
            logger.error("createProcessInstanceWithResult with error ", e);
            loan.statusRequest = Loan.STATUSREQUEST.NO_COMPLETION;
            loan.info = "Can't get Result ProcessInstance";
        }
    }


    protected Map<String, Object> executeUserTaskWithResult(Loan loan, Task task, boolean decision) {
        Map variables = new HashMap<>();
        variables.put("deepReview", Boolean.FALSE);
        variables.put("reviewDecision", decision);
        try {
            ExecuteWithResult executeWithResult = (ExecuteWithResult) withResultAPI.executeTaskWithResult(task,
                    true, "demo", variables, "cranberry-with-result", Duration.ofSeconds(5)).join();
            if (!executeWithResult.timeOut) {
                return executeWithResult.processVariables;
            } else {
                return Map.of("status", "execution in progress");
            }
        } catch (Exception e) {
            logger.error("executeUserTaskWithResult with error ", e);
            return Map.of("status", "task execution error");
        }

    }


    public Map<String, Object> executeMessageWithResult(String ssn) {
        try {
            ExecuteWithResult executeWithResult = withResultAPI.publishNewMessageWithResult(
                    "loan-get-information",
                    ssn,
                    Duration.ofMinutes(1), // loan must be here
                    Collections.emptyMap(),
                    "cranberry-with-result",
                    Duration.ofSeconds(5)).join();
            if (!executeWithResult.timeOut) {
                return executeWithResult.processVariables;
            } else {
                return Map.of("status", "No answer");
            }
        } catch (Exception e) {
            logger.error("executeMessageWithResult with error ", e);
            return Map.of("status", "Publish message with error");
        }

    }

}
