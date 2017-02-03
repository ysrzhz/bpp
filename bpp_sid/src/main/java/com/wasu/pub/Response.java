package com.wasu.pub;

import com.wasu.pub.WSException;
import com.wasu.pub.util.StringUtil;

public class Response<T> extends ReturnResult{
	private T entity;

	public T getEntity() {
		return entity;
	}
	public void setEntity(T entity) {
		this.entity = entity;
	}
	public static Response success(){
		Response r = new Response();
		r.setReturnCode("0");
		r.setReturnMsg("处理成功！");
		return r;
	}
	public static Response success(Object entity){
		Response r = new Response();
		r.setEntity(entity);
		r.setReturnCode("0");
		r.setReturnMsg("处理成功！");
		return r;
	}
	public static Response fail(Throwable t){
		Response r = new Response();
		String message =WSException.getMessageFromEx(t);
		r.setReturnCode("-1");
		r.setReturnMsg(message);
		return r;
	}	
	public String toString(){
		return " returnCode="+this.returnCode+" returnMsg="+this.returnMsg+" entity=["+StringUtil.getObjStr(entity)+"]";
	}
}
