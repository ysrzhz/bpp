package com.wasu.pub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.dao.OrgParamDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysOrgParam;

@Component
@Transactional
public class OrgParamService  extends BaseService{
	@Autowired
	private OrgParamDao paramDao;
	
	public void create(String name, String value, String remark, String orgId) throws Exception{
		if(paramDao.getOrgParam(orgId, name) != null){
			throw new Exception("名称已经存在!");
		}
		SysOrgParam param = new SysOrgParam();
		param.setId(UID.create());
		param.setName(name);
		param.setValue(value);
		param.setRemark(remark);
		param.setOrgId(orgId);
		paramDao.save(param);
		refreshSysAndAllRelateCache();
	}
	
	public void update(String id, String name, String value, String remark, String orgId) throws Exception{
		SysOrgParam param = paramDao.get(id);
		SysOrgParam nameParam = paramDao.getOrgParam(orgId, name);
		if(nameParam != null && !id.equals(nameParam.getId())){
			throw new Exception("名称已经存在!");
		}
		param.setName(name);
		param.setValue(value);
		param.setRemark(remark);
		param.setOrgId(orgId);
		paramDao.update(param);
		refreshSysAndAllRelateCache();
	}
}