package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 标记该类为Spring Boot应用的入口类
@SpringBootApplication
public class MyProjectBackendApplication {

    // 主方法，Java程序的入口点
    public static void main(String[] args) {
        // 使用SpringApplication.run方法启动Spring Boot应用
        // MyProjectBackendApplication.class指定了启动类
        // args是传递给main方法的参数，通常用于配置启动参数
        SpringApplication.run(MyProjectBackendApplication.class, args);
    }

}
