package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面类
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     * 匹配com.sky.mapper包下的所有接口中的所有方法，但我们不需要增强这么多方法，只需要对方法上加了自定义注解 AutoFill的方法进行增强即可
     * 只写annotation表达式会扫描全部，影响效率，加上execution就会只扫描mapper里面带AutoFill注解的方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    private void autoFillPointCut() {
    }

    //使用前置通知也行
    @Around("autoFillPointCut()")
    public Object autoFill(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("开始进行公共字段自动填充...");
        //获取方法参数
        Object[] args = proceedingJoinPoint.getArgs();

        if (args == null || args.length == 0) {
            return null;
        }
        Object entity = args[0];

        //获取到当前被拦截的方法上的数据库操作类型
        //Signature接口提供的信息是比较一般的，例如可以获取方法名，但无法获取方法的参数类型、返回类型等更详细的信息
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();//方法签名对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType value = annotation.value();//获得数据库操作类型

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        Class<?> aClass = entity.getClass();
        //根据不同的操作类型，通过反射为对应的属性赋值
        //为什么只能通过反射来赋值？因为我们不知道传入的entity是什么类型的对象
        //如果直接强转成Category、Employee等类型，可能会报错
        if (value == OperationType.INSERT) {
            //为4个公共字段赋值
            Method setCreateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, currentId);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        } else if (value == OperationType.UPDATE) {
            //为2个公共字段赋值
            Method setUpdateTime = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = aClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
        }

        Object res = proceedingJoinPoint.proceed(args);
        return res;
    }
}
