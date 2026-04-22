package com.glimmer.shopping.shoppingmall.util;

import lombok.Data;
/**
 * 统一 API 响应格式
 */
@Data
public class Result<T> {
    /**
     * 状态码（code）：表示请求的处理结果，常用的有：
     */
    private Integer code;
    /**
     * 提示信息（message）：对状态码的描述，提供更多上下文信息，常用的有：
      - "success"：请求成功
      - "error"：请求失败
      - "validation error"：参数验证失败
      - "not found"：资源未找到
     */
    private String message;
    /**
     * 数据（data）：请求成功时返回的具体数据，可以是任何类型，常见的有：
      - 对象：如用户信息、订单详情等
      - 列表：如商品列表、订单列表等
      - null：当没有数据需要返回时，可以设置为 null
     */
    private T data;

    // 构造器私有，通过静态方法创建
    private Result() {}

    // 成功（无数据）
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    // 成功（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    // 成功（自定义消息和数据）
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 失败（自定义错误码和消息）
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 失败（默认 400）
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }

    // 失败（带数据）
    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
