package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    // 从配置文件中获取JWT的密钥
    @Value("${spring.security.jwt.key}")
    String key;

    // 从配置文件中获取JWT的过期时间（小时）
    @Value("${spring.security.jwt.expire}")
    int expire;

    // 注入StringRedisTemplate用于操作Redis
    @Resource
    StringRedisTemplate template;

    // 使JWT失效
    public boolean invalidateJwt(String headerToken) {
        // 将请求头中的Token转换为标准Token格式
        String token = this.convertToken(headerToken);
        if(token == null) return false;
        // 使用HMAC256算法生成签名
        Algorithm algorithm = Algorithm.HMAC256(key);
        // 创建JWT验证器
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            // 验证Token
            DecodedJWT jwt = jwtVerifier.verify(token);
            // 获取Token的ID
            String id = jwt.getId();
            // 将Token添加到黑名单并设置过期时间
            return deleteToken(id, jwt.getExpiresAt());
        } catch (JWTVerificationException e) {
            // Token验证失败，返回false
            return false;
        }
    }

    // 将Token添加到黑名单
    private boolean deleteToken(String uuid, Date time) {
        // 检查Token是否已经失效
        if(this.isInvalidToken(uuid)) return false;
        // 获取当前时间
        Date now = new Date();
        // 计算Token的剩余过期时间
        long expire = Math.max(time.getTime() - now.getTime(), 0);
        // 将Token添加到Redis黑名单中，并设置过期时间
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }


    // 检查Token是否在黑名单中
    private boolean isInvalidToken(String uuid) {
        return template.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

    // 解析JWT
    public DecodedJWT resolveJwt(String headerToken) {
        // 将请求头中的Token转换为标准Token格式
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        // 使用HMAC256算法生成签名
        Algorithm algorithm = Algorithm.HMAC256(key);
        // 创建JWT验证器
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            // 验证Token
            DecodedJWT verify = jwtVerifier.verify(token);
            // 检查Token是否在黑名单中
            if(this.isInvalidToken(verify.getId())) return null;
            // 获取Token的过期时间
            Date expiresAt = verify.getExpiresAt();
            // 检查Token是否过期
            return new Date().after(expiresAt) ? null : verify;
        } catch (JWTVerificationException e) {
            // Token验证失败，返回null
            return null;
        }
    }

    // 创建JWT
    public String createJwt(UserDetails details, int id, String username) {
        // 使用HMAC256算法生成签名
        Algorithm algorithm = Algorithm.HMAC256(key);
        // 获取Token的过期时间
        Date expire = this.expireTime();
        // 创建JWT并设置相关信息
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString()) // 设置Token的ID
                .withClaim("id", id) // 设置用户ID
                .withClaim("name", username) // 设置用户名
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) // 设置用户权限
                .withExpiresAt(expire) // 设置Token的过期时间
                .withIssuedAt(new Date()) // 设置Token的签发时间
                .sign(algorithm); // 签名
    }

    // 获取Token的过期时间
    public Date expireTime() {
        // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        // 设置Token的过期时间为配置文件中的小时数
        calendar.add(Calendar.HOUR, expire * 24);
        return calendar.getTime();
    }

    // 将DecodedJWT转换为UserDetails
    public UserDetails toUser(DecodedJWT jwt) {
        // 获取Token中的所有声明
        Map<String, Claim> claims = jwt.getClaims();
        // 创建UserDetails并设置相关信息
        return User
                .withUsername(claims.get("name").asString()) // 设置用户名
                .password("******") // 设置密码（此处为占位符）
                .authorities(claims.get("authorities").asArray(String.class)) // 设置用户权限
                .build();
    }

    // 从DecodedJWT中获取用户ID
    public Integer toId(DecodedJWT jwt) {
        // 获取Token中的所有声明
        Map<String, Claim> claims = jwt.getClaims();
        // 返回用户ID
        return claims.get("id").asInt();
    }

    // 将请求头中的Token转换为标准Token格式
    private String convertToken(String headerToken) {
        // 检查请求头中的Token是否为空或格式不正确
        if (headerToken == null || !headerToken.startsWith("Bearer ")) return null;
        // 返回标准Token格式
        return headerToken.substring(7);
    }
}
