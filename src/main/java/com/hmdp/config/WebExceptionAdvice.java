package com.hmdp.config;

import com.hmdp.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 将 Web 请求中的运行时异常转换为统一响应。
 * @author wdk
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionAdvice {

    /**
     * 将运行时异常转换为统一失败响应。
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e);
        return Result.fail("服务器异常");
    }
}
