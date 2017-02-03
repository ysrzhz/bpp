package com.wasu.dc.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jListener implements ServletContextListener {
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String log4jInterval = sce.getServletContext().getInitParameter("log4j-interval");
		String log4jDir = sce.getServletContext().getInitParameter("log4j-dir");
		if (null == log4jInterval) {
			throw new RuntimeException("Log4j config conf path fail.");
		}
		
		long delay = 60 * 1000;
		if (log4jInterval != null) {
			delay = Integer.valueOf(log4jInterval) * 1000;
		}

		try {
			String tempDir = log4jDir.replace("classpath:", Log4jListener.class.getResource("/").getPath());
			File f = new File(tempDir);
			if(f.exists()) {
				PropertyConfigurator.configureAndWatch(tempDir, delay);
				logger.info("Log4j config load success.");
			} else {
				logger.error("log4j configuration file not found, file path is " + tempDir);
			}
		} catch (Exception e) {
			logger.error("load log4j config error.", e);
		}
	}
}