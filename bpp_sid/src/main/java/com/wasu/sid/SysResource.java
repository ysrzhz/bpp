package com.wasu.sid;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

@SuppressWarnings("deprecation")
@Entity
@Table(name = "t_sys_resource")
public class SysResource implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String code;
	private String name;
	private String action;
	private int rank;
	@Index(name = "appId")
	private String appId;
	private String parentId;
	@Transient
	private String parentName;
	@Transient
	private BigInteger auth;
	@Transient
	private List<SysResource> subResources = new ArrayList<SysResource>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id; 
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public BigInteger getAuth() {
		return auth;
	}

	public void setAuth(BigInteger auth) {
		this.auth = auth;
	}

	public List<SysResource> getSubResources() {
		return subResources;
	}

	public void setSubResources(List<SysResource> subResources) {
		this.subResources = subResources;
	}
}