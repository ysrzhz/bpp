package com.wasu.pub.query;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.wasu.pub.WSException;
import com.wasu.pub.util.CacheUtil;
import com.wasu.pub.util.ShowDomainUtil;
import com.wasu.pub.domain.Period;
import com.wasu.pub.domain.WsField;
import com.wasu.pub.domain.ShowDomain;
import com.wasu.pub.util.DateUtil;
import com.wasu.pub.util.Sort;
import com.wasu.pub.util.StringUtil;

public class Query implements Serializable {
	public static final String Conf_leftBracket = "(";
	public static final String Conf_rightBracket = ")";
	public static final String Conf_And = "and";
	public static final String Conf_Or = "or";
	/*
	 * public static final Map<String, String> opMap = new HashMap<String,
	 * String>() { { put("eq", "="); put("ne", "<>"); put("lt", "<"); put("le",
	 * "<="); put("gt", ">"); put("ge", ">="); put("bw", "like"); put("bn",
	 * "not like"); put("in", "in"); put("ni", "not in"); put("ew", "like");
	 * put("en", "not like"); put("cn", "like"); put("nc", "not  like"); } };
	 */
	private static Logger log = Logger.getLogger(Query.class);
	private StringBuilder queryHql = new StringBuilder();

	public String toString(){
		return queryHql.toString();
	}
	
	public Query forupdate(){
		this.queryHql.append(" for update");
		return this;
	}
	/**
	 * 获取与关联表的查询语句
	 * 
	 * @param clzz
	 * @param subClass
	 * @return
	 */
	public Query queryRelateClass(String clzz, String subClass) {
		this.queryHql.append(" select a from ").append(subClass)
				.append("  a ,").append(clzz).append(" t ");
		//取出关联的字段
		ShowDomain sd = ShowDomainUtil.get(clzz);
		WsField field = sd.getRelateField(subClass);
		if (field == null) {
			throw new WSException("", "");
		}
		String fieldName = field.getFieldName();
		String relFieldName = field.getRlFieldCode();
		this.queryHql.append(" where t.").append(fieldName).append(" = ")
				.append(" a.").append(relFieldName).append(" ");

		return this;
	}
	
