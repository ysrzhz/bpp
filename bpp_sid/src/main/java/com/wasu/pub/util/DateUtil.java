package com.wasu.pub.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.wasu.pub.WSException;
import com.wasu.pub.domain.Period;
import com.wasu.pub.domain.WsField;
import com.wasu.pub.domain.ShowDomain;

public class DateUtil {
	private final static String YYYYMMDDHHMMSS19 = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 获取标准时间类型的数据
	 * 
	 * @param date
	 * @return
	 */
	public static String getNormal(Date date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(date);
		} catch (Exception ex) {
			throw new WSException(ex);
		}
	}

	/**
	 * 获取标准时间类型的数据
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNormal(String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.parse(date);
		} catch (Exception ex) {
			throw new WSException(ex);
		}
	}

	/***
	 * 日期中字段如果最后带有_begin,和_end，会被提取到Period.begin和end中做范围查询，如果是其他date，则会提取的Period
	 * .date中 做精确查询 其中value所对应的值是yyyy-MM-dd hh:mm:ss格式的字符串
	 * 
	 * @param param
	 * @return
	 */
	public static Map convertPeriod(String clzz, Map param) {
		if (param == null || param.size() <= 0) {
			return new HashMap();
		}
		Iterator<Entry> it = param.entrySet().iterator();
		Map result = new HashMap();
		ShowDomain sd = ShowDomainUtil.get(clzz);
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			Class cl = CacheUtil.getClass(clzz);
			Field field = null;
			String beginFiled = "";
			String endField = "";
			String fieldName = key;
			try {
				beginFiled = getDateKey(key, "_begin");
				if (!beginFiled.equals("")) {
					fieldName = beginFiled;
				} else {
					endField = getDateKey(key, "_end");
					if (!endField.equals("")) {
						fieldName = endField;
					}
				}

				field = CacheUtil.getDeclaredField(cl, fieldName);
				if (field == null) {
					continue;
				}
				//BeanUtils aa;
				//aa.
				//cl.get
			} catch (Exception e) {
				throw new WSException(e);
			}
			if (field.getType().equals(Date.class)) {
				WsField wsField = sd.getWsField(fieldName);
				String dbFielName = wsField.getDbFieldName();
				if (!beginFiled.equals("") || !endField.equals("")) {
					if (!beginFiled.equals("")) {
						Period period = (Period) result.get(beginFiled);
						if (period == null) {
							period = new Period();
						}
						Date begin = getNormal((String) value);
						period.setBegin(begin);
						period.setKeyField(beginFiled);
						period.setClzz(cl);
						period.setDbFielName(dbFielName);
						result.put(beginFiled, period);

					}
					if (!endField.equals("")) {
						Period period = (Period) result.get(endField);
						if (period == null) {
							period = new Period();
						}
						Date end = getNormal((String) value);
						period.setEnd(end);
						period.setKeyField(endField);
						period.setClzz(cl);
						period.setDbFielName(dbFielName);
						result.put(endField, period);
					}
				} else {//处理纯日期数据
					Period period = new Period();
					Date date = getNormal((String) value);
					period.setDate(date);
					period.setKeyField(key);
					period.setClzz(cl);
					period.setDbFielName(dbFielName);
					result.put(key, period);
				}
			} else {
				result.put(key, value);
			}
		}
		return result;
	}

	private static String getDateKey(String key, String begin) {
		String result = "";
		if (key.length() <= begin.length()) {
			return result;
		}
		if (key.substring(key.length() - begin.length()).equals(begin)) {
			result = key.substring(0, key.length() - begin.length());
		}
		;
		return result;
	}
	
    /**
     * 将日期转换成字符串
     * @param date
     * @param format
     * @return
     */
    public static String date2Str(Date date,String format){
        if(date==null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String result = null;
        sdf.setLenient(false);
        try {
            result = sdf.format(date) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 将时间字符串转换成Date类型时间
     * @param date yyyy-MM-dd HH:mm:ss 类型的时间字符串
     * @return 字符串对应的Date类型时间
     */
    public static Date str2Date(String date) {
        return str2Date(date, YYYYMMDDHHMMSS19);
    }
    
    /**
     * @param dateStr
     *            日期字符串 例如：0000-00-00 00:00:00
     * @param formatStr
     *            格式化字符串，例如：yyyy-MM-dd HH:mm:ss
     * @return Date 日期类型
     */
    public static Date str2Date(String date, String format) {
        Date d = null;
        SimpleDateFormat sf = null;
        try {
            sf = new SimpleDateFormat(format);
            d = sf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            d = null;
        }
        return d;
    }
    
    /**
     * 获取增量时间
     * @param date 时间
     * @param hour 小时增量
     * @return
     */
	public static Date getAddHourOfDate(Date date, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		return cal.getTime();
	}
}