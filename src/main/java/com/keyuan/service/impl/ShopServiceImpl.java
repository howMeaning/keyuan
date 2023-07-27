package com.keyuan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.keyuan.dto.Result;
import com.keyuan.dto.ShopDTO;
import com.keyuan.dto.UserDTO;
import com.keyuan.entity.Shop;
import com.keyuan.mapper.ShopMapper;
import com.keyuan.service.IShopService;
import com.keyuan.service.IShopTypeService;
import com.keyuan.service.IUpLoadService;
import com.keyuan.service.IUserService;
import com.keyuan.utils.*;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.keyuan.utils.RedisContent.CACHESHOPGEO;

/**
 * @descrition:
 * 这里的几个功能:增删改查店铺,查询要用分表查询
 * 这里可能会有热点key问题,也有缓存一致性问题
 * @author:how meaningful
 * @date:2023/5/23
 **/
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Resource
    private ShopMapper shopMapper;

    @Resource
    private IUserService userService;

    @Resource
    private RedisSolve redisSolve;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUpLoadService upLoadService;

    @Resource
    private IShopTypeService shopTypeService;

    private static final ExecutorService GEO_SHOP_EXECUTOR = Executors.newSingleThreadExecutor();


    //防止缓存穿透
    //第一种方式:使用布隆过滤器
