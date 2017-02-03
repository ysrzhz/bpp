package com.wasu.pub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wasu.pub.service.LogService;
import com.wasu.pub.util.Page;
import com.wasu.sid.SysLog;

@Controller
@RequestMapping ("/views/manage")
public class SysLogController{
	@Autowired
	private LogService logService;
	
	@ResponseBody
	@RequestMapping (value = "/log_list", produces = {"application/json;charset=UTF-8"})
	public Page<SysLog> list(int page, int rows, SysLog log) throws Exception {
		return logService.getLogList(page, rows, log);
	}

	//硬编码
	private String change(String old){
		try{
			return new String(old.getBytes("iso-8859-1"),"utf-8");
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
}