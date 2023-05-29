package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.Result;
import com.keyuan.dto.ScaleDTO;
import com.keyuan.entity.Scale;
import com.keyuan.mapper.ScaleMapper;
import com.keyuan.service.IScaleService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/26
 **/
@Slf4j
@Service
public class ScaleServiceImpl extends ServiceImpl<ScaleMapper, Scale> implements IScaleService {
    @Resource
    private ScaleMapper scaleMapper;



    /**
     * 获取Scale,先从缓存找,再找数据库
     * @param goodId
     * @return
     */
    @Override
    public Result getScale(Long goodId,Long shopId) {
           Scale scale = scaleMapper.selectScaleByGoodId(goodId,shopId);
           if (scale==null){
               return Result.fail("抱歉,当前商品没有规格!");
           }
            return Result.ok(scale);
    }

    /**
     * 插入规格
     *获取到所有的价格大小,规格大小,然后变成字符串插入到数据库
     * @param goodDTO
     */
    @Override
    public int insertScale(GoodDTO goodDTO) {
        List<ScaleDTO> scales = goodDTO.getScales();
        StringBuffer scaleBuffer = new StringBuffer();
        StringBuffer priceBuffer = new StringBuffer();
        for (ScaleDTO scale : scales) {
            scaleBuffer.append(scale.getScale()+",");
            priceBuffer.append(scale.getPrice()+",");
        }
        scaleBuffer.deleteCharAt(scaleBuffer.length()-1);
        priceBuffer.deleteCharAt(priceBuffer.length()-1);
        Scale scale = new Scale(null, scaleBuffer.toString(), priceBuffer.toString(), goodDTO.getId(),goodDTO.getShopId());
        log.info("scale:{}",scale);
        int i = scaleMapper.insertScale(scale);
        return i;
    }


}
