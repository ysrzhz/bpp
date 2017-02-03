package com.wasu.pub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * Created by Administrator on 2016/7/26.
 */
public class Configuration {
    private static final Logger log = LoggerFactory.getLogger(Configuration.class);

    /**
     * 读取配置文件信息
     *
     * @param name 读取节点名
     * @param fileName 文件名
     * @return 读取的节点值
     */
    public static String readConfigString(String name, String fileName) {
        String result = "";
        try {
            ResourceBundle rb = ResourceBundle.getBundle(fileName);
            result = rb.getString(name);
        } catch (Exception e) {
            log.error("从" + fileName + "读取" + name + "出错:" + e.getMessage());
        }
        return result;
    }
}
