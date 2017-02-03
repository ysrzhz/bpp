package com.wasu.pub.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.sid.SysOrganization;

@Component("orgDao")
@Transactional
public class OrganizationDao extends BaseDao<SysOrganization> {
	public List<SysOrganization> getRelateOrgs(String orgId) throws Exception {
		String sql = "select o.* from t_sys_organization o inner join t_sys_relate r on r.relatedId=o.id where r.objectId=?";
		return sqlList(sql, orgId);
	}

	public List<SysOrganization> listOther(String id) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT o.id,o.name, IF((SELECT count(*) ");
		sb.append(" FROM t_sys_relate rel  WHERE rel.objectId = '" + id + "' ");
		sb.append(" AND rel.relatedId = o.id)>0,'true','false') AS checked ");
		sb.append(" FROM t_sys_organization o WHERE o.id <> '" + id + "' ");
		return sqlList(sb.toString());
	}

	public List<SysOrganization> listByRole(String roleId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT o.id,o.name, IF((SELECT count(*) ");
		sb.append(" FROM t_sys_relate rel  WHERE rel.relatedId = '" + roleId + "' ");
		sb.append(" AND rel.objectId = o.id)>0,'true','false') AS checked ");
		sb.append(" FROM urm_organization o left join t_sys_relate re on o.id=re.objectId ");
		sb.append(" WHERE re.relatedId = '" + roleId + "' and typeId='orgRole'");
		return sqlList(sb.toString());
	}
	
	public List<SysOrganization> listAllByRole(String roleId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT o.id,o.name, IF((SELECT count(*) ");
		sb.append(" FROM t_sys_relate rel  WHERE rel.relatedId = '" + roleId + "' ");
		sb.append(" AND rel.objectId = o.id)>0,'true','false') AS checked ");
		sb.append(" FROM t_sys_organization o");
		return sqlList(sb.toString());
	}
	
	public SysOrganization getByCode(String code) throws Exception {
		return (SysOrganization)getOne(SysOrganization.class.getName(),"code", code);
	}
}