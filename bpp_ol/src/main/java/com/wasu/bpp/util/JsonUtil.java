package com.wasu.bpp.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wasu.pub.util.ObjectUtil;

/**
 * json处理类
 * 
 */
public class JsonUtil {
	private static final Logger log = Logger.getLogger(JsonUtil.class);
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final ObjectMapper mapper = new ObjectMapper();
//	private static StringWriter writer;
//	private static JsonGenerator gen;
	/*
	 * 将对象序列化成json字符串
	 */
	public static String fromObject(Object object) {
		JsonGenerator gen = null;
		StringWriter writer = null;
		try {
			if (ObjectUtil.isEmpty(object)){
				return "";
			}
			
			writer = new StringWriter();
			gen = new JsonFactory().createGenerator(writer);
			mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
			mapper.writeValue(gen, object);
			return writer.toString();
		} catch (Exception e) {
			log.error("json序列化异常", e);
			return "";
		}finally{
			try {
				if(gen != null) {
					gen.close();
					gen=null;
				}
				
				if(writer !=null){//关闭io
					writer.close();
					writer=null;
				}
			} catch (IOException e) {
				log.error("JsonGenerator close exception", e);
			}
		}
	}

	public static String toJson(Object o){
		JSONObject object = JSONObject.fromObject(o);
		return object.toString();
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