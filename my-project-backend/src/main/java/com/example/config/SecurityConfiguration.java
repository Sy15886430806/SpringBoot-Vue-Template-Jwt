package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVo;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    // 注入JwtUtils工具类
    @Resource
    JwtUtils utils;

    // 注入JwtAuthorizeFilter过滤器
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    // 注入AccountService服务类
    @Resource
    AccountService accountService;

    // 定义SecurityFilterChain的Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 配置请求授权规则
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error").permitAll() // 允许所有对/api/auth/**的请求
                        .anyRequest().authenticated() // 其他所有请求都需要认证
                )
                // 配置表单登录
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login") // 登录处理URL
                        .failureHandler(this::onAuthenticationFailure) // 认证失败处理器
                        .successHandler(this::onAuthenticationSuccess)) // 认证成功处理器
                // 配置登出
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout") // 登出URL
                        .logoutSuccessHandler(this::onLogoutSuccess)) // 登出成功处理器
                // 配置异常处理
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized) // 未授权处理器
                        .accessDeniedHandler(this::onAccessDeny)
                )
                // 禁用CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理，设置为无状态会话
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 在用户名密码认证过滤器之前添加JWT认证过滤器
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                // 构建并返回安全配置
                .build();
    }


    // 认证成功时的处理方法
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 设置响应内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 从认证信息中获取用户对象
        User user = (User) authentication.getPrincipal();
        // 根据用户名或邮箱查找账户信息
        Account account = accountService.findAccountByNameOrEmail(user.getUsername());
        // 创建JWT令牌
        String token = utils.createJwt(user, account.getId(), account.getUsername());
        // 创建授权视图对象，并设置过期时间和令牌
        AuthorizeVo vo = account.asViewObject(AuthorizeVo.class, v -> {
            v.setExpire(utils.expireTime());
            v.setToken(token);
        });

        // 将成功响应写入响应体
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    // 认证失败时的处理方法
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 设置响应内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 将失败响应写入响应体
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    // 注销成功时的处理方法
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        // 设置响应内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 获取响应写入器
        PrintWriter writer = response.getWriter();
        // 从请求头中获取授权信息
        String authorization = request.getHeader("Authorization");
        // 验证并使JWT令牌失效
        if(utils.invalidateJwt(authorization)) {
            // 如果成功，写入成功响应
            writer.write(RestBean.success().asJsonString());
        } else {
            // 如果失败，写入失败响应
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }
    }

    // 访问被拒绝时的处理方法
    public void onAccessDeny(HttpServletRequest request,
                             HttpServletResponse response,
                             AccessDeniedException accessDeniedException) throws IOException {
        // 设置响应内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 将禁止访问的响应写入响应体
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

    // 未授权时的处理方法
    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException {
        // 设置响应内容类型为JSON，并指定字符编码为UTF-8
        response.setContentType("application/json;charset=utf-8");
        // 将未授权的响应写入响应体
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
}