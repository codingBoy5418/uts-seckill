-- 第一个参数，是一个key的数组
local key = KEYS[1]
-- 第二个参数，是一个或多个参数,即...类型
local val = ARGV[1]
-- 第三个参数，过期时间
local expire = tonumber(ARGV[2])

-- 设置值，若key存在值，设置失败，返回0，若不存在值，设置成功，返回1
local keyExit = redis.call("SETNX", key, val);
-- 设置成功，设置过期时间，EXPIRE命令单位为秒，EXPIREAT单位是时间戳
if(keyExit >= 1) then
    redis.call("EXPIRE", key, expire)
    return true
end
-- 返回值为数值
return false