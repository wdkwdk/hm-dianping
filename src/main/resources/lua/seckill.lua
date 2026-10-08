local voucherId = ARGV[1]
local userId = ARGV[2]
local orderId = ARGV[3]

local stockKey = 'seckill:stock:' .. voucherId
local orderKey = 'seckill:order:' .. voucherId

local number = redis.call('get', stockKey)

-- 如果库存不足，返回1
if ((not number) or (tonumber(number) <= 0)) then
    return 1
end

-- 如果用户已购买，返回2
if (redis.call('SISMEMBER', orderKey, userId) == 1) then
    return 2
end

-- 用户未购买且库存充足，将用户写入缓存，库存减1,返回0
redis.call('SADD', orderKey, userId)
redis.call('incrby', stockKey, -1)
redis.call('XADD','stream:orders','*','userId',userId,'voucherId',voucherId,'id',orderId)
return 0

