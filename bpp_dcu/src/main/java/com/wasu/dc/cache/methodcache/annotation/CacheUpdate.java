package com.wasu.dc.cache.methodcache.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheUpdate
{
  public abstract boolean user() default false;
//查询结果对象的主键的属性值，比如id
  public abstract String[] value() default "";
  
  public boolean isClass() default true;//利用method参数中的class来进行分区域
}
