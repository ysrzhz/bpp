package com.wasu.pub.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wasu.sid.SysApplication;
import com.wasu.sid.SysOrganization;
import com.wasu.sid.SysRelate;
import com.wasu.sid.SysResource;
import com.wasu.sid.SysRole;
import com.wasu.sid.SysUser;
import com.wasu.sid.bpp.MmsMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.service.DBCenter;
import com.wasu.pub.domain.IdEntity;
import com.wasu.pub.util.ReflectUtil;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.Sort;

@Service
public class BaseService<T> {
	protected Logger logger = Logger.getLogger(getClass());
	SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfDayAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static final String loginUser = "loginUser";
	public static final String remoteAddr = "remoteAddr";
	public static ThreadLocal<Map<String, Object>> userSession = new ThreadLocal<Map<String, Object>>();//线程缓存的用户
	@Autowired
	protected DBCenter dbCenter;

	public static SysUser getUser() {
		if (userSession.get() == null) {
			return null;
		}
		
		return (SysUser) userSession.get().get(loginUser);
	}

	public static String getRemoteAddr() {
		if (userSession.get() == null) {
			return null;
		}
		
		return (String) userSession.get().get(remoteAddr);
	}

	public void refreshCache(String clzz) {
		dbCenter.refreshCache(clzz);
	}

	/**
	 * 刷新与广告位相关的Cache
	 */
	public void refreshPlaceAndAllRelateCache() {
		dbCenter.refreshCache(MmsMsg.class.getName());
	}

	public void refreshSysAndAllRelateCache() {
		dbCenter.refreshCache(SysApplication.class.getName());
		dbCenter.refreshCache(MmsMsg.class.getName());
		dbCenter.refreshCache(SysOrganization.class.getName());
		dbCenter.refreshCache(SysRelate.class.getName());
		dbCenter.refreshCache(SysResource.class.getName());
		dbCenter.refreshCache(SysRole.class.getName());
		dbCenter.refreshCache(SysUser.class.getName());
	}

	@Transactional
	public void updateStatus(String clzz, List<Long> idList, String filedName, Integer status) {
		Map param = new HashMap();
		param.put(filedName, status + "");
		dbCenter.updateStatus(clzz, idList, param);
	}

	@Transactional
	public void delete(String clzz, List<Long> idList) {
		dbCenter.delete(clzz, idList);
	}

	public Serializable saveOrUpdate(IdEntity entity) {
		if (entity.getId() == null) {
			Response res = dbCenter.insertEntity(entity, entity.getClass().getName());
			return (Serializable) res.getEntity();
		} else {
			dbCenter.update((Serializable) entity, entity.getClass().getName());
		}

		return entity.getId();
	}

	public Serializable save(IdEntity entity) {
		Response res = dbCenter.insertEntity(entity, entity.getClass().getName());
		return (Serializable) res.getEntity();
	}

	public Serializable saveSerial(Serializable entity) {
		Response res = dbCenter.insert(entity, entity.getClass().getName());
		return (Serializable) res.getEntity();
	}

	/**
	 * 默认，按id 倒序排序
	 * @param query
	 * @param page
	 * @param sorts
	 * @return
	 */
	public Page getPage(Object query, Page page, Sort sorts) {
		Map param = new HashMap();
		param = ReflectUtil.getObjectAsMap(query);
		Response response = dbCenter.getFullInfoPage(query.getClass().getName(), param, sorts, page);
		return (Page) response.getEntity();
	}

	//频道重写
	public Page getPage1(Object query, Page page, Sort sorts) {
		Map param = new HashMap();
		param = ReflectUtil.getObjectAsMap(query);
		Response response = dbCenter.getFullInfoPage(query.getClass().getName(), param, sorts, page);
		return (Page) response.getEntity();
	}

	public Page getPage(String clzz, Map param, Page page, Sort sorts) {
		Response response = dbCenter.getFullInfoPage(clzz, param, sorts, page);
		return (Page) response.getEntity();
	}

	//重写获取媒资文件列表
	public Page getPageRsFile(String clzz, Map param, Page page, Sort sorts) {
		Response response = dbCenter.getPage(clzz, param, sorts, page);
		return (Page) response.getEntity();
	}

	public Object getOne(Object query) {
		Map param = new HashMap();
		param = ReflectUtil.getObjectAsMap(query);
		Response response = dbCenter.queryOne(query.getClass().getName(), param);
		return response.getEntity();
	}

	public List<T> getAll(IdEntity query, Sort sorts) {
		Map param = new HashMap();
		param = ReflectUtil.getObjectAsMap(query);
		Response response = dbCenter.getall(query.getClass().getName(), param, null);
		return (List) response.getEntity();
	}

	public T getById(String clzz, Long id) {
		Response response = dbCenter.getById(clzz, id);
		return (T) response.getEntity();
	}
}