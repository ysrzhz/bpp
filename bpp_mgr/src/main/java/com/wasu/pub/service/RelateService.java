package com.wasu.pub.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.dao.RelateDao;
import com.wasu.pub.dao.ResourceDao;
import com.wasu.pub.dao.UserDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysRelate;
import com.wasu.sid.SysResource;
import com.wasu.sid.SysUser;

@Component
@Transactional
public class RelateService  extends BaseService{
	protected static Logger logger = LoggerFactory.getLogger(RelateService.class);
	@Autowired
	private RelateDao relateDao;
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private UserDao userDao;

	public void change(String objectId, String relatedIds, String typeId) throws Exception {
		String[] relateds = relatedIds.split(",");
		relateDao.delete(objectId, null, typeId);
		if (StringUtils.isBlank(relateds[0])) {
			refreshSysAndAllRelateCache();
			return;
		}
		
		for (String related : relateds) {
			SysRelate relate = new SysRelate();
			relate.setId(UID.create());
			relate.setObjectId(objectId);
			relate.setTypeId(typeId);
			relate.setRelatedId(related);
			relateDao.save(relate);
		}
		
		refreshSysAndAllRelateCache();
	}
	
	public void changeRelated(String objectIds, String relatedId, String typeId) throws Exception {
		String[] objects = objectIds.split(",");
		relateDao.delete(null, relatedId, typeId);
		if (StringUtils.isBlank(objects[0])) {
			refreshSysAndAllRelateCache();
			return;
		}
		for (String objectId : objects) {
			SysRelate relate = new SysRelate();
			relate.setId(UID.create());
			relate.setObjectId(objectId);
			relate.setTypeId(typeId);
			relate.setRelatedId(relatedId);
			relateDao.save(relate);
		}
		
		refreshSysAndAllRelateCache();
	}

	public List<SysResource> getRelatedByRoleIdAndAppId(String roleId, String appId) throws Exception {
		return resourceDao.getResources(roleId, appId);
	}
	
	public List<SysUser> getUserByRoleIdAndOrgId(String roleId, String orgId) throws Exception {
		return userDao.getUserByRoleIdAndOrgId(roleId, orgId);
	}
}