//    private CustomerBloomFilter<Long> customerBloomFilter = CustomerBloomFilter.create(Funnels.longFunnel(),100,0.03);

    //第二种方式:使用自制的布隆过滤器
    @Resource
    private CustomerBloomFilter customerBloomFilter;

    //当类进行加载过后进行查找
    @PostConstruct
    private void init(){
        GEO_SHOP_EXECUTOR.submit(this::loadShopData);
    }

    /**
     * 这里shop需要有x y
     * 这里应该要将shop放到guava过滤器当中
     * @param shopDTO
     * @return
     */
    @Override
    @Transactional
    public Result saveShop(ShopDTO shopDTO) {

        //这里需要将用户的phone拿到然后需要update到user表,这里的Phone需要判断是否合规
        String phone = shopDTO.getPhone();
        if (RegexUtil.isPhoneInvaild(phone)) {
            //如果不合格
            return Result.fail("抱歉,当前的手机格式不合规");
        }
        Shop shop = BeanUtil.copyProperties(shopDTO, Shop.class);

        //还需要将当前的ThreadLocal当中的id 进行update到t_shop表当中,这里还需要判断手机是否合规
        Integer result = userService.updateUserPhoneById(shopDTO.getPhone(), shop.getId());

        if (BeanUtil.isEmpty(result)){
            log.error("更新User失败!用户手机号可能已经存在");
        }
        //这里需要将shopDTO转成shop
        //这里还需要根据typeName查typeId,然后赋值给shop
        Long shopTypeId = shopTypeService.getShopTypeIdByName(shopDTO.getShopType());

        if (shopTypeId == null){
            return Result.fail("当前商店没有类型!");
        }

        //图片
        MultipartFile imageFile = shopDTO.getImageFile();

        if (imageFile != null){
            String image = upLoadService.uploadImage(imageFile);
            shop.setImage(image);
        }
        log.warn("当前商家没有照片!");

        shop.setTypeId(shopTypeId);

        // 然后得到当前的时间,然后赋值给createTime,继续赋值
        LocalDateTime now = LocalDateTime.now();
        shop.setCreateTime(now);

        shopMapper.insertShop(shop);

        //这里将shop的id进行插入到布隆过滤器当中
        customerBloomFilter.put(shop.getId());
        return Result.ok("插入Good成功!shopId:"+shop.getId()+"typeId:"+shop.getTypeId());
    }

    /**
     * 三个步骤:
     * 首先查user缓存中是否有相关x轴,y轴,如果有则直接返回查距离,如果没有则报错提醒需要位置
     * @param typeId
     * @param current
     * @return
     */
    @Override
    public Result queryShopDistanceByTypeId(Long typeId,Integer current) {
        UserDTO user = UserHolder.getUser();
        Double x = user.getX();
        Double y = user.getY();
        //这里需要拿到x y
        if (x==null|y == null){
            return Result.fail("当前用户没有距离,无法查找最近相关!");
        }

        //如果当前没有x,y,应该有一个缓存,获取到上次x,y的缓存(后期做到的功能)


        //这里需要先找缓存,然后再找数据库
        //这里需要获取到当前页开始
        int from =(current - 1) *SystemContents.DEFAULT_PAGE_SIZE;

        int end = SystemContents.MAX_PAGE_SIZE;

        //这里需要指定分页参数,因为GEO数据结构下是带有分页结构
        GeoResults<RedisGeoCommands.GeoLocation<String>> result = stringRedisTemplate.opsForGeo().search(CACHESHOPGEO + typeId,
                GeoReference.fromCoordinate(x, y),
                new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().limit(end)
        );

        if (result == null){
            return Result.ok(Collections.emptyList());
        }


        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = result.getContent();

        //表示没有下一页
        if (content.size()<from){
            return Result.ok(Collections.emptyList());
        }

        List<Long> ids  = new ArrayList<>();
        Map<String,Object> objectObjectHashMap = new HashMap<>(content.size());

        //这里进行手动分页,需要进行下一页的拷贝
        content.stream().skip(from).forEach(r->{
            //获取的就是key,value一般是getDistance
            String shopId = r.getContent().getName();
            ids.add(Long.valueOf(shopId));
            Distance distance = r.getDistance();
            objectObjectHashMap.put(shopId,distance);
        });

        //根据id查询数据库
        List<Shop> shops = shopMapper.selectShopById(ids);
        for (Shop shop : shops) {
            Distance distance = (Distance) objectObjectHashMap.get(shop.getId().toString());
            shop.setDistance(distance.getValue());
        }
        return Result.ok(shops);
    }

    @Override
    public Result loadShopDataById(Long id,Long typeId) {
        //这里进行布隆过滤器问题
        if (!customerBloomFilter.contain(id)) {
            //这里表示布隆过滤器当中不存在id,所以直接直接返回这个商店不存在
            return  Result.fail(333,"这个商店不存在!");
        }
        Shop shop = shopMapper.selectShopOne( id, typeId);
        /*if (BeanUtil.isEmpty(shop)){
            return  Result.fail("这个商店不存在!");
        }*/
        Long add = stringRedisTemplate.opsForGeo().add(CACHESHOPGEO + typeId,
                new Point(shop.getX(), shop.getTypeId()), String.valueOf(shop.getId()));
        return Result.ok("加载完毕");
    }

    /**
     * 删除shop
     * 删除缓存+数据库
     * @param id
     * @return
     */
    @Override
    public Result removeShopById(Long id,Long typeId) {
        int i = shopMapper.deleteById(id);
        if (i>0){
            stringRedisTemplate.opsForGeo().remove(CACHESHOPGEO+typeId,String.valueOf(id));
        }
        return Result.ok("删除成功!");
    }

    private  void loadShopData(){
        //1.查询店铺的信息,这里实际上也可以分批的进行查询,如果店铺的数据过多的话
        List<Shop> list = shopMapper.selectAll();
        //2.把店铺分组,按照typeId分组,id相同则放到一个集合,这个集合应该是HashMap
        //TODO:这种写法只适合一对多的情况
        Map<Long,List<Shop>> map = list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        //3.遍历Map,分批完成写入Redis
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {
            //3.1获取类型id
            Long typeId = entry.getKey();
            //3.2获取同类型的店铺的集合
            List<Shop> value = entry.getValue();
            String key = CACHESHOPGEO+typeId;
            //TODO:这个locations的大小就是这个店铺集合的大小
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());

            //3.3写入 redis GEOADD key 经度 纬度 member
            for (Shop shop : value) {
                //这种效率不太好,这种方式下就是一个shop发送一个请求,连接Redis,千万个商铺就会有千万个连接
//                stringRedisTemplate.opsForGeo().add(key,new Point(shop.getX(),shop.getY()),shop.getId().toString());
                locations.add(new RedisGeoCommands.GeoLocation<>(shop.getId().toString(),new Point(shop.getX(),shop.getY())));

                //这里将所有shop的id添加到bloom过滤器当中
                customerBloomFilter.put(shop.getId());
            }
            //批量的增加,提高了很大的效率
            stringRedisTemplate.opsForGeo().add(key,locations);
        }
    }

}
