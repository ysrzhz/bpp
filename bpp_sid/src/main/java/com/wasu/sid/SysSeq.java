package com.wasu.sid;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "t_sys_seq")
public class SysSeq implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "c-assigned")  
    @GenericGenerator(name = "c-assigned", strategy = "assigned")  
	@Column(name="table_name")
	private String tableName; //表名
	@Column(name="min_value")
	private Long minvalue; //最小值
	@Column(name="max_value")
	private Long maxvalue; //最大值
	@Column(name="current_value")
	private Long currentValue; //当前值
	@Column(name="increment_value")
	private Long incrementValue; //步长
	private int cycle; //是否循环（0：不循环，1：循环）

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getMinvalue() {
		return minvalue;
	}

	public void setMinvalue(Long minvalue) {
		this.minvalue = minvalue;
	}

	public Long getMaxvalue() {
		return maxvalue;
	}

	public void setMaxvalue(Long maxvalue) {
		this.maxvalue = maxvalue;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
	}

	public Long getIncrementValue() {
		return incrementValue;
	}

	public void setIncrementValue(Long incrementValue) {
		this.incrementValue = incrementValue;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}



}
