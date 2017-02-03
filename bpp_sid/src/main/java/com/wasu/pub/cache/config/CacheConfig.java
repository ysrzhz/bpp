package com.wasu.pub.cache.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheConfig {
	private static Logger logger = LoggerFactory.getLogger(CacheConfig.class);

	private static List<CacheServer> servers = new ArrayList();
	private static String logLevel;
	private static int heartBeatInterval;
	private static boolean cacheEnable;
	private static boolean cacheManage;
	private static int cacheKeyExpire = 5*60; //单位：秒
	private static Document document;

	public static void init(String path) {
		String classPath = CacheConfig.class.getClassLoader().getResource("").getPath();
		String configLocation = classPath + path;
		try {
			document = new SAXReader().read(new File(configLocation));
			Element serversElement = (Element) document.selectSingleNode("//servers");
			for (Element serverElement : (List<Element>) serversElement.elements("server")) 
			{
				String type = serverElement.attributeValue("type");
				CacheException.notNull(type, "缓存服务类型不可为空");
				String cacheClass = "com.wasu.pub.cache.config.CacheServer" + StringUtils.capitalize(type);
				CacheServer cacheServer = (CacheServer) Class.forName(cacheClass).newInstance();
				for (Element paramElement : (List<Element>) serverElement.elements("param")) 
				{
					cacheServer.addParam(paramElement.attributeValue("name"),paramElement.getTextTrim());
				}
				servers.add(cacheServer);
				logger.info("缓存地址=" + cacheServer);
			}
			logLevel = getConfigValue("//log-level");
			heartBeatInterval = Integer.parseInt(getConfigValue("//heart-beat-interval"));
			cacheEnable = Boolean.parseBoolean(getConfigValue("//cache-enable"));
			cacheManage = Boolean.parseBoolean(getConfigValue("//cache-manage"));
			cacheKeyExpire = Integer.parseInt(getConfigValue("//cache-key-expire","0"));
		} catch (Exception ex) {
			logger.error("缓存配置失败!", ex);
		}
	}

	private static String getConfigValue(String path) {
		Element element = (Element) document.selectSingleNode(path);
		String value = element.getText();
		logger.info("缓存配置" + path + "=" + value);
		return value;
	}
	private static String getConfigValue(String path,String defaultVallue) {
		String value = defaultVallue;
		try{
			Element element = (Element) document.selectSingleNode(path);
			if(element!=null){
				value = element.getText();
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			value = defaultVallue;
		}
		
		logger.info("缓存配置" + path + "=" + value);
		return value;
	}
	public static List<CacheServer> getServers() {
		return servers;
	}

	public static boolean isLogInfo() {
		return "info".equals(logLevel);
	}

	public static boolean isLogDebug() {
		return "debug".equals(logLevel);
	}

	public static int getHeartBeatInterval() {
		return heartBeatInterval;
	}

	public static boolean isCacheEnable() {
		return cacheEnable;
	}

	public static boolean isCacheManage() {
		return cacheManage;
	}
	public static int getCacheKeyExpire() {
	    return cacheKeyExpire;
	  }
}