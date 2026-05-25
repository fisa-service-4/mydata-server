package com.mydata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MydataServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MydataServerApplication.class, args);
  }
}
