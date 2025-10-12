package com.example.budgetTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BudgetToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetToolApplication.class, args);
    }

}