	public Query limitOne(){
		this.queryHql.append(" limit 0,1");
		return this;
	}
	public Query limit(int m,int n){
		this.queryHql.append(" limit "+m+","+n+" ");
		return this;
	}
	public Query queryMap(Map<String, String> map, String clzz) {
		this.select().clzz(clzz).where();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value_obj = map.get(key);
			String value = null;
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					this.eq(key, value);
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(key, value_int_array);
				}  else if (value_obj instanceof Long[]) {
					Long[] value_int_array = (Long[]) value_obj;
					this.in(key, value_int_array);
				}else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(key, value_str_array);
				} else {
					this.eq(key, value);
				}
			}
		}
		return this;
	}

	/**
	 * 只添加MAP的条件
	 * 
	 * @param map
	 * @param clzz
	 * @return
	 */
	public Query queryForMap(Map<String, Object> map, String clzz) {
		if (map == null) {
			return this;
		}
		Map convertMap = DateUtil.convertPeriod(clzz, map);
		ShowDomain sd = ShowDomainUtil.get(clzz);
		Iterator<String> it = convertMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			WsField field = sd.getWsField(key);
			Object value_obj = convertMap.get(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					if(field.isPrecise()){
						this.eq(key, value_obj.toString());
					}else{
						this.bw(key, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(key, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(key, value_str_array);
				} else if (value_obj instanceof Period) {
					this.period((Period) value_obj);
				} else {
					this.eq(key, value_obj.toString());
				}
			}
		}
		return this;
	}
	/**
	 * 获取的hql语句，不带有select关键字
	 * 
	 * @param map
	 * @param clzz
	 * @return
	 */
	public Query deleteMapWithoutSelect(Map<String, Object> map, String clzz) {
		this.queryHql.append("delete from ").append(clzz).append(" t where 1=1 ");
		if (map == null) {
			return this;
		}
		ShowDomain sDomain = ShowDomainUtil.get(clzz);
		Map convertMap = DateUtil.convertPeriod(clzz, map);
		if (convertMap == null) {
			return this;
		}
		Iterator<String> it = convertMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value_obj = convertMap.get(key);
			WsField wf = sDomain.getWsField(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					//todo xiaozf
					if (wf.isPrecise()) {
						this.eq(key, value_obj.toString());
					} else {
						this.bw(key, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(key, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(key, value_str_array);
				} else if (value_obj instanceof Period) {
					this.period((Period) value_obj);
				} else {
					this.eq(key, value_obj.toString());
				}
			}
		}
		return this;
	}
		/**
	 * 获取的hql语句，不带有select关键字
	 * 
	 * @param map
	 * @param clzz
	 * @return
	 */
	public Query queryMapWithoutSelect(Map<String, Object> map, String clzz) {
		this.clzz(clzz).where();
		if (map == null) {
			return this;
		}
		ShowDomain sDomain = ShowDomainUtil.get(clzz);
		Map convertMap = DateUtil.convertPeriod(clzz, map);
		if (convertMap == null) {
			return this;
		}
		Iterator<String> it = convertMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value_obj = convertMap.get(key);
			WsField wf = sDomain.getWsField(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					//todo xiaozf
					if (value_obj.toString().indexOf("_null_") > -1){
						if(value_obj.toString().indexOf("_not_")>-1)
							this.isnotnull(key);
						else
							this.isnull(key);
					}else if (wf.isPrecise()) {
						this.eq(key, value_obj.toString());
					} else {
						this.bw(key, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					if (value_int_array.length == 0){
						continue;
					}
					this.in(key, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					if (value_str_array.length == 0){
						continue;
					}
					this.in(key, value_str_array);
				} else if (value_obj instanceof List){
					List<?> list = (List<?>)value_obj;
					if (list.size() == 0){
						continue;
					}
					String[] value_str_array =  new String[list.size()];
					list.toArray(value_str_array);
					this.in(key, value_str_array);
				}
				else if (value_obj instanceof Period) {
					this.period((Period) value_obj);
				} else {
					this.eq(key, value_obj.toString());
				}
			}
		}
		return this;
	}


	/**
	 * 处理hql的字段类型
	 * 
	 * @param clzz
	 * @param key
	 * @param valueObj
	 *            传入的一定是string类型获取string数组
	 */
	private void dealColumnType(String clzz, String key, Object valueObj) {
		Class cl = CacheUtil.getClass(clzz);
		Field field = null;
		try {
			field = cl.getDeclaredField(key);
		} catch (NoSuchFieldException | SecurityException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		Class fcl = field.getClass();
		if (fcl.equals(Integer.class) || fcl.equals(int.class)) {//整数处理
			this.eqNumber(key, valueObj.toString());

		} else if (fcl.equals(Float.class) || fcl.equals(float.class)) {//浮点数处理
			this.eqNumber(key, valueObj.toString());

		} else if (fcl.equals(Long.class) || fcl.equals(long.class)) {//长整数处理

		} else {//长整数处理
			this.bw(key, valueObj.toString());
		}
	}

	/**
	 * 处理hql的字段类型
	 * 
	 * @param clzz
	 * @param key
	 * @param valueObj
	 *            传入的一定是string类型获取string数组
	 */
	private void dealColumnType(String clzz, String key, String[] valueObjs) {
		Class cl = CacheUtil.getClass(clzz);
		Field field = null;
		try {
			field = cl.getDeclaredField(key);
		} catch (NoSuchFieldException | SecurityException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		Class fcl = field.getClass();
		if (fcl.equals(Integer.class) || fcl.equals(int.class)) {//整数处理

		} else if (fcl.equals(Float.class) || fcl.equals(float.class)) {//浮点数处理

		} else if (fcl.equals(Date.class)) {//日期类型处理

		} else if (fcl.equals(Long.class) || fcl.equals(long.class)) {//长整数处理

		} else if (fcl.equals(Long.class) || fcl.equals(long.class)) {//长整数处理

		}
	}

	public Query queryMapWithoutSelectForFullInfo(Map<String, String> map,
			String clzz) {
		this.clzz(clzz).where();
		Iterator<String> it = map.keySet().iterator();
		ShowDomain sd = ShowDomainUtil.get(clzz);
		while (it.hasNext()) {
			String key = it.next();
			WsField field = sd.getWsField(key);
			Object value_obj = map.get(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					if(field.isPrecise()){
						this.eq(key, value_obj.toString());
					}else{
						this.bw(key, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(key, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(key, value_str_array);
				} else {
					this.eq(key, value_obj.toString());
				}
			}
		}
		return this;
	}

	public Query sort(Sort sorts) {
		if (queryHql.indexOf("order by") == -1) {
			queryHql.append(" order by ");
		}
		queryHql.append(sorts.getTabelSort());

		return this;
	}

	public Query select() {
		queryHql.append(" ").append("select ");
		return this;
	}

	public Query update(String clzz) {
		queryHql.append(" ").append("update ").append(clzz);
		return this;
	}

	public Query clzz(String className) {
		queryHql.append(" from ").append(className).append(" t ");
		return this;
	}

	public Query set(Map<String, String> param) {
		Iterator<Entry<String, String>> it = param.entrySet().iterator();
		queryHql.append(" set ");
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = StringUtil.getObjStr(entry.getValue());
			queryHql.append(key).append("='").append(value).append("'  ");
			if (it.hasNext()) {
				queryHql.append(" , ");
			}
		}

		return this;
	}

	/**
	 * 获取的hql语句，不带有select关键字
	 * 
	 * @param map
	 * @param clzz
	 * @return
	 */
	public Query queryMapFullJoinWher(Map<String, Object> map, String clzz) {
		this.fullJoinWhere(clzz);
		Map convertMap = DateUtil.convertPeriod(clzz, map);
		Iterator<String> it = convertMap.keySet().iterator();
		ShowDomain sd = ShowDomainUtil.get(clzz);
		while (it.hasNext()) {
			String key = it.next();
			WsField field = sd.getWsField(key);
			Object value_obj = convertMap.get(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					if(field.isPrecise()){
						this.eq(key, value_obj.toString());
					}else{
						this.bw(key, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(key, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(key, value_str_array);
				} else if (value_obj instanceof Period) {
					this.period((Period) value_obj);
				} else {
					this.eq(key, value_obj.toString());
				}
			}
		}
		
		return this;
	}

	public Query queryMapFullJoinWhereForSql(Map<String, Object> map,
			String clzz) {
		this.fullJoinWhereForSql(clzz);
		Map convertMap = DateUtil.convertPeriod(clzz, map);
		Iterator<String> it = convertMap.keySet().iterator();
		ShowDomain sd = ShowDomainUtil.get(clzz);
		while (it.hasNext()) {
			String key = it.next();
			WsField field = sd.getWsField(key);
			String dbColumnName = field.getDbFieldName();
			Object value_obj = convertMap.get(key);
			if (null == value_obj || value_obj instanceof Class) {
				continue;
			} else {
				this.and();
				if (value_obj instanceof String) {
					if(field.isPrecise()){
						this.eq(dbColumnName, value_obj.toString());
					}else{
						this.bw(dbColumnName, value_obj.toString());
					}
					//this.
				} else if (value_obj instanceof Integer[]) {
					Integer[] value_int_array = (Integer[]) value_obj;
					this.in(dbColumnName, value_int_array);
				} else if (value_obj instanceof String[]) {
					String[] value_str_array = (String[]) value_obj;
					this.in(dbColumnName, value_str_array);
				} else if (value_obj instanceof Period) {
					this.periodForSql((Period) value_obj);
				} else {
					this.eq(dbColumnName, value_obj.toString());
				}
			}
		}
		log.debug("本次Query产生的sql==" + this.toHql());
		return this;
	}

	/***
	 * 判断clzz中那些字段是与其他表中进行关联，之后进行取出关联的数据 其中多表的数据返回是数组
	 * 
	 * @param clzz
	 * @return
	 */
	public Query fullJoinWhere(String clzz) {
		Class cl = CacheUtil.getClass(clzz);
		ShowDomain sd = ShowDomainUtil.get(cl);
		StringBuilder tablesSb = new StringBuilder();
		StringBuilder whereSb = new StringBuilder();
		Collection<WsField> fields = sd.getFields().values();
		queryHql.append("select  new map( ");
		tablesSb.append(" from ").append(clzz).append(" t ");
		whereSb.append(" where 1=1  ");
		int i = 0;
		for (WsField field : fields) {
			if (field.isTransient()) {
				continue;
			}
			if (i == 0) {
				queryHql.append(" t.").append(field.getFieldName())
						.append(" as ").append(field.getFieldName())
						.append(" ");

			} else {
				queryHql.append(" , t.").append(field.getFieldName())
						.append(" as ").append(field.getFieldName())
						.append(" ");
			}
			if (field.isRelate()) {//判断是关联字段
				queryHql.append(" , ").append(" t").append(i).append(".")
						.append(field.getRlFieldName()).append(" as ")
						.append(field.getRlShwoFieldName());
				tablesSb.append(" , ").append(field.getRlClass()).append(" t")
						.append(i);
				whereSb.append(" and ").append(" t").append(".")
						.append(field.getFieldName()).append(" = ")
						.append(" t").append(i).append(".")
						.append(field.getRlFieldCode());
			}
			i++;

		}
		queryHql.append(" ) ").append(tablesSb).append(whereSb);
		return this;
	}

	/***
	 * 判断clzz中那些字段是与其他表中进行关联，之后进行取出关联的数据 其中多表的数据返回是数组 主要产生Sql语句
	 * 
	 * @param clzz
	 * @return
	 */
	public Query fullJoinWhereForSql(String clzz) {
		Class cl = CacheUtil.getClass(clzz);
		ShowDomain sd = ShowDomainUtil.get(cl);
		StringBuilder tablesSb = new StringBuilder();
		StringBuilder whereSb = new StringBuilder();
		Collection<WsField> fields = sd.getFields().values();
		queryHql.append("select  ");
		tablesSb.append(" from ").append(sd.getTableName()).append(" t ");
		whereSb.append(" where 1=1  ");
		int i = 0;
		for (WsField field : fields) {
			if (field.isTransient()) {
				continue;
			}
			if (i == 0) {
				//queryHql.append("ifnull( t.").append(field.getDbFieldName()).append(" ,'' ) as ").append(field.getFieldName()).append(" ");
				queryHql.append(" t.").append(field.getDbFieldName()).append(" as ").append(field.getFieldName())
						.append(" ");

			} else {
				//queryHql.append(" , ifnull(t.").append(field.getDbFieldName()).append(" ,'' ) as ").append(field.getFieldName()).append(" ");
				queryHql.append(" , t.").append(field.getDbFieldName()).append(" as ").append(field.getFieldName())
						.append(" ");
			}
			if (field.isRelate()) {//判断是关联字段
				Class relCl = CacheUtil.getClass(field.getRlClass());
				ShowDomain relSd = ShowDomainUtil.get(relCl);
				WsField relCodeField = relSd.getWsField(field.getRlFieldCode());
				WsField relNameField = relSd.getWsField(field.getRlFieldName());
				if (relNameField!=null&&relNameField.isRelate()) {//如果关联的，要展示的id也关联了其他的类，则需要提取其他类的数据
					Class relRefCl = CacheUtil.getClass(relNameField.getRlClass());
					ShowDomain relRefSd = ShowDomainUtil.get(relRefCl);
					WsField relRefCodeField = relRefSd.getWsField(relNameField
							.getRlFieldCode());
					WsField relRefNameField = relRefSd.getWsField(relNameField
							.getRlFieldName());
				/*	queryHql.append(" , ").append("ifnull( t").append(i)
							.append(".").append(relNameField.getDbFieldName())
							.append(",'' )").append(" as ")
							.append(relNameField.getRlShwoFieldName());*/
					queryHql.append(" , ").append("ifnull( tRef").append(i)
					.append(".").append(relRefNameField.getDbFieldName())
					.append(",'' )").append(" as ")
					.append(relNameField.getRlShwoFieldName());
					tablesSb.append(" left join  ( ")
							.append(relSd.getTableName()).append(" t")
							.append(i).append("  , ").append(relRefSd.getTableName()).append(" tRef")
							.append(i).append("  )  on ( t.")
							.append(field.getDbFieldName()).append(" = ")
							.append(" t").append(i).append(".")
							.append(relCodeField.getDbFieldName())
							.append(" and  t").append(i).append(".")
							.append(relNameField.getDbFieldName()).append(" = ")
							.append(" tRef").append(i).append(".")
							.append(relRefCodeField.getDbFieldName())
							.append(" ) ")
							;
				} else {//没有继续关联
					queryHql.append(" , ").append("ifnull( t").append(i)
							.append(".").append(relNameField.getDbFieldName())
							.append(",'' )").append(" as ")
							.append(field.getRlShwoFieldName());
					tablesSb.append(" left join  ")
							.append(relSd.getTableName()).append(" t")
							.append(i).append("  on t.")
							.append(field.getDbFieldName()).append(" = ")
							.append(" t").append(i).append(".")
							.append(relCodeField.getDbFieldName());
				}
				//whereSb.append(" and ").append(" t").append(".").append(field.getFieldName()).append(" = ").append(" t").append(i).append(".").append(field.getRlFieldCode());
			}
			i++;

		}
		queryHql.append(" ").append(tablesSb).append(whereSb);
		return this;
	}

	public Query where() {
		queryHql.append("  where 1=1 ");
		return this;
	}

	public Query leftBracket() {
		queryHql.append(" ").append(Conf_leftBracket);
		return this;
	}

	public Query rightBracket() {
		queryHql.append(" ").append(Conf_rightBracket);
		return this;
	}

	public Query and() {
		queryHql.append(" ").append(Conf_And);
		return this;
	}

	public Query or() {
		queryHql.append(" ").append(Conf_Or);
		return this;
	}

	public Query eq(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("=").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query eqNumber(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("=").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query eqDate(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("=").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query period(Period period) {
		queryHql.append(period.toHql());
		return this;
	}

	public Query periodForSql(Period period) {
		queryHql.append(period.toSql());
		return this;
	}

	public Query ne(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("<>").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query lt(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("<").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query le(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append("<=").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query gt(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append(">").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query ge(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append(">=").append(" '")
				.append(value).append("'");
		return this;
	}

	public Query bw(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append(" like").append(" '%")
				.append(value).append("%'");
		return this;
	}

	public Query in(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append(" in (").append(" '")
				.append(value).append("' )");
		return this;
	}

	public Query in(String fieldName, Integer... values) {
		queryHql.append(" t.").append(fieldName).append(" in (");
		for (Integer value : values) {
			queryHql.append(value).append(",");
		}
		queryHql.deleteCharAt(queryHql.length() - 1);
		queryHql.append(" )");
		return this;
	}
	public Query in(String fieldName, Long... values) {
		queryHql.append(" t.").append(fieldName).append(" in (");
		for (Long value : values) {
			queryHql.append(value).append(",");
		}
		queryHql.deleteCharAt(queryHql.length() - 1);
		queryHql.append(" )");
		return this;
	}
	public Query in(String fieldName, String... values) {
		queryHql.append(" t.").append(fieldName).append(" in (");
		for (String value : values) {
			queryHql.append(" '").append(value).append("' ").append(",");
		}
		queryHql.deleteCharAt(queryHql.length() - 1);
		queryHql.append(" )");
		return this;
	}

	public Query ni(String fieldName, String value) {
		queryHql.append(" t.").append(fieldName).append(" not in (")
				.append(" '").append(value).append("' )");
		return this;
	}

	public Query isnull(String fieldName) {
		queryHql.append(" t.").append(fieldName).append(" is null ");
		return this;
	}
	public Query isnotnull(String fieldName) {
		queryHql.append(" t.").append(fieldName).append(" is not null ");
		return this;
	}
	public String count() {
		String hql = queryHql.toString();
		hql = "select count(*)  " + hql.substring(hql.indexOf("from"));
		return hql;
	}

	public String toHql() {
		return queryHql.toString();
	}

	public Query orderBy() {
		queryHql.append(" order by ");
		return this;
	}

	public Query sort(String fieldName, boolean isAsc) {
		queryHql.append(" t.").append(fieldName);
		if (isAsc) {
			queryHql.append(" asc");
		} else {
			queryHql.append(" desc");
		}
		return this;
	}
}
