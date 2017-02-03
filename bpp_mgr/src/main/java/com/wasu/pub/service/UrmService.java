package com.wasu.pub.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.wasu.pub.service.DBCenter;
import com.wasu.sid.SysApplication;
import com.wasu.sid.SysOrgParam;
import com.wasu.sid.SysResource;
import com.wasu.sid.SysUser;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wasu.pub.Response;
import com.wasu.pub.WSException;
import com.wasu.pub.dao.BaseDao;
import com.wasu.pub.dao.OrgParamDao;
import com.wasu.pub.dao.OrganizationDao;

@Service
public class UrmService {
	private static String appCode = "mms";
	private static String urmUrl;
	private SysUser user;
	private HttpSession session;
	private Set<String> unAuthActions = new HashSet<String>();
	private Map<String, SysResource> resourceCodeMap = new HashMap<String, SysResource>();
	private Map<String, String> paramMap = new HashMap<String, String>();
	private List<SysResource> resourceTree = new ArrayList<SysResource>();
	public static final String URM_SESSION = "urmSession";
	@Autowired
	private DBCenter dbCenter;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private OrganizationDao orgDao;
	@Autowired
	private OrgParamDao paramDao;
	@Autowired
	private ResourceService resourceService;

	public UrmService() {
	}

	public static UrmService getUrmService(UrmService urmService, HttpSession session) {
		UrmService service = new UrmService();
		service.setBaseDao(urmService.getBaseDao());
		service.setDbCenter(urmService.getDbCenter());
		service.setOrgDao(urmService.getOrgDao());
		service.setParamDao(urmService.getParamDao());
		service.setResourceService(urmService.getResourceService());
		service.session = session;
		service.session.setAttribute(URM_SESSION, service);
		return service;
	}

	public static UrmService get(HttpSession session) {
		Object service = session.getAttribute(URM_SESSION);
		return service == null ? null : (UrmService) service;
	}

	/**
	 * 验证旧密码
	 */
	public String validationPassWord(String loginName, String password) throws Exception {
		Map map = new HashMap();
		map.put("loginName", loginName);
		Response response = dbCenter.queryOne(SysUser.class.getName(), map);
		SysUser user = (SysUser) response.getEntity();
		if (null == user) {
			return "-1";
		}
		
		String md5password = DigestUtils.md5Hex(password);
		if (user.getPassword().equals(md5password)) {
			return "0";
		} else {
			return "-1";
		}
	}

	/**
	 * 修改密码
	 */
	public String editPassword(String loginName, String password) throws Exception {
		Map map = new HashMap();
		map.put("loginName", loginName);
		Response response = dbCenter.queryOne(SysUser.class.getName(), map);
		SysUser dbUser = (SysUser) response.getEntity();
		if (null == dbUser) {
			return "-1";
		}
		
		dbUser.setPassword(DigestUtils.md5Hex(password));
		dbCenter.update(dbUser, SysUser.class.getName());
		resourceService.refreshSysAndAllRelateCache();
		return "0";
	}

	/**
	 * 登录
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SysUser login(String name, String password) throws Exception {
		Map map = new HashMap();
		map.put("loginName", name);
		Response response = dbCenter.queryOne(SysUser.class.getName(), map);
		SysUser dbUser = (SysUser) response.getEntity();
		WSException.notNull(dbUser, "050001");//用户不存在
		WSException.assertFor(dbUser.getPassword().equals(DigestUtils.md5Hex(password)), "050002");//密码错误
		SysApplication app = (SysApplication) baseDao.getOne(SysApplication.class.getName(), "code", appCode);
		WSException.notNull(app, "050003", "消息系统编码" + appCode + "不存在!");
		user = dbUser;
		List<SysResource> resources = resourceService.getResources(dbUser.getId(), app.getId());
		dbUser.setResources(resources);
		dbUser.setOrg(orgDao.get(user.getOrgId()));
		dbUser.setOrgParams(paramDao.getParams(user.getOrgId()));
		dbUser.setRelateOrgs(orgDao.getRelateOrgs(user.getOrgId()));
		unAuthActions.clear();
		resourceCodeMap.clear();
		Map<String, SysResource> resourceIdMap = new HashMap<String, SysResource>();
		for (SysResource resource : this.user.getResources()) {
			resourceCodeMap.put(resource.getCode(), resource);
			resourceIdMap.put(resource.getId(), resource);
			if (!StringUtils.isBlank(resource.getAction())
					&& (resource.getAuth() == null || resource.getAuth().intValue() == 0)) {
				unAuthActions.add(resource.getAction());
			}
		}

		Collections.sort(this.user.getResources(), new BeanComparator("rank"));
		resourceTree.clear();
		for (SysResource resource : this.user.getResources()) {
			if (StringUtils.isBlank(resource.getParentId())) {
				resourceTree.add(resource);
			} else {
				SysResource parent = resourceIdMap.get(resource.getParentId());
				if (parent != null) {
					parent.getSubResources().add(resource);
				}
			}
		}
		
		user = dbUser;
		paramMap.clear();
		if (this.user.getOrgParams() != null && this.user.getOrgParams().size() > 0) {
			for (SysOrgParam param : this.user.getOrgParams()) {
				paramMap.put(param.getName(), param.getValue());
			}
		}

		return dbUser;
	}

	public String createAuthScript() {
		return createAuthScript("id");
	}

	public String createAuthScript(String name) {
		String script = "<script type=\"text/javascript\">\r\n$(function(){\r\n";
		if (user == null || user.getResources() == null) {
			return "";
		}
		
		for (SysResource resource : user.getResources()) {
			if (resource.getAuth() != null) {
				if (resource.getAuth().intValue() == 1) {
					script += "$(\"[" + name + "='" + resource.getCode() + "']\").show();\r\n";
				} else if (!StringUtils.isBlank(resource.getAction())) {
					script += "$(\"[" + name + "='" + resource.getCode() + "']\").attr('action','"
							+ resource.getAction() + "');\r\n";
				}
			} else {
				script += "$(\"[" + name + "='" + resource.getCode() + "']\").hide();\r\n";
			}
		}
		
		return script + "});\r\n</script>\r\n";
	}

	public Set<String> getUnAuthActions() {
		return unAuthActions;
	}

	public boolean isUnAuth(String url) {
		for (String action : unAuthActions) {
			if (url.endsWith(action)) {
				return true;
			}
		}
		
		return false;
	}

	public static void setAppCode(String appCode) {
		UrmService.appCode = appCode;
	}

	public static void setUrmUrl(String urmUrl) {
		UrmService.urmUrl = urmUrl;
	}

	public SysUser getUser() {
		return user;
	}

	public SysResource getResource(String code) {
		return resourceCodeMap.get(code);
	}

	public List<SysResource> getResourceTree() {
		return resourceTree;
	}

	public String getParam(String name) {
		return paramMap.get(name);
	}

	public DBCenter getDbCenter() {
		return dbCenter;
	}

	public void setDbCenter(DBCenter dbCenter) {
		this.dbCenter = dbCenter;
	}

	public void setUser(SysUser user) {
		this.user = user;
	}

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
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

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public ResourceService getResourceService() {
		return resourceService;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
}