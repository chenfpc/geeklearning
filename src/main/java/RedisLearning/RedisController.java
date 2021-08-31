package RedisLearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * REDIS 常见命令操作
 * 包含pipeline以及批量操作
 */
@RestController
public class RedisController {
    @Autowired
    private JedisPool jedisPool;


    @PostMapping(value = "/test")
    public String test() {
        Jedis jedis = jedisPool.getResource();
        jedis.set("test", "test123");
        return jedis.get("test");
    }
}
