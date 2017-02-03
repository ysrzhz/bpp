package com.wasu.dc.service.imp;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.wasu.pub.WSException;
import com.wasu.dc.dao.MethodDao;
import com.wasu.pub.service.DBCenter;
import com.wasu.pub.service.Seq;
import com.wasu.pub.query.Query;
import com.wasu.pub.util.CacheUtil;
import com.wasu.pub.util.ObjectUtil;
import com.wasu.sid.SysSeq;

@Service("seq")
public class SeqImpl implements Seq {
	@Resource
	private MethodDao methodCachedDao;
	@Resource
	private DBCenter dbCenter;
	private static Logger log = Logger.getLogger(SeqImpl.class);
	private static int ID_CACHE_SEQUENCE = 2000;//id缓存机制,每次缓存的ID数目
	//缓存每个sequence对应的缓存的id剩余的值
	private static Map<String, Long> cacheIdSurplus = new ConcurrentHashMap<String, Long>();//缓存中剩余的id值
	//缓存每个sequence以及当前的ID值
	private static Map<String, Long> currentIds = new ConcurrentHashMap<String, Long>();//缓存中最新的id值

	/**
	 * 根据序列号类别获取SequenceId的值
	 * @param clzz 类名
	 * @param nodeId 运营商ID
	 * @return 返回SequenceId的值
	 * @throws SQLException
	 */
	public synchronized Long getSeq(String clzz) {
		log.debug("getSequenceId method start.param [ clzz = " + clzz + "]");
		Long sequenceId = 1000L; //序列号
		SysSeq entity = null;
		try {
			Query queryHql = new Query();
			queryHql = queryHql.clzz(SysSeq.class.getName()).where().and().eq("tableName", clzz);
			String hql = queryHql.toHql();
			entity = (SysSeq) methodCachedDao.getOneForUpdate(hql, SysSeq.class);
			Class cl = CacheUtil.getClass(clzz);
			if (entity == null) {//如果为空,则插入系列号生成器表(系列号类别,值)
				sequenceId++;
				entity = new SysSeq();
				entity.setTableName(clzz);
				entity.setCurrentValue(sequenceId);
				entity.setIncrementValue(1l); //设置步长值
				entity.setMinvalue(sequenceId);
				entity.setMaxvalue(new Long(Integer.MAX_VALUE)); //设置最大值
				entity.setCycle(0);
				methodCachedDao.save(entity, cl);
			} else {//否则更新系列号生系列号生成器表的值
				sequenceId = getSequenceFormCache(clzz, entity);
			}

			log.debug("Generator after  [ sequenceId = " + sequenceId + "]");
		} catch (Exception se) {
			se.printStackTrace();
			log.error("生成序列号失败: 表名= " + clzz, se);
			throw new WSException("", "生成序列号失败: 表名= " + clzz, se);
		}

		log.debug("getSequenceId method end  [ sequenceId = " + sequenceId + "]");
		return sequenceId;
	}

	public synchronized Long getSeq(String clzz, int size) {
		log.debug("getSequenceId method start.param [ clzz = " + clzz + "]");
		Long sequenceId = getSeq(clzz); //序列号
		sequenceId++;
		SysSeq entity = null;
		try {
			Class cl = CacheUtil.getClass(clzz);
			Query queryHql = new Query();
			queryHql = queryHql.clzz(SysSeq.class.getName()).where().and().eq("tableName", clzz);
			String hql = queryHql.toHql();
			entity = (SysSeq) methodCachedDao.getOneForUpdate(hql, SysSeq.class);
			entity.setTableName(clzz);
			entity.setCurrentValue(sequenceId + size + 50);
			methodCachedDao.update(entity, cl);
			currentIds.clear();
			cacheIdSurplus.clear();
			log.debug("Generator after  [ sequenceId = " + sequenceId + "]");
		} catch (Exception se) {
			se.printStackTrace();
			log.error("生成序列号失败: 表名= " + clzz, se);
			throw new WSException("", "生成序列号失败: 表名= " + clzz, se);
		}

		log.debug("getSequenceId method end  [ sequenceId = " + sequenceId + "]");
		return sequenceId;
	}

