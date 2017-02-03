package com.wasu.dc.service.imp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wasu.pub.Response;
import com.wasu.pub.WSException;
import com.wasu.dc.cache.CacheService;
import com.wasu.dc.cache.methodcache.annotation.CacheQuery;
import com.wasu.dc.cache.methodcache.annotation.CacheRefresh;
import com.wasu.dc.cache.methodcache.annotation.CacheRegion;
import com.wasu.dc.cache.methodcache.annotation.CacheUpdate;
import com.wasu.dc.config.ConfigContext;
import com.wasu.dc.dao.MethodDao;
import com.wasu.pub.query.Query;
import com.wasu.pub.service.DBCenter;
import com.wasu.pub.domain.IdEntity;
import com.wasu.pub.domain.ShowDomain;
import com.wasu.pub.domain.WsField;
import com.wasu.dc.cache.config.CacheObject;
import com.wasu.pub.util.CacheUtil;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.ShowDomainUtil;
import com.wasu.pub.util.Sort;
import com.wasu.pub.util.StringUtil;

@Service("dbCenter")
public class DBCenterImpl implements DBCenter {

    private Logger logger = Logger.getLogger(DBCenterImpl.class);
    @Resource
    private MethodDao methodCachedDao;

    /**
   * 直接操作缓存 添加
   */
	public void putCache(String clzz,String key, Object data) {
		try {
			CacheService.set(clzz+key, new CacheObject(data));
		} catch (Exception ex) {
			logger.error("处理缓存失败，调用方法为：DBCenterImpl.putCache key" +  " data = "  + StringUtil.getObjStr(data)+"exception=\r\n" + WSException.getStackTraceText(ex));
		}
	}

	/**
	* 直接操作缓存 删除
	*/
	public void remove(String key) {
		try{
		  CacheService.remove(key);
		}catch (Exception ex){
		  String msg = "删除缓存失败，调用方法为：DBCenterImpl.putCache key "+key;
		  msg = "exception=\r\n" + WSException.getStackTraceText(ex);
		  logger.error(msg);
		}
	}
	/**
	* 直接操作缓存 获取
	*/
	public Object getCache(String clzz, String key) {
		try {
			key = clzz + key;
			String timekey = "time_" + clzz;
			List values = CacheService.mget(new String[]{key, timekey});
			if (values != null) {
				long startTime = System.currentTimeMillis();
				Object value = null;
				Object time = null;
				value = values.get(0);
				time = values.get(1);
				if (value != null) {
					CacheObject cacheObject = (CacheObject) value;
					value = cacheObject.getValue();
					//logger.info("缓存查询到数据   01 cache invoke cache cost time:"+(System.currentTimeMillis()-startTime)+"ms");
					if (time == null || (time != null) && (cacheObject.getTime() > Long.parseLong(time.toString()))) {
						return value;
					}
				}
			}
		} catch (Exception ex) {
			String msg = "处理缓存失败，调用方法为：为：DBCenterImpl.putCache key=" + key;
			msg = "exception=\r\n" + WSException.getStackTraceText(ex);
			logger.error(msg);
		}
		return null;
	}

    //增加缓存数据失效时间
    @Override
    public void putCacheDelay(String clzz, String key, Object data, int delayTime) {
        try {
            CacheService.set(clzz + key, new CacheObject(data));
            CacheService.expire(clzz + key, delayTime);
        } catch (Exception ex) {
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("处理缓存失败，调用方法为：为：DBCenterImpl.putCache");
            sBuilder.append(" key=").append(key);
            sBuilder.append(" data=").append(StringUtil.getObjStr(data));
            sBuilder.append(" 数据失效时间=").append(delayTime);
            sBuilder.append(" exception=\r\n").append(WSException.getStackTraceText(ex));
            logger.error(sBuilder.toString());
        }
    }

