package com.chuncongcong.sw;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@MapperScan("com.chuncongcong.sw.mapper")
@SpringBootApplication
public class SwAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwAppApplication.class, args);
    }
}
