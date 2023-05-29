package com.keyuan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/6
 **/
@AllArgsConstructor
@Data
public class Result {
    private Integer code;
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;
    public static Result ok(){
        return new Result(HttpStatus.OK.value(), true,null,null,null);
    }
    public static Result ok(Object data){
        return new Result(HttpStatus.OK.value(),true,null,data,null);
    }

    public static Result ok(Integer code,Object data){
        return new Result(code, true,null,null,null);
    }
    /**
     * 返回多个数据
     * @param datas
     * @return
     */
    public static Result ok(List<Object> datas){
        return new Result(HttpStatus.OK.value(),true,null,datas,null);
    }

    public static Result fail(String errorMsg){
        return new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(),false,errorMsg,null,null);
    }
    public static Result fail(Integer code,String errorMsg){
        return new Result(code,false,errorMsg,null,null);
    }
}
