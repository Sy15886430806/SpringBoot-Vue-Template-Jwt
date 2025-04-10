package com.example.config; // 定义包名，表示该类位于com.example.config包中

import org.springframework.context.annotation.Bean; // 引入Spring框架的@Bean注解，用于定义Bean
import org.springframework.context.annotation.Configuration; // 引入Spring框架的@Configuration注解，用于定义配置类
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入Spring Security的BCryptPasswordEncoder类，用于密码加密

@Configuration // 标记该类为配置类，Spring会自动扫描该类并注册其中的Bean
public class WebConfiguration {

    @Bean // 标记该方法为Bean的生成方法，Spring会自动调用该方法生成Bean并注册到Spring容器中
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 创建并返回一个BCryptPasswordEncoder实例，用于密码加密
    }
}
