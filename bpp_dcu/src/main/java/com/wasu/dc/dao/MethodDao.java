package com.wasu.dc.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.wasu.dc.util.SqlUtil;
import com.wasu.pub.domain.IdEntity;
import com.wasu.pub.util.Page;

@Repository("methodCachedDao")
public class MethodDao implements Serializable {
	@Resource
	private SessionFactory sessionFactory;
	private static final int BATCHSIZE = 3000;//批量操作执行行数
	private static String SELECT_HQL = "select ";
	private static String DELETE_HQL = "delete ";
	private static String FROM_HQL = "from ";
	protected Logger logger = Logger.getLogger(getClass());

	//@CacheUpdate
	public void insert(Object entity, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.save(entity);
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void insertBatch(List datas, Class clzz) throws Exception {
		Session session = null;
		String sql = null;
		try {
			if (datas == null || datas.isEmpty()) {
				return;
			}
			
			if (session == null) {
				session = getSession();
			}
			if (datas.size() <= BATCHSIZE) {
				sql = SqlUtil.getBatchInsert(datas, clzz.getName());
				//logger.info("sql="+sql);
				session.createSQLQuery(sql).executeUpdate();
			} else {//分批次
				int page = datas.size() % BATCHSIZE == 0 ? datas.size() / BATCHSIZE : datas.size() / BATCHSIZE + 1;
				for (int i = 0; i < page; i++) {
					int begin = i * BATCHSIZE;
					int end = (i + 1) * BATCHSIZE < datas.size() ? (i + 1) * BATCHSIZE : datas.size();
					List splitData = datas.subList(begin, end);
					sql = SqlUtil.getBatchInsert(splitData, clzz.getName());
					//logger.info("sql="+sql);
					session.createSQLQuery(sql).executeUpdate();
				}
			}
			//session.flush();
		} catch (Exception ex) {
			logger.error("sql==" + sql);
			logger.error(ex);
			throw ex;
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public Long insertEntity(IdEntity entity, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.save(entity);
			//session.flush();
			return entity.getId();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return null;
	}

	/*
	 * @CacheUpdate public Long insert(List datas,Class clzz) { Session session
	 * = null; try { session = getSession();
	 *
	 * session.save(entity); return entity.getId(); } finally {
	 * //session.close(); } }
	 */
	//@CacheQuery
	public Object getById(Long id, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			return session.byId(clzz).load(id);
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return null;
	}

	//@CacheQuery
	public Object getById(String id, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			return session.byId(clzz).load(id);
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return null;
	}

	//@CacheUpdate
	public void delete(Long id, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			//String hql = DELETE_HQL + " " + clzz.getName() + " id=" + id;
			StringBuilder hql = new StringBuilder(DELETE_HQL);
			hql.append(" ").append(clzz.getName()).append(" id=").append(id);
			session.createQuery(hql.toString()).executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	///@CacheUpdate
	public void delete(String hql, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			//String hql = DELETE_HQL + " " + clzz.getName() + " id=" + id;
			session.createQuery(hql).executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void deleteAll(Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			//String hql = DELETE_HQL + " " + clzz.getName();
			StringBuilder hql = new StringBuilder(DELETE_HQL);
			hql.append(" ").append(clzz.getName());
			session.createQuery(hql.toString()).executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void delete(List<Long> ids, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			StringBuilder sb = new StringBuilder();
			sb.append(DELETE_HQL).append(" ").append(clzz.getName()).append(" where  id in (");
			int i = 0;
			if (ids != null && !ids.isEmpty()) {
				for (Long id : ids) {
					if (i == 0) {
						sb.append(id);
						i++;
					} else {
						sb.append(" , ").append(id);
					}
				}
			}
			sb.append(" ) ");
			session.createQuery(sb.toString()).executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	///@CacheUpdate
	public void update(Object entity, Class clzz) {

		Session session = null;
		//Transaction tx=null;
		try {
			if (session == null) {
				session = getSession();
			}
			//tx=session.beginTransaction();
			//tx.begin();
			session.update(entity);
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//tx.commit();
			//session.close();
		}

	}

	//@CacheUpdate
	public void updateByHql(String hql, Class clzz) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.createQuery(hql).executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheQuery
	public List findByHql(String hql, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			return session.createQuery(hql).list();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return null;
	}

	//@CacheQuery
	public Page findByHql(String hql, Page page, Class clzz) {
		//查询分页的最大条数
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			if (page.getTotalCount() == 0) {
				String countHql = SqlUtil.prepareCountHql(hql);
				Long totalCount = (Long) session.createQuery(countHql).list().get(0);
				page.setTotalCount(Integer.parseInt(Long.toString(totalCount)));
			}
			Query query = session.createQuery(hql);
			int pageNo = page.getPageNo();
			int pageSize = page.getPageSize();
			query.setFirstResult((pageNo - 1) * pageSize);
			query.setMaxResults(pageSize);
			List datas = query.list();
			page.setResults(datas);
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return page;
	}

	//@CacheQuery
	public Object getOne(String hql, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			Query query = createQuery(session, hql);
			query.setMaxResults(1);
			logger.debug("methodao getone hql=" + hql);
			return query.uniqueResult();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return null;
	}

	//@CacheQuery
	public Object getOneForUpdate(String hql, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			Query query = createQuery(session, hql);
			query.setMaxResults(1);
			query.setLockOptions(LockOptions.UPGRADE);
			logger.debug("methodao getone hql=" + hql);
			return query.uniqueResult();
		} finally {
			//session.close();
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();//spring-applicationContext.xml中myPointcut已执行sessionFactory.getSession()
	}

	public Query createQuery(Session session, String hql) {
		return session.createQuery(hql);
	}

	/**
	 * 执行Sql语句
	 * @param clazz,类类型
	 * @param sql,sql语句
	 * @param params,传入参数
	 */
	public List getSqlList(Class<?> clazz, String sql, Object... params) {
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setCacheable(false);
			if (clazz != null) {//结果类型转换，默认返回List<Object[]>
				if (clazz.getName().equals(Map.class.getName())) {
					query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				} else {//addEntity通过原生态sql去关联RsAdPlay各字段(所以为RsAdPlay中定义的数据库字段名，不能起别名)，而aliasToBean通过setter方法去设置值(所以要起别名为RsAdPlay的属性名)
					query.addEntity(clazz);//query.setResultTransformer(Transformers.aliasToBean(clzz))会导致bigint和Long映射异常：java.math.BigInteger->Long
				}
			}

			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}

			return query.list();
		} catch (Exception e) {
			logger.error("query sql exception:" + e.getMessage(), e);
			e.printStackTrace();
			return null;
		} finally {
			//session.close();
			//session=null;
		}
	}
	
	//执行Sql语句
	//@CacheQuery
	public List sqlList(Class clzz, String sql, Object... params) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			SQLQuery query = sql(session, clzz, sql, true);
			query.setCacheable(false);
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			return query.list();
		} catch (Exception e) {
			logger.error("query sql exception:" + e.getMessage(), e);
			e.printStackTrace();
		} finally {
			//session.close();
		}
		return null;
	}

	public int execute(String sql) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			SQLQuery query = session.createSQLQuery(sql);
			return query.executeUpdate();
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return 0;
	}

	/**
	 * 支持自定义查询总数
	 * 
	 * @param clzz
	 * @param sql
	 * @param countSql
	 * @param page
	 * @return
	 */
	public Page sqlListCountSql(String sql, String countSql, Page page, Class clzz) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			if (page.getTotalCount() == 0) {
				SQLQuery query = sql(session, countSql);
				query.setCacheable(false);
				Map map = (Map) query.list().get(0);

				int totalCount = ((BigInteger) map.values().iterator().next()).intValue();
				page.setTotalCount(totalCount);
			}

			SQLQuery query = sql(session, sql);
			int pageNo = page.getPageNo();
			int pageSize = page.getPageSize();
			query.setFirstResult((pageNo - 1) * pageSize);
			query.setMaxResults(pageSize);
			query.setCacheable(false);
			List list = query.list();
			page.setResults(list);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			//session.close();
		}
		return page;
	}

