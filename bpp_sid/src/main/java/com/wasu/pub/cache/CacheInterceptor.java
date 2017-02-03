package com.wasu.pub.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
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

import com.wasu.pub.cache.annoation.CacheQuery;
import com.wasu.pub.cache.config.CacheObject;

@Component
@Aspect
public class CacheInterceptor implements Ordered {
	protected Logger logger = LoggerFactory.getLogger(CacheInterceptor.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Pointcut("@annotation(com.wasu.pub.cache.annoation.CacheQuery)")
	public void pointCut() {
	}

	/**
	 * 只用于缓存查询功能
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("pointCut()")
	public Object doCache(ProceedingJoinPoint pjp) throws Throwable 
	{
		//如果缓存开关未开启，则不查询缓存
		Object result = null;
		if (!CacheService.isCacheEnable()) {
			result = pjp.proceed();
			return result;
		}
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
		Method method = methodSignature.getMethod();
		String region = pjp.getTarget().getClass().getSimpleName();
		CacheQuery cacheQuery = (CacheQuery) method.getAnnotation(CacheQuery.class);
		try {
			if (cacheQuery == null) {
				result = pjp.proceed();
				return result;
			}
		    if(cacheQuery.isClass()){
		      Object[] args = pjp.getArgs();
		      //生成key 值
		      if(args!=null){
		        for(Object o:args){
		          if(o!=null &&o instanceof Class){
		            region =((Class) o).getName();
		            break;
		          }
		        }
		      }
		    }
			result = query(region, method.getName(), pjp.getArgs(), cacheQuery.value(), cacheQuery.user(), pjp);
		} catch (Exception e) {
			this.logger.error("缓存异常", e);
		}
		return result;
	}

    private Object query(String clazz, String method, Object[] args, String timeKeyValue,boolean user, ProceedingJoinPoint pjp) throws Throwable 
    {
    	long startTime = System.currentTimeMillis();
      Object value =null;
      String log = "";
      String queryKey = getQueryKey(clazz, method, args);
      String timeKey = getTimeKey(clazz, args, timeKeyValue, user);
      List values = CacheService.mget(new String[] { queryKey,timeKey });
      if(values!=null){
    	  value = values.get(0);
    	  if (value != null) {
    		  log = "缓存查询到数据 ";
    		  CacheObject cacheObject = (CacheObject) value;
    		  value = cacheObject.getValue();
			  logger.info(log+(System.currentTimeMillis()-startTime)+"ms cache key:queryKey=" + queryKey + " timeKey="+timeKey +" is query cache:"+true+",value="+value);
			  return value;
    	  }
      }
      long startTime1 = System.currentTimeMillis();
      value = pjp.proceed();
      
      CacheService.set(queryKey, new CacheObject(value));
      
      logger.info("02 cache invoke DB cost time:"+(System.currentTimeMillis()-startTime1)+"ms");
    
      return value;
	}

    /**
     * 生成 缓存的 key 值
     * @param region
     * @param method
     * @param args
     * @return
     */
	public String getQueryKey(String region, String method, Object[] args) {
		StringBuffer queryKey = new StringBuffer();
		queryKey.append("query_").append(region).append(".").append(method).append("[");
		for (int i = 0; i < args.length; i++) {
			queryKey.append(getFiledsInfo(args[i]));
			if (i !=(args.length-1)){
				queryKey.append(",");
			}
		}

		return queryKey.append("]").toString();
	}

	/**
	 * 生成缓存时间的key值
	 * @param region
	 * @param args
	 * @param timeKeyValue
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static String getTimeKey(String region, Object[] args,
			String timeKeyValue, boolean user) throws Exception {
		String timeKey = new StringBuilder().append("time_").append(region)
				.toString();
		timeKey = new StringBuilder()
				.append(timeKey)
				.append((user) && (CacheService.getUser() != null) ? new StringBuilder()
						.append("|user=").append(CacheService.getUser())
						.toString()
						: "").toString();
		if (StringUtils.isBlank(timeKeyValue)) {
			return timeKey;
		}
		String[] values = timeKeyValue.split(",");
		for (String value : values) {
			String[] valueItem = value.split("=");
			String[] argValue = valueItem[1].split("\\.");
			Object arg = args[(java.lang.Integer.parseInt(argValue[0]) - 1)];
			if ((arg != null) && (argValue.length == 2)) {
				arg = PropertyUtils.getProperty(arg, argValue[1]);
			}
			if ((arg != null) && (arg.toString().length() != 0)) {
				timeKey = new StringBuilder().append(timeKey).append("|")
						.append(valueItem[0]).append("=").append(arg)
						.toString();
			}
		}
		return timeKey;
	}

	public int getOrder() {
		return 0;
	}
	
	/** 
	 * 根据属性名获取属性值 
	 * */  
	private Object getFieldValueByName(String fieldName, Object o) {  
		try {    
			String firstLetter = fieldName.substring(0, 1).toUpperCase();    
			String getter = "get" + firstLetter + fieldName.substring(1);    
			Method method = o.getClass().getMethod(getter, new Class[] {});    
			Object value = method.invoke(o, new Object[] {});    
			return value;    
		} catch (Exception e) {    
			logger.error(e.getMessage(),e);    
			return null;    
		}    
	}
	
   /** 
    * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list 
    * */  
    private String getFiledsInfo(Object o){  
    	if (o instanceof String || o instanceof Integer || o instanceof List)
    	{
    		return o.toString();
    	}
    	StringBuffer fieldValue = new StringBuffer();
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			fieldValue.append(fields[i].getName());
			fieldValue.append("=");
			fieldValue.append(getFieldValueByName(fields[i].getName(), o));
			if(i!=(fields.length-1)){
				fieldValue.append(",");
			}
		}
        return fieldValue.toString();  
    }  
	     
	   /** 
	    * 获取对象的所有属性值，返回一个对象数组 
	    * */  
//	public Object[] getFiledValues(Object o){  
//		String[] fieldNames = this.getFiledName(o);
//		Object[] value = new Object[fieldNames.length];
//		for (int i = 0; i < fieldNames.length; i++)
//		{
//			value[i] = this.getFieldValueByName(fieldNames[i], o);
//		}
//	    return value;  
//	}      
	public static void main(String[] args){
		//String region, String method, Object[] args
		List<String> aa= new ArrayList();
		aa.add("ddd");
		Object[] cc=new Object[1];
		cc[0]=aa;
		//String timeKey =getTimeKey("region","batchUpdate",);
	}
}
