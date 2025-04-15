package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice//@RestControllerAdvice用于标识当前类为REST风格对应的异常处理器
@Slf4j
public class GlobalExceptionHandler {

    private final HttpMessageConverters messageConverters;

    public GlobalExceptionHandler(HttpMessageConverters messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 捕获业务异常
     */
    @ExceptionHandler// 隐式匹配，Spring会默认处理该参数类型及其子类的异常。
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获SQLIntegrityConstraintViolationException异常
     * 用户名已存在，抛出的异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String msg = ex.getMessage();
        if (msg.contains("Duplicate entry")) {
            String[] split = msg.split(" ");
            String username = split[2];
            return Result.error("用户名:" + username + MessageConstant.ALREADY_EXIST);
        } else
            return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
