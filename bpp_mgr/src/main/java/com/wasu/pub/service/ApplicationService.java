package com.wasu.pub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.dao.ApplicationDao;
import com.wasu.pub.util.UID;
import com.wasu.sid.SysApplication;

@Component
@Transactional
public class ApplicationService {
	@Autowired
	private ApplicationDao appDao;

	public void create(String name, String code) throws Exception {
		SysApplication app = new SysApplication();
		app.setId(UID.create());
		app.setName(name);
		app.setCode(code);
		appDao.save(app);
	}

	public void update(String id, String name, String code) throws Exception {
		SysApplication app = appDao.get(id);
		app.setName(name);
		app.setCode(code);
		appDao.update(app);
	}

	public List<SysApplication> listOrderByName() throws Exception {
		return appDao.listOrderByName();
	}
}