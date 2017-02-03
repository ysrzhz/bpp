package com.wasu.pub.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sort  implements Serializable{
	Map<String, Boolean> sortsMap = new LinkedHashMap();

	public Sort() {
	}

	public Sort(String columnNamePage) {
		if ((null == columnNamePage) || (columnNamePage.trim().isEmpty())) {
			return;
		}
		String[] array = columnNamePage.trim().split("-");
		if (array.length == 1) {
			setTableSort(array[0], false);
		} else if (array.length == 2)
			if ("asc".equalsIgnoreCase(array[1])) {
				setTableSort(array[0], false);
			} else
				setTableSort(array[0], true);
	}

	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = sortsMap.keySet().iterator();
		sb.append( "com.wasu.util.Sort");
		while(it.hasNext()){
			String key =it.next();
			Boolean value=sortsMap.get(key);
			sb.append(" key=").append(key).append(",value=").append(value);
		}
		return sb.toString();
	}
	public Sort(String columnName, boolean isDesc) {
		setTableSort(columnName, isDesc);
	}

	public Map<String, Boolean> getSortsMap() {
		return this.sortsMap;
	}

	public void setSortsMap(Map<String, Boolean> sortsMap) {
		this.sortsMap = sortsMap;
	}

	public void setTableSort(String columnName, boolean isDesc) {
		this.sortsMap.put(columnName, Boolean.valueOf(isDesc));
	}

	public String getTabelSort() {
		if ((this.sortsMap == null) || (this.sortsMap.isEmpty())) {
			return null;
		}

		Iterator it = this.sortsMap.entrySet().iterator();
		StringBuffer sortBuf = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			sortBuf.append(",");
			sortBuf.append((String) entry.getKey()).append(" ");
			if (((Boolean) entry.getValue()).booleanValue()) {
				sortBuf.append("DESC");
			} else {
				sortBuf.append("ASC");
			}
		}

		return sortBuf.substring(1);
	}
	
	/**
	 * 是否包含指定的排序字段
	 * @param columnTable
	 * @return
	 */
	public boolean containTableSort(String columnTable){
		return this.sortsMap.containsKey(columnTable);
	}
}