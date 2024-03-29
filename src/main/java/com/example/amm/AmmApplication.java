package com.example.amm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan(basePackages = "com.example.amm")
@SpringBootApplication
public class AmmApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmmApplication.class, args);
    }

}
