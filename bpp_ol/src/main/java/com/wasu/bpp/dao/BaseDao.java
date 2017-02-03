package com.wasu.bpp.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.WSException;
import com.wasu.pub.query.Query;
import com.wasu.pub.service.DBCenter;
import com.wasu.pub.domain.IdEntity;
import com.wasu.pub.util.Page;

@Component("baseDao")
@Transactional
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseDao<E extends Serializable>{	
	protected Logger logger = Logger.getLogger(getClass());
	@Autowired
	protected DBCenter dbCenter;
	
	public Object getOne(String clzz, String key, String value) {
		Map map = new HashMap();
		map.put(key, value);
		Response response = dbCenter.queryOne(clzz, map);
		return response.getEntity();
	}
	
	public Object getOne(Map param) {
		Class<?> clzz =getEntityClass();
		Response response = dbCenter.queryOne(clzz.getName(), param);
		return response.getEntity();
	}	
	
	protected int sqlUpdate(String sql, Object... params) throws Exception {
		Class<?> clzz =getEntityClass();
		return dbCenter.sqlUpdate(clzz.getName(), sql, params);
	}
	
	public List<E> sqlList(String sql, Object... params) throws Exception{
		Class<?> clzz =getEntityClass();
		return dbCenter.sqlList(clzz.getName(), sql, params);
	}
	
	public List sqlList(Class<?> clzz,String sql, Object... params) throws Exception{
		return dbCenter.sqlList(clzz.getName(), sql, params);
	}
	
	/**
	 * 支持分页SQL查询
	 * @param sql
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public Response sqlList(String sql,Page page,String clzz)throws Exception{
		return dbCenter.sqlList(sql, page,clzz);
	}

	public Response sqlListCountSql(String sql, String countSql, Page page,String clzz){
		return dbCenter.sqlListCountSql(sql, countSql, page,clzz);
	}
	
	public Response sqlListCountSqlNoCache(String sql, String countSql, Page page,String clzz){
		return dbCenter.sqlListCountSqlNoCache(sql, countSql, page,clzz);
	}

	public List<E> list(Map param) throws Exception{
		Class<?> clzz =getEntityClass();
		Response response= dbCenter.getall(clzz.getName(), param, null);
		return (List)response.getEntity();
	}	

	public List<E> list() throws Exception{
		Class<?> clzz =getEntityClass();
		return dbCenter.list(clzz.getName());
	}
	
	public List<E> list(Query query) throws Exception{
		Class<?> clzz =getEntityClass();
		Response response= dbCenter.queryList(clzz.getName(), query);
		return (List)response.getEntity();
	}	
	
	public E get(Serializable id) throws Exception{
		Class<?> clzz =getEntityClass();
		return (E)dbCenter.get(clzz.getName(),id);
	}
	
	public void save(E entity) throws Exception{
		 dbCenter.save(entity,entity.getClass().getName());
	}
	
	public Long saveEntity(IdEntity entity) throws Exception{
		return dbCenter.saveEntity(entity,entity.getClass().getName());
	}
	
	public void update(E entity) throws Exception{
		 dbCenter.update(entity,entity.getClass().getName());
	}

	public void delete(E entity) throws Exception{
		dbCenter.delete(entity,entity.getClass().getName());
	}
	
	public void deleteById(Serializable id) throws Exception {
		Class<?> clzz =getEntityClass();
		dbCenter.deleteById(clzz.getName(),id);
	}

	private Class<E> getEntityClass()  {
		Type genType = this.getClass().getGenericSuperclass();
		if (genType instanceof ParameterizedType) {
			Class<E> genClass = (Class<E>) ((ParameterizedType) genType).getActualTypeArguments()[0];
			return genClass;
		}
		
		throw new WSException("06000000",getClass().getName() + "没有定义父类泛型参数BaseDal<E>");
	}

	public DBCenter getDbCenter() {
		return dbCenter;
	}

	public void setDbCenter(DBCenter dbCenter) {
		this.dbCenter = dbCenter;
	}
	
	public Integer getCount(String sql){
		Class<?> clzz =getEntityClass();
		return dbCenter.getCount(clzz.getName(), sql);
	}
}