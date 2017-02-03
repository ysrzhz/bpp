package com.wasu.pub.cache;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class AspectLogInceptor {
	private static Logger logger = Logger.getLogger(AspectLogInceptor.class);
	
	public void pointCut(){
	}
	
    public Object doClock(ProceedingJoinPoint pjp) throws Throwable{
        Object result = null;
	    MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
	    String className = methodSignature.getDeclaringTypeName();
	    String methodName = methodSignature.getName();
	    long startTime = System.currentTimeMillis(); //计时开始  
	    result = pjp.proceed();
		
	    long endTime = System.currentTimeMillis(); //计时结束  
        if (logger.isInfoEnabled()) {  
            logger.info("[" + className + "." + methodName + "] 执行时间:" +(endTime - startTime) + " ms ");  
        }  
		
        return result;  
	}
}