package com.camunda.loan.serviceloan;

import com.camunda.loan.controller.LoanController;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServiceLoan {
    Logger logger = LoggerFactory.getLogger(ServiceLoan.class.getName());

    public Map<String, Loan> mapLoan = new HashMap<>();
    ZeebeClient zeebeClient;

    ServiceLoan(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    public Map<String, Object> applyLoanApplication(Integer amount, String ssn) {

        Loan loan = new Loan();
        loan.amount = amount;
        loan.ssn = ssn;
        mapLoan.put(ssn, loan);
        createProcessInstanceWithResult(loan);
        return loan.getMap();
    }

    public Map<String, Object> loanReview(String id, Integer amount, String ssn) {
        Map<String, Object> result = new HashMap<>();
        Loan loan = mapLoan.get(id);

        return result;

    }

    public List<Map<String, Object>> getListLoans() {
        return mapLoan.values().stream()
                .map(Loan::getMap)
                .toList();
    }


    /**
     * Using the withResult() API
     * @param loan to populate
     */
    private void createProcessInstanceWithResult(Loan loan){
        try {
            ProcessInstanceResult loanApplication = zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId("LoanApplication")
                    .latestVersion()
                    .variables(loan.getMap())
                    .withResult()
                    .requestTimeout(Duration.ofSeconds(5))
                    .send().join();
            loan.loanId = (String) loanApplication.getVariablesAsMap().get("loanId");
            loan.loanAcceptance=(String) loanApplication.getVariablesAsMap().get("loanAcceptance");
            loan.age=(Integer) loanApplication.getVariablesAsMap().get("age");
            loan.creditScore=(Integer) loanApplication.getVariablesAsMap().get("creditScore");
            loan.messageCustomer=(String) loanApplication.getVariablesAsMap().get("messageCustomer");
            loan.dateAcceptance = new Date();
            loan.status= Loan.STATUSLOAN.COMPLETED;
        }catch (Exception e) {
            logger.error("createProcessInstanceWithResult with error ", e);
            loan.status= Loan.STATUSLOAN.NO_COMPLETION;
            loan.info="Can't get Result ProcessInstance";
        }
    }


}


