package com.wasu.pub.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * 对象工具类
 * 
 */
public class ObjectUtil {
	/**
	 * 对象是否为空判断
	 * @param obj
	 * @return 是否为空布尔值
	 * */
	public static boolean  isEmptyOrNull(Object o) {
		if (o == null||o.equals("")) {
			return true;
		}
		return false;
	}
	

    public static boolean isEmpty(List<?> list)
    {
        return (list == null || list.isEmpty());
    }
    
    public static boolean isEmpty(Map map)
    {
        return (map == null || map.isEmpty());
    }
    
    public static boolean isEmpty(Set set)
    {
        return set == null || set.isEmpty();
    }
    
    public static boolean isEmpty(String value)
    {
        return (value == null || "".equals(value.trim())||"null".endsWith(value));
    }
    
    public static boolean isEmpty(Object value)
    {
        return (value == null);
    }
    
    public static boolean isEmpty(Long value)
    {
        return (value == null);
    }
    
    public static boolean isEmpty(Integer value)
    {
        return (value == null);
    }
    
    public static boolean isEmpty(String[] arrValue)
    {
        return (arrValue == null || arrValue.length == 0);
    }
    
    public static boolean isEmpty(Object[] arrObject)
    {
        return (arrObject == null || arrObject.length == 0);
    }
    
    public static String substituteParams(String msgtext, Object params[]) {
        if (params == null || msgtext == null) {
            return msgtext;
        }
        MessageFormat mf = new MessageFormat(msgtext);
        return mf.format(params);
    }
    
       /**
     * 判断一个ID成员是否在另外一个List内
     * @param sourceIds
     * @param targetIds
     * @return 返回 比较成员中不存在目标成员中的List集合
     */
    public static List<Long> compareIdData(List<Long> sourceIds,List<Long> targetIds){
        if(isEmpty(sourceIds)){
            return null;
        }
        if(isEmpty(targetIds)){
            return sourceIds;
        }
        
        List<Long> returnIds = new ArrayList<Long>();
        for(Long sourceId : sourceIds){
            boolean result = false;//默认对象不存在令一个集合中
            for(Long targetId : targetIds){
                if(sourceId.intValue() == targetId.intValue()){
                    result = true;
                    break;
                }
            }
            if(!result){
                returnIds.add(sourceId);
            }
        }
        return returnIds;
    }
    
    
    public static byte[] serialize(Object object) throws Exception {
        if (object == null) {
          return null;
        }
        ByteArrayOutputStream baos  = null;
        Hessian2Output ho = null;
        try{
        	baos=new ByteArrayOutputStream();
        	ho = new Hessian2Output(baos);
	        ho.writeObject(object);
	        ho.flushBuffer();
	        byte[] bytes = baos.toByteArray();
	        return bytes;
        }finally{
        	if(ho != null){
        		ho.close();
        	}
        	if(baos != null){
        		baos.close();
        	}
        }
      }

      public static Object unserialize(byte[] bytes) throws Exception {
        if (bytes == null) {
          return null;
        }
        ByteArrayInputStream bais =null;
    	Hessian2Input hi = null;
        try{
        	
        	bais = new ByteArrayInputStream(bytes);
        	hi = new Hessian2Input(bais);
        	return hi.readObject();
        }finally{
        	if(hi != null){
        		hi.close();
        	}
        	if(bais != null){
        		bais.close();
        	}
        }
      }
}