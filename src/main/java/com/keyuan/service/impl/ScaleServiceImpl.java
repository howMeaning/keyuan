package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.CollectionUtil;
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
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public List<ScaleDTO> getScale(Long goodId,Long shopId) {
       Scale scale = scaleMapper.selectScaleByGoodId(goodId,shopId);
       if (scale==null){
               return Collections.emptyList();
       }
           //不为null,进行字符的拆分转成List<ScaleDTO>
        String prices = scale.getPrice();
        String scales = scale.getScale();
        log.info("prices:{}",prices);
        log.info("scales:{}",scales);
        if (scales==null &&prices==null){
            return Collections.emptyList();
        }
        List<String> scaleList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        String[] priceStr = prices.split(",");
        String[] scaleStr = scales.split(",");
        Collections.addAll(scaleList,priceStr);
        Collections.addAll(priceList,scaleStr);
        log.info("scaleListlen:{}",scaleList.size());
        log.info("priceListlen:{}",priceList.size());
        List<ScaleDTO> scaleDTOS = new ArrayList<>();
        //将两个相同的集合转成一个Map集合
        Map<String, String> map= IntStream.range(0,scaleList.size())
                .collect(HashMap::new,(m,i)->m.put(scaleList.get(i),priceList.get(i)),(m,n)->{});
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            ScaleDTO scaleDTO = BeanUtil.copyProperties(scale, ScaleDTO.class);
            scaleDTO.setScaleName(stringStringEntry.getKey()).setScalePrice(new BigDecimal(stringStringEntry.getValue()));
            scaleDTOS.add(scaleDTO);
        }
        return scaleDTOS;
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
            scaleBuffer.append(scale.getScaleName()+",");
            priceBuffer.append(scale.getScalePrice()+",");
        }
        scaleBuffer.deleteCharAt(scaleBuffer.length()-1);
        priceBuffer.deleteCharAt(priceBuffer.length()-1);
        Scale scale = new Scale(null, scaleBuffer.toString(), priceBuffer.toString(), goodDTO.getId(),goodDTO.getShopId());
        log.info("scale:{}",scale);
        int i = scaleMapper.insertScale(scale);
        return i;
    }


}
