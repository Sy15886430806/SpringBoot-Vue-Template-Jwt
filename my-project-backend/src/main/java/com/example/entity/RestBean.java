package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;


// 定义一个泛型记录类RestBean，用于封装返回结果
public record RestBean<T>(int code, T data, String message) {
    // 静态方法，用于创建一个表示成功的RestBean实例，包含数据
    public static <T> RestBean<T> success(T data) {
        // 返回一个code为200，数据为传入的data，消息为"请求成功"的RestBean实例
        return new RestBean<>(200, data, "请求成功");
    }

    // 静态方法，用于创建一个表示成功的RestBean实例，不包含数据
    public static <T> RestBean<T> success() {
        // 调用success(T data)方法，传入null作为数据
        return success(null);
    }

    // 静态方法，用于创建一个表示未授权的RestBean实例
    public static <T> RestBean<T> unauthorized(String message) {
        // 调用failure(int code, String message)方法，传入401和消息
        return failure(401, message);
    }

    // 静态方法，用于创建一个表示禁止访问的RestBean实例
    public static <T> RestBean<T> forbidden(String message) {
        // 调用failure(int code, String message)方法，传入401和消息
        return failure(401, message);
    }

    // 静态方法，用于创建一个表示失败的RestBean实例
    public static <T> RestBean<T> failure(int code, String message) {
        // 返回一个code为传入的code，数据为null，消息为传入的message的RestBean实例
        return new RestBean<>(code, null, message);
    }

    // 实例方法，将当前RestBean实例转换为JSON字符串
    public String asJsonString() {
        // 使用JSONObject的toJSONString方法将当前实例转换为JSON字符串
        // JSONWriter.Feature.WriteNulls表示在JSON字符串中包含null值
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
