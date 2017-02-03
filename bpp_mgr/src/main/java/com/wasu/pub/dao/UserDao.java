package com.wasu.pub.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.sid.SysUser;

@Component("userDao")
@Transactional
public class UserDao extends BaseDao<SysUser> {
	/**
	 * 根据查询条件列出用户列表
	 */
	public List<Map> list(String orgId, String name) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT u.id,u.loginName,u.manager,u.name,u.orgId,u.password, o.name AS orgName ");
		sb.append("FROM t_sys_user u LEFT JOIN t_sys_organization o ON o.id = u.orgId  where 1=1 ");
		if (StringUtils.isNotBlank(name)) {
			sb.append("and (u.name like '%"+name+"%' or u.loginName like '%"+name+"%' )");
		}
		
		if (StringUtils.isNotBlank(orgId)) {
			sb.append("and u.orgId = '"+orgId+"' ");
		}
		
		return sqlListByMap(sb.toString());
	}

	public SysUser getLoginUser(String loginName) throws Exception {
		Map param = new HashMap();
		param.put("loginName", loginName);
		return (SysUser) getOne(param);
	}
	
	/**
	 * 获取角色相关的资源
	 */
	public List<SysUser> getUserByRoleIdAndOrgId(String roleId, String orgId) throws Exception {
		StringBuffer sb = new StringBuffer("SELECT u.id,u.name, ");
		sb.append("IF((SELECT count(*) FROM t_sys_relate rel WHERE rel.objectId = u.id ");
		sb.append(" AND rel.relatedId = '" + roleId + "' )>0,\"true\",\"false\") ");
		sb.append(" AS checked  FROM t_sys_user u  WHERE u.orgId = '" + orgId + "' ORDER BY u.name ");
		return sqlList(sb.toString());
	}
}