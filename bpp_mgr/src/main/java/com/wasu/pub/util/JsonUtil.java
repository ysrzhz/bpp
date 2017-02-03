package com.wasu.pub.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * json处理类
 * 
 */
public class JsonUtil {
	private static final Logger log = Logger.getLogger(JsonUtil.class);
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/*
	 * 将对象序列化成json字符串
	 */
	public static String fromObject(Object object) {
		if (object == null){
			return "[]";
		}
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		try {
			mapper.writeValue(writer, object);
		} catch (Exception e) {
			log.error("json序列化异常", e);
		}
		return writer.toString();
	}

	/*
	 * 将对象序列化成json字符串
	 */
	public static void writeObject(Writer writer, Object object) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		try {
			mapper.writeValue(writer, object);
		} catch (Exception e) {
			log.error("json序列化异常", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static <T>T json2Object(String json, Class<T> calss) {
		Gson gson = new Gson();
		return gson.fromJson(json, calss);

	}
}