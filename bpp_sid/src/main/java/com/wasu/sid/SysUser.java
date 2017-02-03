package com.wasu.sid;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "t_sys_user")
public class SysUser  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	@Index(name = "loginName")
	private String loginName;
	@JsonIgnore
	private String password;
	@Index(name = "orgId")
	private String orgId;
	private String manager;
	@Transient
	private List<SysResource> resources;
	@Transient
	private String orgName;
	@Transient
	private SysOrganization org;
	@Transient
	private List<SysOrgParam> orgParams;
	@Transient
	private List<SysOrganization> relateOrgs;
	@Transient
	private String checked;
	private String area;
	private String areaName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public List<SysResource> getResources() {
		return resources;
	}

	public void setResources(List<SysResource> resources) {
		this.resources = resources;
	}

	public SysOrganization getOrg() {
		return org;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public void setOrg(SysOrganization org) {
		this.org = org;
	}

	public List<SysOrganization> getRelateOrgs() {
		return relateOrgs;
	}

	public void setRelateOrgs(List<SysOrganization> relateOrgs) {
		this.relateOrgs = relateOrgs;
	}

	public boolean getChecked() {
		return Boolean.parseBoolean(checked);
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public List<SysOrgParam> getOrgParams() {
		return orgParams;
	}

	public void setOrgParams(List<SysOrgParam> orgParams) {
		this.orgParams = orgParams;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}