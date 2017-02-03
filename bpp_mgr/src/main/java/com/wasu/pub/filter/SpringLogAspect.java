package com.wasu.pub.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.wasu.pub.service.LogService;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.StringUtil;
import com.wasu.sid.SysLog;
import com.wasu.sid.SysUser;

@Aspect
@Component
public class SpringLogAspect {
	private static final Logger log = Logger.getLogger(Logger.class);
	private static final int len = 20000;//content最大长度
	private final static int num = StringUtil.getCpuSize() * 4;
	public static final ExecutorService pool = Executors.newFixedThreadPool(num);
	private static HashMap<String, String> map = new HashMap<String, String>();

	static {
		map.put("/login", "password");
		map.put("/editPassword", "oldPassword,newPassword");
	}

	@Resource
	private LogService logService;

	@Around(value = "execution(* com.wasu.pub.controller.*.*(..))")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		ServletRequestAttributes servletContainer = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = servletContainer.getRequest();
		String url = request.getServletPath();
		Object user_obj = request.getSession().getAttribute("loginUser");
		Map<String, Object> params = new HashMap<String, Object>();
		Enumeration<String> paramNames = request.getParameterNames();
		String filter = map.get(url);
		String value = null;
		while (paramNames != null && paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String values[] = request.getParameterValues(paramName);
			if (values != null && values.length != 0) {
				value = values[0];
				if (filter != null && filter.indexOf(paramName) >= 0) {
					value = "******";
				}
				
				params.put(paramName, value);
			}
		}
		
		try {
			Object object = joinPoint.proceed();
			doLog(logService, user_obj, url, StringUtil.getClientIp(request), params, object);
			return object;
		} catch (Exception e) {
			SysLog lg = new SysLog();
			lg.setApp(1);//0:未知 1:后台 2:数据能力接口
			lg.setLev(3);//日志级别 1：info 2：warn 3：error
			lg.setOperationtime(new Date());
			if (user_obj != null) {
				SysUser user = (SysUser) user_obj;
				lg.setU_id(user.getId());
				lg.setU_account(user.getLoginName());
				lg.setU_name(user.getName());
			}
			
			StringBuffer sb = new StringBuffer();
			sb.append("[请求地址：" + url + "]");
			sb.append("[请求参数：" + JSON.toJSONString(params) + "]");
			sb.append("[异常信息：" + e.toString() + "]");
			lg.setContent(getContent(sb.toString()));
			logService.addLog(lg);
			return null;
		}
	}

	public void doLog(LogService logService, Object user_obj, String url, String ip, Map<String, Object> input_params, Object output_obj) {
		pool.execute(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				try {
					sb.append("[请求地址：" + url + "]");
					SysLog lg = new SysLog();
					lg.setApp(1);//0:未知 1:后台 2:数据能力接口
					lg.setIp(ip);
					lg.setLev(1);//日志级别 1：info 2：warn 3：error
					lg.setOperationtime(new Date());
					if (user_obj != null) {
						SysUser user = (SysUser) user_obj;
						lg.setU_id(user.getId());
						lg.setU_account(user.getLoginName());
						lg.setU_name(user.getName());
					}
					
					try {
						sb.append("[请求参数：" + JSON.toJSONString(input_params) + "]");
						if (output_obj instanceof ArrayList) {
							List<Object> page = (List<Object>) output_obj;
							if (page != null) {
								if (page.size() > 1) {
									sb.append("[响应数据：[" + page.size() + "][" + JSON.toJSONString(page.get(0)) + "]]");
								} else {
									sb.append("[响应数据：" + JSON.toJSONString(page) + "]");
								}
							}
						} else if (output_obj instanceof Page) {
							Page<Object> page = (Page<Object>) output_obj;
							if (page != null) {
								if (page.getResults() != null && page.getResults().size() > 0) {
									sb.append("[响应数据：[" + page.getResults().size() + "][" + JSON.toJSONString(page.getResults().get(0)) + "]]");
								} else {
									sb.append("[响应数据：" + JSON.toJSONString(output_obj) + "]");
								}
							}
						}
					} catch (Throwable e) {
						if (e instanceof ClassCastException) {
							if (output_obj != null)
								sb.append("[响应数据：" + JSON.toJSONString(output_obj) + "]");
						} else {
							log.error("around", e);
							if (output_obj == null) {
								lg.setLev(3);
							}
						}
					}
					
					lg.setContent(getContent(sb.toString()));
					logService.addLog(lg);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					input_params.clear();
				}
			}
		});
	}
	
	//获取日志的content
	private String getContent(String content){
		if(content!=null && content.length()>len){
			return content.substring(0,len);
		}
		
		return content;
	}
}