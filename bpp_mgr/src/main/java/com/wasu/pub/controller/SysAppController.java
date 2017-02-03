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
import com.wasu.pub.dao.ApplicationDao;
import com.wasu.pub.dao.ResourceDao;
import com.wasu.pub.service.ApplicationService;
import com.wasu.sid.SysApplication;
import com.wasu.sid.UrmException;

@Controller
@RequestMapping("/views/manage")
public class SysAppController {

	private static Logger log = Logger.getLogger(SysAppController.class);
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private ResourceDao resourceDao;
	
	@Autowired
	private ApplicationService appService;
	
	private String id;
	private String name;
	private String code;


	@RequestMapping(value = "/app_list", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String list() throws Exception {
		//查询的结果以name排序
		List list = appService.listOrderByName();
		String json_result = null;
		try {
			json_result = JSON.json(list);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
		}
		return json_result;
	}
	@RequestMapping(value = "/app_create", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String create(String name,String code) throws Exception {
		name =this.change(name);
		code=this.change(code);
		SysApplication exist = appDao.getByCode(code);
		if(exist != null){
			//throw new Exception("应用编码[" + code + "]已经存在");
			return fail("应用编码[" + code + "]已经存在");
		}
		appService.create(name, code);
		return success();
	}
	@RequestMapping(value = "/app_update", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String update(String id,String name,String code) throws Exception {
		name =this.change(name);
		code=this.change(code);
		SysApplication exist = appDao.getByCode(code);
		if(exist != null && !exist.getId().equals(id)){
			//throw new Exception("应用编码[" + code + "]已经存在");
			return fail("应用编码[" + code + "]已经存在");
		}
		appService.update(id, name, code);
		return success();
	}
	@RequestMapping(value = "/app_delete", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String delete(String id) throws Exception {
		try{
			UrmException.assertFor(1>resourceDao.getAppResources(id).size(), "-1", "该应用存在资源关联，无法删除！");
		}catch (Exception e){
			return fail(e.getMessage());
		}
		//UrmException.assertFor(1>resourceDao.getAppResources(id).size(), "-1", "该应用存在资源关联，无法删除！");
		appDao.deleteById(id);
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
		}
		return "";
	}

	private String fail(String errorMessage) {
		try {
			Map map = new HashMap();
			map.put("ret", "1");
			map.put("retInfo", errorMessage);
			return JSON.json(map);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
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
