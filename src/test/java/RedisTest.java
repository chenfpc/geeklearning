import RedisLearning.JedisUtils;
import RedisLearning.RedisApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.*;

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


    @Test
    public void pipeCompare() {
        Jedis jedis = jedisPool.getResource();;
        Map<String, String> data = new HashMap<String, String>();
        jedis.select(8);//使用第8个库
        jedis.flushDB();//清空第8个库所有数据
        // hmset
        long start = System.currentTimeMillis();
        // 直接hmset
        for (int i = 0; i < 10000; i++) {
            data.clear();  //清空map
            data.put("k_" + i, "v_" + i);
            jedis.hmset("key_" + i, data); //循环执行10000条数据插入redis
        }
        long end = System.currentTimeMillis();
        System.out.println("    共插入:[" + jedis.dbSize() + "]条 .. ");
        System.out.println("1,未使用PIPE批量设值耗时" + (end - start) / 1000 + "秒..");
        jedis.select(8);
        jedis.flushDB();
        // 使用pipeline hmset
        Pipeline pipe = jedis.pipelined();
        start = System.currentTimeMillis();
        //
        for (int i = 0; i < 10000; i++) {
            data.clear();
            data.put("k_" + i, "v_" + i);
            pipe.hmset("key_" + i, data); //将值封装到PIPE对象，此时并未执行，还停留在客户端
        }
        pipe.sync(); //将封装后的PIPE一次性发给redis
        end = System.currentTimeMillis();
        System.out.println("    PIPE共插入:[" + jedis.dbSize() + "]条 .. ");
        System.out.println("2,使用PIPE批量设值耗时" + (end - start) / 1000 + "秒 ..");
//--------------------------------------------------------------------------------------------------
        // hmget
        Set<String> keys = jedis.keys("key_*"); //将上面设值所有结果键查询出来
        // 直接使用Jedis hgetall
        start = System.currentTimeMillis();
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        for (String key : keys) {
            //此处keys根据以上的设值结果，共有10000个，循环10000次
            result.put(key, jedis.hgetAll(key)); //使用redis对象根据键值去取值，将结果放入result对象
        }
        end = System.currentTimeMillis();
        System.out.println("    共取值:[" + jedis.dbSize() + "]条 .. ");
        System.out.println("3,未使用PIPE批量取值耗时 " + (end - start) / 1000 + "秒 ..");

        // 使用pipeline hgetall
        result.clear();
        start = System.currentTimeMillis();
        for (String key : keys) {
            pipe.hgetAll(key); //使用PIPE封装需要取值的key,此时还停留在客户端，并未真正执行查询请求
        }
        pipe.sync();  //提交到redis进行查询

        end = System.currentTimeMillis();
        System.out.println("    PIPE共取值:[" + jedis.dbSize() + "]条 .. ");
        System.out.println("4,使用PIPE批量取值耗时" + (end - start) / 1000 + "秒 ..");

        jedis.disconnect();
    }

}
