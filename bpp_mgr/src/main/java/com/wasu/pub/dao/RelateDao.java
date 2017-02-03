package com.wasu.pub.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.query.Query;
import com.wasu.sid.SysRelate;

@Component("relateDao")
@Transactional
public class RelateDao extends BaseDao<SysRelate> {
	public void delete(String objectId, String relateId, String typeId) throws Exception {
		Query query = new Query();
		query=query.clzz(SysRelate.class.getName()).where();
		if (StringUtils.isBlank(typeId)) {
			query =query.and().isnull("typeId");
		} else {
			query =query.and().eq("typeId",typeId);
		}
		
		if (!StringUtils.isBlank(objectId)) {
			query =query.and().eq("objectId",objectId);
		}
		
		if (!StringUtils.isBlank(relateId)) {
			query =query.and().eq("relatedId",relateId);
		}
		
		List<SysRelate> list = list(query);
		for (SysRelate relate : list) {
			delete(relate);
		}
	}

	/**
	 * 查询是否被关联
	 */
	public int existRelate(String id) throws Exception {
		String sql = "select count(*) from t_sys_relate where relatedId = '" + id+ "' ";
		return getCount(sql);
	}
	
	public void deleteByRelateId(String relateId) throws Exception {
		String sql = "delete from t_sys_relate where relatedId = '" + relateId+ "' ";
		dbCenter.execute(SysRelate.class.getName(), sql);
	}

	/**
	 * 查询出所有关联记录，用于object一方维护关联关系（删除时）
	 */
	public List<SysRelate> getByObjectId(String id) throws Exception {
		Map param = new HashMap();
		param.put("objectId", id);
		return list(param);
	}

	/**
	 * 新增或编辑角色的时候进行角色名称唯一性判断
	 * @param name
	 * @return
	 */
	public int existRole(String name) throws Exception {
		String sql = "select count(*) from t_sys_role where name = '" + name+ "' ";
		return getCount(sql);
	}
}