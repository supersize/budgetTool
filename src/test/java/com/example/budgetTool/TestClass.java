package com.example.budgetTool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class TestClass {
    @BeforeEach
    public void beforeEach () {
        System.out.println("Test before");
    }

    @AfterEach
    public void afterTest () {
        System.out.println("Test after");
    }


    @Test
    @DisplayName("같은지 보는 테스트!")
    public void test01 () {
        int generatedValue = 1;

        this.numberSeven();

        if (generatedValue == this.numberSeven()) {
            System.out.println("it's same!!!");
        } else System.out.println("it's not same!");

    }

    public void test02 () {

        this.numberSeven();
    }


    public int numberSeven () {
        return 7;
    }
}
