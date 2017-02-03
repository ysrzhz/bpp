package com.wasu.bpp.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.wasu.pub.util.StringUtil;

import net.sf.json.JSONObject;

public class StringUtils {
	private static Logger logger = Logger.getLogger(StringUtils.class);
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	public static boolean isEmpty(String str) {
		if (str==null || str.trim().length() == 0) {
			return true;
		}

		return false;
	}
	
	public static String getStr(Object o) {
		if (o == null) {
			return "";
		}

		return o.toString();
	}
	
	public static List<String> split(String str, char ch) {
		List<String> list = null;
		char c;
		int ix = 0, len = str.length();
		for (int i = 0; i < len; i++) {
			c = str.charAt(i);
			if (c == ch) {
				if (list == null)
					list = new ArrayList<String>();
				list.add(str.substring(ix, i));
				ix = i + 1;
			}
		}
		
		if (ix > 0)
			list.add(str.substring(ix));
		return list;
	}

	/**
	 * 把不足长度的数字在前面补零
	 */
	public static String fixLength(Integer num, Integer length) {
		int currentLen = num.toString().length();
		int n = length - currentLen;
		StringBuilder result = new StringBuilder();
		for (; n > 0; n--) {
			result.append("0");
		}
		
		result.append(num);
		return result.toString();
	}

	/**
	 * 把不足长度的字符在前面补零
	 */
	public static String fixLength(String str, Integer length) {
		int currentLen = str.length();
		int n = length - currentLen;
		StringBuilder result = new StringBuilder();
		for (; n > 0; n--) {
			result.append("0");
		}
		
		result.append(str);
		return result.toString();
	}

	/**
	 * 根据给定的字符串，取固定长度，长度不够尾部补0
	 * @param str
	 * @param length
	 * @return
	 */
	public static String fixStrTail(String str, Integer length) {
		char[] charArray = str.toCharArray();
		int len = charArray.length;
		StringBuilder result = new StringBuilder();
		for (int n = 0; n < length; n++) {
			if (n < len) {
				result.append(charArray[n]);
			} else {
				result.append("0");
			}
		}
		
		return result.toString();
	}

	public static String getTime(Date date) {
		try {
			if (date == null) {
				return null;
			}

			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据给定的字符串，取固定长度，长度不够头部补0
	 * @param str
	 * @param length
	 * @return
	 */
	public static String fixStrStart(String str, Integer length) {
		char[] charArray = str.toCharArray();
		int len = charArray.length;
		StringBuilder result = new StringBuilder();
		for (int n = 0; n < length; n++) {
			if (n < len) {
				result.append(charArray[n]);
			} else {
				result.insert(0, "0");
			}
		}
		return result.toString();
	}

	/**
	 * 字符串长度处理类
	 * @param name 要处理字符串
	 * @param maxLength 最大字节长度
	 * @return
	 */
	public static String resolveNameLength(String name, int maxLength) {
		if (name == null) {
			return null;
		}
		//节目单名称长度处理
		int len = 0;

		try {
			len = new String(name.getBytes("utf-8")).getBytes().length;
			while (len > maxLength) {
				name = "" + name.substring(0, name.length() - 1);
				len = new String(name.getBytes("utf-8")).getBytes().length;
			}
		} catch (UnsupportedEncodingException e) {
			name = name.substring(0, 32) + "..";
		}

		return name;
	}

	public static String toString2(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	//获取请求参数：防止sql语句中'null'出现
	public static String getParam(String str) {
		if (str == null) {
			return "";
		}

		return str;
	}
	
	//获取请求参数：防止sql语句中'null'出现
	public static String getJsonStr(JSONObject jsonObj, String key) {
		if(jsonObj.containsKey(key)){
			return jsonObj.getString(key);
		}
		
		return null;
	}

	//获取返回字符串
	public static String getRetStr(String retCode, String retMsg, String charsetName, Object ... params) throws Exception {
		JSONObject retObj = new JSONObject();
		retObj.put("retCode", retCode);
    	retObj.put("retMsg", retMsg);
    	if(params!=null && params.length>0 && params.length%2==0){//key/value
    		for(int i=0;i<params.length/2;i++){
    			retObj.put(params[i*2], params[i*2+1]);
    		}
    	}
    	
        return URLDecoder.decode(retObj.toString(), charsetName);
	}
	
	//获取JSONObject
	public static JSONObject getJsonObj(Object ... params) {
		JSONObject jsonObj=new JSONObject();
		if(params!=null && params.length>0 && params.length%2==0){//key/value
    		for(int i=0;i<params.length/2;i++){
    			jsonObj.put(params[i*2], params[i*2+1]);
    		}
    	}
		
		return jsonObj;
	}
	
	/**
	 * 日期比较
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @return 开始时间大于等于结束时间返回true, 否则返回false
	 */
	public static boolean dateComparison(Date beginTime, Date endTime) {
		try {
			if (beginTime == null || endTime == null) {
				return false;
			}
			
			if (beginTime.getTime() >= endTime.getTime()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	//根据xml字符串得到参数map
  	public static Map<String,String> parseXml(String xmlStr){
  		try {
  			Document doc=DocumentHelper.parseText(xmlStr);
  			Element root=doc.getRootElement();//根节点
  			Map<String,String> paramMap=new ConcurrentHashMap<String,String>();
  			parseElement(paramMap,root,root.getName());
  			return paramMap;
  		} catch (Exception e) {
  			logger.error("根据xml字符串得到参数map时出错："+e);
  			return null;
  		}
  	}
  	
  	//根据xml字符串得到参数map
  	@SuppressWarnings("unchecked")
  	private static void parseElement(Map<String,String> paramMap,Element elt,String name){
  		name=(elt.isRootElement()?"":name+"/");//字段名不加根节点
  		List<Element> list=elt.elements();
  		if(list!=null && list.size()>0){
  			for(int i=0;i<list.size();i++){
  				Element elts=list.get(i);
  				List<Element> lists=elts.elements();
  				if(lists!=null && lists.size()>0){
  					parseElement(paramMap,elts,name+elts.getName());
  				}else{
  					paramMap.put(name+elts.getName(), elts.getText());
  				}
  			}
  		}
  	}
  	
  	//获取指定长度的随机字符串
    public static String getRandomStr(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        
        return sb.toString();
    }
    
    public static String getSign(Map<String,Object> map, String mchIdKey){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        
        String result = sb.toString();
        result += "key=" + mchIdKey;
        result = StringUtil.getMd5Str(result).toUpperCase();
        return result;
    }
    
    //将从API返回的XML数据映射到Java对象
    public static String getXMLFromObj(Object obj) {
    	XStream xStream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));//解决XStream对出现双下划线的bug
    	return xStream.toXML(obj);//将要提交给API的数据对象转换成XML格式数据Post给API
    }
    
    //将从API返回的XML数据映射到Java对象
    public static Object getObjFromXML(String xml, Class<?> clzz) {
    	XStream xStream = new XStream(new DomDriver());
    	xStream.alias("xml", clzz);
    	xStream.ignoreUnknownElements();//暂时忽略掉一些新增的字段
    	return xStream.fromXML(xml);
    }
    
    public static Map<String,Object> getParamMap(Object obj){
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
            	Object fieldValue = field.get(obj);
                if(fieldValue!=null && !StringUtils.isEmpty(String.valueOf(fieldValue))){
                    map.put(field.getName(), fieldValue);
                }
            } catch (Exception e) {}
        }
        
        return map;
    }
}