package com.wasu.bpp.wechat;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.io.StreamException;
import com.wasu.bpp.util.HttpUtil;
import com.wasu.bpp.util.StringUtils;

//微信支付服务
@Service
public class WechatPayService {
	private static Logger logger = Logger.getLogger(WechatPayService.class);
	@Value("${REFUND_FEE_TYPE}")
	private String REFUND_FEE_TYPE;
	@Value("${DOWNLOAD_BILL_API}")
	private String DOWNLOAD_BILL_API;
	@Value("${REFUND_API}")
	private String REFUND_API;

	//对账单下载
	public String getWechatBill(BillDownReqData bdrd, String mchId_key) {
		try {
			if (bdrd==null || StringUtils.isEmpty(bdrd.getMch_id()) || StringUtils.isEmpty(bdrd.getAppid())) {
				logger.error("对账单API系统返回失败，无请求参数或商户号为空或公众号为空");
				return null;
			}
			
			//向微信支付发起查询请求
			bdrd.initProp(mchId_key);//初始化属性，所有参数赋值后调用(nonce_str、sign除外)
			String xmlStr = StringUtils.getXMLFromObj(bdrd);//将要提交给API的数据对象转换成XML格式数据Post给API
        	logger.info("API，POST过去的数据是："+xmlStr);
        	String retXmlStr = HttpUtil.sendHttpsPost(DOWNLOAD_BILL_API, xmlStr);
			if (StringUtils.isEmpty(retXmlStr)) {
				logger.info("获取对账单为空");
				return null;
			}
			
			try{
				RetObj retObj = (RetObj) StringUtils.getObjFromXML(retXmlStr, RetObj.class);
				if (retObj == null || retObj.getReturn_code() == null) {
					logger.error("对账单API请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
					return null;
				}
				
				if ("FAIL".equals(retObj.getReturn_code())) {//注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
					logger.error("对账单API系统返回失败，请检测Post给API的数据是否规范合法");
					return null;
				}
			} catch (StreamException e) {//成功返回, 返回交易记录：这里成功的时候是直接返回纯文本的对账单文本数据，非XML格式
			}
			
			return retXmlStr;
		} catch (Exception e) {
			logger.error("获取对账单时错误："+e);
			return null;
		}
	}

	//微信退款
	public Map<String, String> wechatRefund(WechatRefundReqData wrrd, String mchId_key, String mchId_certPath, String mchId_certPass) {
		try {
			if (wrrd==null || StringUtils.isEmpty(wrrd.getMch_id()) || StringUtils.isEmpty(wrrd.getAppid())) {
				logger.error("对账单API系统返回失败，无请求参数或商户号为空或公众号为空");
				return null;
			}
			
			//向微信支付发起退款
			wrrd.initProp(mchId_key, REFUND_FEE_TYPE);//初始化属性，所有参数赋值后调用(nonce_str、sign、refund_fee_type除外)
    		String xmlStr = StringUtils.getXMLFromObj(wrrd);//将要提交给API的数据对象转换成XML格式数据Post给API
    		String retXmlStr = HttpUtil.sendHttpsCertPost(REFUND_API, xmlStr, mchId_certPath, mchId_certPass);//向微信支付发起退款申请
			logger.info("微信退款返回xml字符串[" + retXmlStr + "]");
			if (StringUtils.isEmpty(retXmlStr)) {
				logger.error("微信退款API请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
				return null;
			}

			logger.info("微信退款返回成功");
			return StringUtils.parseXml(retXmlStr);//根据xml字符串得到参数map
		} catch (Exception e) {
			logger.error("微信退款时出错：" + e);
			return null;
		}
	}
}