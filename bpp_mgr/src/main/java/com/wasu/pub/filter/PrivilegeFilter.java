package com.wasu.pub.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.wasu.pub.service.BaseService;
import com.wasu.sid.SysUser;

/**
 * session失效时，页面超时无法跳转过滤器
 * 针对访问IEPG后台管理系统的URL进行权限的过滤，如果该用户发起的HTTP请求具备访问权限，
 * 那么用户的请求将继续进行，否则，权限过滤器将向客户端响应“您无权此操作”的提示信息。权限 过滤器可以通过web.xml进行配置，具体的配置如下：
 * 1、通过初始化参数名“doNotFilterURL”，配置不需要过滤的一些URL，并以“,”隔开。
 * 2、通过url-pattern配置需要权限过滤器过滤的url，当前的配置为“/*”，此配置可以根据实际情况进行改动。
 * 该权限过滤器加入了缓存的功能，检测到用户第一次发起HTTP请求具备访问的权限，那么就将该请求
 * 缓存起来，当用户再次发起请求的时候，那么就从缓存中去判断该请求是否具有访问权限。
 * 
 */
public class PrivilegeFilter implements Filter {
	private static final Logger logger = Logger.getLogger(PrivilegeFilter.class);
	private static final String loginURL = "/views/login.jsp";
	private static final String loginSpringUrl = "/login";
	private static final String loginStrutsUrl = "/login.do";
	private static final String logoutSpringUrl = "/logout";
	private static final String logoutStrutsUrl = "/logout.do";
	private String[] doNotFilterURL;//不需要过滤的URL

	public void init(FilterConfig filterConfig) throws ServletException {
		String params = filterConfig.getInitParameter("doNotFilterURL");
		if (params != null) {
			String urls[] = params.split(",");
			doNotFilterURL = new String[urls.length];
			for (int i = 0, size = urls.length; i < size; i++) {
				doNotFilterURL[i] = urls[i];
			}
		}
	}

	/**
	 * 对一些URL不进行过滤拦截。
	 * 获取HTTP请求的URI，如果该请求是登录的首页，则不进行拦截。判断doNotFilterURL是否为null，
	 * 如果不为null，如果该数组中含HTTP请求的URI，则也不进行拦截。
	 * @param request HTTP请求
	 * @param response HTTP响应
	 * @param filterChain 过滤器链
	 * @return 返回 trur or false，来判断该HTTP请求的URL是否要进行拦截。
	 * @throws IOException 抛出HTTP请求的IO异常，由容器来捕获。
	 * @throws ServletException 抛出Servlet异常，由容器来捕获。
	 * @return boolean - 返回true说明该HTTP请求的URL过滤器不进行拦截，否则过滤器对它进行拦截。
	 * @exception throws IOException HTTP请求的IO异常，ServletException Servlet执行异常
	 */
	private boolean doNotFiler(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String path = removeContextRoot(req);
		if (path != null && path.length() != 0) {
			path = path.trim();
		}

		if (path.startsWith("/resources")) {//静态资源不拦截
			return true;
		}

		//在登录时需要过滤loginURL页面，跳转到登录页面，登录成功后不需要进行过滤，执行下面的语句
		HttpSession session = req.getSession();
		SysUser loginUser = (SysUser) session.getAttribute("loginUser");
		if (null == loginUser) {//用户未登陆
			if (loginURL.equals(path) || loginSpringUrl.equals(path) || loginStrutsUrl.equals(path) || logoutSpringUrl.equals(path) || logoutStrutsUrl.equals(path)) {//判断是否为登陆操作：登录登出页面不进行拦截
				return true;
			}

			if (doNotFilterURL != null) {//会话失效时，非登录、注销以及页面显示资源的不过滤的url需要过滤s
				for (String url : doNotFilterURL) {
					if (url != null && path.contains(url.trim())) {
						return true;
					}
				}
			}
		} else {
			Map<String, Object> userSession = BaseService.userSession.get();
			if (null == userSession) {
				String remoteAddr = request.getRemoteAddr();
				userSession = new HashMap<String, Object>();
				userSession.put(BaseService.loginUser, loginUser);
				userSession.put(BaseService.remoteAddr, remoteAddr);
				BaseService.userSession.set(userSession);//缓存用户信息
			}

			if (path.startsWith("/views") || path.startsWith("/manager")) {//已登录的用户 视图和后台action操作不进行拦截
				return true;
			}

			if (doNotFilterURL != null) {//会话失效时，非登录、注销以及页面显示资源的不过滤的url需要过滤
				for (String url : doNotFilterURL) {
					if (url != null && path.contains(url.trim())) {
						return true;
					}
				}
			}
			
			session.setMaxInactiveInterval(60 * 60);
		}

		return false;//其他情况拦截
	}

	/**
	 * 访问的HTTP请求的URL中，去掉contextRoot。
	 * 首先重HttpServletRequest对象中获取ContentRoot，然后再从该对象中获取http请求的URI，
	 * 最后从获取的URI字符串中去掉ContextRoot后，以字符串类型放回。
	 * @param request HttpServletRequest对象
	 * @return 返回字符串类型：返回的字符串中去掉了ContentRoot。
	 */
	private String removeContextRoot(HttpServletRequest request) {
		String contextRoot = request.getContextPath();
		String requestPath = request.getRequestURI();
		if (requestPath.contains(contextRoot)) {
			requestPath = requestPath.substring(contextRoot.length());
		}

		return requestPath;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		SysUser user = (SysUser) session.getAttribute("loginUser");
		String requestPath = removeContextRoot(req);//获取HTTP请求的URI
		boolean canNotFilter = doNotFiler(request, response, filterChain);//登录页处理
		if (canNotFilter) {
			if (null != user) {
				req.getRequestDispatcher(requestPath).forward(req, res);
			} else {
				filterChain.doFilter(request, response);
			}
			return;
		}

		//用户未登录，或者登录的用户session失效，请求将转发到登录页面。
		if (user == null) {
			String responseUrl = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
			res.sendRedirect(responseUrl + loginURL);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("privilege filter pass , the requestURI: " + requestPath);
		}
		
		filterChain.doFilter(request, response);
	}

	/**
	 * 清理操作
	 */
	public void destroy() {
		this.doNotFilterURL = null;
	}
}