	//执行Sql语句,返回map
	//@CacheQuery
	public Page sqlList(Class clzz, String sql, Page page) {
		return sqlListCountSql(sql, SqlUtil.prepareCountHql(sql), page, clzz);
	}

	private SQLQuery sql(Session session, String sql) {

		SQLQuery query = session.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query;
	}

	//执行Sql语句
	//@CacheQuery
	public int getCountBySql(Class clzz, String sql) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			SQLQuery query = session.createSQLQuery(sql);
			query.setCacheable(false);
			return ((BigInteger) query.list().get(0)).intValue();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return 0;
	}

	//@CacheQuery
	public SQLQuery sql(Session session, Class clzz, String sql, boolean transform) {

		SQLQuery query = session.createSQLQuery(sql);
		if (transform && clzz != null) {
			if (clzz.getName().equals(HashMap.class.getName())) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			} else {
				query.setResultTransformer(Transformers.aliasToBean(clzz));//Oracle数据库时addEntity(clzz)
			}
		}
		return query;
	}

	//@CacheQuery
	public int sqlUpdate(Class clzz, String sql, Object... params) {
		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			SQLQuery query = sql(session, clzz, sql, false);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					query.setParameter(i, params[i]);
				}
			}
			return query.executeUpdate();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
		return 0;
	}

	//@CacheQuery
	public List list(Class clzz) {

		return findByHql(" from " + clzz.getName(), clzz);
	}

	//@CacheQuery
	public Object get(Class clzz, Serializable id) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			return session.get(clzz, id);
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void save(Object entity, Class clzz) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.save(entity);
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void delete(Object entity, Class clzz) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.delete(entity);
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheUpdate
	public void deleteById(Class clzz, Serializable id) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}
			session.delete(get(clzz, id));
			//session.flush();
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		} finally {
			//session.close();
		}
	}

	//@CacheQuery
	public CriteriaWrapper from(Class clazz) {

		Session session = null;
		try {
			if (session == null) {
				session = getSession();
			}

			return new CriteriaWrapper(session.createCriteria(clazz));
		} finally {
			//session.close();
		}
	}

	public class CriteriaWrapper {
		private Criteria criteria;

		public CriteriaWrapper(Criteria criteria) {
			this.criteria = criteria;
		}

		public CriteriaWrapper eq(String name, Object value) {
			criteria.add(Restrictions.eq(name, value));
			return this;
		}

		public CriteriaWrapper like(String name, Object value) {
			criteria.add(Restrictions.like(name, value.toString(), MatchMode.ANYWHERE));
			return this;
		}

		public CriteriaWrapper lt(String name, Object value) {
			criteria.add(Restrictions.lt(name, value));
			return this;
		}

		public CriteriaWrapper le(String name, Object value) {
			criteria.add(Restrictions.le(name, value));
			return this;
		}

		public CriteriaWrapper gt(String name, Object value) {
			criteria.add(Restrictions.gt(name, value));
			return this;
		}

		public CriteriaWrapper ge(String name, Object value) {
			criteria.add(Restrictions.ge(name, value));
			return this;
		}

		public CriteriaWrapper notNull(String name) {
			criteria.add(Restrictions.isNotNull(name));
			return this;
		}

		public CriteriaWrapper isNull(String name) {
			criteria.add(Restrictions.isNull(name));
			return this;
		}

		public CriteriaWrapper asc(String name) {
			criteria.addOrder(Order.asc(name));
			return this;
		}

		public CriteriaWrapper desc(String name) {
			criteria.addOrder(Order.desc(name));
			return this;
		}

		public List list() {
			return criteria.list();
		}

		public Object unique() {
			return criteria.uniqueResult();
		}

		public Object first() {
			List list = criteria.list();
			Object e = list == null || list.size() == 0 ? null : list.get(0);
			return e;
		}

		public int count() {
			return ((Integer) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
		}

		public Criteria criteria() {
			return criteria;
		}
	}

}