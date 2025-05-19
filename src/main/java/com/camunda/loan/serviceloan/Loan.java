package com.camunda.loan.serviceloan;

import java.util.Date;
import java.util.Map;

public class Loan {
    public enum STATUSLOAN {NO_COMPLETION, IN_PROGRESS, COMPLETED}

    public STATUSLOAN status;
    public String info;
    public String loanId;
    public String ssn;
    public Integer amount;
    public String loanAcceptance;
    public Integer age;
    public Integer creditScore;
    public String messageCustomer;
    public Date dateAcceptance;

    public Map<String, Object> getMap() {
        return Map.of("loanId", loanId == null ? "" : loanId,
                "status", status == null ? "" : status.name(),
                "loanAcceptance", loanAcceptance == null ? "" : loanAcceptance,
                "age", age == null ? "" : age,
                "creditScore", creditScore == null ? "" : creditScore,
                "info", info == null ? "" : info,
                "ssn", ssn,
                "amount", amount,
                "messageCustomer", messageCustomer == null ? "" : messageCustomer,
                "dateAcceptance", dateAcceptance == null ? "" : dateAcceptance.toString() + "");
    }
}
