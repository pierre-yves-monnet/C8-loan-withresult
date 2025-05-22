package com.camunda.loan.serviceloan;

import com.camunda.loan.worker.GetInformationWorker;
import com.camunda.loan.worker.GetLoanInformationWorker;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskList;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.exception.TaskListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServiceLoan {
    // Needed for the withResultAPI
    final WithResultC8Impl withResultC8Impl;
    final WithResultAPIImpl withResultAPIImpl;
    final C8Factory c8Factory;
    final private IMPLEMENTATION implementation = IMPLEMENTATION.WITHRESULT;
    Logger logger = LoggerFactory.getLogger(ServiceLoan.class.getName());
    private final Map<String, Loan> mapLoan = new HashMap<>();
    ServiceLoan(WithResultC8Impl withResultC8Impl, WithResultAPIImpl withResultAPIImpl, C8Factory c8Factory) {
        this.withResultC8Impl = withResultC8Impl;
        this.withResultAPIImpl = withResultAPIImpl;
        this.c8Factory = c8Factory;
    }

    public Map<String, Object> applyLoanApplication(Integer amount, String ssn) {
        logger.info("----- Start Apply loan application for amount {} and ssn {}", amount, ssn);
        Loan loan = new Loan();
        loan.amount = amount;
        loan.ssn = ssn;
        mapLoan.put(ssn, loan);
        createProcessInstanceSynchronousExecution(loan);
        logger.info("----- End Apply loan application for amount {} and ssn {} loanId {}", amount, ssn, loan.loanId);
        return loan.getMap();
    }

    /**
     * @param loanId   loan Id
     * @param decision decision to accept it or reject it
     * @return loan information
     */
    public Map<String, Object> reviewLoan(String loanId, boolean decision) {
        logger.info("----- Start review LoanId [{}]", loanId);
        Loan loan = getLoanById(loanId);
        if (loan == null) {
            return Map.of("status", "loan not found");
        }

        try {
            TaskSearch taskSearch = new TaskSearch().setProcessInstanceKey(loan.loanId);

            TaskList taskList = c8Factory.getTaskListClient().getTasks(taskSearch);
            if (taskList.size() != 1) {
                return Map.of("status", "task not found");
            }
            Map<String, Object> result = executeUserTaskSynchronousExecution(loan, taskList.get(0), decision);


            // Update the loan status:
            loan.statusLoan = decision ? Loan.STATUSLOAN.ACCEPTED : Loan.STATUSLOAN.REJECTED;
            loan.messageInternal = (String) result.getOrDefault(GetLoanInformationWorker.VARIABLE_LOAN_INFO, null);
            mapLoan.put(loan.ssn, loan);


            logger.info("----- End review LoanId {} variablesLoan[{}]", loanId, result.get(GetInformationWorker.VARIABLE_LOAN));
            return loan.getMap();
        } catch (TaskListException e) {
            logger.error("----- review LoanId [{}} Task not found", loanId);

            return Map.of("status", "task not found");
        }
    }

    public Map<String, Object> getLoanInformation(String ssn) {
        logger.info("----- Start getLoanInformation ssn [{}]", ssn);

        Map<String, Object> info = executeMessageSynchronouslyExecution(ssn);

        logger.info("----- End getLoanInformation ssn [{}] info [{}]", ssn, info);
        return info;
    }

    /**
     * Get the list of loan
     *
     * @return list of Loans map
     */
    public List<Map<String, Object>> getListLoans() {
        return mapLoan.values().stream()
                .map(Loan::getMap)
                .toList();
    }

    private Loan getLoanById(String loanId) {
        return mapLoan.values().stream().filter(loan -> loan.loanId.equals(loanId)).findFirst().orElse(null);
    }

    /**
     * Here you are: you have to implement these methods now
     *
     * @param loan to create. Information must be fulfill in the loan object (creditScore...)
     */
    private void createProcessInstanceSynchronousExecution(Loan loan) {
        switch (implementation) {
            case C8:
                withResultC8Impl.createProcessInstanceWithResult(loan);
                break;
            case WITHRESULT:
                withResultAPIImpl.createProcessInstanceWithResult(loan);
                break;
            case MINE:
                // It's up to you
        }
    }



    /* ******************************************************************** */
    /*                                                                      */
    /*  Game start here                                                     */
    /*                                                                      */
    /*  Rewrite this method to synchronously run the call.                  */
    /* As a solution, withResultC8Impl and withResultAPIImpl implement the  */
    /* code                                                                 */
    /* ******************************************************************** */

    /**
     * Execute a user task
     *
     * @param loan     loan to execute the review
     * @param task     task to execute
     * @param decision decision schoose
     * @return the different information around the loan
     */
    private Map<String, Object> executeUserTaskSynchronousExecution(Loan loan, Task task, boolean decision) {
        switch (implementation) {
            case C8:
                // Not exist
                return Collections.emptyMap();
            case WITHRESULT:
                return withResultAPIImpl.executeUserTaskWithResult(loan, task, decision);
            case MINE:
                // It's up to you
                return Collections.emptyMap();
        }
        return Collections.emptyMap();

    }

    public Map<String, Object> executeMessageSynchronouslyExecution(String ssn) {
        switch (implementation) {
            case C8:
                // Not exist
                return Collections.emptyMap();
            case WITHRESULT:
                return withResultAPIImpl.executeMessageWithResult(ssn);
            case MINE:
                // It's up to you
                return Collections.emptyMap();
        }
        return Collections.emptyMap();
    }


    enum IMPLEMENTATION {C8, WITHRESULT, MINE}


}


