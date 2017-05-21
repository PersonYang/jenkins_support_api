package  com.apin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2016/8/19.
 */
public class RedisExecuteTemplate {

    private static final Logger logger= LoggerFactory.getLogger(RedisExecuteTemplate.class);
    @Autowired
    private JedisPool jedisPool;

    public Object excute(ExecuteCallback executeCallback) throws Throwable{
        Jedis jedis=null;
        try
        {
            jedis=jedisPool.getResource();
            return executeCallback.command(jedis);
        }catch (Throwable e){
            logger.error("Redis error",e.getCause());
            throw e;
        }finally {
            if(null!=jedis){
                jedis.close();
            }
        }
    }

    public interface ExecuteCallback{
        public Object command(Jedis jedis);
    }
}
