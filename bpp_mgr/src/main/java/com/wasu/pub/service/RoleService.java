package com.wasu.pub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.dao.RoleDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysRole;

@Component
@Transactional
public class RoleService extends BaseService{
	@Autowired
	private RoleDao roleDao;
	
	public void create(String name, String remark) throws Exception{
		SysRole role = new SysRole();
		role.setId(UID.create());
		role.setName(name);
		role.setRemark(remark);
		roleDao.save(role);
		refreshSysAndAllRelateCache();
	}
	
	public void update(String id, String name, String remark) throws Exception{
		SysRole role = roleDao.get(id);
		role.setName(name);
		role.setRemark(remark);
		roleDao.update(role);
		refreshSysAndAllRelateCache();
	}
}