package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.User;
import com.keyuan.mapper.UserMapper;
import com.keyuan.service.IUserService;
import com.keyuan.utils.RedisContent;
import com.keyuan.utils.RedisSolve;
import com.keyuan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @descrition:
 * 这里需要实现的几个功能
 * 插入当前的user
 * 然后需要查看是否有shop,如果有则返回
 * 需要随时5分钟获取user定位,来实现user附近的功能
 *
 * 这里的几个难点:
 * 哪里选择进行user缓存的存储 这个存储必须和Interrupter的时机相同
 *
 *这里应该有一个queryXY方法,方法中有修改经度维度的方法,然后存在ThreadLocal当中
 * @author:how meaningful
 * @date:2023/5/25
 **/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisSolve redisSolve;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param user
     * @return
     */
    @Override
    @Transactional
    public Result createUser(User user) {
        //这里生成token
        String token = UUID.randomUUID().toString(true);
        user.setToken(token);
        //找缓存
        int success = userMapper.insertUser(user);
        if (success==0){
            return Result.fail("创建用户失败!");
        }

        Map<Long,String> userMap = new HashMap<>();

        //将用户存在缓存当中
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        log.info("UserDTO:{}",userDTO);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),true,true);
        map.replaceAll((fieldName, fieldValue) -> fieldValue.toString());
        log.info("map:{}",map);
        stringRedisTemplate.opsForHash().putAll(RedisContent.CACHEUSER+token,map);

        userMap.put(user.getId(),token);
        return Result.ok(userMap);
    }

    @Override
    public Integer updateUserPhoneById(String phone,Long id) {
        //一个人创建了多个店,如果存在,则不需要修改
        Integer i = userMapper.updateUserPhoneById(phone,id);
        return i;
      }

    @Override
    public Result getUserByToken(String token) {
        UserDTO userDTO=null;
        //首先找缓存,如果缓存中存在则直接返回,如果缓存当中不存在则查数据库,进行插入缓存
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(RedisContent.CACHEUSER + token);
        //这里表示存在缓存,则直接序列换返回
        userDTO = BeanUtil.fillBeanWithMap(entries, new UserDTO(), false);
        if (BeanUtil.isEmpty(userDTO)){
            //不存在则找数据库
            User user = userMapper.selectUserByToken(token);
            if (user!=null){
                userDTO = BeanUtil.copyProperties(user, UserDTO.class);
                Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                        CopyOptions.create().setIgnoreNullValue(true)
                                .setFieldValueEditor((feildName, fieldValue) -> fieldValue.toString()));
               stringRedisTemplate.opsForHash().putAll(RedisContent.CACHEUSER+token,map);
                return Result.ok(userDTO);
            }
            //这里表示数据库不存在
            return Result.fail("用户未创建!");
        }

        return Result.ok(userDTO);
    }

    @Override
    public Result updateAreaById(Double x,Double y,Long id){
        //这里进行修改数据库操作,如果成功则删除缓存
        Integer i = userMapper.updateAreaById(x, y,id);
        UserHolder.removeUser();
        if (i>0){
            //这里表示成功删除缓存
            stringRedisTemplate.delete(RedisContent.CACHEUSER+id);
            return Result.ok("修改位置成功!");
        }else {
            return Result.fail("修改位置失败,可能未打开定位!");
        }
    }

    /**
     * 这里尽量还是前端传好一点
     * @param shopId
     * @return
     */
    @Override
    public Long selectUserByShopId(Long shopId) {
        return userMapper.selectUserByShopId(shopId);
    }

    @Override
    public User selectUserById(Long id) {
        return  userMapper.selectUserById(id);
    }


}
