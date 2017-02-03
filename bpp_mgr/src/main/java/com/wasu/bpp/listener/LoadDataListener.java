package com.wasu.bpp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

//数据初始化加载
public class LoadDataListener implements ServletContextListener {
	public static final Logger logger = LoggerFactory.getLogger(LoadDataListener.class);
	public static ApplicationContext appCtx=null;
	
	public void contextInitialized(ServletContextEvent event) {
		try {
			long time = System.currentTimeMillis();
			logger.info("初始化数据加载");
			//Bean beanName=(Bean)WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext()).getBean("beanName");
			logger.info("初始化数据加载完毕,共用了" + (System.currentTimeMillis() - time) + "ms");
		} catch (Exception ex) {
			logger.error("初始化数据加载错误", ex);
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
	}
}