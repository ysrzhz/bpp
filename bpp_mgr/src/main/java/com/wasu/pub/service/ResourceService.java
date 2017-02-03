package com.wasu.pub.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wasu.pub.WSException;
import com.wasu.pub.dao.ResourceDao;
import com.wasu.sid.SysResource;

@Service("resourceService")
@Transactional
public class ResourceService extends BaseService {
	@Autowired
	private ResourceDao resourceDao;

	public void delete(String id) throws Exception {
		int count = resourceDao.findChildrenCount(id);
		WSException.assertFor(1 > count, "-1", "该资源下存在子资源，无法删除");
		resourceDao.deleteById(id);
		refreshSysAndAllRelateCache();
	}

	public void update(SysResource resource) throws Exception {
		String id = resource.getId();
		String parentId = resource.getParentId();
		WSException.assertFor(!id.equals(parentId), "-1", "不能设置parent为自己");
		resourceDao.update(resource);
		refreshSysAndAllRelateCache();
	}
	
	public String export(String appId) throws Exception {
		List<SysResource> resources = resourceDao.getAppResources(appId);
		List<SysResource> result = new ArrayList<SysResource>();
		for(SysResource res : resources){
			SysResource resource = new SysResource();
			PropertyUtils.copyProperties(resource, res);
			resource.setAppId(null);
			result.add(resource);
		}
		
		return new Gson().toJson(result);
	}
	
	public void importResource(String appId, File json) throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream(json), "UTF-8");
		List<SysResource> resources = new Gson().fromJson(reader,  new TypeToken<List<SysResource>>() {}.getType());
		resourceDao.deleteAppResource(appId);
		for(SysResource res : resources){
			res.setAppId(appId);
			resourceDao.save(res);
		}
		refreshSysAndAllRelateCache();
	}
	
	public List<SysResource> getResources(String userId, String appId) throws Exception{
		List<SysResource> appResources = resourceDao.getAppResources(appId);
		List<SysResource> userResources = resourceDao.getUserResources(userId, appId);
		for(SysResource appResource : appResources){
			for(SysResource userResource : userResources){
				if(appResource.getId().equals(userResource.getId())){
					appResource.setAuth(BigInteger.valueOf(1));
				}
			}
		}
		
		return appResources;
	}
}