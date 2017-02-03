package com.wasu.pub.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.wasu.pub.Response;
import com.wasu.pub.query.Query;
import com.wasu.pub.domain.IdEntity;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.Sort;

public interface DBCenter {
	public Response insert(List entity, String clzz);

	public Response insertForBatch(List entity, String clzz) throws Exception;

	public Response insert(Serializable entity, String clzz);

	public Response insertEntity(IdEntity entity, String clzz);

	public Response update(Serializable entity, String clzz);

	public Response delete(String clzz, Long id);

	public Response execute(String clzz, String sql);//delete,create,update

	public Response delete(String clzz, List<Long> ids);

	public Response getById(String clzz, Long id);

	public Response<Map> deal(String opCode, Map data);

	public Response queryOne(String clzz, Query query);

	public Response queryOne(String clzz, String hql);

	public Response queryOne(String clzz, Map query);

	public Response queryList(String clzz, Query query);

	public Response queryList(String clzz, Map query);

	public Response getPage(String clzz, Map param, Sort sorts, Page page);

	public Response getPage(String clzz, Query param, Sort sorts, Page page);

	public Response getPage(String clzz, String relateClzz, Map param, Sort sorts, Page page);

	public Response getFullInfoPage(String clzz, Map param, Sort sorts, Page page);

	public Response getall(String clzz, Map param, Sort sorts);

	public Response updateStatus(String clzz, List<Long> idList, Map<String, String> param);

	public void update(String clzz, List idList, Map<String, String> param);

	public void update(String clzz, Object id, Map<String, String> param);

	public Integer getCount(String clzz, String sql);

	//执行Sql语句
	public List sqlList(String clzz, String sql, Object... params);

	public List sqlListForMap(String clzz, String sql, Object... params);

	public List sqlListNoCache(String clzz, String sql, Object... params);

	public List getSqlList(String clzz, String sql, Object... params);
	
	public List getSqlList(String clzz, String sql, Boolean retMap, Object... params);

	public List sqlListUpdataCache(String clzz, String sql, Object... params);

	//执行SQL语句，支持分页查询
	public Response sqlList(String sql, Page page, String clzz);

	//支持自定义查询总数
	public Response sqlListCountSql(String sql, String countSql, Page page, String clzz);

	public Response sqlListCountSqlNoCache(String sql, String countSql, Page page, String clzz);

	public int sqlUpdate(String clzz, String sql, Object... params);

	public int sqlUpdateIgnoreCache(String clzz, String sql, Object... params);

	//按照实例执行
	public List list(String clzz);

	public Object get(String clzz, Serializable id);

	public void save(Object entity, String clzz);

	public void refreshCache(String clzz);

	public void putCache(String clzz, String key, Object data);

	public Object getCache(String clzz, String key);

	public Response getPageByHql(String clzz, String hql, Page page);

	//缓存数据增加失效时间
	public void putCacheDelay(String clzz, String key, Object data, int delayTime);

	//放入缓存队列
	public Long pushCache(String key, String... values);

	//从缓存队列取值
	public String popCache(String key);

	//获取缓存队列长度
	public Long queueLenCache(String key);

	//删除缓存
	public void delCache(String key);

	//保存对象：成功：返回对象ID，否则返回0
	public Long saveEntity(IdEntity entity, String clzz);

	public void delete(Object entity, String clzz);

	public void deleteAll(String clzz);

	public void delete(String clzz, Map param);

	public void deleteById(String clzz, Serializable id);

	public int executeSQL(String clzz, String sql);

	public int hqlUpdate(String clzz, String sql);
}