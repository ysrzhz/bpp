package com.wasu.pub.cache.annoation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheQuery {
	public abstract boolean user() default false;//是否区分用户属性
	public abstract String value() default "";//查询结果对象的主键的属性值，比如id，多个pk属性以英文逗号分隔
	public boolean isClass() default true;//利用method参数中的class来进行分区域
}