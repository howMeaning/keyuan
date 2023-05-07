package com.keyuan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/6
 **/
@AllArgsConstructor
@Data
public class Result {
    private Boolean success;
    private String errorMsg;
    private Object data;
    private Long total;
    public static Result ok(){
        return new Result(true,null,null,null);
    }
    public static Result ok(Object data){
        return new Result(true,null,data,null);
    }

    /**
     * 返回多个数据
     * @param datas
     * @return
     */
    public static Result ok(List<Object> datas){
        return new Result(true,null,datas,null);
    }

    public static Result fail(String errorMsg){
        return new Result(false,errorMsg,null,null);
    }
}
