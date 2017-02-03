package com.wasu.pub.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.wasu.pub.annotation.WsTitle;
import com.wasu.pub.annotation.WsValidate;
import com.wasu.pub.domain.WsField;
import com.wasu.pub.domain.ShowDomain;
import com.wasu.pub.domain.WsFieldValidate;

public class ShowDomainUtil {

	private static Map<Class, ShowDomain> map = new HashMap();
	/**
	 * 获取clss中的WsTitle注解的值
	 * @param cl
	 * @return
	 */
	public static ShowDomain get(String cl) {
		Class clzz = CacheUtil.getClass(cl);
		return get(clzz);
	}
	
	

	/**
	 * 获取clss中的WsTitle注解的值
	 * @param clzz
	 * @return
	 */
	public static ShowDomain get(Class clzz) {
		ShowDomain aa =map.get(clzz);//从缓存中获取
		if(aa!=null){
			return aa;
		}
		ShowDomain sd = new ShowDomain();
		sd.setTableName(clzz.getSimpleName());
		Annotation[] anotations = clzz.getAnnotations();//读取类的数据
		if (anotations != null){
			for (Annotation an : anotations) {
                if(an.annotationType().equals(WsTitle.class)){
                	WsTitle cc = (WsTitle) an;
                	String chName=cc.chName();
                	String  code =cc.code();
                	sd.setEntityName(code);
                	sd.setEntityChName(chName);
                }
                if(an.annotationType().equals(Table.class)){
                	Table cc = (Table) an;
                	String tableName=cc.name();
                	sd.setTableName(tableName);
                }
			}
		}
		
		Collection<Field> fields=CacheUtil.getDeclaredFields(clzz);
		Map<String,WsField> wsFields = new HashMap();
		for(Field field:fields){
			anotations =field.getAnnotations();
			WsField wsField = new WsField();
			wsField.setFieldName(field.getName());
			wsField.setFieldClass(field.getType().getName());
			//设置一些默认值
			wsField.setQuery(false);
			wsField.setRelate(false);
			wsField.setShow(true);
			wsField.setSelect(false);
			wsField.setAutoCode(false);
			wsField.setSwitch(false);
			wsField.setSeq(9999);
			for (Annotation an : anotations) { 
                if(an.annotationType().equals(WsTitle.class)){
                	WsTitle cc = (WsTitle) an;
                	String chName=cc.chName();
                	String  code =cc.code();
                	boolean isShow=cc.isShow();
                	boolean isQuery=cc.isQuery();
                	int seq = cc.seq();
                	
                	boolean isRelate = cc.isRelate();
                	String rlClass = cc.rlClass();
                	String rlFieldCode =cc.rlFieldCode();
                	String rlFieldName =cc.rlFieldName();
                	String rlShowFieldName =cc.rlShowFieldName();
                	if(rlShowFieldName==null||rlShowFieldName.equals("")){
                		rlShowFieldName=rlFieldName;
                	}
                	boolean isSelect = cc.isSelect();
                	boolean isNumber =cc.isNumber();
                	boolean isAutoCode=cc.isAutoCode();
                	boolean isSwitch=cc.isSwitch();
                	String selectOptionName=cc.selectOptionName();
                	wsField.setRlShwoFieldName(rlShowFieldName);
                	wsField.setFieldChName(chName);
                	wsField.setQuery(isQuery);
                	wsField.setSeq(seq);
                	wsField.setShow(isShow);
                	wsField.setRelate(isRelate);
                	wsField.setRlFieldCode(rlFieldCode);
                	wsField.setRlClass(rlClass);
                	wsField.setRlFieldName(rlFieldName);
                	wsField.setSelect(isSelect);
                	wsField.setSelectOptionName(selectOptionName);
                	wsField.setPrecise(cc.isPrecise()); //是否精确查询
                	wsField.setNumber(isNumber);
                	wsField.setAutoCode(isAutoCode);
                	wsField.setSwitch(isSwitch);
                }
                if(an.annotationType().equals(Transient.class)){
                	wsField.setTransient(true);
                }
                if(an.annotationType().equals(Column.class)){
                	Column cc = (Column) an;
                	wsField.setDbAliasName(cc.name());
                }
                if(an.annotationType().equals(Id.class)){
                	wsField.setId(true);
                }
                if(an.annotationType().equals(WsValidate.class)){
                	WsValidate cc = (WsValidate) an;
                	boolean isRequired=cc.isRequired();
                	int length =cc.length();
                	String type=cc.type();
                	WsFieldValidate v=new WsFieldValidate();
                	v.setMaxLength(length);
                	v.setRequired(isRequired);
                	v.setType(type);
                	wsField.setValidate(v);
                }
			}
			wsFields.put(field.getName(),wsField);
		}
		sd.setFields(wsFields);
		map.put(clzz, sd);
		return sd;
	}

}
