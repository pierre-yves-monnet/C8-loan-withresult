
package com.camunda.loan.controller;

import com.camunda.loan.serviceloan.ServiceLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class LoanController {
    Logger logger = LoggerFactory.getLogger(LoanController.class.getName());


    ServiceLoan serviceLoan;
    LoanController(ServiceLoan serviceLoan) {
        this.serviceLoan = serviceLoan;
    }

    @PostMapping("loan/applyLoan")
    public Map<String,Object> applyLoan(@RequestBody Map<String, Object> body) {
        Integer amount = Integer.parseInt(body.get("amount").toString());
        String ssn = body.get("ssn").toString();
        return serviceLoan.applyLoanApplication( amount,  ssn);
    }

    @GetMapping("loan/list")
    public List<Map<String,Object>> listLoans() {
        return serviceLoan.getListLoans();
    }

    @PutMapping("loan/review")
    public Map<String,Object> reviewLoan( @RequestBody Map<String,Object>information) {
        String loanId = information.get("loanId").toString();
        String decision =information.get("decision").toString();
        return serviceLoan.reviewLoan(loanId, "true".equalsIgnoreCase(decision));
    }
    @GetMapping("loan/info")
    public Map<String,Object> loanInformation(@RequestParam String ssn)
    {
        return serviceLoan.getLoanInformation(ssn);
    }

}
