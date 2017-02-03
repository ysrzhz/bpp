package com.wasu.pub;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import com.wasu.pub.util.PropFileUtil;

public class WSException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private static Properties errorProperties=null;
	private String errorCode;
	private String errorMsg;
	private Throwable ex;
    
	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	//初始化错误编码
	static{
		errorProperties= PropFileUtil.readPropertiesFile("errorcode.properties");
	}
	public WSException() {
		//ex=this;
	}
	//接受异常
	public WSException(Throwable ex) {
		this.ex=ex;
	}
	public WSException(String errorCode) {
		this.errorCode = errorCode;
		this.errorMsg = errorProperties.getProperty(errorCode);
		//ex=this;
	}

	public WSException(String errorCode, String errorMsg) {
		this.errorCode = errorCode;
		if(errorMsg==null||"".equals(errorMsg.trim())){
			errorMsg=errorProperties.getProperty(errorCode);
		}
		this.errorMsg = errorMsg;
		//ex=this;
	}
	public WSException(String errorCode,Throwable ex) {
		this.errorCode = errorCode;
		this.errorMsg = errorProperties.getProperty(errorCode);
		//ex=this;
	}
	public WSException(String errorCode, String errorMsg, Throwable ex) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.ex = ex;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("errorCode=" + errorCode + " errorMsg=" + errorMsg );
		if(ex!=null){
			sb.append(" 详细异常信息为：\r\n").append(getStackTraceText(ex));
		}
		return sb.toString();
	}

	public static String getMessageFromEx(Throwable t){
		String exTxt = getStackTraceText(t);
		String message="";
		if(message.indexOf("duplicat")>=0){
			message="数据重复，请重新填写！";
		}
		return message;
	}
	/**
	 * 获取所有的异常栈的数据信息
	 * @param t
	 * @return
	 */
	public static String getStackTraceText(Throwable t) {
		PrintWriter pw = null;
		try {
			StringWriter sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			return sw.toString();
		} catch (Exception e) {
		} finally {
			try {
				pw.close();
			} catch (Exception ex) {
			}
		}
		return "";
	}
	
	public static void notNull(Object value, String code) {
		String message= errorProperties.getProperty(code);
		assertFor(value != null && value.toString().trim().length() > 0, code, message);
	}
		
	public static void notNull(Object value, String code, String message) {
		assertFor(value != null && value.toString().trim().length() > 0, code, message);
	}
	
	public static void lengthBetween(String value,int minLength,int maxLength, String code, String message) throws Exception {
		assertFor(value != null && value.trim().length()>= minLength && value.trim().length() <= maxLength, code, message);
	}
	
	public static void assertFor(boolean condition, String code, String message)  {
		
		if (!condition) {
			throw new WSException(code, message);
		}
	}
	
	public static void assertFor(boolean condition, String code)  {
		String message= errorProperties.getProperty(code);
		if (!condition) {
			throw new WSException(code, message);
		}
	}		
}