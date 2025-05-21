package com.emiteai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmiteaiApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmiteaiApiApplication.class, args);
    }
}
