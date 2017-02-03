package com.wasu.pub.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.wasu.pub.dao.RoleDao;
import com.wasu.pub.service.RoleService;
import com.wasu.pub.service.UrmService;
import com.wasu.sid.SysResource;
import com.wasu.sid.SysRole;
import com.wasu.sid.SysUser;
import com.wasu.sid.UrmException;

@Controller
@RequestMapping("/")
public class SysLoginController extends BaseController {
	private Logger logger = Logger.getLogger(getClass());
	private static final String indexUrl ="/views/index.jsp";
	private static final String loginUrl ="/views/login.jsp";
	private static final String APP_CODE = "OMS";
	private static String defaultModule;
	@Autowired
    private UrmService urmService;
	@Autowired
	private RoleDao roleDao;
	
	/**
	 * 用户登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/login")
	public String login(HttpServletRequest request){
		String pageUrl="/login";
		String msg="";
		HttpSession session=request.getSession();
		SysUser user = (SysUser)session.getAttribute("loginUser");
		SysResource headResource = null;
		List<SysRole> roleList=null;
		if(user==null){
			String userName = request.getParameter("username");
			String password = request.getParameter("password");
			if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
				msg = "用户名或密码为空!";
				request.setAttribute("msg", msg);
				return pageUrl;
			}
			
			try {
				user = UrmService.getUrmService(urmService, session).login(userName, password);
				headResource = getUrmClient().getResourceTree().get(0);
				defaultModule = headResource.getCode();
				roleList=roleDao.getUserRoleList(user.getOrgId());
			} catch(UrmException e) {
				msg = e.getMessage();
				request.setAttribute("msg", msg);
				logger.error(msg);
				return pageUrl;
			} catch (Throwable e) {
				msg = "用户名或者密码错误，请重新输入!";
				request.setAttribute("msg", msg);
				logger.error(msg);
				return pageUrl;
			}

			session.setAttribute("loginUser", user);
			session.setAttribute("module", defaultModule);
			session.setAttribute("roleList", roleList);
		}
		
		return "redirect:"+indexUrl;
	}
	
	/**
	 * 用户注销
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request){

		HttpSession session=request.getSession();
		session.removeAttribute("loginUser");
		return "redirect:"+loginUrl;
	}
	
	/**
	 * 获取权限树
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/menuTree")
	@ResponseBody
	public List<SysResource> menuTree(HttpServletRequest request) {
		String module = request.getParameter("module");
		if (StringUtils.isBlank(module)) {
			module = defaultModule;
		}
		List<SysResource> resourceList = getUrmClient().getResourceTree();
		if(resourceList != null){
			for(SysResource resource : resourceList) {
				if (resource.getCode().equalsIgnoreCase(module)) {
					return resource.getSubResources();
				}
			}
		}
		
		return new ArrayList<SysResource>();
	}
	
	/**
	 * 有序的获取所有模块
	 * @return
	 */
	@RequestMapping(value="/module")
	@ResponseBody
	public Map<String, String> getModule() {
		Map<String, String> moduleMap = new LinkedHashMap<String, String>();
		List<SysResource> resourceList = getUrmClient().getResourceTree();
		if(resourceList != null){
			for(SysResource resource : resourceList) {
				moduleMap.put(resource.getCode(), resource.getName());
			}
		}
		return moduleMap;
	}
	
	@RequestMapping(value="/content")
	public ModelAndView pageList(String module){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/content");
		mv.addObject("module", module);
		return mv;
	}
	
	/**
	 * 改变当前操作的模块
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/changeModule")
	public @ResponseBody Map<String, Object> setDefaultModule(HttpServletRequest request) {
		String module = request.getParameter("module");
		if (!StringUtils.isBlank(module)) {
			defaultModule = module;
			HttpSession session=request.getSession();
			session.setAttribute("module", defaultModule);
		} else {
			return result(false, "切换模块失败");
		}
		
		return result(true, "切换模块成功");
	}
	
	@RequestMapping(value = "/validationPassWord")
	public @ResponseBody Map<String, Object> validationPassWord(HttpServletRequest request) {
		String password = request.getParameter("password");
		try {
			SysUser user = (SysUser) request.getSession().getAttribute("loginUser");
			if (user != null) {
				getUrmClient().validationPassWord(user.getLoginName(), password);
			} else {
				return result(false, "session失效，请重新登录");
			}
		} catch (UrmException e) {
			logger.error(e.getMessage(), e);
			return result(false, e.getMessage());
		} catch (Exception e) {
			logger.error("调用URM异常，检查是否配置了urmUrl参数!", e);
			return result(false, "调用URM异常，检查是否配置了urmUrl参数!");
		}
		return result(true, "成功");
	}

	@RequestMapping(value = "/editPassword")
	public @ResponseBody Map<String, Object> editPassword(HttpServletRequest request) {
		String oldp = request.getParameter("oldPassword");
		String newp = request.getParameter("newPassword");
		if(oldp.equals(newp)){
			return result(false, "新密码和旧密码一致，请重新修改");
		}
		try {
			SysUser user = (SysUser) request.getSession().getAttribute("loginUser");
			String result = getUrmClient().validationPassWord(user.getLoginName(), oldp);
			if(!"0".equals(result)){
				return result(false, "旧密码不正确");
			}
			getUrmClient().editPassword(user.getLoginName(), newp);
		} catch (UrmException e) {
			logger.error(e.getMessage(), e);
			return result(false, e.getMessage());
		} catch (Exception e) {
			logger.error("调用URM异常，检查是否配置了urmUrl参数!", e);
			return result(false, "调用URM异常，检查是否配置了urmUrl参数!");
		}
		return result(true, "成功");
	}

	public UrmService getUrmService() {
		return urmService;
	}

	public void setUrmService(UrmService urmService) {
		this.urmService = urmService;
	}
	
}
