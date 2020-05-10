package com.nxist.gmall.gmallredissontest.redissonTest;

import com.nxist.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 * @author YuanmaoXu
 * @date 2020/3/21 13:40
 */

@Controller
public class RedissonController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("testRedisson")
    @ResponseBody
    public String testRedisson() {
        RLock lock = redissonClient.getLock("lock");//获取分布式锁
        Jedis jedis = redisUtil.getJedis();
        lock.lock();//加分布式锁
        try {
            String v = jedis.get("k");
            if (StringUtils.isBlank(v)) {
                v = "1";
            }
            System.out.println("->" + v);
            jedis.set("k", Integer.parseInt(v) + 1 + "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }finally {
            jedis.close();
            lock.unlock();//解除分布式锁
        }
        return "success";
    }
}
