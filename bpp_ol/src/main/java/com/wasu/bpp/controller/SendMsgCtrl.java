package com.wasu.bpp.controller;

import com.wasu.bpp.dao.MmsMsgDao;
import com.wasu.bpp.domain.MsgRequest;
import com.wasu.bpp.util.StringUtils;
import com.wasu.pub.util.StringUtil;

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

//消息发送接口
@Controller
public class SendMsgCtrl {
    private static Logger logger = Logger.getLogger(SendMsgCtrl.class);
    private static final String ENCODING = StandardCharsets.UTF_8.toString();
    @Resource
    private MmsMsgDao mmsMsgDao;
    
    //消息发送
    @RequestMapping(value = "/sendMsg",produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String sendMsg(HttpServletRequest req) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line=null;
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            
            String reqStr = URLDecoder.decode(sb.toString(), ENCODING);//解码
            logger.info("请求参数：" + reqStr);
            MsgRequest msgReq = getReqParam(JSONObject.fromObject(reqStr));//获取请求参数
			String errMsg = checkParam(msgReq);//校验必填参数
			if (errMsg != null) {
				logger.error("请求参数有误[" + msgReq.toString() + "]：" + errMsg);
	            return StringUtils.getRetStr("-1", "消息发送失败："+errMsg, ENCODING);
			}
            
			return mmsMsgDao.sendMsg(msgReq);//消息发送
        } catch (Exception e) {
        	logger.error("消息发送失败："+e);
        	return StringUtils.getRetStr("-1", "消息发送失败："+e, ENCODING);
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
    
    private MsgRequest getReqParam(JSONObject jsonObj) {
    	MsgRequest req = new MsgRequest();
		req.setTitle(StringUtils.getJsonStr(jsonObj, "title"));
		req.setSendTime(StringUtils.getJsonStr(jsonObj, "sendTime"));
		req.setContent(StringUtils.getJsonStr(jsonObj, "content"));
		req.setStbId(StringUtils.getJsonStr(jsonObj, "stbId"));
		req.setAreaId(StringUtils.getJsonStr(jsonObj, "areaId"));
		req.setCustId(StringUtils.getJsonStr(jsonObj, "custId"));
		req.setCronExpr(StringUtils.getJsonStr(jsonObj, "cronExpr"));
		req.setValidTime(StringUtils.getJsonStr(jsonObj, "validTime"));
		req.setScope(StringUtils.getJsonStr(jsonObj, "scope"));
		req.setVtype(StringUtils.getJsonStr(jsonObj, "vtype"));
		req.setDataSrc(StringUtils.getJsonStr(jsonObj, "dataSrc"));
		req.setEncoding(ENCODING);
		return req;
	}
    
    private String checkParam(MsgRequest req) {
		if (StringUtil.isBlank(req.getSendTime())) {
			return "sendTime can not be null";
		}
		
		if (StringUtil.isBlank(req.getContent())) {
			return "content can not be null";
		}
		
		if (StringUtil.isBlank(req.getStbId()) && StringUtil.isBlank(req.getAreaId()) && StringUtil.isBlank(req.getCustId())) {
			return "stbId or areaId or custId can not all be null";
		}
		
		if (StringUtil.isBlank(req.getValidTime())) {
			return "validTime can not be null";
		}
		
		if (StringUtil.isBlank(req.getScope())) {
			return "scope can not be null";
		}
		
		if (!req.getScope().matches("^[0-3]$")) {
			return "scope must be 0 or 1 or 2 or 3";
		}
    	
    	if (StringUtil.isBlank(req.getVtype())) {
			return "vtype can not be null";
		}
    	
    	if (!req.getVtype().matches("^[0-2]$")) {
			return "vtype must be 0 or 1 or 2";
		}
    	
    	if (StringUtil.isBlank(req.getDataSrc())) {
			return "dataSrc can not be null";
		}
    	
    	if (!req.getDataSrc().matches("^[1-4]$")) {
			return "dataSrc must be 1 or 2 or 3 or 4";
		}

		return null;
	}
}