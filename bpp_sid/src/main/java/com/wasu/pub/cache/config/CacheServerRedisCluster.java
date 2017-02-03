package com.wasu.pub.cache.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import com.wasu.pub.cache.CacheService;

public class CacheServerRedisCluster extends CacheServer {
	private JedisCluster jc ;

	public void init() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(paramInt("maxTotal", 200));
		config.setMaxIdle(paramInt("maxIdle", 20));
		String hostAndPorts = param("hostAndPorts");
		String[] address = hostAndPorts.split(";");
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		for (String addr : address) {
			String[] hostAndPort = addr.split(":");
			jedisClusterNodes.add(new HostAndPort(hostAndPort[0],Integer.parseInt(hostAndPort[1])));
		}

		//初始化参数(timeout:5000, maxRedirections:100)
		//timeout默认是2000考虑到网络环境可能有延误
		//maxRedirection默认是5考虑到集群数量的不同该值适当增大
		jc = new JedisCluster(jedisClusterNodes,paramInt("timeout", 500),paramInt("maxRedirection", 200),config);


	}

	public boolean test() {
//		Jedis jedis = null;
		try {
//			jedis = (Jedis) this.pool.getResource();
//			jedis.get("test");
			
			jc.get("test");
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	public synchronized void set(final String key, final Object value) throws Exception {
		operate(new JedisOperate() {
			public Object operate(JedisCluster jedis) throws Exception {
				String retObj = jedis.set(key.getBytes(), value == null ? null : CacheService.serialize(value));
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

	public synchronized void mset(List<Object> keyValues) throws Exception {
		final List<byte[]> byteKeyValues = new ArrayList<byte[]> ();
		for (int i = 0; i < keyValues.size(); i++) {
			byteKeyValues.add(i % 2 == 0 ? keyValues.get(i).toString().getBytes()
					: CacheService.serialize(keyValues.get(i)));
		}

		operate(new JedisOperate() {
			public Object operate(JedisCluster jedis) throws Exception {
				return jedis.mset((byte[][]) byteKeyValues.toArray(new byte[0][]));
			}
		});
	}

	public Object get(final String key) throws Exception {
		return operate(new JedisOperate() {
			public Object operate(JedisCluster jedis) throws Exception {
				
				byte[] value = jedis.get(key.getBytes());
				return CacheService.unserialize(value);
			}
		});
	}

	public List<Object> mget(String[] keys) throws Exception {
		final List<byte[]> byteKeys = new ArrayList<byte[]>();
		for (String key : keys) {
			byteKeys.add(key.getBytes());
		}
		return (List<Object>) operate(new JedisOperate() {
			public List<Object> operate(JedisCluster jedis) throws Exception {
				List<Object> list = new ArrayList<Object>();
				for (byte[] bs : byteKeys) {
					byte[] value = jedis.get(bs);
					list.add(CacheService.unserialize(value));
				}
				return list;
			}
		});
	}

	public synchronized void remove(final String key) throws Exception {
		operate(new JedisOperate() {
			public Object operate(JedisCluster jedis) {
				return jedis.del(new byte[][]{key.getBytes()});
			}
		});
	}

	public Set<String> keys(final String pattern) throws Exception {
//		return (Set) operate(new JedisOperate() {
//			public Set<String> operate(JedisCluster jedis) {
//				Set keys = new HashSet();
//				for (byte[] key : jedis.keys(pattern.getBytes())) {
//					keys.add(new String(key));
//				}
//				return keys;
//			}
//		});
		
		throw new Exception("jedis 当前版本不支持 keys操作");
	}

	public synchronized void clear() throws Exception {
//		operate(new JedisOperate() {
//			public Object operate(JedisCluster jedis) {
//				if (jedis == null) {
//					return null;
//				}
//				for (byte[] key : jedis.keys("*".getBytes())) {
//					jedis.del(new byte[][]{key});
//				}
//				return null;
//			}
//		});
		throw new Exception("jedis 当前版本不支持 keys操作");
	}

	private <T> T operate(JedisOperate<T> operate) throws Exception {
		if ((!CacheConfig.isCacheEnable()) || (!isActive())) {
			return null;
		}
//		Jedis jedis = null;
		try {
//			jedis = (Jedis) this.pool.getResource();
			
			return operate.operate(jc);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}



	public String toString() {
		//return param("ip") + ":" + param("port");
		return param("hostAndPorts");
	}

	public static abstract interface JedisOperate<T> {
		public abstract T operate(JedisCluster paramJedis) throws Exception;
	}

	public Long push(final String key, final String ... values) throws Exception{

		return (Long) operate(new JedisOperate<Object>() {
			public Object operate(JedisCluster jedis){
				return jedis.lpush(key, values);
			}
		});
	}

	public String pop(final String key) throws Exception{
		return (String) operate(new JedisOperate<Object>() {
			public Object operate(JedisCluster jedis){
				return jedis.rpop(key);
			}
		});
	}

	public Long queueLen(String key) throws Exception {
		return (Long) operate(new JedisOperate<Object>() {
			public Object operate(JedisCluster jedis) {return jedis.llen(key);}
		});
	}

	public synchronized void expire(final String key, int delayTime) throws Exception{
		operate(new JedisOperate<Object>() {
			public Object operate(JedisCluster jedis){
				return jedis.expire(key, delayTime);
			}
		});
	}
	
}
