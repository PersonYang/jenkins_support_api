package com.apin.common.utils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 2016/8/19.
 */
public class Redis {

    private static final Logger logger= LoggerFactory.getLogger(Redis.class);
    
    @Autowired
    private RedisExecuteTemplate redisExecuteTemplate;


    
    public Long removeKey(RedisType redisType,String key) throws Throwable{
    	final String redisKey=redisType.toString()+key;
        return (Long)redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
            @Override
			public Object command(Jedis jedis) {
                return jedis.del(redisKey);
            }
        });
    }
    /**
     * @param redisType
     * @param key
     * @return
     * @throws Throwable
     */
    public String get(RedisType redisType,String key) throws Throwable{
        final String redisKey=redisType.toString()+key;
        return (String)redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
            @Override
			public Object command(Jedis jedis) {
                return jedis.get(redisKey);
            }
        });
    }

    public String hget(RedisType redisType,String key, final String field) throws Throwable{
        final String redisKey=redisType.toString()+key;
        return (String)redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
            @Override
            public Object command(Jedis jedis) {
                return jedis.hget(redisKey, field);
            }
        });
    }

    public List<String> hmget(RedisType redisType,String key, final String... fields) throws  Throwable{
        final String redisKey=redisType.toString()+key;
        return (List<String>) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
            @Override
            public Object command(Jedis jedis) {
                return jedis.hmget(redisKey,fields);
            }
        });
    }
    @SuppressWarnings("unchecked")
	public List<String> hvals(RedisType redisType,String key) throws  Throwable{
    	final String redisKey=redisType.toString()+key;
    	return (List<String>) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
    		@Override
    		public Object command(Jedis jedis) {
    			return jedis.hvals(redisKey);
    		}
    	});
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    public List<String> lrange(RedisType redisType,String key) throws  Throwable{
    	final String redisKey=redisType.toString()+key;
    	return getList(redisKey);
    }
    @SuppressWarnings("unchecked")
    public List<String> getList(final String key) throws  Throwable{
    	return (List<String>) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
    		@Override
    		public Object command(Jedis jedis) {
    			return jedis.lrange(key, 0, -1);
    		}
    	});
    }

	/**
	 * 
	 * 添加到数组中
	 * @param enterroom
	 * @param groupId
	 * @param merchantId
	 * @throws Throwable
	 */
	public void save(RedisType type, String groupId, final String merchantId) throws Throwable {
		final String redisKey = type + groupId;
		saveList(redisKey, merchantId);
	}
	
	
	
	
	
	public void saveOfferPriceList(RedisType type, String helfKey,String value) throws Throwable{
		final String key =  type+helfKey;
		saveList(key, value);
	}
	
	/**
	 * 
	 * key type list 添加数据
	 * @param key
	 * @param value
	 * @throws Throwable
	 * 
	 */
	public long saveList(final String key,final String value) throws Throwable{
		return (long) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback(){
			@Override
			public Object command(Jedis jedis) {
				return jedis.rpush(key, value);
			}
		});
	}
	public String save(final String key,final String value) throws Throwable{
		return (String) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback(){
			@Override
			public Object command(Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}
	
	public long expire(RedisType redisType,String key, final int delay) throws Throwable{
        final String redisKey=redisType.toString()+key;
        return (long) redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback() {
            @Override
            public Object command(Jedis jedis) {
                return jedis.expire(redisKey, delay);
            }
        });
    }

	public long expireAt(final String saveKey,final Date endDate) throws Throwable {
		return (long)redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback(){
			@Override
			public Object command(Jedis jedis) {
				 return jedis.expireAt(saveKey, endDate.getTime());
			}
		});
	}

	public long getExpire(final String saveKey) throws Throwable {
		return (long)redisExecuteTemplate.excute(new RedisExecuteTemplate.ExecuteCallback(){
			@Override
			public Object command(Jedis jedis) {
				return jedis.ttl(saveKey);
			}
		});
	}

	public long increament(RedisType type, String groupId) throws Throwable {
		final String redisKey = type + groupId;
		String auctionNum = get(type, groupId);
		if(StringUtils.isNumeric(auctionNum)){
			long num = Long.valueOf(auctionNum)+1;
			save(redisKey, String.valueOf(num));
			return num;
		}else{
			save(redisKey, String.valueOf("1"));
			return 1;
		}
	}


}
