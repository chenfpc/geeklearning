package RedisLearning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JedisUtils {
    @Autowired
    private JedisPool pool;
    /**
     * 删除多个字符串key 并释放连接
     *
     * @param keys*
     * @return 成功返回value 失败返回null
     */
    public boolean mdel(List<String> keys) {
        Jedis jedis = null;
        boolean flag = false;
        try {
            jedis = pool.getResource();//从连接借用Jedis对象
            Pipeline pipe = jedis.pipelined();//获取jedis对象的pipeline对象
            for (String key : keys) {
                pipe.del(key); //将多个key放入pipe删除指令中
            }
            pipe.sync(); //执行命令，完全此时pipeline对象的远程调用
            flag = true;
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return flag;
    }


    /**
     * 根据pattern返回满足条件的keys
     * @param pattern
     * @return
     */
    public Set keys(String pattern) {
        Jedis jedis = null;
        Set set = null;
        try {
            jedis = pool.getResource();//从连接借用Jedis对象
            Set keySets = jedis.keys(pattern);
            set = keySets;
        } catch (Exception e) {
            pool.returnBrokenResource(jedis);
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return set;
    }
}
