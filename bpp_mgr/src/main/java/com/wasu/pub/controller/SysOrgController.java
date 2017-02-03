package com.wasu.pub.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.wasu.pub.dao.OrgParamDao;
import com.wasu.pub.dao.OrganizationDao;
import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.UserDao;
import com.wasu.pub.service.OrgParamService;
import com.wasu.pub.service.OrganizationService;
import com.wasu.sid.SysOrganization;
import com.wasu.sid.SysRelate;
import com.wasu.sid.UrmException;

@Controller
@RequestMapping("/views/manage")
public class SysOrgController {
	private static Logger log = Logger.getLogger(SysOrgController.class);
	@Autowired
	private OrganizationDao orgDao;
	@Autowired
	private OrgParamDao paramDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RelateDao relateDao;
	@Autowired
	private OrganizationService orgService;
	@Autowired
	private OrgParamService paramService;

	@RequestMapping(value = "/org_list", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String list(String id) throws Exception {
		try {
			List list = orgService.list(id);
			return JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}

	@RequestMapping(value = "/org_create", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String create(String code,String name, String manager,String remark) throws Exception {
		name =this.change(name);
		code=this.change(code);
		remark =this.change(remark);
		SysOrganization exist = orgDao.getByCode(code);
		if (exist != null) {
			return fail("组织编码[" + code + "]已经存在");
		}
		
		orgService.create(name, code, manager, remark);
		return success();
	}
	
	@RequestMapping(value = "/org_update", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String update(String id,String name,String code,String manager,String remark) throws Exception {
		name =this.change(name);
		code=this.change(code);
		remark =this.change(remark);
		SysOrganization exist = orgDao.getByCode(code);
		if (exist != null && !exist.getId().equals(id)) {
			return fail("组织编码[" + code + "]已经存在");
		}
		
		orgService.update(id, name, code, manager, remark);
		return success();
	}
	
	@RequestMapping(value = "/org_delete", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String delete(String id) throws Exception {
		try{
			UrmException.assertFor(1 > userDao.list(id,null).size(), "-1", "该组织存在用户关联，无法删除！");
			UrmException.assertFor(1 > relateDao.existRelate(id), "-1", "该组织被其他组织关联，无法删除！");
		}
		catch (Exception e){
			return fail(e.getMessage());
		}
		List<SysRelate> relates = relateDao.getByObjectId(id);
		for (SysRelate rel : relates) {
			relateDao.delete(rel);
		}
		
		orgDao.deleteById(id);
		return success();
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
	
	@RequestMapping(value = "/org_listAllByRole", produces = { "application/json;charset=UTF-8" })
	@ResponseBody		
	public String listAllByRole(String id) throws Exception {
		try {
			List list = orgDao.listAllByRole(id);
			String json_result = JSON.json(list);
			return json_result;
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	@RequestMapping(value = "/org_listByRole", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String listByRole(String id) throws Exception {
		try {
			List list = orgDao.listByRole(id);
			return JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	public static Logger getLog() {
		return log;
	}

	public static void setLog(Logger log) {
		SysOrgController.log = log;
	}

	public OrganizationDao getOrgDao() {
		return orgDao;
	}

	public void setOrgDao(OrganizationDao orgDao) {
		this.orgDao = orgDao;
	}

	public OrgParamDao getParamDao() {
		return paramDao;
	}

	public void setParamDao(OrgParamDao paramDao) {
		this.paramDao = paramDao;
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

	public OrganizationService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrganizationService orgService) {
		this.orgService = orgService;
	}

	public OrgParamService getParamService() {
		return paramService;
	}

	public void setParamService(OrgParamService paramService) {
		this.paramService = paramService;
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