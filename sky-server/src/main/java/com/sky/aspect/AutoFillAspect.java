package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面 实现公共字段自动填充处理逻辑
 */

@Slf4j
@Aspect // 自定义切面类注解
@Component  // 自定义切面类也是一个bean对象，也需要交给IPC容器进行管理
public class AutoFillAspect {

    // 定义切入点和通知 因为切面类其实就是通知加上切入点

    /**
     * 指定切入点 其实就是对哪 些类的哪些方法进行拦截
     */

    // execution(* com.sky.mapper.*.*(..)) 指定com.sky.mapper包下的所有类所有方法不限返回类型与参数个数
    // @annotation(com.sky.annotation.AutoFill) 指定方法上加上自定义注解的方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){} // 切点表达式

    /**
     * 定义通知 (前置通知), 在通知中进行公共字段的赋值
     */
    @Before("autoFillPointCut()") // 指定切点表达式
    public void autoFill(JoinPoint joinPoint){
        // 前置通知这个方法往往需要要求传进来一个参数 就是连接点 通过这个连接点就可以知道哪些方法被拦截到了 以及被拦截到的这个方法它的参数是什么样的
        log.info("开始进行公共字段的自动填充...");

        // 获取到当前被拦截到的方法上的数据库操作类型

        // TODO 这个步骤不懂签名对象的转型
        MethodSignature signature = (MethodSignature)joinPoint.getSignature(); // 获得签名对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class); // 获得方法上的注解对象
        OperationType value = annotation.value(); // 获得数据库操作类型

        // 获取到当前被拦截的方法的参数 -- 实体类对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        Object entity = args[0];

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型, 为对应的属性反射赋值
        if (value == OperationType.INSERT){
            // 为4个公共字段赋值
            try {
                // TODO 复习反射相关的知识
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                // 通过反射来为对象属性进行赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (value == OperationType.UPDATE) {
            // 为2个公共字段进行赋值

            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射来为对象属性进行赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
