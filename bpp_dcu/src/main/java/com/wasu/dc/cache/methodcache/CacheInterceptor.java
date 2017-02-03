package com.wasu.dc.cache.methodcache;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.wasu.dc.cache.CacheService;
import com.wasu.dc.cache.methodcache.annotation.CacheQuery;
import com.wasu.dc.cache.methodcache.annotation.CacheRefresh;
import com.wasu.dc.cache.methodcache.annotation.CacheRegion;
import com.wasu.dc.cache.methodcache.annotation.CacheUpdate;
import com.wasu.dc.cache.config.CacheObject;
import com.wasu.pub.util.CacheUtil;

@Component("dcuCacheInterceptor")
@Aspect
public class CacheInterceptor implements Ordered {
	protected Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Pointcut("@annotation(com.wasu.dc.cache.methodcache.annotation.CacheQuery)||@annotation(com.wasu.dc.cache.methodcache.annotation.CacheRefresh) || @annotation(com.wasu.dc.cache.methodcache.annotation.CacheUpdate)")
	public void pointCut() {
	}

	@Around("pointCut()")
	public Object doCache(ProceedingJoinPoint pjp) throws Throwable {
		long b = System.currentTimeMillis();
		if (!CacheService.isCacheEnable()) {
			Object value = pjp.proceed();
			return value;
		}
		Object result = null;
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
		Method method = methodSignature.getMethod();
		String region = pjp.getTarget().getClass().getSimpleName();
		CacheQuery cacheTimeQuery = (CacheQuery) method.getAnnotation(CacheQuery.class);
		CacheUpdate cacheTimeUpdate = (CacheUpdate) method.getAnnotation(CacheUpdate.class);
		CacheRefresh cacheFresh = (CacheRefresh) method.getAnnotation(CacheRefresh.class);
		try {

			if (cacheTimeQuery != null) {
				if (cacheTimeQuery.isClass()) {
					Object[] args = pjp.getArgs();
					String clzz = CacheUtil.getMethodClassParamValue(method, args, CacheRegion.class);
					if (clzz != null) {
						region = clzz;
					}
				}
				result = query(region, method.getName(), pjp.getArgs(), cacheTimeQuery.value(), cacheTimeQuery.user(),
						pjp);
			} else if (cacheTimeUpdate != null) {
				if (cacheTimeUpdate.isClass()) {
					Object[] args = pjp.getArgs();
					String clzz = CacheUtil.getMethodClassParamValue(method, args, CacheRegion.class);
					if (clzz != null) {
						region = clzz;
					}
				}
				result = pjp.proceed();
				update(region);
			} else if (cacheFresh != null) {

				Object[] args = pjp.getArgs();
				String clzz = CacheUtil.getMethodClassParamValue(method, args, CacheRegion.class);
				if (clzz != null) {
					region = clzz;
				}

				result = pjp.proceed();
				update(region);
			} else {
				result = pjp.proceed();
			}
		} catch (Exception e) {
			this.logger.error("缓存异常", e);
		}
		return result;
	}

	private Object query(String region, String method, Object[] args, String timeKeyValue, boolean user,
			ProceedingJoinPoint pjp) throws Throwable {
		Object value = null;
		Object time = null;
		String queryKey = getQueryKey(region, method, args);
		String timeKey = getTimeKey(region);
		List values = CacheService.mget(new String[] { queryKey, timeKey });
		if (values != null) {
			long startTime = System.currentTimeMillis();
			value = values.get(0);
			time = values.get(1);
			if (value != null) {
				CacheObject cacheObject = (CacheObject) value;
				value = cacheObject.getValue();
				if (time == null || (time != null) && (cacheObject.getTime() > Long.parseLong(time.toString()))) {
					return value;
				}
			}
		}
		long startTime = System.currentTimeMillis();
		value = pjp.proceed();
		CacheService.set(queryKey, new CacheObject(value));
		//logger.info("02 cache invoke DB cost time:" +
		//(System.currentTimeMillis() - startTime) + "ms");

		return value;
	}

	private Object query1(String region, String method, Object[] args, String timeKeyValue, boolean user,
			ProceedingJoinPoint pjp) throws Throwable {
		String queryKey = getQueryKey(region, method, args);
		String timeKey = getTimeKey(region);
		List values = CacheService.mget(new String[] { queryKey, timeKey });
		Object value = null;
		Object time = null;
		String log = "";
		if (values == null) {
			log = "缓存查询为空，查询数据库，设置缓存";
			value = pjp.proceed();
			CacheService.set(queryKey, new CacheObject(value));
			CacheService.log(new StringBuilder().append(log).append(".").append(queryKey).toString());
			return value;
		}
		value = values.get(0);
		time = values.get(1);

		if (value == null) {
			log = "缓存查询为空，查询数据库，设置缓存";
			value = pjp.proceed();
			CacheService.set(queryKey, new CacheObject(value));
		} else {
			log = "缓存查询到数据";
			CacheObject cacheObject = (CacheObject) value;
			value = cacheObject.getValue();
			if ((time != null) && (cacheObject.getTime() < Long.parseLong(time.toString()))) {
				log = new StringBuilder().append(log).append(",但是过期，查询数据库，设置缓存").toString();
				value = pjp.proceed();
				CacheService.set(queryKey, new CacheObject(value));
			}
		}
		CacheService.log(new StringBuilder().append(log).append(".").append(queryKey).toString());
		return value;
	}

	private void update(String region) throws Throwable {

		Set<String> timeKeys = new HashSet();
		String timeKey = getTimeKey(region);
		timeKeys.add(timeKey);
		long time = System.currentTimeMillis();
		CacheService.set(timeKey, Long.valueOf(time));
	}

	public static String getQueryKey(String region, String method, Object[] args) {
		Object[] theArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			theArgs[i] = (args[i] == null ? "NULL" : args[i]);
			if ((theArgs[i] instanceof Object[])) {
				theArgs[i] = new StringBuilder().append("[")
						.append(StringUtils.join((Object[]) (Object[]) theArgs[i], ",")).append("]").toString();
			}
		}
		String queryKey = new StringBuilder().append("query_").append(region).append(".").append(method).append("(")
				.append(StringUtils.join(theArgs, ",")).append(")").toString();

		return queryKey;
	}

	public static String getTimeKey(String region) throws Exception {
		String timeKey = new StringBuilder().append("time_").append(region).toString();
		return timeKey;
	}

	public int getOrder() {
		return 0;
	}

	public static void main(String[] args) {

		//String aa = CacheUtil.getMethodClassParamValue(method);
	}
}
