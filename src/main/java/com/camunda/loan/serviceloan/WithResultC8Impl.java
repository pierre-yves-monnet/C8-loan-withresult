package com.camunda.loan.serviceloan;

import com.camunda.loan.worker.GetInformationWorker;
import com.camunda.loan.worker.LoanDecisionWorker;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class WithResultC8Impl {
    Logger logger = LoggerFactory.getLogger(WithResultC8Impl.class.getName());

    C8Factory c8Factory;

    WithResultC8Impl(C8Factory c8Factory) {
        this.c8Factory = c8Factory;
    }

    /**
     * Using the withResult() API
     *
     * @param loan to populate
     */
    protected void createProcessInstanceWithResult(Loan loan) {
        try {
            ProcessInstanceResult loanApplication = c8Factory.getZeebeClient().newCreateInstanceCommand()
                    .bpmnProcessId("CranberryApplication")
                    .latestVersion()
                    .variables(loan.getMap())
                    .withResult()
                    .requestTimeout(Duration.ofSeconds(5))
                    .send().join();
            loan.loanId = String.valueOf(loanApplication.getProcessInstanceKey());
            loan.statusRequest = Loan.STATUSREQUEST.COMPLETED;
            loan.statusLoan = Loan.STATUSLOAN.valueOf((String) loanApplication.getVariablesAsMap().getOrDefault(LoanDecisionWorker.VARIABLE_STATUS_LOAN, null));
            loan.loanRiskAcceptance = (String) loanApplication.getVariablesAsMap().getOrDefault(LoanDecisionWorker.VARIABLE_LOAN_RISK_ACCEPTANCE, null);
            loan.age = (Integer) loanApplication.getVariablesAsMap().getOrDefault(GetInformationWorker.VARIABLE_AGE, null);
            loan.creditScore = (Integer) loanApplication.getVariablesAsMap().getOrDefault(GetInformationWorker.VARIABLE_CREDIT_SCORE, null);
            loan.lastName = (String) loanApplication.getVariablesAsMap().getOrDefault(GetInformationWorker.VARIABLE_LAST_NAME, null);
            loan.messageCustomer = (String) loanApplication.getVariablesAsMap().get("messageCustomer");
            loan.dateAcceptance = new Date();

        } catch (Exception e) {
            logger.error("createProcessInstanceWithResult with error ", e);
            loan.statusRequest = Loan.STATUSREQUEST.NO_COMPLETION;
            loan.info = "Can't get Result ProcessInstance";
        }
    }

}
