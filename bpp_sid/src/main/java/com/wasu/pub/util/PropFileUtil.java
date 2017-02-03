package com.wasu.pub.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wasu.pub.WSException;

public class PropFileUtil {
	//读取property文件的内容
	public static Properties readPropertiesFile(String filename) {
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
			properties.load(inputStream);

		} catch (IOException e) {
			throw new WSException("030001","读取属性文件失败！",e);
		} finally {
			try {
				inputStream.close(); //�ر���
			} catch (Exception ex) {
			}
		}
		return properties;
	}
}
