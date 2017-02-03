package com.wasu.dc.config;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.wasu.pub.WSException;
import com.wasu.dc.cache.annotation.FullCache;

public class ConfigContext {

	private static Map<String, Boolean> methodEntityClassName = new HashMap();
	private static Map<String, Class> classNameMap = new HashMap();

	public static boolean isMethodCache(String clzz) {
		Boolean isMethod = true;
		Boolean isCache = methodEntityClassName.get(clzz);
		if (isCache != null) {
			return isCache;
		}
		try {
			Class cl = classNameMap.get(clzz);
			if (cl == null) {
				cl = Class.forName(clzz);
				classNameMap.put(clzz, cl);
			}
			Annotation[] anotations = cl.getAnnotations();
			if (anotations != null) {
				for (Annotation a : anotations) {
					if (a instanceof FullCache) {
						isMethod = false;
						methodEntityClassName.put(clzz, isMethod);
						return false;
					}
				}
			}
		} catch (Exception ex) {
			throw new WSException("040001", ex);
		}
		isMethod = true;
		methodEntityClassName.put(clzz, isMethod);
		return isMethod;
	}
	
	//返回class
	public static Class getClassByClassName(String clzz){
		try {
			Class cl = classNameMap.get(clzz);
			if (cl == null) {
				cl = Class.forName(clzz);
				classNameMap.put(clzz, cl);
			}
            return cl;
		} catch (Exception ex) {
			throw new WSException("040001", ex);
		}		
	}
}