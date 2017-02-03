package com.wasu.bpp.exception;

import org.apache.log4j.Logger;

/**
 * iEGP 异常类
 * 
 * @author Dengyong 903760
 * @version 1.0
 * @since Nov 4, 2009
 */
public class MmsRuntimeException extends RuntimeException
{
	private static final Logger logger = Logger.getLogger(MmsRuntimeException.class);
	private String code; 
	private String message;

	public String getMessage()
	{
		return message;
	}

	public MmsRuntimeException(String code)
	{
//		this.message = CommonUtil.getRecourceMessageByKey(code);
		this.code = code;
	}
	
	public MmsRuntimeException(long code)
	{
	    this(code +"");
	}
	
	public MmsRuntimeException(String code, String message)
	{
		this.message = message;
		this.code = code;
	}
	
	public MmsRuntimeException(long code, String message)
	{
		this.message = message;
		this.code = code + "";
	}

	public MmsRuntimeException(String code, Throwable cause)
	{
		super(cause);
		this.code = code;
//		this.message = CommonUtil.getRecourceMessageByKey(code);
	}

	public MmsRuntimeException(String code, String message, Throwable cause)
	{
		super(cause);
		this.message = message;
		this.code = code;
	}
	
	public String getCode()
	{
		return code;
	}
}
