package com.wasu.dc.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wasu.pub.domain.ShowDomain;
import com.wasu.pub.domain.WsField;
import com.wasu.pub.util.CacheUtil;
import com.wasu.pub.util.ShowDomainUtil;
import com.wasu.pub.util.StringUtil;

public class SqlUtil {
  public static  String prepareCountHql(String hql) {
    String fromHql = hql;
    fromHql = "from" + StringUtils.substringAfter(fromHql, "from");
    fromHql = StringUtils.substringBefore(fromHql, "order by");
    int whereIndex = fromHql.indexOf("where");
    int leftIndex = fromHql.indexOf("left join");
    if (leftIndex >= 0) {
      if (whereIndex >= 0) {
        String temp = StringUtils.substringBefore(fromHql, "left");
        fromHql = temp + " where "
            + StringUtils.substringAfter(fromHql, "where");
      } else {
        fromHql = StringUtils.substringBefore(fromHql, "left");
      }
    }
    String countHql = "select count(*) " + fromHql;
    return countHql;
  }

  /**
   * 获取批量插入语句
   * @param datas
   * @param clzz
   * @return
   */
  public static String getBatchInsert(List datas,String clzz){
       if(datas==null){
         return null;
       }
       StringBuilder sb = new StringBuilder();
       ShowDomain sd = ShowDomainUtil.get(clzz);
       List<WsField> fields = sd.getFieldList();
       sb.append("insert into ").append(sd.getTableName()).append(" ( ");
       for(int i=0;i<fields.size();i++){
         WsField field = fields.get(i);
         if(field.isTransient()){
           continue;
         }
         if(i==0){
             sb.append(field.getDbFieldName());
         }else{
           sb.append(" , ").append(field.getDbFieldName());
         }
       }
       sb.append(" ) values ");
       for(int i=0;i<datas.size();i++){
         Object o =datas.get(i);
         if(i==0){
            sb.append(" (");
         }else{
           sb.append(",(");
         }
        //BeanUtils.
         for(int j=0;j<fields.size();j++){
           WsField field = fields.get(j);
           if(field.isTransient()){
             continue;
           }
           String fieldName = field.getFieldName();
           String value =StringUtil.toStringNullConvert( CacheUtil.get(clzz, o, fieldName));
           String ooSql="";
           if(value==null){
             ooSql="null";
           }else{
             ooSql="'"+value+"'";
           }
           if(j==0){
             
             sb.append(ooSql);
           }else{
             sb.append(" , ").append(ooSql);
           }
         }      
         sb.append(" ) ");
       }
       return sb.toString();
  }
  
}
