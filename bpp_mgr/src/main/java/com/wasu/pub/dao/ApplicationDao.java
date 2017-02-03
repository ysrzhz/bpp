package com.wasu.pub.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.query.Query;
import com.wasu.sid.SysApplication;

@Component
@Transactional
public class ApplicationDao extends BaseDao<SysApplication> {
	public SysApplication getByCode(String code) throws Exception{
		return (SysApplication)getOne(SysApplication.class.getName(),"code", code);
	}
	
	public List<SysApplication> listOrderByName() throws Exception {
		Query query = new Query();
		query.clzz(SysApplication.class.getName()).orderBy().sort("name", true);
		Response response= dbCenter.queryList(SysApplication.class.getName(), query);
		return (List)response.getEntity();
	}
}