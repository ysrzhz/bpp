package com.wasu.pub.util;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.wasu.pub.WSException;
import com.wasu.pub.cache.CacheService;

import net.sf.json.JSONObject;

public class StringUtil {
	private static Logger logger = Logger.getLogger(StringUtil.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static boolean isBlank(String str) {
		return StringUtils.isBlank(str);
	}
	
	public static String getObjStr(Object o) {
		if (o == null) {
			return "";
		}

		return o.toString();
	}

	public static String toStringList(List datas) {
		if (datas == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (Object o : datas) {
			sb.append(getObjStr(o)).append(" ");
		}
		
		return sb.toString();
	}

	public static String toStringMap(Map datas) {
		if (datas == null) {
			return "";
		}

		Iterator it = datas.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			Object o = it.next();
			Object value = datas.get(o);
			sb.append(getObjStr(o)).append("=").append(value).append(" ");
		}
		return sb.toString();
	}

	public static String toStringCollect(Collection datas) {
		if (datas == null) {
			return "";
		}

		Iterator it = datas.iterator();
		StringBuilder sb = new StringBuilder();
		while (it.hasNext()) {
			Object o = it.next();
			sb.append(getObjStr(o)).append(" ");
		}
		return sb.toString();
	}

	public static String toStringNull(Object o) {
		if (o == null) {
			return null;
		}
		
		return o.toString();
	}

	public static String toStringNullConvert(Object o) {
		if (o == null) {
			return null;
		}

		String value;
		if (o instanceof Date) {
			value = sdf.format(o);
		} else {
			value = o.toString();
		}
		
		value = value.replace("'", "''");
		return value;
	}

	//获取可用CPU个数
	public static int getCpuSize() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	//获取JSONObject：{"key1":"value1", "key2":"value2"}
	public static JSONObject getJsonObj(Object ... params) {
		JSONObject jsonObj=new JSONObject();
		if(params!=null && params.length>0 && params.length%2==0){//key/value
    		for(int i=0;i<params.length/2;i++){
    			jsonObj.put(params[i*2], params[i*2+1]);
    		}
    	}
		
		return jsonObj;
	}
		
	/**
	 * 日期比较
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return 开始时间大于等于结束时间返回true, 否则返回false
	 */
	public static boolean dateComparison(Date beginTime, Date endTime) {
		try {
			if (beginTime!=null && endTime!=null && beginTime.getTime() >= endTime.getTime()) {
				return true;
			}
			
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	//MD5概要算法编码：str,原始字符串
	public static String getMd5Str(String str) {
		try {
			return bytesToHexStr(MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8")));
		} catch (Exception e) {
			return null;
		}
	}
	
	//字节数组转换成16进制字符串
	public static String bytesToHexStr(byte[] bytes){
		String str="";
		for(int i=0;i<bytes.length;i++){
			String tempStr=Integer.toHexString(bytes[i] & 0XFF);
			if(tempStr.length()==1){
				str+="0"+tempStr;
			}else{
				str+=tempStr;
			}
		}
		
		return str.toUpperCase();
	}
	
	//获取客户端真实IP
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}
	
	//获取缓存
	public static Object getCache(String clazz, String key) {
		try {
			key = clazz + key;
			return CacheService.get(key);
		} catch (Exception e) {
			logger.error("获取缓存失败[key=" + key + "]：" + WSException.getStackTraceText(e));
			return null;
		}
	}
	
	//写入缓存
	public static void putCache(String clazz, String key, Object obj) {
		try {
			key = clazz + key;
			CacheService.set(key, obj);
		} catch (Exception e) {
			logger.error("写入缓存失败[key=" + key + ",data=" + getObjStr(obj) + "]：" + WSException.getStackTraceText(e));
		}
	}
}