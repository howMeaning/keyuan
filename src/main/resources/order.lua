---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by Administrator.
--- DateTime: 2023/5/16 14:15
---
---解决的逻辑:
---不允许重复下单
---
---创建单操作
---
---创建消息队列
---
---第二种问题:需要先消费不确认才能进入pending-list
---
--
--local status = ARGV[1]
--
--
--
--local orderId = ARGV[3]
--
--local orderNumber = ARGV[4]
--
--local shopName = ARGV[5]
--
--local shopPhone = ARGV[6]
--

--
--
--
--redis.call(
--        "xadd",
--        streamName.."success",
--        orderId,
--        "orderId",orderId,
--        "orderNumber", orderNumber,
--        "shopName",shopName,
--        "shopPhone",shopPhone
--)
--
--if(status == 300)
--then
--
--    ---创建分组
--    redis.call("xgroup","create",streamName.."success","consumerA","0")
--    ---将超过20分钟未消费的转移到consumerB
--    redis.call("xclaim","test2","mygroup","consumerB",tostring(a),tostring(orderId))
--else
--    ---400
--    redis.call("xreadgroup","group","g1","consumer","orderConsumer","1",streamName,orderId)
--end

-----20分钟
local time = 20 * 60 * 1000

local orderKey = ARGV[1]

local orderId = ARGV[2]

local streamName = ARGV[3]

local orderNumber = ARGV[4]
--return  redis.call("HGET","test4","n2") boolean类型

-----查看是否有订单,如果有订单报 1
if  redis.call("HGET",orderKey,orderId) then
    return 1
end
---创建订单
redis.call("HSET",orderKey,orderId,ARGV[5])

redis.call(
        "xadd",
        streamName,
        orderId,
        "orderId",orderId,
        "orderNumber", orderNumber
)
---创建分组
redis.call("xgroup","create",streamName,"mygroup","0")
---将超过20分钟未消费的转移到consumerB
redis.call("xclaim",streamName,"mygroup","consumerB",time,tostring(orderId))

return 0