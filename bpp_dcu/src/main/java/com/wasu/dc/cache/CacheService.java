package com.wasu.dc.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.alibaba.com.caucho.hessian.io.HessianInput;
import com.alibaba.com.caucho.hessian.io.HessianOutput;
import com.wasu.dc.cache.methodcache.CacheConfig;

public class CacheService {
	private static Logger logger = LoggerFactory.getLogger(CacheService.class);

	private static ThreadLocal<String> userLocal = new ThreadLocal();

	public static void set(String key, Object value) throws Exception {
		server(key).set(key, value);
	}

	public static void setAll(String key, Object value) throws Exception {
		List<CacheServer> servers = CacheServers.getActiveServers();
		for (CacheServer server : servers) {
			server(key).set(key, value);
		}
	}

	public static Object get(String key) throws Exception {
		return server(key).get(key);
	}

	public static List<Object> mget(String[] keys) throws Exception {
		if (keys == null || keys.length <= 0) {
			return null;
		}
		return server(keys[0]).mget(keys);
	}

	public static void remove(String key) throws Exception {
		server(key).remove(key);
	}

	private static CacheServer server(String key) {
		return CacheServers.getShardServer(key);
	}

	public static byte[] serialize(Object object) throws Exception {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//ObjectOutputStream oos = new ObjectOutputStream(baos);
		HessianOutput ho = new HessianOutput(baos);
		ho.writeObject(object);
		//oos.writeObject(object);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	public static Object unserialize(byte[] bytes) throws Exception {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		//ObjectInputStream ois = new ObjectInputStream(bais);
		HessianInput hi = new HessianInput(bais);
		//return ois.readObject();
		return hi.readObject();
	}

	public static Map<String, Object> describe(Object value) throws Exception {
		Map map = BeanUtils.describe(value);
		map.remove("class");
		return map;
	}

	public static void setUser(String userId) {
		userLocal.set(userId);
		CacheServers.setShardToken(userId);
	}

	public static String getUser() {
		return (String) userLocal.get();
	}

	public static boolean isCacheEnable() {
		return (CacheConfig.isCacheEnable()) && (CacheServers.isActive());
	}

	public static void log(String message) {
		if (CacheConfig.isLogDebug())
			logger.debug(message);
		else if (CacheConfig.isLogInfo())
			logger.info(message);
	}

	public static void expire(String key, int delayTime) throws Exception {
		server(key).expire(key, delayTime);
	}

	public static Long push(String key, String... values) throws Exception {
		return server(key).push(key, values);
	}

	public static String pop(String key) throws Exception {
		return server(key).pop(key);
	}

	public static Long queueLen(String key) throws Exception {
		return server(key).queueLen(key);
	}

	public static void main(String[] args) {
		try {
			Jedis jedis = null;
			for (String server : new String[] { "10.1.2.232" }) {
				jedis = new Jedis(server);
				for (byte[] key : jedis.keys("*".getBytes())) {
					jedis.del(new byte[][] { key });
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
