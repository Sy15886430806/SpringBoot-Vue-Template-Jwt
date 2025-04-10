package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

// 使用@Data注解，这是Lombok库提供的注解，可以自动生成getter、setter、toString、equals和hashCode方法
@Data
public class AuthorizeVo {
    // 定义一个String类型的成员变量username，用于存储用户名
    String username;
    // 定义一个String类型的成员变量role，用于存储用户角色
    String role;
    // 定义一个String类型的成员变量token，用于存储授权令牌
    String token;
    // 定义一个Date类型的成员变量expire，用于存储令牌的过期时间
    Date expire;
}
