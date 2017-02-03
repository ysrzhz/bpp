package com.wasu.pub.domain;

import com.wasu.pub.domain.WsFieldValidate;

public class WsField {

	private String fieldName;//字段名称
	private String fieldChName;//字段中文名称
	private boolean isShow;//是否展现在列表中
	private boolean isQuery;//是否展现在查询中
	private Integer seq;//排序号

	private boolean isRelate;//是否具有关联字段
	private String rlClass;//关联的实体类
	private String rlFieldCode;//该实体类中关联的字段
	private String rlFieldName;//该实体类中需要取出的字段
	private String rlShwoFieldName;//需要展示的字段名称

	private boolean isTransient = false;//判断是否是数据库字段
	private String dbAliasName;//判断是否有数据库重写字段
	private String fieldClass;
	private boolean isId=false;//是否是Id字段

	private boolean isSelect;//是否是下拉框
	private String selectOptionName;//下拉框选用的option名称；
   
	private boolean isAutoCode;//是否自动生成code
	
	private boolean isPrecise = true; //是否精确查询
	private WsFieldValidate validate;//是否验证

	private boolean isNumber;
	private boolean isSwitch; //是否是Switch按钮
	/**
	 * 判断是否使用combogrid
	 * @return
	 */
	public boolean isRelSelect(){
		if(isSelect&&(selectOptionName==null||selectOptionName.equals(""))){
			return true;
		}
		return false;
	}
	
	public String getDbFieldName(){
		if(dbAliasName!=null&&!dbAliasName.trim().equals("")){
			return dbAliasName;
		}
		return fieldName;
	}
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	
	public String getFieldChName() {
		return fieldChName;
	}

	public void setFieldChName(String fieldChName) {
		this.fieldChName = fieldChName;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public boolean isQuery() {
		return isQuery;
	}

	public void setQuery(boolean isQuery) {
		this.isQuery = isQuery;
	}

	public boolean isRelate() {
		return isRelate;
	}

	public void setRelate(boolean isRelate) {
		this.isRelate = isRelate;
	}

	public String getRlClass() {
		return rlClass;
	}

	public void setRlClass(String rlClass) {
		this.rlClass = rlClass;
	}

	public String getRlFieldCode() {
		return rlFieldCode;
	}

	public void setRlFieldCode(String rlFieldCode) {
		this.rlFieldCode = rlFieldCode;
	}

	public String getRlFieldName() {
		return rlFieldName;
	}

	public void setRlFieldName(String rlFieldName) {
		this.rlFieldName = rlFieldName;
	}

	public String getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(String fieldClass) {
		this.fieldClass = fieldClass;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public String toString() {
		return "fieldName=" + fieldName + " fieldChName=" + fieldChName
				+ " isShow=" + isShow + " isQuery=" + isQuery + " rlClass="
				+ rlClass + " rlFieldCode=" + rlFieldCode + " rlFieldName="
				+ rlFieldName;
	}

	public String getRlShwoFieldName() {
		return rlShwoFieldName;
	}

	public void setRlShwoFieldName(String rlShwoFieldName) {
		this.rlShwoFieldName = rlShwoFieldName;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getSelectOptionName() {
		return selectOptionName;
	}

	public void setSelectOptionName(String selectOptionName) {
		this.selectOptionName = selectOptionName;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int compareTo(WsField b) {
		if(b==null||this.seq==null||b.seq==null) return 0;
		return this.seq.compareTo(b.seq);
	}
	
	public String getDbAliasName() {
		return dbAliasName;
	}
	public void setDbAliasName(String dbAliasName) {
		this.dbAliasName = dbAliasName;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public boolean isId() {
		return isId;
	}

	public void setId(boolean isId) {
		this.isId = isId;
	}

	public WsFieldValidate getValidate() {
		return validate;
	}

	public void setValidate(WsFieldValidate validate) {
		this.validate = validate;
	}

	public boolean isPrecise() {
		return isPrecise;
	}

	public void setPrecise(boolean isPrecise) {
		this.isPrecise = isPrecise;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public void setNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}

	public boolean isAutoCode() {
		return isAutoCode;
	}

	public void setAutoCode(boolean isAutoCode) {
		this.isAutoCode = isAutoCode;
	}

	public boolean isSwitch() {
		return isSwitch;
	}

	public void setSwitch(boolean isSwitch) {
		this.isSwitch = isSwitch;
	}
	
	
	

}
