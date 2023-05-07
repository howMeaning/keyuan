package com.keyuan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/3/7
 **/
@Accessors(chain = true)
@Data
public class Good {
    @TableId
    private Long id;

    private String goodName;

    private Integer goodPrice;

    private GoodType goodType;

    @TableField(exist = false)
    private String image;

    private Long soleNum;


    private String isDetail;

}
