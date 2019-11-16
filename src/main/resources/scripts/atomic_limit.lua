--
-- Created by IntelliJ IDEA.
-- User: nico
-- Date: 2019/11/15
-- Time: 10:14 下午
-- To change this template use File | Settings | File Templates.
--

local limit = tonumber(ARGV[1])
if limit == 0
then
    return false
end
local cnt = redis.call('INCR', KEYS[1])
if cnt > limit
then
    return true
end
if cnt == 1
then
    redis.call('PEXPIRE', KEYS[1], ARGV[2])
end
return false


--local times = redis.call('incr',KEYS[1])
--
--if times == 1 then
--    --redis.call('expire',KEYS[1], ARGV[1])
--end
--
--if times > tonumber(ARGV[2]) then
--    return true
--end
--return false