package com.wasu.bpp.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.wasu.pub.service.DBCenter;
import com.wasu.bpp.domain.QueueBean;
import com.wasu.bpp.queue.NotifyMsgStatusQueue;
import com.wasu.bpp.queue.SendMsgQueue;
import com.wasu.bpp.util.HttpUtil;
import com.wasu.pub.util.StringUtil;
import com.wasu.sid.bpp.MmsMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SendMsgTask {
	private static Logger logger = Logger.getLogger(SendMsgTask.class);
	private static final String JSONSTR="{\"header\": {\"commond\": \"MSGPUSH\"}, \"body\": {}}";
	private static final String KEYNAME="stbid";//groupid
	private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	@Resource
	DBCenter dbCenter;
	
	//调用消息发送接口配置
	@Value("${XMPP_MSGURL}")
	private String XMPP_MSGURL;
	@Value("${XMPP_SYSTEMCODE}")
	private String XMPP_SYSTEMCODE;
	@Value("${XMPP_SYSTEMVERCODE}")
	private String XMPP_SYSTEMVERCODE;
	@Value("${XMPP_MTYPE}")
	private String XMPP_MTYPE;
	@Value("${XMPP_SENDTYPE}")
	private String XMPP_SENDTYPE;
	@Value("${XMPP_PRIORITY}")
	private String XMPP_PRIORITY;
	@Value("${XMPP_SENDSTBNUM}")
	private long XMPP_SENDSTBNUM;//消息发送到XMPP的stb个数：0-不限制
	
	//用户中心终端列表查询接口地址
	@Value("${IUC_STBIDURL}")
	private String IUC_STBIDURL;
	@Value("${IUC_SHOWNUM}")
	private long IUC_SHOWNUM;//用户中心每页显示条数

	public void execute() throws Exception {
		try {
			while (true) {
				PriorityBlockingQueue<QueueBean> queue = SendMsgQueue.getQueue();
				QueueBean bean = queue.poll(2, TimeUnit.SECONDS);//取出一个QueueBean(获取并移除此队列的头部)
				if (bean==null) {//bean为空
					break;//退出循环
				}
				
				if (!StringUtil.dateComparison(new Date(), bean.getSendTime())) {//发送时间>当前时间
					SendMsgQueue.add(bean);//重新加入队列
					break;//退出循环
				}
				
				sendMsg(bean);//发送XMPP消息
			}
		} catch (InterruptedException e) {
			logger.error("poll QueueBean from queue occur Exception" + e.getMessage());
		} catch (Exception e) {
			logger.error("发送XMPP消息失败：" + e.getMessage());
		}
    }
    
	//发送XMPP消息
	private void sendMsg(QueueBean bean) throws Exception{
		try {
	    	JSONArray jsonArr=new JSONArray();//消息接收者
	    	Map<String, String> isExistMap=new HashMap<String, String>();//用于判断区域编号或客户编号下的stbId是否存在
	    	if(StringUtils.isNotBlank(bean.getStbId())){//机顶盒编号不为空
	    		String[] stbIds=bean.getStbId().split(",");
	    		jsonArr=JSONArray.fromObject(stbIds);//消息接收者
		    	if(StringUtils.isNotBlank(bean.getAreaId()) || StringUtils.isNotBlank(bean.getCustId())){//区域编号或客户编号不为空
		    		for(String stbId: stbIds){
			    		isExistMap.put(stbId, null);//用于判断区域编号或客户编号下的stbId是否存在
			    	}
		    	}
	    	}
	    	
	    	bean.setSubId(1L);//子消息编号
	    	bean.setStatus("1");//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
	    	if(StringUtils.isNotBlank(bean.getAreaId())){//区域编号不为空
	    		String[] areaIds=bean.getAreaId().split(",");
	    		for(String areaId: areaIds){
	    			handleAreaCust(areaId, null, jsonArr, isExistMap, bean);//获取区域下的机顶盒列表
		    	}
	    	}
	    	
	    	if(StringUtils.isNotBlank(bean.getCustId())){//客户编号不为空
	    		String[] custIds=bean.getCustId().split(",");
	    		for(String custId: custIds){
	    			handleAreaCust(null, custId, jsonArr, isExistMap, bean);//获取客户下的机顶盒列表
		    	}
	    	}
	    	
	    	sendXmppMsg(bean, jsonArr);//发送XMPP消息
	    	updateStatus(bean.getDataSrc(), bean.getId(), bean.getStatus());//更新状态
		} catch (Exception e) {
			updateStatus(bean.getDataSrc(), bean.getId(), "2");//更新状态，发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			logger.error("发送XMPP消息失败：" + e.getMessage());
		}
    }
    
    //获取区域下的机顶盒列表
	private void handleAreaCust(String areaId, String custId, JSONArray jsonArr, Map<String, String> isExistMap, QueueBean bean) throws Exception{
		long currPage=0;//当前页
		while(true){//循环调用终端列表查询接口
			JSONObject retObj=HttpUtil.getStbList(IUC_STBIDURL, areaId, custId, ++currPage, IUC_SHOWNUM);//调用终端列表查询接口
			if(retObj==null || retObj.isNullObject()){
				break;//退出循环
			}
			
			JSONArray retArr=retObj.getJSONArray("results");
			if(retArr!=null && retArr.size()>0){
				for(Object obj: retArr){
					if(obj instanceof JSONObject){
						JSONObject jsonObj=(JSONObject)obj;
						if(!isExistMap.containsKey(jsonObj.getString("bill_id"))){//区域编号或客户编号下的stbId不存在
							jsonArr.add(jsonObj.getString("bill_id"));
							if(XMPP_SENDSTBNUM!=0 && jsonArr.size()>=XMPP_SENDSTBNUM){//条数大于等于消息发送到XMPP的stb个数：0-不限制
								sendXmppMsg(bean, jsonArr);//发送XMPP消息
								jsonArr.clear();//清空消息接收者
								bean.setSubId(bean.getSubId()+1);
								bean.setHasSend("1");//是否已发送到xmpp(多条发送)：0-否；1-是
							}
						}
					}
				}
			}
			
			if(currPage>=retObj.getLong("pageCount")){//当前页大于等于总页数
				break;//退出循环
			}
		}
    }
	
	//发送XMPP消息
	private void sendXmppMsg(QueueBean bean, JSONArray jsonArr) {
		if(!"1".equals(bean.getHasSend()) && jsonArr.size()==0){//是否已发送到xmpp(多条发送)：0-否；1-是；机顶盒、区域及客户都为空；机顶盒为空、区域及客户下的机顶盒列表都为空
			bean.setStatus("2");//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			return;
		}
		
		if(jsonArr.size()>0){
			JSONObject xmppObj=JSONObject.fromObject(JSONSTR);//返回json对象
			JSONObject xmppBodyObj=xmppObj.getJSONObject("body");//body json对象
			xmppBodyObj.put("systemCode", XMPP_SYSTEMCODE);
			xmppBodyObj.put("systemVerCode", StringUtil.getMd5Str(XMPP_SYSTEMCODE+XMPP_SYSTEMVERCODE).substring(8, 24));
			xmppBodyObj.put("omid", String.valueOf(bean.getId()));
			xmppBodyObj.put("mtype", XMPP_MTYPE);
			xmppBodyObj.put("vtype", bean.getVtype());
			xmppBodyObj.put("time", new SimpleDateFormat(yyyyMMddHHmmss).format(bean.getSendTime()));
			xmppBodyObj.put("sendtype", XMPP_SENDTYPE);//发送类型：01-点对点消息；02-群组消息
			xmppBodyObj.put("param", StringUtil.getJsonObj(KEYNAME, jsonArr));//获取消息接收者
			xmppBodyObj.put("priority", XMPP_PRIORITY);
			xmppBodyObj.put("title", bean.getTitle());
			xmppBodyObj.put("content", bean.getContent()+" ");//加空格，不加空格会把数组字符串转成JSONArray
			xmppBodyObj.put("validtime", new SimpleDateFormat(yyyyMMddHHmmss).format(bean.getValidTime()));
			xmppBodyObj.put("serialno", bean.getId()+"_"+bean.getSubId());
			xmppBodyObj.put("scope", bean.getScope());
			//额外附加字段
			xmppBodyObj.put("imgPath", "");
			xmppBodyObj.put("linkPath", "");
			xmppBodyObj.put("sender", "");
			xmppBodyObj.put("senderName", "");
			xmppBodyObj.put("isMark", "");
			xmppBodyObj.put("isPlaceTop", "");
			xmppBodyObj.put("markValidTime", "");
			xmppBodyObj.put("scopeLocation", "");
			String jsonStr=HttpUtil.sendHttpPost(XMPP_MSGURL, xmppObj.toString());//调用XMPP消息发送接口发送post请求(JSON)
			JSONObject jsonObj=null;//转换时已丢弃跟节点oss-response
			JSONObject bodyObj=null;
			if(StringUtils.isBlank(jsonStr) || (jsonObj=JSONObject.fromObject(jsonStr))==null || jsonObj.isNullObject() || (bodyObj=jsonObj.getJSONObject("body"))==null){//接口返回错误
				bean.setStatus("2");//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
				logger.error("发送XMPP消息时错误："+jsonStr);
				return;
			}
			
			if(!"0".equals(bodyObj.getString("result"))){//接口返回错误
				bean.setStatus("2");//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
				logger.error("发送XMPP消息时错误："+bodyObj.getString("desc"));
			}
		}
    }
		
    //更新状态
    private void updateStatus(String dataSrc, Long id, String status) {
		Map<String, String> paramMap=new HashMap<String, String>();
		paramMap.put("status", status);
		paramMap.put("lastUpdateTime", new SimpleDateFormat(yyyyMMddHHmmss).format(new Date()));
        dbCenter.update(MmsMsg.class.getName(), id, paramMap);//更新状态
        if(!"0".equals(dataSrc)){//调用状态同步接口，数据来源：0-MMS；1-用户中心；2-智能推荐系统；3-酒店管理系统；4-终端升级
        	NotifyMsgStatusQueue.putMsgStatus(dataSrc, JSONArray.fromObject("[{\"msgId\":\""+id+"\",\"status\":\""+status+"\"}]"));//加入通知消息状态队列
        }
    }
}