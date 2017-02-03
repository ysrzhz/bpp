package com.wasu.pub.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.wasu.pub.annotation.WsTitle;

/**
 * 
 * @cms-domain
 * @BaseEntityPrimaryKey.java
 * @author 904032
 * @2014年3月21日-上午11:21:02
 */
@MappedSuperclass
public abstract class IdEntity{

	/** 以下部分为数据库字段 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "paymentableGenerator", strategy = "hilo")	
	@WsTitle(chName="编号",isShow=false)
	protected Long id;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	@XStreamConverter(value=DateConverter.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	protected Date createTime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") 
	@XStreamConverter(value=DateConverter.class)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	protected Date lastUpdateTime;
	/** 以上部分为数据库字段 */
	
	
	/** 以下部分为页面特殊查询条件 */
	@XStreamOmitField
	@Transient  
	@DateTimeFormat(pattern="yyyy-MM-dd")
	protected Date lastUpdateTimeStart;
	@XStreamOmitField
	@Transient  
	@DateTimeFormat(pattern="yyyy-MM-dd")
	protected Date lastUpdateTimeEnd;
	@XStreamOmitField
	@Transient  
	@DateTimeFormat(pattern="yyyy-MM-dd")
	protected Date createTimeStart;
	@XStreamOmitField
	@Transient  
	@DateTimeFormat(pattern="yyyy-MM-dd")
	protected Date createTimeEnd;
	@XStreamOmitField
	@Transient  
	protected List<Long> idList;
	@XStreamOmitField
	@Transient  
	protected List<String> queryGroupIds;
	@XStreamOmitField
	@Transient  
	protected Long notCompareId;//排除在外的Id
	/** 以上部分为页面特殊查询条件  */
	
	
	
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	
	public Date getLastUpdateTimeStart() {
		return lastUpdateTimeStart;
	}

	public void setLastUpdateTimeStart(Date lastUpdateTimeStart) {
		this.lastUpdateTimeStart = lastUpdateTimeStart;
	}

	public Date getLastUpdateTimeEnd() {
		return lastUpdateTimeEnd;
	}

	public void setLastUpdateTimeEnd(Date lastUpdateTimeEnd) {
		this.lastUpdateTimeEnd = lastUpdateTimeEnd;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public List<Long> getIdList() {
		return idList;
	}

	public void setIdList(List<Long> idList) {
		this.idList = idList;
	}

	public List<String> getQueryGroupIds() {
		return queryGroupIds;
	}

	public void setQueryGroupIds(List<String> queryGroupIds) {
		this.queryGroupIds = queryGroupIds;
	}

	public Long getNotCompareId() {
		return notCompareId;
	}

	public void setNotCompareId(Long notCompareId) {
		this.notCompareId = notCompareId;
	}

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 初始化特殊页面展示字段
	 */
	public abstract void init4page();
}
