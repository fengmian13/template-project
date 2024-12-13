package com.facialrecognition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.facialrecognition"})
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class FacialRecognitionApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FacialRecognitionApiApplication.class, args);
    }
}
