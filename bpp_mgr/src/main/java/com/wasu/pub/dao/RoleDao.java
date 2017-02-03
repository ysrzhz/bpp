package com.wasu.pub.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.query.Query;
import com.wasu.sid.SysRole;

@Component("roleDao")
@Transactional
public class RoleDao extends BaseDao<SysRole> {
	public List<SysRole> getUserRoleList(String userId) throws Exception {
		String sql = "select ro.* from t_sys_role ro left join t_sys_relate re on ro.id=re.relatedId where re.objectId=?";
		return sqlList(sql, userId);
	}

	public List<SysRole> listByName() throws Exception {
		Query query = new Query();
		query.clzz(SysRole.class.getName()).orderBy().sort("name", true);
		Response response= dbCenter.queryList(SysRole.class.getName(), query);
		return (List)response.getEntity();
	}

	public List<SysRole> listByUser(String userId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT distinct ro.id,ro.name, IF((SELECT count(*) ");
		sb.append(" FROM t_sys_relate rel  WHERE rel.objectId = '" + userId + "' ");
		sb.append(" AND rel.relatedId = ro.id)>0,'true','false') AS checked ");
		sb.append(" FROM t_sys_role ro left join t_sys_relate re on ro.id=re.relatedId ");
		return sqlList(sb.toString());
	}
	
	public List<SysRole> listByOrg(String orgId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT ro.id,ro.name, IF((SELECT count(*) ");
		sb.append(" FROM t_sys_relate rel  WHERE rel.objectId = '" + orgId + "' ");
		sb.append(" AND rel.relatedId = ro.id)>0,'true','false') AS checked ");
		sb.append(" FROM t_sys_role ro ");
		return sqlList(sb.toString());
	}
}