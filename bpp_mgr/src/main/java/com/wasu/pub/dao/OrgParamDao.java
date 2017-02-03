package com.wasu.pub.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.sid.SysOrgParam;

@Component("paramDao")
@Transactional
public class OrgParamDao extends BaseDao<SysOrgParam> {
	public List<SysOrgParam> getParams(String orgId) throws Exception {
		Map param = new HashMap();
		param.put("orgId", orgId);
		return list(param);
	}
	
	public SysOrgParam getOrgParam(String orgId, String name) throws Exception {
		Map param = new HashMap();
		param.put("orgId", orgId);
		param.put("name", name);
		return (SysOrgParam) getOne(param);
	}
}