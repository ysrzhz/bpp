package com.wasu.pub.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.RoleDao;
import com.wasu.pub.service.RoleService;
import com.wasu.sid.SysRelate;
import com.wasu.sid.SysRole;
import com.wasu.sid.SysUser;
import com.wasu.sid.UrmException;

@Controller
@RequestMapping("/views/manage")
public class SysRoleController {
	private static Logger log = Logger.getLogger(SysRoleController.class);
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private RelateDao relateDao;
	@Autowired
	private RoleService roleService;

	@RequestMapping(value = "/role_list", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String list() throws Exception {
		try {
			List list = roleDao.listByName();
			return JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	@RequestMapping(value = "/role_listByUserId", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String listByUserId(String userId) throws Exception {
		try {
			List list = roleDao.listByUser(userId);
			return JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	@RequestMapping(value = "/role_listByOrg", produces = { "application/json;charset=UTF-8" })
	@ResponseBody		
	public String listByOrg(String id) throws Exception {
		try {
			List list  = roleDao.listByOrg(id);
			return JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	@RequestMapping(value = "/role_create", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String create(String name,String remark) throws Exception {
		try{
			name =this.change(name);
			remark =this.change(remark);
			UrmException.assertFor(1 > relateDao.existRole(name), "-1", "角色名称不可以重复");
			roleService.create(name, remark);
			return success();
		}catch (Exception e){
			return fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/role_update", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String update(String id,String name,String remark) throws Exception {
		name =this.change(name);
		remark =this.change(remark);
		SysRole role = roleDao.get(id);
		String oldName = role.getName();
		if(!name.equals(oldName)){
			try{
				UrmException.assertFor(1 > relateDao.existRole(name), "-1", "角色名称不可以重复");
			}catch (Exception e){
				return fail(e.getMessage());
			}
		}
		
		roleService.update(id, name, remark);
		return success();
	}
	
	@RequestMapping(value = "/role_delete", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String delete(String id) throws Exception {
		try{
			UrmException.assertFor(1 > relateDao.existRelate(id), "-1", "该角色被组织或用户关联，无法删除");
			List<SysRelate> relates = relateDao.getByObjectId(id);
			for (SysRelate rel : relates) {
				relateDao.delete(rel);
			}
			
			roleDao.deleteById(id);
			return success();
		}catch (Exception e){
			return fail(e.getMessage());
		}
	}

	@RequestMapping(value = "/listRoleByCurrentUser", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String listRoleByCurrentUser(HttpServletRequest request) throws Exception {
		try {
			HttpSession session = request.getSession();
			SysUser user = (SysUser) session.getAttribute("loginUser");
			List list = roleDao.listByUser(user.getId());
			return JSON.json(list);
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

	private String fail(String errorMessage) {
		try {
			Map map = new HashMap();
			map.put("ret", "1");
			map.put("retInfo", errorMessage);
			return JSON.json(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public RelateDao getRelateDao() {
		return relateDao;
	}

	public void setRelateDao(RelateDao relateDao) {
		this.relateDao = relateDao;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	//硬编码转码，解决乱码问题
	public String change(String old){
		try{
			return new String(old.getBytes("iso-8859-1"),"utf-8");
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}