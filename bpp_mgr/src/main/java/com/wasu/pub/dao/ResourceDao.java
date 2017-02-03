package com.wasu.pub.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.query.Query;
import com.wasu.sid.SysResource;

@Component("resourceDao")
@Transactional
public class ResourceDao extends BaseDao<SysResource> {
	/**
	 * 获取应用下的资源
	 */
	public List<SysResource> getResources(String appId) throws Exception {
		StringBuffer sb = new StringBuffer("select r1.*, (select name from t_sys_resource r2 where r1.parentId = r2.id) as parentName from t_sys_resource r1 where appId = ? order by rank asc");
		return sqlList(sb.toString(), appId);
	}

	/**
	 * 查询该资源下子资源的个数
	 */
	public int findChildrenCount(String id) throws Exception {
		Query query = new Query();
		String hql = query.select().clzz(SysResource.class.getName()).where().and().eq("parentId", id).count();
		Response response = dbCenter.queryOne(SysResource.class.getName(), hql);
		return ((Long) response.getEntity()).intValue();
	}

	/**
	 * 获取角色相关的资源
	 */
	public List<SysResource> getResources(String roleId, String appId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT res.id,res.name,res.parentId,");
		sb.append("(SELECT count(*) FROM t_sys_relate rel  WHERE rel.relatedId = res.id ");
		sb.append(" AND rel.objectId = ?) AS auth FROM t_sys_resource res WHERE res.appId = ?");
		sb.append(" ORDER BY res.rank ");
		return sqlList(sb.toString(), roleId, appId);
	}

	/**
	 * 获取用户有权限的资源
	 */
	public List<SysResource> getUserResources(String userId, String appId) throws Exception {
		//获取用户菜单：通过用户找组织，通过用户及组织找角色(组织传入)或通过组织找角色(组织为orgRole)，接着通过角色找资源及应用，最后过滤掉不符合的应用
		String sql = "SELECT DISTINCT r.* FROM t_sys_resource r"
				+"      LEFT JOIN t_sys_relate l ON r.id = l.relatedId AND l.typeId = r.appId"
				+"      LEFT JOIN t_sys_relate l1 ON l1.relatedId = l.objectId AND l1.objectId = ?"
				+"     INNER JOIN t_sys_user u ON u.orgId = l1.typeId AND u.id = l1.objectId"
				+"     WHERE r.appId = ?"
				+"     UNION"
				+ "   SELECT DISTINCT r.* FROM t_sys_resource r"
				+ "     LEFT JOIN t_sys_relate l ON r.id = l.relatedId AND l.typeId = r.appId"
				+ "     LEFT JOIN t_sys_relate l1 ON l1.relatedId = l.objectId AND l1.typeId = 'orgRole'" 
				+ "    INNER JOIN t_sys_user u ON u.orgId = l1.objectId AND u.id = ?" 
				+ "    WHERE r.appId = ?";
		return sqlList(sql, userId, appId, userId, appId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<SysResource> getAppResources(String appId) throws Exception {
		Map param = new HashMap();
		param.put("appId", appId);
		return list(param);
	}

	public SysResource getResource(String appId, String code) throws Exception {
		Map param = new HashMap();
		param.put("appId", appId);
		param.put("code", code);
		return (SysResource) getOne(param);
	}

	public void deleteAppResource(String appId) throws Exception {
		sqlUpdate("delete from t_sys_resource where appId=?", appId);
	}
}