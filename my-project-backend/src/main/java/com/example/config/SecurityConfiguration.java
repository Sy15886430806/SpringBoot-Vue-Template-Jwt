package com.example.config;

import com.example.entity.RestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
public class SecurityConfiguration {

    @Bean
// 定义一个Spring Bean，名为filterChain，用于配置安全过滤器链
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 方法参数http是HttpSecurity对象，用于配置HTTP安全性
        return http
                // 调用HttpSecurity对象的配置方法
                .authorizeHttpRequests(conf -> conf

                        // 配置请求授权规则
                        .requestMatchers("/api/auth/**").permitAll() // 允许所有对"/api/auth/**"路径的请求
                        .anyRequest().authenticated() // 其他任何请求都需要进行身份验证
                )
                // 配置表单登录相关的设置
                .formLogin(conf -> conf
                        // 设置处理登录请求的URL路径
                        .loginProcessingUrl("/api/auth/login")
                        // 设置认证失败时的处理器，调用onAuthenticationFailure方法处理失败逻辑
                        .failureHandler(this::onAuthenticationFailure)
                        // 设置认证成功时的处理器，调用onAuthenticationSuccess方法处理成功逻辑
                        .successHandler(this::onAuthenticationSuccess))
                // 配置注销功能
                .logout(conf -> conf
                        // 设置注销请求的URL路径
                        .logoutUrl("/api/auth/logout")
                        // 设置注销成功后的处理器，调用当前对象的onLogoutSuccess方法
                        .logoutSuccessHandler(this::onLogoutSuccess)).csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 设置会话管理策略为无状态
                ).build(); // 构建并返回SecurityFilterChain对象
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 设置响应的内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 认证成功时的处理方法
        // 向客户端返回一个表示成功的JSON字符串
        // RestBean.success() 创建一个表示成功的RestBean对象
        // asJsonString() 方法将RestBean对象转换为JSON字符串
        response.getWriter().write(RestBean.success().asJsonString()); // 向客户端返回"登录成功"信息
    }

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 认证失败时的处理方法
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString()); // 向客户端返回"登录失败"信息
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        // 注销成功时的处理方法
    }
}