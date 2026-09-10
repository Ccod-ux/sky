package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result<?> exceptionHandler(BaseException ex){
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理数据库约束异常，例如员工账号重复
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<?> sqlIntegrityConstraintViolationExceptionHandler(
            SQLIntegrityConstraintViolationException ex) {
        log.error("数据库约束异常：{}", ex.getMessage());
        return duplicateDataResult(ex.getMessage());
    }

    /**
     * Spring/MyBatis 常见的重复键异常
     * @param ex
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> duplicateKeyExceptionHandler(DuplicateKeyException ex) {
        log.error("数据库重复键异常：{}", ex.getMessage());
        return duplicateDataResult(ex.getMessage());
    }

    /**
     * 处理未明确捕获的系统异常
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<?> exceptionHandler(Exception ex) {
        log.error("系统异常", ex);
        return Result.error("系统繁忙，请稍后再试");
    }

    private Result<?> duplicateDataResult(String message) {
        if (message != null && message.contains("Duplicate entry")) {
            return Result.error("员工账号已存在");
        }
        return Result.error("数据操作失败");
    }
}
