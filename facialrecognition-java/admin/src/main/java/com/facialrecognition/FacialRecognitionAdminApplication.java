package com.facialrecognition;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.facialrecognition"})
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
@MapperScan(basePackages = {"com.facialrecognition.mappers"})
public class FacialRecognitionAdminApplication {
    public static void main(String[] args) {
       SpringApplication.run(FacialRecognitionAdminApplication.class, args);
    }
}
