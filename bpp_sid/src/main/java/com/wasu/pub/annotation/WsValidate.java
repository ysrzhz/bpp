package com.wasu.pub.annotation;
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface WsValidate {
    //不为空限制
	public boolean isRequired() default false;
	//长度限制
	public int length() default 0;
	//校验类型设置
	public String type() default "";
	
}
