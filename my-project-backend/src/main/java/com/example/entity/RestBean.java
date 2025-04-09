package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

// 定义一个名为RestBean的泛型记录类，用于封装返回结果
public record RestBean<T>(int code, T data, String message) {
    // 记录类的构造函数，包含三个字段：code, data, message
    // 其中code表示返回的状态码，data表示返回的数据，message表示返回的消息

    // 静态方法，用于创建一个表示成功的RestBean实例，包含数据
    public static <T> RestBean<T> success(T data) {
        // 返回一个新的RestBean实例，状态码为200，数据为传入的data，消息为"请求成功"
        return new RestBean<>(200, data, "请求成功");
    }

    // 静态方法，用于创建一个表示成功的RestBean实例，不包含数据
    public static <T> RestBean<T> success() {
        // 调用上述success方法，传入null作为数据
        return success(null);
    }

    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }

    // 实例方法，将当前RestBean实例转换为JSON字符串
    public String asJsonString() {
        // 使用JSONObject的toJSONString方法将当前实例转换为JSON字符串
        // JSONWriter.Feature.WriteNulls表示在转换过程中保留null值
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
