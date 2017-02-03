package com.wasu.pub.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wasu.pub.domain.ShowDomain;
import com.wasu.pub.domain.WsField;
import com.wasu.pub.util.ShowDomainUtil;

public class ReflectUtil {
	private static Logger logger = Logger.getLogger(ReflectUtil.class);

	public static void setFieldValue(Object target, String fname, Class ftype, Object fvalue) {
		if ((target == null) || (fname == null) || ("".equals(fname)) || ((fvalue != null) && (!ftype.isAssignableFrom(fvalue.getClass())))) {
			return;
		}
		
		try {
			Class clazz = target.getClass();
			Field field = clazz.getDeclaredField(fname);
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			field.set(target, fvalue);
		} catch (Exception me) {
			if (logger.isDebugEnabled()) {
				logger.debug(me);
			}
		}
	}

	public static Object copyObject(Object obj) throws Exception {
		Field[] fields = obj.getClass().getDeclaredFields();
		Object newObj = obj.getClass().newInstance();
		int i = 0;
		for (int j = fields.length; i < j; i++) {
			String propertyName = fields[i].getName();
			Object propertyValue = getProperty(obj, propertyName);
			setProperty(newObj, propertyName, propertyValue);
		}
		
		return newObj;
	}

	private static Object setProperty(Object bean, String propertyName, Object value) throws Exception {
		try {
			Class clazz = bean.getClass();
			Field field = clazz.getDeclaredField(propertyName);
			Method method = clazz.getDeclaredMethod(getSetterName(field.getName()), new Class[] { field.getType() });
			return method.invoke(bean, new Object[] { value });
		} catch (Exception e) {
			throw e;
		}
	}

	private static Object getProperty(Object bean, String propertyName) throws Exception {
		try {
			Class clazz = bean.getClass();
			Field field = clazz.getDeclaredField(propertyName);
			Method method = clazz.getDeclaredMethod(getGetterName(field.getName()), new Class[0]);
			return method.invoke(bean, new Object[0]);
		} catch (Exception e) {
			throw e;
		}
	}

	private static String getGetterName(String propertyName) {
		String method = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		return method;
	}

	private static String getSetterName(String propertyName) {
		String method = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		return method;
	}

	/**
	 * @param obj
	 * @param isQuery 是否只转换查询标签的字段
	 * @return
	 */
	public static Map<String, Object> getObjectAsMap(Object obj, boolean isQuery) {
		Map map = new HashMap();
		if (obj == null) {
			return map;
		}
		
		ShowDomain sd = ShowDomainUtil.get(obj.getClass().getName());
		Class clazz = obj.getClass();
		Method[] methods = clazz.getMethods();
		String methodname = "";
		for (int i = 0; i < methods.length; i++) {
			methodname = methods[i].getName();
			if (methodname.startsWith("get")) {
				try {
					Object value = methods[i].invoke(obj, new Object[0]);
					if (value == null || value.toString().trim().equals("")) {
						continue;
					}
					if ((value != null) && ((value instanceof String))) {
						String str = (String) value;
						value = str.trim();
					}
					WsField wf = sd.getWsField(getFieldName(methodname));
					if (wf != null && wf.isQuery() && isQuery) {
						map.put(getFieldName(methodname), value);
					} else if (!isQuery) {
						map.put(getFieldName(methodname), value);
					}

				} catch (IllegalArgumentException e) {
					logger.debug("Convert JavaBean to Map Error!", e);
				} catch (IllegalAccessException e) {
					logger.debug("Convert JavaBean to Map Error!", e);
				} catch (InvocationTargetException e) {
					logger.debug("Convert JavaBean to Map Error!", e);
				}
			}
		}
		
		return map;
	}

	/**
	 * 支持转换所有字段
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> getObjectAsMap(Object obj) {
		return getObjectAsMap(obj, false);
	}

	private static String getFieldName(String str) {
		String firstChar = str.substring(3, 4);
		String out = firstChar.toLowerCase() + str.substring(4);
		return out;
	}
}