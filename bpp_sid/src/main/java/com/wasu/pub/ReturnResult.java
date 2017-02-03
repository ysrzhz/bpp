package com.wasu.pub;

import java.io.Serializable;
import java.util.Date;

public class ReturnResult implements Serializable{
	private static final long serialVersionUID = 1L;
	protected String returnCode;
	protected String returnMsg;
	private Date outputDate;
	private String traceId;

	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMsg() {
		return returnMsg;
	}
	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}
	public Date getOutputDate() {
		return outputDate;
	}
	public void setOutputDate(Date outputDate) {
		this.outputDate = outputDate;
	}
	public String getTraceId() {
		return traceId;
	}
	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	
	
	

}
