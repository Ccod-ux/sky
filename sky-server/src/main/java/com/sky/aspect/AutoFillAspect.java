package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面。
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 拦截 Mapper 中标注了 @AutoFill 的方法。
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 在 Mapper 执行前填充公共字段。
     */
    @Around("autoFillPointCut()")
    public Object autoFill(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0 || args[0] == null) {
            return joinPoint.proceed();
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if (autoFill.value() == OperationType.INSERT) {
            invokeSetter(entity, AutoFillConstant.SET_CREATE_TIME, now);
            invokeSetter(entity, AutoFillConstant.SET_CREATE_USER, currentId);
            invokeSetter(entity, AutoFillConstant.SET_UPDATE_TIME, now);
            invokeSetter(entity, AutoFillConstant.SET_UPDATE_USER, currentId);
        } else if (autoFill.value() == OperationType.UPDATE) {
            invokeSetter(entity, AutoFillConstant.SET_UPDATE_TIME, now);
            invokeSetter(entity, AutoFillConstant.SET_UPDATE_USER, currentId);
        }

        return joinPoint.proceed();
    }

    /**
     * 通过反射调用实体对象的 setter。
     */
    private void invokeSetter(Object target, String methodName, Object value) {
        try {
            Method method = target.getClass().getMethod(methodName, value == null ? Long.class : value.getClass());
            method.invoke(target, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(
                    "公共字段自动填充失败，找不到或无法调用方法：" + methodName, e);
        }
    }
}
