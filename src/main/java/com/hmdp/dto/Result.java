package com.hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装接口响应状态、提示信息和返回数据。
 * @author wdk
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;

    /**
     * 创建不带数据的成功响应。
     */
    public static Result ok(){
        return new Result(true, null, null, null);
    }
    /**
     * 创建携带返回数据的成功响应。
     */
    public static Result ok(Object data){
        return new Result(true, null, data, null);
    }
    /**
     * 创建包含分页数据和总数的成功响应。
     */
    public static Result ok(List<?> data, Long total){
        return new Result(true, null, data, total);
    }
    /**
     * 创建携带错误信息的失败响应。
     */
    public static Result fail(String errorMsg){
        return new Result(false, errorMsg, null, null);
    }
}
