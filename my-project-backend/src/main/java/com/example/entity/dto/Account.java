package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data // 使用Lombok注解@Data，自动生成getter、setter、toString、equals、hashCode方法
@TableName("db_account") // 使用MyBatis-Plus注解@TableName，指定该实体类对应的数据库表名为"db_account"
@AllArgsConstructor // 使用Lombok注解@AllArgsConstructor，自动生成包含所有字段的构造方法
// 定义一个名为Account的公共类，实现BaseData接口
public class Account implements BaseData {
    @TableId(type = IdType.AUTO) // 使用MyBatis-Plus注解@TableId，指定该字段为数据库表的主键，并且主键生成策略为自增
    Integer id; // 定义一个Integer类型的字段id，表示账户的唯一标识
    String username; // 定义一个String类型的字段username，表示账户的用户名
    String password; // 定义一个String类型的字段password，表示账户的密码
    String email; // 定义一个String类型的字段email，表示账户的电子邮件地址
    String role; // 定义一个String类型的字段role，表示账户的角色
    Date registerTime; // 定义一个Date类型的字段registerTime，表示账户的注册时间
}
