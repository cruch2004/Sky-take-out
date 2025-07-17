package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解   用来标识某个方法需要进行功能字段自动填充处理
 */
@Target(ElementType.METHOD) // 指定当前注解会加在什么位置
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    // 指定属性 指定当前数据库的操纵类型 通过枚举的方式来指定

    OperationType value();  // OperationType 是我们自定义好的枚举类型 insert、update
}
