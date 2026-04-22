package com.glimmer.shopping.shoppingmall.Handler;


import com.glimmer.shopping.shoppingmall.Exception.BusinessException;
import com.glimmer.shopping.shoppingmall.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "系统内部错误：" + e.getMessage());
    }
}
