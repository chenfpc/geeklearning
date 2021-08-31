import RedisLearning.JedisUtils;
import RedisLearning.RedisApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class RedisTest {
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private JedisUtils jedisUtils;
    @Test
    public void testCommond() {
        // 工具类初始化
        Jedis jedis = jedisPool.getResource();

        for (int i = 0; i < 100; i++) {
            // 设值
            jedis.set("n" + i, String.valueOf(i));
        }
        jedis.close();;
        System.out.println("keys from redis return =======" + jedis.keys("*"));

    }

    // 使用pipeline批量删除
    @Test
    public void testPipelineMdel() {
        // 工具类初始化
        List<String> keys = new ArrayList<>();
        HashSet<String> keySet =  (HashSet) jedisUtils.keys("n*");
        keys =new ArrayList<String>(keySet);
        jedisUtils.mdel(keys);
        System.out.println("after mdel the redis return ---------" + jedisUtils.keys("*"));
    }

}
