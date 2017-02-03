package com.wasu.bpp.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.wasu.pub.service.DBCenter;
import com.wasu.bpp.dao.MmsMsgDao;
import com.wasu.bpp.queue.NotifyMsgStatusQueue;
import com.wasu.bpp.util.HttpUtil;
import com.wasu.pub.util.StringUtil;
import com.wasu.sid.bpp.MmsMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetMsgListTask {
	private static Logger logger = Logger.getLogger(GetMsgListTask.class);
	private static final String JSONSTR="{\"header\": {\"commond\": \"BUSINESSMSGLIST\"}, \"body\": {}}";
	private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	@Resource
    private MmsMsgDao mmsMsgDao;
	@Resource
	private DBCenter dbCenter;
	
	//调用消息列表查询接口配置
	@Value("${XMPP_MSGURL}")
	private String XMPP_MSGURL;
	@Value("${XMPP_SYSTEMCODE}")
	private String XMPP_SYSTEMCODE;
	@Value("${XMPP_SYSTEMVERCODE}")
	private String XMPP_SYSTEMVERCODE;
	@Value("${XMPP_SEARCHTYPE}")
	private String XMPP_SEARCHTYPE;//查询类型：0-分页查询；1-按消息ID查询
	@Value("${XMPP_QRYNUM}")
	private long XMPP_QRYNUM;//查询消息ID数

	public void execute() throws Exception {
		try {
			long qryBeginPos=0L;
			while (true) {
				List<MmsMsg> list=mmsMsgDao.getStatusSyncList(qryBeginPos, XMPP_QRYNUM);//状态同步列表查询
	    		if(list==null || list.size()==0){
	    			break;//退出循环
	    		}
	    		
	    		Map<Long, String> dataSrcMap=new HashMap<Long, String>();
	      		StringBuffer sb=new StringBuffer();
	      		for(MmsMsg mmsMsg: list){
	      			dataSrcMap.put(mmsMsg.getId(), mmsMsg.getDataSrc());
	      			if(sb.length()!=0) sb.append(",");
	      			sb.append(mmsMsg.getId());
	      		}
	      		
				getMsgList(dataSrcMap, sb.toString());//调用消息列表查询接口
				if(list.size()<XMPP_QRYNUM){//记录数小于XMPP_QRYNUM，即没有未处理的记录
					break;//退出循环
				}
				
				qryBeginPos+=XMPP_QRYNUM;
			}
		} catch (Exception e) {
			logger.error("调用消息列表查询接口失败：" + e);
		}
    }
	
	//调用消息列表查询接口
	private void getMsgList(Map<Long, String> dataSrcMap, String msgIds) {
		JSONObject xmppObj=JSONObject.fromObject(JSONSTR);//返回json对象
		JSONObject xmppBodyObj=xmppObj.getJSONObject("body");//body json对象
		xmppBodyObj.put("systemCode", XMPP_SYSTEMCODE);
		xmppBodyObj.put("systemVerCode", StringUtil.getMd5Str(XMPP_SYSTEMCODE+XMPP_SYSTEMVERCODE).substring(8, 24));
		xmppBodyObj.put("searchType", XMPP_SEARCHTYPE);//查询类型：0-分页查询；1-按消息ID查询
		xmppBodyObj.put("msgIds", msgIds);
		String jsonStr=HttpUtil.sendHttpPost(XMPP_MSGURL, xmppObj.toString());//调用XMPP消息列表查询接口发送post请求(JSON)
		JSONObject jsonObj=null;//转换时已丢弃跟节点oss-response
		JSONObject bodyObj=null;
		if(StringUtils.isBlank(jsonStr) || (jsonObj=JSONObject.fromObject(jsonStr))==null || jsonObj.isNullObject() || (bodyObj=jsonObj.getJSONObject("body"))==null){//接口返回错误
			logger.error("调用消息列表查询接口时错误："+jsonStr);
			return;
		}
		
		if(!"0".equals(bodyObj.getString("result"))){//接口返回错误
			logger.error("调用消息列表查询接口时错误："+bodyObj.getString("desc"));
			return;
		}
		
		//消息列表处理
		JSONArray jsonArr=bodyObj.getJSONArray("mlist");
		if(jsonArr==null || jsonArr.size()==0){
			return;
		}
		
		Map<String, JSONArray> retMap=new HashMap<String, JSONArray>();
		for(Object obj: jsonArr){
			handleMsg((JSONObject)obj, retMap, dataSrcMap);//消息处理
		}
		
		if(retMap!=null && retMap.size()>0){
			Iterator<String> it=retMap.keySet().iterator();
	        while(it.hasNext()){
	        	String dataSrc=it.next();
	        	NotifyMsgStatusQueue.putMsgStatus(dataSrc, retMap.get(dataSrc));//加入通知消息状态队列
	        }
        }
    }
    
    //消息处理
    private void handleMsg(JSONObject msgObj, Map<String, JSONArray> retMap, Map<Long, String> dataSrcMap) {
    	String status=getStatus(msgObj.getInt("state"));//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
		Map<String, String> paramMap=new HashMap<String, String>();
		paramMap.put("status", status);//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
		paramMap.put("lastUpdateTime", new SimpleDateFormat(yyyyMMddHHmmss).format(new Date()));
        dbCenter.update(MmsMsg.class.getName(), msgObj.getLong("omid"), paramMap);//更新状态
        JSONObject retMsgObj=new JSONObject();
        retMsgObj.put("msgId", msgObj.getString("omid"));
        retMsgObj.put("status", status);
        String dataSrc=dataSrcMap.get(msgObj.getLong("omid"));
        if(!"0".equals(dataSrc)){//数据来源：0-MMS；1-用户中心；2-智能推荐系统；3-酒店管理系统；4-终端升级
        	JSONArray jsonArr=retMap.get(dataSrc);
            if(jsonArr==null){
            	jsonArr=new JSONArray();
            	retMap.put(dataSrc, jsonArr);
            }
            
            jsonArr.add(retMsgObj);
        }
    }
    
    //获取消息状态
    private String getStatus(int state) {
    	String status=String.valueOf(state);
    	if(state>=0 && state<=4){//0-发送成功;1-发送失败;2-已达;3-已读;4-已删除
    		status=(state==0?"3":String.valueOf(state+1));//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
    	}
    	
    	return status;
    }
}