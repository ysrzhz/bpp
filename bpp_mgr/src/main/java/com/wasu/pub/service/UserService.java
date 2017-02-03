package com.wasu.pub.service;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.WSException;
import com.wasu.pub.service.DBCenter;
import com.wasu.pub.dao.BaseDao;
import com.wasu.pub.dao.OrgParamDao;
import com.wasu.pub.dao.OrganizationDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysApplication;
import com.wasu.sid.SysOrganization;
import com.wasu.sid.SysResource;
import com.wasu.sid.SysUser;

@Component
@Transactional
public class UserService  extends BaseService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DBCenter dbCenter;
	@Autowired
	private ResourceService resourceService;	
	@Autowired
	private OrganizationDao orgDao;
	@Autowired
	private OrgParamDao paramDao;

	public void init(SysOrganization org) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "loginName", "coship");
		if (user == null) {
			create("超级管理员", "coship", org.getId(), "1", "0");
		}
	}

	public void create(String name, String loginName, String orgId, String manager, String area) throws Exception {
		SysUser user = new SysUser();
		user.setId(UID.create());
		user.setName(name);
		user.setLoginName(loginName);
		user.setPassword(DigestUtils.md5Hex("111111"));
		user.setOrgId(orgId);
		user.setManager(manager);
		user.setArea(area);
		dbCenter.insert(user, SysUser.class.getName());
		refreshSysAndAllRelateCache();
	}

	public void update(String id, String name, String loginName, String orgId, String manager) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "id", id);;
		user.setOrgId(orgId);
		user.setName(name);
		user.setLoginName(loginName);
		user.setOrgId(orgId);
		user.setManager(manager);
		dbCenter.update(user,SysUser.class.getName());
		refreshSysAndAllRelateCache();
	}

	public void password(String id, String password) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "id", id);
		if (null == user) {
			throw new WSException("050001", "不存在该用户");
		}
		
		user.setPassword(DigestUtils.md5Hex(password));
		dbCenter.update(user,SysUser.class.getName());
		refreshSysAndAllRelateCache();
	}

	/**
	 * 修改密码接口调用该方法
	 * 
	 * @throws Exception
	 */
	public void editPassword(String loginName, String password) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "loginName", "coship");
		if(null == user){
			throw new WSException("050001", "不存在该用户");
		}
		
		user.setPassword(DigestUtils.md5Hex(password));
		dbCenter.update(user, SysUser.class.getName());
		refreshSysAndAllRelateCache();
	}

	public void manager(String id, String manager) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "id", id);
		user.setManager(manager);
		dbCenter.update(user, SysUser.class.getName());
		refreshSysAndAllRelateCache();
	}

	public SysUser login(String loginName, String password, String appCode) throws Exception {
		SysUser user =(SysUser) baseDao.getOne(SysUser.class.getName(), "loginName", "coship");
		WSException.notNull(user, "050001");//用户不存在
		WSException.assertFor(user.getPassword().equals(DigestUtils.md5Hex(password)), "050002");//密码错误
		SysApplication app = (SysApplication)baseDao.getOne(SysApplication.class.getName(),"code" ,appCode);
		WSException.notNull(app, "050003", "应用编码" + appCode + "不存在!");
		List<SysResource> resources = resourceService.getResources(user.getId(), app.getId());
		user.setResources(resources);
		user.setOrg(orgDao.get(user.getOrgId()));
		user.setOrgParams(paramDao.getParams(user.getOrgId()));
		user.setRelateOrgs(orgDao.getRelateOrgs(user.getOrgId()));
		return user;
	}

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

	public DBCenter getDbCenter() {
		return dbCenter;
	}

	public void setDbCenter(DBCenter dbCenter) {
		this.dbCenter = dbCenter;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
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
}