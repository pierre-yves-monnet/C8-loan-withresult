package com.camunda.loan.serviceloan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Loan {
    public STATUSREQUEST statusRequest;
    public STATUSLOAN statusLoan;
    public String info;
    public String lastName;
    public String loanId;
    public String ssn;
    public Integer amount;
    public String loanRiskAcceptance;
    public Integer age;
    public Integer creditScore;
    public String messageCustomer;
    public String messageInternal;
    public Date dateAcceptance;

    public Map<String, Object> getMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("loanId", loanId == null ? "" : loanId);
        result.put("statusRequest", statusRequest == null ? "" : statusRequest.name());
        result.put("statusLoan", statusLoan == null ? "" : statusLoan.name());
        result.put("lastName", lastName);
        result.put("loanRiskAcceptance", loanRiskAcceptance == null ? "" : loanRiskAcceptance);
        result.put("age", age == null ? "" : age);
        result.put("creditScore", creditScore == null ? "" : creditScore);
        result.put("info", info == null ? "" : info);
        result.put("ssn", ssn);
        result.put("amount", amount);
        result.put("messageCustomer", messageCustomer == null ? "" : messageCustomer);
        result.put("messageInternal", messageInternal == null ? "" : messageInternal);
        result.put("dateAcceptance", dateAcceptance == null ? "" : dateAcceptance.toString());
        return result;
    }
    public enum STATUSREQUEST {NO_COMPLETION, IN_PROGRESS, COMPLETED}

    public enum STATUSLOAN {ACCEPTED, REJECTED, REVIEW}


}
