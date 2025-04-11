package com.example.utils;

// 定义一个名为Const的公共类
public class Const {
    // 定义一个公共的、静态的、常量的字符串变量JWT_BLACK_LIST
    // 该变量用于表示JWT（JSON Web Token）的黑名单的键前缀
    // "jwt:blacklist:"这个字符串将用于标识存储在缓存或数据库中的JWT黑名单条目
    public static final String JWT_BLACK_LIST = "jwt:blacklist:";

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit";
    public static final String VERIFY_EMAIL_DATA = "verify:email:data";

    public static final int ORDER_LIMIT = -101;
    public static final int ORDER_CORS = -102;

    public static final String FLOW_LIMIT_COUNT = "flow:counter:";
    public static final String FLOW_LIMIT_BLOCK = "flow:block:";
}
