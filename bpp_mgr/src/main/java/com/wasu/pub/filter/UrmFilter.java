package com.wasu.pub.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.wasu.pub.service.UrmService;

public class UrmFilter implements Filter {	
	protected static Logger logger = Logger.getLogger(UrmFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = ((HttpServletRequest)request);
		String url = httpRequest.getRequestURI();
		UrmService urmClient = UrmService.get(httpRequest.getSession());
		if(urmClient != null && urmClient.isUnAuth(url)){
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("<span style='font-size:14px;color:red'>[" + url + "]资源未授权!</span>");
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

	@Override
	public void init(FilterConfig config) throws ServletException {}
}