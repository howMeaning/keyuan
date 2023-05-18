package com.keyuan.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.keyuan.entity.Optional;
import com.keyuan.entity.Snake;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @descrition: 专门用来前后端交互的类
 * @author:how meaningful
 * @date:2023/5/9
 **/
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoodDTO {
    /**
     * id,可以没有,数据库自动生成
     */
    private Long id;
    /**
     * 食品名
     */
    private String foodName;
    /**
     * 食品类型
     */
    private String foodType;
    /**
     * 销量
     */
    private Long soleNum;

    /**
     *
     * 大份 10
     * 中份 11
     * 小份 14
     */
    private String foodPrices;
    /**
     * 小食:
     * 火腿 2元
     * 鸡腿 6元
     */
    private   Map<String, Integer> foodSnakes;
    /**
     * 可选:
     * 面条
     * 米粉
     * 河粉
     */
    private String foodOptionals;
    /**
     * 口味要求,有就是true没有就是false
     */
    private Boolean foodFlavor;
    /**
     * 照片文件
     */
    @TableField(exist = false)
    private MultipartFile image;
}
