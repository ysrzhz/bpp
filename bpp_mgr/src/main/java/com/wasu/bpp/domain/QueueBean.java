package com.wasu.bpp.domain;

import java.util.Date;

public class QueueBean implements Comparable<QueueBean>{
	private Long id;
	private String title;
	private Date sendTime;
	private String content;
	private String stbId;
	private String areaId;
	private String custId;
	private String cronExpr;
	private Date validTime;
	private String scope;
	private String vtype;
	private String dataSrc;
	//以下两个字段处理队列时使用
	private Long subId;//子消息编号
	private String status;//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
	private String hasSend;//是否已发送到xmpp(多条发送)：0-否；1-是
	
	@Override
	public int compareTo(QueueBean o) {
		long sendTime1 = this.getSendTime().getTime();
		long sendTime2 = o.getSendTime().getTime();
		if (sendTime1 > sendTime2) {
			return 1;
		} else if (sendTime1 < sendTime2) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
    public boolean equals(Object obj) {
		if(obj==null ){
            return false; 
        }
		
        if(this==obj){
            return true; 
        }
        
        if(obj instanceof QueueBean){
        	QueueBean bean=(QueueBean)obj;
        	if(this.getId().longValue()==bean.getId().longValue()){
        		return true;
        	}
        }
		
		return false;
	}
	
	@Override 
    public int hashCode() {
		return 37 * 17 + (getId() == null ? 0 : this.getId().hashCode());
    }
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDataSrc() {
		return dataSrc;
	}
	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}

	public Long getSubId() {
		return subId;
	}
	public void setSubId(Long subId) {
		this.subId = subId;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getHasSend() {
		return hasSend;
	}
	public void setHasSend(String hasSend) {
		this.hasSend = hasSend;
	}
}