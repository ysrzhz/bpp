package com.wasu.pub.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.wasu.pub.WSException;

/**
 * 将一些对象放入缓存中，以便加快速度执行
 * 
 * @author Administrator
 *
 */
public class CacheUtil {
	private static Map<String, Class> classCache = new HashMap();
	private static Map<Class, Map<String, Field>> fields = new HashMap();
	private static Map<Class, Map<String, Method>> methods = new HashMap();
	private static Logger log = Logger.getLogger(CacheUtil.class);

	public static Class getClass(String clzz) {
		Class c = classCache.get(clzz);
		if (c != null) {
			return c;
		}
		Class b = null;
		try {
			b = Class.forName(clzz);
		} catch (ClassNotFoundException e) {
			log.error("class not found " + clzz + " e=\r\n");
		}
		if (b != null) {
			classCache.put(clzz, b);
		}
		return b;
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 * 
	 * @param clzz
	 *            : 子类对象
	 * @param fieldName
	 *            : 父类中的属性名
	 * @return 父类中的属性对象
	 */

	public static Field getDeclaredField(Class clzz, String fieldName) {
		Map<String, Field> map = fields.get(clzz);
		if (map != null) {
			return map.get(fieldName);
		}
		Field field = null;
		map = new HashMap();
		for (; clzz != Object.class; clzz = clzz.getSuperclass()) {
			try {
				Field[] allFields = clzz.getDeclaredFields();
				for (Field fd : allFields) {
					map.put(fd.getName(), fd);
				}
			} catch (Exception e) {
				//这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。
				//如果这里的异常打印或者往外抛，则就不会执行clazz =
				//clazz.getSuperclass(),最后就不会进入到父类中了
			}
		}

		return map.get(fieldName);
	}

	/**
	 * 循环向上转型, 获取对象的 DeclaredField
	 * 
	 * @param clzz
	 *            : 子类对象
	 *            : 父类中的属性名
	 * @return 父类中的属性对象
	 */

	public static Collection<Field> getDeclaredFields(Class clzz) {
		Map<String, Field> map = fields.get(clzz);
		if (map != null) {
			return map.values();
		}

		Field field = null;
		map = new HashMap();
		for (; clzz != Object.class; clzz = clzz.getSuperclass()) {
			try {
				Field[] allFields = clzz.getDeclaredFields();
				for (Field fd : allFields) {
					map.put(fd.getName(), fd);
				}
			} catch (Exception e) {
			}
		}
		return map.values();
	}

	public static Object get(String clzz, Object owner, String fieldName) {
		try {
			Class cl = owner.getClass();
			Method getMethod = null;
			Map<String, Method> methodMap = methods.get(cl);
			if (methodMap != null) {
				getMethod = methodMap.get(fieldName);
			}
			if (getMethod == null) {
				methodMap = new HashMap();
				BeanInfo beaninfo = Introspector.getBeanInfo(cl);
				PropertyDescriptor[] porpertydescriptors = beaninfo
						.getPropertyDescriptors();
				for (PropertyDescriptor pd : porpertydescriptors) {
					if (pd.getName().equals(fieldName)) {
						getMethod = pd.getReadMethod(); //获得该属性的get方法
						Method tmpMethod = getDeclaredMethod(cl,
								getMethod.getName());
						getMethod = tmpMethod;
						methodMap.put(fieldName, tmpMethod);
						methods.put(cl, methodMap);
						break;
					}
				}
			}

			return getMethod.invoke(owner);
		} catch (Exception e) {
			e.printStackTrace();
			String message = WSException.getMessageFromEx(e);
			message = message + " fieldName=" + fieldName;
			log.error(message);
		}
		return null;
	}

	public static Method getDeclaredMethod(Class cl, String methodName) {
		Method method = null;
		for (Class<?> clazz = cl; clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName);
				return method;
			} catch (Exception e) {
			}

		}
		return null;
	}

	public static String getMethod(String clzz, String fieldName) {
		Class cl = getClass(clzz);
		Field field = getDeclaredField(cl, fieldName);
		Type type = field.getType();
		String name = type.getTypeName();
		return name;
	}

	public static String getMethodClassParamValue(Method method, Object[] args,
			Class cacheRegion) {
		String paramValue = "";
		try {
			Parameter[] params = method.getParameters();
			for (int i = 0; i < params.length; i++) {
				Parameter param = params[i];
				Object o = param.getAnnotation(cacheRegion);
				if (o != null) {
					return (String) args[i];
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramValue;
	}

}
