package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFilter extends OncePerRequestFilter {

    // 注入JwtUtils工具类，用于处理JWT相关操作
    @Resource
    JwtUtils utils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取"Authorization"字段
        String authorization = request.getHeader("Authorization");
        // 解析JWT，获取DecodedJWT对象
        DecodedJWT jwt = utils.resolveJwt(authorization);
        // 如果JWT解析成功，不为null
        if(jwt != null) {
            // 将JWT转换为UserDetails对象
            UserDetails user = utils.toUser(jwt);
            // 创建一个UsernamePasswordAuthenticationToken对象，用于身份验证
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 设置认证的详细信息，包括请求来源等
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证信息存储到SecurityContext中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 将用户ID存储到请求属性中，方便后续使用
            request.setAttribute("id", utils.toId(jwt));
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