    @Override
    public Long pushCache(String key, String... values) {
        try{
            return CacheService.push(key,values);
        }catch(Exception e){
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("处理缓存失败，调用方法为：DBCenterImpl.pushCache ");
            sBuilder.append(" key=").append(key);
            sBuilder.append(" 队列个数=").append(values.length);
            sBuilder.append(" 队列值为=");
            sBuilder.append(ArrayUtils.toString(values));
            sBuilder.append(" exception=\r\n").append(WSException.getStackTraceText(e));
            logger.error(sBuilder.toString());
        }
        return 0L;
    }

    @Override
    public String popCache(String key) {
        try{
            return CacheService.pop(key);
        }catch (Exception e){
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("处理缓存失败，调用方法为：DBCenterImpl.pushCache ");
            sBuilder.append(" key=").append(key);
            sBuilder.append(" exception=\r\n").append(WSException.getStackTraceText(e));
            logger.error(sBuilder.toString());
        }
        return "";
    }

    @Override
    public Long queueLenCache(String key) {
        try{
            return CacheService.queueLen(key);
        }catch (Exception e){
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("处理缓存失败，调用方法为：DBCenterImpl.queueLenCache ");
            sBuilder.append(" key=").append(key);
            sBuilder.append(" exception=\r\n").append(WSException.getStackTraceText(e));
            logger.error(sBuilder.toString());
        }
        return 0L;
    }

