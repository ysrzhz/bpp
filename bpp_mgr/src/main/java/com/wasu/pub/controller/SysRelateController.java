package com.wasu.pub.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.common.json.JSON;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.ResourceDao;
import com.wasu.pub.service.RelateService;
import com.wasu.pub.service.ResourceService;
import com.wasu.sid.SysResource;

@Controller
@RequestMapping("/views/manage")
public class SysRelateController {
	private static Logger log = Logger.getLogger(SysRelateController.class);
	@Autowired
	private RelateDao relateDao;
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private RelateService relateService;
	@Autowired
	private ResourceService resourceService;
	protected List<?> list;

	@RequestMapping(value = "/relate_change", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public String change(String objectId,String relatedIds,String typeId) throws Exception {
		relateService.change(objectId, relatedIds, typeId);
		return success();
	}
	
	@RequestMapping(value = "/relate_changeRelated", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String changeRelated(String objectId,String relatedIds,String typeId) throws Exception {
		relateService.changeRelated(objectId, relatedIds, typeId);
		return success();
	}	
	
	@RequestMapping(value = "/relate_getRelate", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String getRelate(String objectId,String typeId) throws Exception {
		list = resourceDao.getResources(objectId, typeId);
		List listMap=jsonTree("id", "name", "parentId", new NodeHandler() {
			public void handle(Map<String, Object> node) {
				SysResource resource = (SysResource) node.get("value");
				node.put("checked", resource.getAuth().intValue() == 1);
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
	
	@RequestMapping(value = "/relate_getUserRate", produces = { "application/json;charset=UTF-8" })
	@ResponseBody	
	public String getUserRate(String objectId, String typeId) throws Exception {
		List list = relateService.getUserByRoleIdAndOrgId(objectId, typeId);
		try {
			return JSON.json(list);
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
	
	public static Logger getLog() {
		return log;
	}
	public static void setLog(Logger log) {
		SysRelateController.log = log;
	}
	
	public RelateDao getRelateDao() {
		return relateDao;
	}
	public void setRelateDao(RelateDao relateDao) {
		this.relateDao = relateDao;
	}
	
	public ResourceDao getResourceDao() {
		return resourceDao;
	}
	public void setResourceDao(ResourceDao resourceDao) {
		this.resourceDao = resourceDao;
	}
	
	public RelateService getRelateService() {
		return relateService;
	}
	public void setRelateService(RelateService relateService) {
		this.relateService = relateService;
	}
	
	public ResourceService getResourceService() {
		return resourceService;
	}
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
}