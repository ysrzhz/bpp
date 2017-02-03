package com.wasu.sid.bpp;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.wasu.pub.annotation.WsTitle;
import com.wasu.pub.domain.IdEntity;

//消息表
@Entity
@Table(name = "mms_msg")
@WsTitle(chName = "消息表", code = "mms_msg")
public class MmsMsg extends IdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Column(name="title",nullable=true,length=255)
	private String title;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	@XStreamConverter(value=DateConverter.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date sendTime;
	
	@Column(name="content",nullable=false,length=1000)
	private String content;
	
	@Column(name="stbId",nullable=true,length=20000)
	private String stbId;
	
	@Column(name="areaId",nullable=true,length=20000)
	private String areaId;
	
	@Column(name="custId",nullable=true,length=20000)
	private String custId;
	
	@Column(name="cronExpr",nullable=true,length=100)
	private String cronExpr;
	
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	@XStreamConverter(value=DateConverter.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date validTime;
	
	@Column(name="scope",nullable=false,length=2)
	private String scope;
	
	@Column(name="vtype",nullable=false,length=2)
	private String vtype;
	
	@Column(name="status",nullable=false,length=5)
	private String status;
	
	@Column(name="sysUserId",nullable=false,length=20)
	private String sysUserId;
		
	@Column(name="sysRoleId",nullable=false,length=20)
	private String sysRoleId;
	
	@Column(name="dataSrc",nullable=false,length=2)
	private String dataSrc;

	@Override
	public void init4page() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStbId() {
		return stbId;
	}

	public void setStbId(String stbId) {
		this.stbId = stbId;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getCronExpr() {
		return cronExpr;
	}

	public void setCronExpr(String cronExpr) {
		this.cronExpr = cronExpr;
	}
	
	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getVtype() {
		return vtype;
	}

	public void setVtype(String vtype) {
		this.vtype = vtype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(String sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getSysRoleId() {
		return sysRoleId;
	}

	public void setSysRoleId(String sysRoleId) {
		this.sysRoleId = sysRoleId;
	}

	public String getDataSrc() {
		return dataSrc;
	}

	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}
}