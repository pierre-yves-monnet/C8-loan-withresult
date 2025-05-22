package com.camunda.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan


@EnableScheduling

public class CranberryApplication {
    Logger logger = LoggerFactory.getLogger(CranberryApplication.class.getName());

    public static void main(String[] args) {

        SpringApplication.run(CranberryApplication.class, args);
        // thanks to Spring, the class AutomatorStartup.init() is active.
    }

}
