package com.wasu.pub.cache.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.wasu.pub.cache.CacheService;

public class CacheServerRedis extends CacheServer {
	private JedisPool pool;

	public void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(paramInt("maxTotal",200));
		config.setMaxIdle(paramInt("maxIdle",20));
		this.pool = new JedisPool(config, param("ip"), paramInt("port", 6379));
	}

	public boolean test() { 
		Jedis jedis = null;
		try {
			jedis = (Jedis) this.pool.getResource();
			jedis.get("test");
		} catch (Exception ex) {
			return false;
		} finally {
			if (jedis != null) {
				this.pool.returnResourceObject(jedis);
			}
		}
		return true;
	}

	public synchronized void  set(final String key, final Object value) throws Exception {
		operate(new JedisOperate() {
			public Object operate(Jedis jedis) throws Exception {
				String retObj = jedis.set(key.getBytes(),value == null ? null : CacheService.serialize(value));
				int expireTime=CacheConfig.getCacheKeyExpire();//key过期时间，单位秒
				if(expireTime==0){//如果设置为0，即设置key永不过期
					jedis.persist(key.getBytes());//设置key永不过期
				}else{
					jedis.expire(key.getBytes(),expireTime);//设置key过期时间，单位为秒
				}
				
				return retObj;
			}
		});
	}

	public synchronized void  mset(List<Object> keyValues) throws Exception {
		final List byteKeyValues = new ArrayList();
		for (int i = 0; i < keyValues.size(); i++) {
			byteKeyValues.add(i % 2 == 0 ? keyValues.get(i).toString().getBytes()
					: CacheService.serialize(keyValues.get(i)));
		}

		operate(new JedisOperate() {
			public Object operate(Jedis jedis) throws Exception {
				return jedis.mset((byte[][]) byteKeyValues.toArray(new byte[0][]));
			}
		});
	}

	public Object get(final String key) throws Exception {
		return operate(new JedisOperate() {
			public Object operate(Jedis jedis) throws Exception {
				byte[] value = jedis.get(key.getBytes());
				return CacheService.unserialize(value);
			}
		});
	}

	public Long push(final String key, final String ... values) throws Exception{

		return (Long) operate(new JedisOperate<Object>() {
			public Object operate(Jedis jedis){
				return jedis.lpush(key, values);
			}
		});
	}

	public List<Object> mget(String[] keys) throws Exception {
		final List byteKeys = new ArrayList();
		for (String key : keys) {
			byteKeys.add(key.getBytes());
		}
		return (List) operate(new JedisOperate() {
			public List<Object> operate(Jedis jedis) throws Exception {
				List<byte[]> values = jedis.mget( (byte[][]) byteKeys.toArray(new byte[0][]) );
				List list = new ArrayList();
				for (byte[] value : values) {
					list.add(CacheService.unserialize(value));
				}
				return list;
			}
		});
	}

	public synchronized void remove(final String key) throws Exception {
		operate(new JedisOperate() {
			public Object operate(Jedis jedis) {
				return jedis.del(new byte[][] { key.getBytes() });
			}
		});
	}

	public Set<String> keys(final String pattern) throws Exception {
		return (Set) operate(new JedisOperate() {
			public Set<String> operate(Jedis jedis) {
				Set keys = new HashSet();
				for (byte[] key : jedis.keys(pattern.getBytes())) {
					keys.add(new String(key));
				}
				return keys;
			}
		});
	}

	public synchronized  void clear() throws Exception {
		operate(new JedisOperate() {
			public Object operate(Jedis jedis) {
				for (byte[] key : jedis.keys("*".getBytes())) {
					jedis.del(new byte[][] { key });
				}
				return null;
			}
		});
	}
	
	/**
	 * 将指定Key的Value原子性的递增1
	 * @param key
	 * @throws Exception
	 */
	public void incr(String key) throws Exception {
		operate(new JedisOperate() {
			public Object operate(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	private <T> T operate(JedisOperate<T> operate) throws Exception {
		if ((!CacheConfig.isCacheEnable()) || (!isActive())) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = (Jedis) this.pool.getResource();
			return operate.operate(jedis);
		} finally {
			if (jedis != null)
				this.pool.returnResourceObject(jedis);
		}
	}

	public String toString() {
		return param("ip") + ":" + param("port");
	}

	public static abstract interface JedisOperate<T> {
		public abstract T operate(Jedis paramJedis) throws Exception;
	}

	@Override
	public void expire(String key, int delayTime) throws Exception {
		operate(new JedisOperate() {
		      public Object operate(Jedis jedis) throws Exception {
		    	  Object returnObj = jedis.expire(key.getBytes(),CacheConfig.getCacheKeyExpire());//单位为秒
		    	  return returnObj;
		      }
		    });
	}
}
