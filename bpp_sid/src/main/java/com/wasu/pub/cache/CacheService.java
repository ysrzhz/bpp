package com.wasu.pub.cache;

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
import com.wasu.pub.cache.config.CacheConfig;
import com.wasu.pub.cache.config.CacheServer;
import com.wasu.pub.cache.config.CacheServers;

public class CacheService {
	private static Logger logger = LoggerFactory.getLogger(CacheService.class);

	private static ThreadLocal<String> userLocal = new ThreadLocal();

	public static void set(String key, Object value) throws Exception {
		server().set(key, value);
	}

	public static void mset(List<Object> keyValues) throws Exception {
		server().mset(keyValues);
	}

	public static Object getCache(String clzz, String key) throws Exception {
		return server().get(clzz+"_"+key);
	}
	
	public static Object get(String key) throws Exception {
		return server().get(key);
	}

	public static List<Object> mget(String[] keys) throws Exception {
		return server().mget(keys);
	}

	public static void remove(String key) throws Exception {
		server().remove(key);
	}
	
	public static Long push(String key,String... values) throws Exception {
		return server().push(key, values);
	}
	
	public static void expire(String key,int delayTime) throws Exception {
		server().expire(key, delayTime);
	}

	private static CacheServer server() {
		return CacheServers.getShardServer();
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
}