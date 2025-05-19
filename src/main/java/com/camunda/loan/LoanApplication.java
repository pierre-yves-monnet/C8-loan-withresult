package com.camunda.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan


@EnableScheduling

public class LoanApplication {
    Logger logger = LoggerFactory.getLogger(LoanApplication.class.getName());

    public static void main(String[] args) {

        SpringApplication.run(LoanApplication.class, args);
        // thanks to Spring, the class AutomatorStartup.init() is active.
    }

}
