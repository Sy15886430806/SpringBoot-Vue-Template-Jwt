package com.example.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public interface BaseData {

    // 泛型方法，将当前对象转换为指定类型的视图对象，并执行消费者操作
    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer) {
        // 调用另一个asViewObject方法创建视图对象
        V v = this.asViewObject(clazz);
        // 执行消费者操作
        consumer.accept(v);
        // 返回视图对象
        return v;
    }

    // 泛型方法，将当前对象转换为指定类型的视图对象
    default <V> V asViewObject(Class<V> clazz) {
        try {
            // 获取指定类的所有声明字段
            Field[] declaredFields = clazz.getDeclaredFields();
            // 获取指定类的无参构造函数
            Constructor<V> constructor = clazz.getConstructor();
            // 创建指定类的实例
            V v = constructor.newInstance();
            // 遍历所有声明字段，并调用convert方法进行转换
            for (Field declarefField : declaredFields) convert(declarefField, v);
            // 返回转换后的视图对象
            return v;
        } catch (ReflectiveOperationException exception) {
            // 如果反射操作抛出异常，则抛出运行时异常
            throw new RuntimeException(exception.getMessage());
        }
    }

    // 私有方法，将当前对象的字段值转换为视图对象的字段值
    private void convert(Field field, Object vo) {
        try {
            // 获取当前对象的指定字段
            Field source = this.getClass().getDeclaredField(field.getName());
            // 设置字段可访问
            field.setAccessible(true);
            source.setAccessible(true);
            // 将当前对象的字段值设置到视图对象中
            field.set(vo, source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            // 忽略非法访问异常和字段不存在异常
        }
    }
}
