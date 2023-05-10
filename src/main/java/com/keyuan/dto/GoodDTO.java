package com.keyuan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/9
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoodDTO {
    private Long id;

    private String foodName;

    private String foodType;

    private Long soleNum;

    /**
     *
     * 大份 10
     * 中份 11
     * 小份 14
     */
    private Map<String, Map<String,Integer>> foodPrices;
    /**
     * 小食:
     * 火腿 2元
     * 鸡腿 6元
     */
    private Map<String,Map<String,Integer>> foodSnakes;
    /**
     * 可选:
     * 面条
     * 米粉
     * 河粉
     */
    private Map<String,List<Integer>> foodOptionals;

    private Integer flavorId;

    @TableField(exist = false)
    private String image;
}
