package com.wasu.pub.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.UserDao;
import com.wasu.pub.service.UserService;
import com.wasu.sid.SysRelate;
import com.wasu.sid.SysUser;

@Controller
@RequestMapping("/views/manage")
public class SysUserController {
	private static Logger log = Logger.getLogger(SysUserController.class);
	@Autowired
	private UserDao userDao;
	@Autowired
	private RelateDao relateDao;
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/user_list", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String list(String orgId,String name) throws Exception {
		if (StringUtils.isNotBlank(name)) {
			name = this.change(name);
		}
		
		List list = userDao.list(orgId, name);
		return jsonList(list);
	}
	
	@RequestMapping(value = "/user_create", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String create(String loginName, String name, String manager, String area, String orgId) throws Exception {
		name =this.change(name);
		SysUser exist = userDao.getLoginUser(loginName);
		if (exist != null) {
			throw new Exception("登录名称[" + loginName + "]已经存在");
		}
		
		userService.create(name, loginName, orgId, manager, area);
		return success();
	}
	
	@RequestMapping(value = "/user_update", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String update(String id, String name, String loginName,String orgId,String manager) throws Exception {
		name =this.change(name);
		SysUser exist = userDao.getLoginUser(loginName);
		if (exist != null && !exist.getId().equals(id)) {
			throw new Exception("登录名称[" + loginName + "]已经存在");
		}
		
		userService.update(id, name, loginName, orgId, "0");
		return success();
	}
	
	@RequestMapping(value = "/user_delete", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String delete(String id) throws Exception {
		List<SysRelate> relates = relateDao.getByObjectId(id);
		for(SysRelate rel : relates){
			relateDao.delete(rel);
		}
		
		userDao.deleteById(id);
		return success();
	}
	
	@RequestMapping(value = "/user_password", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String password(String id, String password) throws Exception {
		userService.password(id, password);
		return success();
	}
	
	@RequestMapping(value = "/user_editPassword", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String editPassword(String loginName,String password) throws Exception {
		userService.editPassword(loginName, password);
		return success();
	}
	
	private String jsonList(Object list){
		try {
			String json_result = JSON.json(list);
			return json_result;
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	private String success() {
		try {
			Map map = new HashMap();
			map.put("ret", "0");
			map.put("retInfo", "处理成功！");
			return JSON.json(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public RelateDao getRelateDao() {
		return relateDao;
	}

	public void setRelateDao(RelateDao relateDao) {
		this.relateDao = relateDao;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	//硬编码
	private String change(String old){
		try{
			return new String(old.getBytes("iso-8859-1"),"utf-8");
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}