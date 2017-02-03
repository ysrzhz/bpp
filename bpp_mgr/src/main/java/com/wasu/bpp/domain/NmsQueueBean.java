package com.wasu.bpp.domain;

import net.sf.json.JSONArray;

public class NmsQueueBean{
	private String dataSrc;
	private JSONArray jsonArr;
	
	public String getDataSrc() {
		return dataSrc;
	}
	public void setDataSrc(String dataSrc) {
		this.dataSrc = dataSrc;
	}
	
	public JSONArray getJsonArr() {
		return jsonArr;
	}
	public void setJsonArr(JSONArray jsonArr) {
		this.jsonArr = jsonArr;
	}
}