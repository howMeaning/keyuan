# 客源桌面小程序

## 1.版本:

| 技术        | 版本          |
| ----------- | ------------- |
| SpringBoot  | 2.5.7         |
| Redis       | 2.6.4         |
| RabbitMQ    | 2.5.7         |
| MySql       | 8.0.26        |
| Mybatis     | 2.1.4         |
| MybatisPlus | 3.4.3         |
| Letture     | 6.2.2         |
| Druid       | 1.2.8         |
| webSocket   | 2.0.0.RELEASE |
| redisson    | 3.13.6        |
| letture     | 6.2.2         |
| hutool      | 5.8.11        |
| jdk         | 8             |

前端:

后端:SpringBoot,Redis,

github的路径:https://github.com/howMeaning/keyuan

git的路径:

## 2.功能的实现

### (1)食品模块

#### 1.1上架食品

Post请求路径:http://localhost:8081/good/insertGood

**注意:以下的所有json,都需要在请求体内发送**

JSON模板:

```
 {
        "id": null,
        "foodName": "食品名称",
        "foodType": "textType",
        "foodSnakes": {
            "小食1": 100,
            "小食2": 100,
            "小食3": 100
        },
        "foodPrice": 10,
        "scales": [
            {
                "id": null,
                "scale": "小",
                "price": 10,
                "goodId": 12
            }
        ],
        "foodOptionals": [
            "米粉",
            "肠粉",
            "炒粉"
        ],
        "foodFlavor": false,
        "shopId": 10,
        "image": null
    }
```

#### 1.2查找食品

Get请求:http://localhost:8081/good/searchAll/{shopId}

- 需要传一个ShopId

例如:http://localhost:8081/good/searchAll/1

运用的是restful风的get请求

#### 1.3热销榜前五食品

Get请求:http://localhost:8081/good/searchAll/{shopId}

- 需要传一个ShopId

例如:http://localhost:8081/good/searchAll/1

运用的是restful风的get请求

#### 1.4:根据id逻辑下架食品

- 逻辑删除食品下,食品并不是真正的删除,会有一个删除时间,到了时间需要发个**(1.5)删除食品**请求
- 这个时候删除食品可以重新上架食品

Post请求:http://localhost:8081/good/logicRemoveGoodbyId

需要传一个参数:

id:食品id

例如:

![image-20230529020234584](image\image-20230529020234584.png)

#### 1.5根据id下架食品

- 真正的下架食品

post请求:http://localhost:8081/good/removeGoodById

需要传两个参数:

id:食品id

shopId:商家id

#### 1.6:根据id查找所有小食:

#### 1.7根据id查找所有规模

### (2)订单模块:

#### 2.1:创建订单:

当支付的时候调用这个接口

请求:

POST请求:http://localhost:8081/order/createOrder

三种状态码:

```java
 /**
     * 交易成功,交易失败,交易等待
     */
    public static final int SUCCESS_STATUS=400;
	
    public static final int FAIL_STATUS=333;

    public static final int WAITING_STATUS=300;
```

参数必须放到请求体当中:

json:

```json
{
        "id": null,
        "orderNumber": 1001,
        "goodId": "1001,1002",
        "shopId": 10,
        "remark": "不要放辣椒",
        "userId": 10001,
        "useType": 0,
        "tableId": 10,
        "createTime": "2023-05-29T02:40:13.37",
        "payTime": "2023-05-29T03:40:13.37",
        "refundTime": "2023-05-29T04:40:13.37",
        "status": 333,
        "payMoney": 10.5
    }
```

形式需要以这种为标准

#### 2.2.创建确认订单

- 当支付完成的时候调用这个接口

POST请求:http://localhost:8081/order/confirmOrder

```
{
        "id": null,
        "orderNumber": 1001,
        "goodId": "1001,1002",
        "shopId": 10,
        "remark": "不要放辣椒",
        "userId": 10001,
        "useType": 0,
        "tableId": 10,
        "createTime": "2023-05-29T02:40:13.37",
        "payTime": "2023-05-29T03:40:13.37",
        "refundTime": "2023-05-29T04:40:13.37",
        "status": 333,
        "payMoney": 10.5
    }
```

#### 2.3webSocket接受订单消息

地址:http://localhost:8081/order/webSokect/{uid}

参数:uid,userId,这里需要用RestFul风格

### (3)商家模块

#### 3.1查找商家距离根据类型

Get请求:http://localhost:8081/shop/of/distance/{typeId}

#### 3.2添加商家

post请求:http://localhost:8081/shop/of/saveShop

json:

```
{
        "id": null,
        "shopType": "汉堡",
        "shopName": "华莱士",
        "address": "xxxxx",
        "phone": "13612345678",
        "function": "外卖定制",
        "openHour": "2023-10-23T23:21:11",
        "x": 11.3,
        "y": 11.4,
        "sold": 100,
        "avgPrice": 15,
        "imageFile": null,
        "area": "广州"
}
```

#### 3.3加载商家

- 只有加载了才会有距离显示,当某个商店意外无法显示则需要加载

Post请求;http://localhost:8081/shop/of/saveShop

两个参数:

id:

typeid:

这两个参数不一定放到请求体

#### 3.4删除商家:

Post请求:http://localhost:8081/shop/of/deleteShopById

id:

typeid:

这两个参数不一定放到请求体

### (4)用户模块:

#### 4.1创建用户

Post请求:http://localhost:8081/user/createUser

json

```
{
        "id": null,
        "name": "test",
        "shopId": 10,
        "phone": "13612345678",
        "icon": null,
        "x": 12.11,
        "y": 12.22,
        "area": "深圳",
        "token": "US121ASAF",
        "createTime": "2023-05-29T10:08:59.14"
    
```

需要放到请求体

#### 4.2改变用户距离

Post请求:http://localhost:8081/user/updateArea

需要传递三个参数:

x:经度

y:维度

id:userId

#### 4.3根据token获取用户

Get请求:http://localhost:8081/user//getUser

