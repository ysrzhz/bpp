package com.wasu.pub.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ShowDomain {

	
	private String entityName;//英文名称   
	private String entityChName;//中文名称
	private Map<String,WsField>  fields;
	private List<WsField>  fieldList=null;
	private List<WsField>  titleFieldList = null;//中文title不为空
	private String tableName;
	
	/**
	 * 获取需要关联的字段
	 * @return
	 */
	public List<WsField> getRelateFields(){
		if(fields==null){
			return null;
		}
		List<WsField> rlFields = new ArrayList();
		WsField[] wsfields =(WsField[])fields.values().toArray();
		for(WsField field:wsfields){
			if(field.isRelate()){
				rlFields.add(field);
			}
		}
		return rlFields;
	}
	
	public WsField getRelateField(String relClzz){
		if(fields==null){
			return null;
		}
		//WsField[] wsfields =(WsField[])(fields.values().toArray());
		for(WsField field:fieldList){
			if(field.isRelate()&&field.getRlClass().equals(relClzz)){
				return field;
			}
		}
		return null;
	}
	
	/**
	 * 判断是否是Id字段
	 * @return
	 */
	public WsField getIdField(){
		if(fieldList==null){
			return null;
		}
		for(WsField field:fieldList){
			if(field.isId()){
				return field;
			}
		}
		return null;
	}
	
	public WsField getWsField(String fieldName){
		return fields.get(fieldName);
	}
	
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
 
	public String getEntityChName() {
		return entityChName;
	}
	public void setEntityChName(String entityChName) {
		this.entityChName = entityChName;
	}
	
	public String toString(){
		String result= "entityName="+entityName+" entityChName="+entityChName+" tableName="+tableName +" fieldList=[";
		WsField[] fields2=(WsField[])fields.values().toArray();
		if(fields!=null){
			for(WsField field:fields2){
				result+=" wsField=[ "+field.toString()+" ]";
			}
		}
		
		return result;
	}

	public Map<String, WsField> getFields() {
		return fields;
	}

	public void setFields(Map<String, WsField> fields) {
		this.fields = fields;
		if(fields!=null){
			fieldList= new ArrayList();
			titleFieldList = new ArrayList();
			Iterator it =fields.entrySet().iterator();
			while(it.hasNext()){
				Entry entry = (Entry) it.next();
				WsField field=(WsField)entry.getValue();
				if(field.getFieldChName()!=null&&!field.getFieldName().equals("id")){
					titleFieldList.add(field);
				}
				fieldList.add(field);
			}
		}
		sort();
	}

	public List<WsField> getTitleFieldList() {
		return titleFieldList;
	}

	public void setTitleFieldList(List<WsField> titleFieldList) {
		this.titleFieldList = titleFieldList;
	}

	public List<WsField> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<WsField> fieldList) {
		this.fieldList = fieldList;
	}

	public void sort(){
		Collections.sort(fieldList, (a, b) -> a.compareTo(b));
		Collections.sort(titleFieldList, (a, b) -> a.compareTo(b));
	}
	
	public static void main(String[] args){
		
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	
	
}
