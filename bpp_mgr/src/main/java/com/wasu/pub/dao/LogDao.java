package com.wasu.pub.dao;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.query.Query;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.Sort;
import com.wasu.sid.SysLog;

/**
 * @Title: LogDao.java
 * @Package com.wasu.pub.dao.sys
 * @Description: TODO(添加描述)
 * @author 余龙
 * @date 2016年1月18日 下午3:42:16
 */
@Component("logDao")
@Transactional
public class LogDao  extends BaseDao<SysLog>{
	public Response addLog(SysLog log) throws Exception {
		Response rs =  dbCenter.insert(log, SysLog.class.getName());
		dbCenter.refreshCache(SysLog.class.getName());
		return rs;
	}

	public void deletLog(String date){
		StringBuffer sb = new StringBuffer("delete from iads_sys_log  where ");
		sb.append("operationtime <= ").append("'").append(date).append("'");
		dbCenter.executeSQL(SysLog.class.getName(),sb.toString());
	}
	
	public Page<SysLog> getLogList(int page, int rows, SysLog log) throws Exception {
		Query query = new Query();
		query.clzz(SysLog.class.getName()).where();
		if (log.getU_account() != null && !"null".equals(log.getU_account()) && !"".equals(log.getU_account())){
			query.and().bw("u_account", log.getU_account());
		}
		
		if (log.getU_name() != null && !"null".equals(log.getU_name()) && !"".equals(log.getU_name())){
			query.and().bw("u_name", new String(log.getU_name().getBytes("ISO-8859-1"),"utf-8"));
		}
		
		if (log.getContent() != null && !"null".equals(log.getContent()) && !"".equals(log.getContent())){
			query.and().bw("content",new String(log.getContent().getBytes("ISO-8859-1"),"utf-8"));
		}

		if ((log.getStartTime() != null && log.getEndTime().equals("") && !"".equals(log.getStartTime())) || (!"null".equals(log.getStartTime())&&"null".equals(log.getEndTime()))){
			query.and().ge("operationtime", log.getStartTime());
		}
		
		if ((log.getEndTime() != null && log.getStartTime().equals("") && !"".equals(log.getEndTime())) || (!"null".equals(log.getEndTime())&&"null".equals(log.getStartTime()))){
			query.and().le("operationtime", log.getEndTime());
		}
		
		if (log.getStartTime() != null&&log.getEndTime() != null&&!"".equals(log.getStartTime())&&!"".equals(log.getEndTime())){
			query.and().ge("operationtime", log.getStartTime()).and().le("operationtime", log.getEndTime());
		}
		
		Page<SysLog> pageObj = new Page<SysLog>(rows, page);
		Response response = dbCenter.getPage(SysLog.class.getName(), query, new Sort("operationtime", true), pageObj);
		return response == null ? new Page<SysLog>() : (Page<SysLog>) response.getEntity();
	}
}