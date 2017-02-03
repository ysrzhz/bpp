package com.wasu.pub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.dao.LogDao;
import com.wasu.pub.util.Page;
import com.wasu.sid.SysLog;

@Component
@Transactional
public class LogService extends BaseService{
	@Autowired
	private LogDao logDao;
	
	public Response addLog(SysLog log) throws Exception {
		return logDao.addLog(log);
	}
	
	public Page<SysLog> getLogList(int page, int rows, SysLog log) throws Exception {
		return logDao.getLogList(page, rows, log);
	}
}