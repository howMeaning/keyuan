package com.keyuan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/24
 **/
@Data
public class UserDTO {

    private Long id;

    private String name;

    private String icon;

    private Double x;

    private Double y;

    private String token;
}
