package com.keyuan;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.keyuan.dto.GoodDTO;
import com.keyuan.dto.ScaleDTO;
import com.keyuan.entity.Good;
import com.keyuan.entity.Snake;
import com.keyuan.mapper.GoodMapper;
import com.keyuan.service.IScaleService;
import com.keyuan.service.ISnakeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

/**
 * @descrition:
 * @author:how meaningful
 * @date:2023/5/30
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoodTest {
    @Resource
    private GoodMapper goodMapper;
    @Resource
   private IScaleService scaleService;

    @Resource
    private ISnakeService snakeService;

    @Test
    public  void  testString(){
        String[] s = new String[]{"1","2","3"};
        String join = String.join(",", s);
        System.out.println(join);
        List<String> list = Arrays.asList(s);
        System.out.println(list);
    }
    @Test
    public void testGoodType(){
        Map<String,List<GoodDTO>> map = new HashMap<>();
        List<Good> goods = goodMapper.selectAll();
        List<Snake> snake =null;
        List<ScaleDTO> scaleDTOS  =null;
        List<GoodDTO> goodDTOS = null;
        List<String> list = goodMapper.selectAllType();
        Set<String> set  = new HashSet<>(list);
        for (String s : set) {
            goodDTOS = new ArrayList<>();
            for (Good good : goods) {
                if (s.equals(good.getFoodType())){
                    Integer hasScale = good.getHasScale();
                    if (hasScale!=null){
                        //这里
                        scaleDTOS = scaleService.getScale(good.getId(), good.getShopId());
                    }
                    String snakeId = good.getSnakeId();
                    if (!StrUtil.isBlank(snakeId)){
                        snake = snakeService.getSnake(snakeId, good.getShopId());
                    }
                    //这里
                    //这里不应该带销量
                    good.setSoleNum(null);
                    GoodDTO goodDTO = BeanUtil.copyProperties(good, GoodDTO.class);
                    goodDTO.setFoodSnakes(snake).setScales(scaleDTOS);
                    goodDTO.setImage(good.getImage());
                    goodDTOS.add(goodDTO);

                }
            }
            map.put(s,goodDTOS);

            System.out.println("s:+"+s+"+goodDTOs:"+goodDTOS.size());

        }

    }
}
