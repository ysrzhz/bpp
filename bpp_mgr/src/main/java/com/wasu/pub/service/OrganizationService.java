package com.wasu.pub.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.dao.OrganizationDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysOrganization;

@Component
@Transactional
public class OrganizationService  extends BaseService{
	@Autowired
	private OrganizationDao orgDao;
	
	public SysOrganization init() throws Exception{
		SysOrganization org = orgDao.getByCode("coship");
		if(org == null){
			return create("超级管理员", "coship", "1", "初始化建立的管理员组织");
		}else{
			return org;
		}
	}
	
	public SysOrganization create(String name, String code, String manager, String remark) throws Exception{
		SysOrganization org = new SysOrganization();
		org.setId(UID.create());
		org.setName(name);
		org.setCode(code);
		org.setManager(manager);
		org.setRemark(remark);
		orgDao.save(org);
		refreshSysAndAllRelateCache();
		return org;
	}
	
	public void update(String id, String name, String code, String manager, String remark) throws Exception{
		SysOrganization org = orgDao.get(id);
		org.setName(name);
		org.setCode(code);
		org.setManager(manager);
		org.setRemark(remark);
		orgDao.update(org);
		refreshSysAndAllRelateCache();
	}

	public List<SysOrganization> list(String id) throws Exception {
		if(StringUtils.isBlank(id)){
			return orgDao.list();
		}else{
			return orgDao.listOther(id);
		}
	}
}