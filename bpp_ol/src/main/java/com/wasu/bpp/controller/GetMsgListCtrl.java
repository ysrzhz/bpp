package com.wasu.bpp.controller;

import com.wasu.bpp.dao.MmsMsgDao;
import com.wasu.bpp.util.StringUtils;
import com.wasu.pub.util.StringUtil;
import com.wasu.sid.bpp.MmsMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//消息状态列表查询接口
@Controller
public class GetMsgListCtrl {
    private static Logger logger = Logger.getLogger(GetMsgListCtrl.class);
    private static final String ENCODING = StandardCharsets.UTF_8.toString();
    @Resource
    private MmsMsgDao mmsMsgDao;
    
    //消息状态列表查询
    @RequestMapping(value = "/getMsgList",produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String getMsgList(HttpServletRequest req) throws Exception {
        BufferedReader br = null;
        try {
			br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
             
			String reqStr = URLDecoder.decode(sb.toString(), ENCODING);//解码
            logger.info("请求参数：" + reqStr);
            Map<String, String> paramMap = getReqParam(JSONObject.fromObject(reqStr));//获取请求参数
			String errMsg = checkParam(paramMap);//校验必填参数
			if (errMsg != null) {
				logger.error("请求参数有误[" + reqStr + "]：" + errMsg);
	            return StringUtils.getRetStr("-1", "消息状态列表查询失败："+errMsg, ENCODING);
			}
			
			return getMsgList(paramMap);//消息状态列表查询
        } catch (Exception e) {
        	logger.error("消息状态列表查询失败："+e);
        	return StringUtils.getRetStr("-1", "消息状态列表查询失败："+e, ENCODING);
        }finally {
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                	br=null;
                }
            }
        }
    }
    
    //消息状态列表查询
    public String getMsgList(Map<String, String> paramMap) throws Exception {
    	List<MmsMsg> list=mmsMsgDao.getMsgStatusList(paramMap.get("msgIds"), paramMap.get("dataSrc"));//消息状态列表查询
    	if(list==null || list.size()==0){
    		return StringUtils.getRetStr("-1", "没有满足条件的记录", ENCODING);
    	}
    	
    	JSONArray retJsonArr=new JSONArray();
    	for(MmsMsg mmsMsg: list){
    		JSONObject retMsgObj=new JSONObject();
            retMsgObj.put("msgId", String.valueOf(mmsMsg.getId()));
            retMsgObj.put("status", mmsMsg.getStatus());
            retJsonArr.add(retMsgObj);;
    	}
    	
    	return StringUtils.getRetStr("0", "消息状态列表查询成功", ENCODING, "msgList", retJsonArr);
    }
    
    private Map<String, String> getReqParam(JSONObject jsonObj) {
    	Map<String, String> paramMap=new HashMap<String, String>();
    	paramMap.put("searchType", StringUtils.getJsonStr(jsonObj, "searchType"));
    	paramMap.put("msgIds", StringUtils.getJsonStr(jsonObj, "msgIds"));
    	paramMap.put("dataSrc", StringUtils.getJsonStr(jsonObj, "dataSrc"));
    	paramMap.put("encoding", ENCODING);
		return paramMap;
	}
    
    private String checkParam(Map<String, String> paramMap) {
    	String searchType=paramMap.get("searchType");
		if (StringUtil.isBlank(searchType)) {
			return "searchType can not be null";
		}
		
		if (!searchType.matches("^[1]$")) {
			return "searchType must be 1";
		}
		
		String msgIds=paramMap.get("msgIds");
		if (StringUtil.isBlank(msgIds)) {
			return "msgIds can not be null";
		}
		
		if(msgIds.split(",").length>1000){
			return "num of msgIds can not be more than 1000";
		}
		
		if (StringUtil.isBlank(paramMap.get("dataSrc"))) {
			return "dataSrc can not be null";
		}
    	
    	if (!paramMap.get("dataSrc").matches("^[1-4]$")) {
			return "dataSrc must be 1 or 2 or 3 or 4";
		}
		
		return null;
	}
}