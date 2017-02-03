package com.wasu.pub.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.wasu.pub.WSException;
import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.ResourceDao;
import com.wasu.pub.service.ResourceService;
import com.wasu.sid.SysResource;
import com.wasu.sid.UID;
import com.wasu.sid.UrmException;

@Controller
@RequestMapping("/views/manage")
public class SysResourceController {
	private static Logger log = Logger.getLogger(SysResourceController.class);
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private RelateDao relateDao;
	private List list;
	@Autowired
	private ResourceService resourceService;

	@RequestMapping(value = "/resource_list", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String export(String appId) throws Exception {
		String json = resourceService.export(appId);
		return json;
	}
	
	@RequestMapping(value = "/resource_importResource", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String importResource(String importResourceFileName,String appId, File importResource) throws Exception {
		if (importResourceFileName == null || !importResourceFileName.endsWith(".json")) {
			throw new UrmException("-1", "请选择json格式的文件");
		}
		
		resourceService.importResource(appId, importResource);
		return success();
	}
	
	@RequestMapping(value = "/resource_create", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String create(String id,String code,String appId,String action, String parentId,String rank,String name) throws Exception {
		try{
			name =this.change(name);
			SysResource resource = new SysResource();
			resource.setId(UID.create());
			setResource(resource , id, code, appId, action, parentId, rank, name);
			resourceDao.save(resource);
			return success();
		}catch (Exception e){
			return fail(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/resource_update", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String update(String id,String code,String appId,String action, String parentId,String rank,String name) throws Exception {
		name =this.change(name);
		SysResource resource = new SysResource();
		resource.setId(id);
		setResource (resource , id, code, appId, action, parentId, rank, name);
		resourceService.update(resource);
		return success();
	}
	
	@RequestMapping(value = "/resource_delete", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String delete(String id) throws Exception {
		try{
			relateDao.deleteByRelateId(id);
			resourceService.delete(id);
			return success();
		}catch (WSException e){
			return fail(e.getErrorMsg());
		}
	}

	private void setResource(SysResource resource,String id,String code,String appId,String action,String parentId,String rank,String name ) throws Exception {
		if (!StringUtils.isBlank(code)) {
			SysResource theResource = resourceDao.getResource(appId, code);
			if (theResource != null && !theResource.getId().equals(id)) {
				throw new Exception("编码[" + code + "]已经存在");
			}
		}
		
		resource.setAppId(appId);
		resource.setAction(action);
		resource.setName(name);
		resource.setCode(code);
		resource.setParentId(parentId);
		resource.setRank(rank==null||rank.equals("")?0:new Integer(rank));
	}
	
	@RequestMapping(value = "/resource_getResourceTree", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String getResourceTree(String appId) throws Exception {
		list = resourceDao.getResources(appId);
		List listMap=jsonTree("id", "name", "parentId", new NodeHandler() {
			public void handle(Map<String, Object> node) {
				SysResource res = (SysResource) node.get("value");
				node.put("action", res.getAction());
				node.put("code", res.getCode());
				node.put("rank", res.getRank());
				node.put("appId", res.getAppId());
				node.put("parentId", res.getParentId());
				node.put("parentName", res.getParentName());
				node.put("iconCls", node.get("children") == null ? "treenode-sub" : "treenode-top");
			}
		});
		
		try {
			return JSON.json(listMap);
		} catch (IOException e) {
			log.error("获取时间段编排列表数据异常, 数据转换失败", e);
			return null;
		}
	}
	
	protected interface NodeHandler{
		public void handle(Map<String, Object> node);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> jsonTree(String idName, String textName, String parentIdName, NodeHandler handler) throws JsonGenerationException, Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<Object, Map<String, Object>> types = new HashMap<Object, Map<String, Object>>();
		for(Object value : this.list){
			Map<String, Object> typeNode = new HashMap<String, Object>();
			Object id = PropertyUtils.getProperty(value, idName);
			typeNode.put("id", id);
			typeNode.put("text", PropertyUtils.getProperty(value, textName));
			typeNode.put("value", value);
			types.put(id, typeNode);
		}
		
		for(Object value : this.list){
			Object parentId = null;
			try{				
				parentId = PropertyUtils.getProperty(value, parentIdName);
			}catch(NoSuchMethodException ex){}
			Map<String, Object> parent = null;
			if(parentId != null){				
				parent = types.get(parentId);
			}
			
			Map<String, Object> typeNode = types.get(PropertyUtils.getProperty(value, idName));
			if(parent == null){
				list.add(typeNode);
			}else{
				List<Map<String,Object>> children = (List<Map<String,Object>>)parent.get("children");
				if(children == null){
					children = new ArrayList<Map<String,Object>>();
					parent.put("children", children);
				}
				
				children.add(typeNode);
			}
		}
		
		for(Map<String, Object> node : types.values()){
			if(handler != null){			
				handler.handle(node);
			}
			
			node.remove("value");
		}
		
		return list;
	}	
	
	@RequestMapping(value = "/resource_getUserResources", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String getUserResources(String userId,String appId) throws Exception {
		list = resourceService.getResources(userId, appId);
		List listMap=jsonTree("id", "name", "parentId", new NodeHandler() {
			public void handle(Map<String, Object> node) {
				SysResource resource = (SysResource) node.get("value");
				node.put("checked", resource.getAuth() != null && resource.getAuth().intValue() == 1);
				node.put("iconCls", node.get("children") == null ? "treenode-sub" : "treenode-top");
			}
		});
		
		try {
			return JSON.json(listMap);
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