    @Override
    public void delCache(String key) {
        try {
            CacheService.remove(key);
        } catch (Exception e) {
            //TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @CacheUpdate
    public Response delete(@CacheRegion String clzz, List<Long> ids) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.delete(ids, cl);
        return Response.success();
    }

    @CacheRefresh
    public void refreshCache(@CacheRegion String clzz) {
        int i = 0;
        i++;
    }

    @CacheUpdate
    public Response updateStatus(@CacheRegion String clzz, List<Long> idList,
                                 Map<String, String> param) {

        Query query = new Query();
        Class cl = CacheUtil.getClass(clzz);
        query.update(clzz).set(param).where();
        String hsql = query.toHql();
        for (Long id : idList) {
            String idStr = " id=" + id;
            String hsqlE = hsql + idStr;
            methodCachedDao.updateByHql(hsqlE, cl);
        }

        return Response.success();
    }

    @CacheUpdate
    public void update(@CacheRegion String clzz, List idList,
                       Map<String, String> param) {
        Class cl = CacheUtil.getClass(clzz);
        Query query = new Query();
        query.update(clzz).set(param).where();
        String hsql = query.toHql();
        ShowDomain sd = ShowDomainUtil.get(clzz);
        WsField field = sd.getIdField();
        for (Object id : idList) {
            String idStr = " " + field.getDbFieldName() + "='" + id + "'";
            String hsqlE = hsql + idStr;
            methodCachedDao.updateByHql(hsqlE, cl);
        }
    }

    @CacheUpdate
    public void update(@CacheRegion String clzz, Object id,
                       Map<String, String> param) {
        Query query = new Query();
        Class cl = CacheUtil.getClass(clzz);
        query.update(clzz).set(param).where();
        String hsql = query.toHql();
        ShowDomain sd = ShowDomainUtil.get(clzz);
        WsField field = sd.getIdField();
        String idStr = " and " + field.getDbFieldName() + "='" + id + "'";
        String hsqlE = hsql + idStr;
        methodCachedDao.updateByHql(hsqlE, cl);
    }

    @Override
    @CacheQuery
    public Response getPage(@CacheRegion String clzz, Map param, Sort sorts,
                            Page page) {
        Query query = new Query();
        Class cl = CacheUtil.getClass(clzz);
        query = query.queryMapWithoutSelect(param, clzz);
        if (sorts != null) {
            query = query.sort(sorts);
        }
        String hql = query.toHql();
        page = methodCachedDao.findByHql(hql, page, cl);
        return Response.success(page);
    }

    @Override
    @CacheQuery
    public Response getPage(@CacheRegion String clzz, Query query, Sort sorts,
                            Page page) {
        Class cl = CacheUtil.getClass(clzz);
        if (sorts != null) {
            query = query.sort(sorts);
        }
        String hql = query.toHql();
        page = methodCachedDao.findByHql(hql, page, cl);
        return Response.success(page);

    }

    /**
     * clzz和relateClzz要有关系，最终结果是relateClzz类的实例
     */
    @CacheQuery
    public Response getPage(@CacheRegion String clzz, String relateClzz,
                            Map param, Sort sorts, Page page) {
        Query query = new Query();
        query = query.queryRelateClass(clzz, relateClzz);
        query = query.queryForMap(param, clzz);
        if (sorts != null) {
            query = query.sort(sorts);
        }
        String hql = query.toHql();
        Class cl = CacheUtil.getClass(clzz);
        page = methodCachedDao.findByHql(hql, page, cl);
        return Response.success(page);

    }

    /**
     * 获取所有的数据，包括关联信息的数据 返回数据对象为MAP的数据
     */
    @CacheQuery
    public Response getFullInfoPage(@CacheRegion String clzz, Map param,
                                    Sort sorts, Page page) {
        logger.debug("getFullInfoPage() start.");
        Query query = new Query();
        query = query.queryMapFullJoinWhereForSql(param, clzz);
        Class cl = CacheUtil.getClass(clzz);
        ShowDomain sd = ShowDomainUtil.get(cl);
        if (sorts != null) {
            query = query.sort(sorts);
        }
        String sql = query.toHql();
        logger.info("getFullInfoPage()|execute sql:" + sql);
        try {
            page = methodCachedDao.sqlList(cl, sql, page);
            return Response.success(page);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @CacheQuery
    public Response getall(@CacheRegion String clzz, Map param, Sort sorts) {
        Query query = new Query();
        query = query.queryMapWithoutSelect(param, clzz);
        if (sorts != null) {
            query = query.sort(sorts);
        }
        Class cl = CacheUtil.getClass(clzz);
        String hql = query.toHql();

        List list = methodCachedDao.findByHql(hql, cl);
        return Response.success(list);

    }

    /**
     * 支持SQL分页查询
     *
     * @param sql
     * @param page
     * @return
     */
    @CacheQuery
    public Response sqlList(String sql, Page page, @CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        page = methodCachedDao.sqlList(cl, sql, page);

        return Response.success(page);
    }

    @Override
    @CacheUpdate
    public Response insertEntity(IdEntity entity, @CacheRegion String clzz) {
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Long id = null;

        Class cl = CacheUtil.getClass(clzz);
        id = methodCachedDao.insertEntity(entity, cl);

        return Response.success(id);
    }

    /*
     * @Override public Response insert(List data, String clzz) { boolean isMethod
     * = ConfigContext.isMethodCache(clzz); Long id = null; if (isMethod) { Class
     * cl =CacheUtil.getClass(clzz); id = methodCachedDao.insert(data,cl); } else
     * {
     *
     * } return Response.success(id); }
     */
    @Override
    @Transactional
    @CacheUpdate
    public Response insert(List entities, @CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        for (Object entity : entities) {
            methodCachedDao.insert(entity, cl);
        }
        return Response.success();
    }

    @CacheUpdate
    public Response insertForBatch(List entities, @CacheRegion String clzz)
            throws Exception {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.insertBatch(entities, cl);
        return Response.success();
    }

    @Override
    @Transactional
    @CacheUpdate
    public Response insert(Serializable entity, @CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.insert(entity, cl);
        return Response.success();
    }

    @Override
    @CacheUpdate
    public Response update(Serializable entity, @CacheRegion String clzz) {
        try {
            Class cl = CacheUtil.getClass(clzz);
            methodCachedDao.update(entity, cl);
            return Response.success();
        } catch (Exception ex) {
            logger.error("Class: " + clzz + ", Exception: " + ex);
            return Response.fail(ex);
        }

    }

    @Override
    @CacheUpdate
    public Response delete(@CacheRegion String clzz, Long id) {
        Class cl = ConfigContext.getClassByClassName(clzz);
        methodCachedDao.delete(id, cl);
        return Response.success();
    }

    @Override
    @CacheQuery
    public Response getById(@CacheRegion String clzz, Long id) {
        Class cl = ConfigContext.getClassByClassName(clzz);
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Object entity = methodCachedDao.getById(id, cl);
        return Response.success(entity);
    }

    @CacheQuery
    public Response queryOne(@CacheRegion String clzz, Query query) {
        String hql = query.toHql();
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Class cl = CacheUtil.getClass(clzz);
        List list = methodCachedDao.findByHql(hql, cl);
        if (list != null && list.size() > 0) {
            return Response.success(list.get(0));
        }
        return Response.success();
    }

    @CacheQuery
    public Response queryOne(@CacheRegion String clzz, String hql) {
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Class cl = CacheUtil.getClass(clzz);
        Object entity = methodCachedDao.getOne(hql, cl);
        return Response.success(entity);
    }

    @CacheQuery
    public Response queryList(@CacheRegion String clzz, Query query) {
        String hql = query.toHql();
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Class cl = CacheUtil.getClass(clzz);
        List list = methodCachedDao.findByHql(hql, cl);
        return Response.success(list);
    }

    @Override
    public Response deal(@CacheRegion String opCode, Map data) {
        //TODO Auto-generated method stub
        return null;
    }

    public MethodDao getMethodCachedDao() {
        return methodCachedDao;
    }

    public void setMethodCachedDao(MethodDao methodCachedDao) {
        this.methodCachedDao = methodCachedDao;
    }

    @Override
    @CacheQuery
    public Response queryOne(@CacheRegion String clzz, Map param) {
        Query query = new Query();
        query = query.queryMapWithoutSelect(param, clzz);
        String hql = query.toHql();
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Class cl = CacheUtil.getClass(clzz);
        Object o = methodCachedDao.getOne(hql, cl);
        return Response.success(o);
    }

    @CacheQuery
    public Response queryList(@CacheRegion String clzz, Map param) {
        Query query = new Query();
        query = query.queryMapWithoutSelect(param, clzz);
        String hql = query.toHql();
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        Class cl = CacheUtil.getClass(clzz);
        List o = methodCachedDao.findByHql(hql, cl);
        return Response.success(o);
    }

    //执行Sql语句
    @CacheQuery
    public List sqlList(@CacheRegion String clzz, String sql, Object... params) {
        Class cl = null;
        if (clzz != null) {
            cl = CacheUtil.getClass(clzz);
        }
        List list = methodCachedDao.sqlList(cl, sql, params);
        return list;
    }

    @Override
    public List sqlListForMap(String clzz, String sql, Object... params) {
        Class cl = HashMap.class;
        List list = methodCachedDao.sqlList(cl, sql, params);
        return list;
    }

    /**
     * 执行Sql语句
     * @param clzz,类类型
     * @param sql,sql语句
     * @param params,传入参数
     */
    @CacheQuery
    public List getSqlList(@CacheRegion String clzz, String sql, Object... params) {
        Class cl = null;
        if (clzz != null) {
            cl = CacheUtil.getClass(clzz);
        }
        
        return methodCachedDao.getSqlList(cl, sql, params);
    }
    
    /**
     * 执行Sql语句
     * @param clzz,类类型
     * @param sql,sql语句
     * @param retMap,是否获取Map：true,Map；false,实体类
     * @param params,传入参数
     */
    @CacheQuery
    public List getSqlList(@CacheRegion String clzz, String sql, Boolean retMap, Object... params) {
        Class cl = null;
        if (clzz != null) {
            cl = CacheUtil.getClass(clzz);
        }
        
        return methodCachedDao.getSqlList((retMap?Map.class:cl), sql, params);
    }
    
	@CacheUpdate
	public List sqlListUpdataCache(@CacheRegion String clzz, String sql, Object... params) {
		Class cl = CacheUtil.getClass(clzz);
		List list = methodCachedDao.sqlList(cl, sql, params);
		return list;
	}

    public List sqlListNoCache(@CacheRegion String clzz, String sql, Object... params) {
        Class cl = CacheUtil.getClass(clzz);
        List list = methodCachedDao.sqlList(cl, sql, params);
        return list;
    }

    /**
     * 支持自定义查询总数SQL
     *
     * @param clzz
     * @param sql
     * @param countSql
     * @param page
     * @return
     */
    @CacheQuery
    public Response sqlListCountSql(String sql, String countSql, Page page,
                                    @CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        page = methodCachedDao.sqlListCountSql(sql, countSql, page, cl);
        return Response.success(page);
    }

    public Response sqlListCountSqlNoCache(String sql, String countSql, Page page,
                                           @CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        page = methodCachedDao.sqlListCountSql(sql, countSql, page, cl);
        return Response.success(page);
    }

    @CacheUpdate
    public int sqlUpdate(@CacheRegion String clzz, String sql, Object... params) {
        Class cl = CacheUtil.getClass(clzz);
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        if (isMethod) {
            int i = methodCachedDao.sqlUpdate(cl, sql, params);
            return i;
        } else {

        }
        return 0;
    }

    @CacheQuery
    public List list(@CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        if (isMethod) {
            return methodCachedDao.list(cl);
        } else {

        }
        return null;
    }

    @CacheQuery
    public Object get(@CacheRegion String clzz, Serializable id) {
        Class cl = CacheUtil.getClass(clzz);
        return methodCachedDao.get(cl, id);
    }

    @CacheUpdate
    public void save(Object entity, @CacheRegion String clzz) {
        methodCachedDao.save(entity, entity.getClass());
    }

    /**
     * 保存对象
     *
     * @param entity
     * @return 成功：返回对象ID，否则返回0
     */
    @CacheUpdate
    public Long saveEntity(IdEntity entity, @CacheRegion String clzz) {
        return methodCachedDao.insertEntity(entity, entity.getClass());
    }

    @CacheUpdate
    public void delete(Object entity, @CacheRegion String clzz) {
        methodCachedDao.delete(entity, entity.getClass());
    }

    @CacheUpdate
    public void delete(@CacheRegion String clzz, Map param) {
        Query query = new Query();
        query = query.deleteMapWithoutSelect(param, clzz);
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.delete(query.toHql(), cl);
    }

    //delete,create,update
    @CacheUpdate
    public Response execute(@CacheRegion String clzz, String sql) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.execute(sql);
        return Response.success();
    }

    @CacheUpdate
    public int executeSQL(@CacheRegion String clzz, String sql) {
        Class cl = CacheUtil.getClass(clzz);
        int rows = 0;
        rows = methodCachedDao.execute(sql);
        return rows;
    }

    @CacheUpdate
    public void deleteById(@CacheRegion String clzz, Serializable id) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.deleteById(cl, id);
    }

    @CacheQuery
    public Integer getCount(@CacheRegion String clzz, String sql) {
        Class cl = CacheUtil.getClass(clzz);
        return methodCachedDao.getCountBySql(cl, sql);
    }

    @CacheUpdate
    public void deleteAll(@CacheRegion String clzz) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.deleteAll(cl);
    }

    @CacheQuery
    public Response getPageByHql(@CacheRegion String clzz, String hql, Page page) {
        logger.debug("getPageByHql() start.");
        Class cl = CacheUtil.getClass(clzz);
        logger.info("getPageByHql()|execute sql:" + hql);
        try {
            page = methodCachedDao.findByHql(hql, page, cl);
            return Response.success(page);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int sqlUpdateIgnoreCache(String clzz, String sql, Object... params) {
        Class cl = CacheUtil.getClass(clzz);
        boolean isMethod = ConfigContext.isMethodCache(clzz);
        if (isMethod) {
            int i = methodCachedDao.sqlUpdate(cl, sql, params);
            return i;
        } else {

        }
        return 0;
    }

    @CacheUpdate
    public int hqlUpdate(String clzz, String hql) {
        Class cl = CacheUtil.getClass(clzz);
        methodCachedDao.updateByHql(hql, cl);
        return 0;
    }

}