	/**
	 * 从缓存获取ID:获取规则：1 如果currentIds缓存中不存在相应的sequence，则表示该sequence还未缓存，
	 * 需先从db获取后将sequence当前值以及分配的缓存ID保存到缓存 2 如果cacheIdSurplus为空或者剩余的缓存ID小于等于10个，
	 * 则从db获取后更新缓存中sequence的当前值以及分配的新的缓存ID保存到缓存 3
	 * 如果上面2中情况都不满足。则直接从缓存获取sewuence，并更新currentIds和cacheIdSurplus中对应的sequence的值
	 * @param tableName
	 * @param entity
	 * @return
	 */
	private Long getSequenceFormCache(String tableName, SysSeq entity) {
		Long sequenceId = 0l; //序列号
		Long currentId = null;//先不用缓存
		Long incrementValue = entity.getIncrementValue();//获取步长值
		Long dbId = entity.getCurrentValue();
		if (dbId >= Integer.MAX_VALUE) { //整数的最大值
			dbId = 0l;
		}
		
		if (ObjectUtil.isEmpty(currentId)) {
			sequenceId = handlerDateToCache(dbId, tableName, entity, incrementValue);
			currentIds.put(tableName, sequenceId);
		} else {
			Long cacheId = cacheIdSurplus.get(tableName);
			if (ObjectUtil.isEmpty(cacheId) || cacheId < incrementValue + 10l) {
				sequenceId = handlerDateToCache(dbId, tableName, entity, incrementValue);
				currentIds.put(tableName, sequenceId);
			} else {
				sequenceId = currentId + incrementValue;
				currentIds.put(tableName, sequenceId);
				cacheIdSurplus.put(tableName, cacheId - incrementValue);
			}
		}
		
		return sequenceId;
	}

	/**
	 * 检测到当前sequence在缓存中没有记录或者缓存中当前分配的ID已经使用只剩余10个时，
	 * 开始重新分配ID。新增或者更新缓存中的缓存ID数值和当前ID
	 * @param dbId
	 * @param tableName
	 * @param entity
	 * @param incrementValue
	 * @return
	 */
	private Long handlerDateToCache(Long dbId, String clzz, SysSeq entity, Long incrementValue) {
		Long sequenceId = dbId + incrementValue;
		Map param = new HashMap();
		param.put("currentValue", sequenceId);
		dbCenter.update(SysSeq.class.getName(), clzz, param);
		cacheIdSurplus.put(clzz, ID_CACHE_SEQUENCE - incrementValue);
		return sequenceId;
	}

	/**
	 * 从数据库获取最新的ID。每次都更新数据库，兼容老的方式
	 * @param tableName
	 * @param entity
	 * @return
	 */
	private Long getSequenceFormDb(String tableName, SysSeq entity) {
		Long sequenceId = 0l; //序列号
		sequenceId = entity.getCurrentValue();
		if (sequenceId >= Integer.MAX_VALUE) { //整数的最大值
			sequenceId = 0l;
		}
		
		Long IncrementValue = entity.getIncrementValue();//获取步长值
		sequenceId = sequenceId + IncrementValue;//根据步长递增序列号
		Map param = new HashMap();
		param.put("currentValue", sequenceId);
		dbCenter.update(SysSeq.class.getName(), tableName, param);
		return sequenceId;
	}

	private void writerFile(String tableName, String content) {
		String fileName = "F:\\测试sequence\\log_" + tableName + ".log";
		try {
			FileWriter writer = new FileWriter(fileName, true);//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer.write(content);
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public MethodDao getMethodCachedDao() {
		return methodCachedDao;
	}

	public void setMethodCachedDao(MethodDao methodCachedDao) {
		this.methodCachedDao = methodCachedDao;
	}

	public DBCenter getDbCenter() {
		return dbCenter;
	}

	public void setDbCenter(DBCenter dbCenter) {
		this.dbCenter = dbCenter;
	}
}