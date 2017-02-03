package com.wasu.pub.domain;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.thoughtworks.xstream.annotations.XStreamOmitField;
/**
 * 
 * @cms-domain
 * @BaseEntityStatus.java
 * @2014年3月21日-上午9:12:38
 */
@MappedSuperclass
public abstract class BaseEntityStatus extends IdEntity {

	@XStreamOmitField
	protected Long status;//0-可编辑； 1-已发布 2；已下线

	@XStreamOmitField
	@Transient  
	protected Long notstatus;

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getNotstatus() {
		return notstatus;
	}

	public void setNotstatus(Long notstatus) {
		this.notstatus = notstatus;
	}
	@Override
	public void init4page() {
	}
    public String toString()
    {
      return ReflectionToStringBuilder.toString(this);
    }
}
