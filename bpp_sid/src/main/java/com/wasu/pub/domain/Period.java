package com.wasu.pub.domain;

import java.util.Date;

import com.wasu.pub.util.DateUtil;

/***
 * 进行时间段查询的标志字段
 * @author Administrator
 *
 */
public class Period {

	private Class clzz;
	private String keyField;
	private String dbFielName;
	private Date begin;//用于范围查询
	private Date end;
	private Date date;//用于制定日期查询
	private StringBuilder sb = new StringBuilder();
	
	//转换成hql语句
	public String toHql(){
		boolean isAnd=false;
		if(date!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(keyField).append("='").append(DateUtil.getNormal(date)).append("'");
			isAnd=true;
		}
		if(begin!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(keyField).append(">='").append(DateUtil.getNormal(begin)).append("'");
			isAnd=true;
		}
		if(end!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(keyField).append("<='").append(DateUtil.getNormal(end)).append("'");
			isAnd=true;
		}
		return sb.toString();
	}
	
	//转换成hql语句
	public String toSql(){
		boolean isAnd=false;
		if(date!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(dbFielName).append("='").append(DateUtil.getNormal(date)).append("'");
			isAnd=true;
		}
		if(begin!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(dbFielName).append(">='").append(DateUtil.getNormal(begin)).append("'");
			isAnd=true;
		}
		if(end!=null){
			if(isAnd){
				sb.append(" and ");
			}
			sb.append(" t.").append(dbFielName).append("<='").append(DateUtil.getNormal(end)).append("'");
			isAnd=true;
		}
		return sb.toString();
	}
	public Class getClzz() {
		return clzz;
	}
	public void setClzz(Class clzz) {
		this.clzz = clzz;
	}
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public Date getBegin() {
		return begin;
	}
	public void setBegin(Date begin) {
		this.begin = begin;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDbFielName() {
		return dbFielName;
	}
	public void setDbFielName(String dbFielName) {
		this.dbFielName = dbFielName;
	}
	
	
	
}
