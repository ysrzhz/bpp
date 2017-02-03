package com.wasu.bpp.domain;

import java.io.Serializable;

public class MsgRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String sendTime;
	private String content;
	private String stbId;
	private String areaId;
	private String custId;
	private String cronExpr;
	private String validTime;
	private String scope;
	private String vtype;
	private String dataSrc;
	private String encoding;

	@Override
	public String toString() {
		return "title=" + title 
			+ ", sendTime=" + sendTime 
			+ ", content=" + content 
			+ ", stbId=" + stbId
			+ ", areaId=" + areaId 
			+ ", custId=" + custId 
			+ ", cronExpr=" + cronExpr 
			+ ", validTime=" + validTime
			+ ", scope=" + scope 
			+ ", vtype=" + vtype 
			+ ", dataSrc=" + dataSrc
			+ ", encoding=" + encoding;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
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
	
	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
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

	public String getDataSrc() {
		return dataSrc;
	}

	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}