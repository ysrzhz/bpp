package com.wasu.bpp.wechat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.wasu.bpp.util.StringUtils;

//微信支付接口
@Controller
public class WechatPayCtrl {
	private static Logger logger = Logger.getLogger(WechatPayCtrl.class);
	@Value("${MCHID}")
	private String MCHID;
	@Value("${MCHID_APPID}")
	private String MCHID_APPID;
	@Value("${MCHID_KEY}")
	private String MCHID_KEY;
	@Value("${MCHID_CERTPATH}")
	private String MCHID_CERTPATH;
	@Value("${MCHID_CERTPASS}")
	private String MCHID_CERTPASS;
	@Resource
    private WechatPayService wps;
	
	//对账单下载
	public List<WechatBill> getWechatBill() {
		String billDate = "20170113";//银行退款账单拉取时间
		String billType = "ALL";//账单类型 ALL，返回当日所有订单信息，默认值 SUCCESS，返回当日成功支付的订单；REFUND，返回当日退款订单；REVOKED，已撤销的订单
		BillDownReqData bdrd = new BillDownReqData();//账单类型 ALL，返回当日所有订单信息，默认值 SUCCESS，返回当日成功支付的订单；REFUND，返回当日退款订单；REVOKED，已撤销的订单
		bdrd.setMch_id(MCHID);//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
		bdrd.setAppid(MCHID_APPID);//微信分配的公众号ID（开通公众号之后可以获取到）
		bdrd.setDevice_info(null);//商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
		bdrd.setBill_date(billDate);//下载对账单的日期，格式：yyyyMMdd 例如：20140603
		bdrd.setBill_type(billType);//账单类型 ALL，返回当日所有订单信息，默认值 SUCCESS，返回当日成功支付的订单；REFUND，返回当日退款订单；REVOKED，已撤销的订单
		String wechatBillStr = wps.getWechatBill(bdrd, MCHID_KEY);
		if (StringUtils.isEmpty(wechatBillStr)) {
			logger.info("无对账单");
			return null;
		}

		//解析获取到的数据
		List<WechatBill> list = new ArrayList<WechatBill>();
		String[] wechatBillStrs = wechatBillStr.split("\r\n");
		int indx = 1;//第1行和最后2行数据不是交易记录数据
		for (String billInfo : wechatBillStrs) {
			if (indx == 1 || indx > (wechatBillStrs.length - 2)) {
				continue;
			}

			String[] billInfos = billInfo.split(",");
			logger.info("微信获取账单，billType="+billType+"、length="+billInfos.length+"、billInfo="+billInfo);
			WechatBill wechatBill = new WechatBill();
			wechatBill.setTrade_time(billInfos[0].substring(1));//交易时间
			wechatBill.setAppid(billInfos[1].substring(1));//公众账号ID
			wechatBill.setMch_id(billInfos[2].substring(1));//商户号
			wechatBill.setDevice_info(billInfos[4].substring(1));//设备号
			wechatBill.setOpenid(billInfos[7].substring(1));//用户标识
			wechatBill.setBank_type(billInfos[10].substring(1));//付款银行
			if (billType != null && "REFUND".equals(billType.trim())) {//当日退款的订单
				//交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,退款申请时间,退款成功时间,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率
				wechatBill.setTransaction_id(billInfos[16].substring(1));//微信退款单号
				wechatBill.setOut_trade_no(billInfos[17].substring(1));//商户退款单号
				wechatBill.setTrade_type(billInfos[20].substring(1));//退款类型
				wechatBill.setTrade_state(billInfos[21].substring(1));//退款状态
				wechatBill.setTotal_fee(billInfos[18].substring(1));//退款金额
				wechatBill.setProduct_name(billInfos[22].substring(1));//商品名称
			} else {//当日成功支付的订单
				//交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,商品名称,商户数据包,手续费,费率
				wechatBill.setTransaction_id(billInfos[5].substring(1));//微信订单号
				wechatBill.setOut_trade_no(billInfos[6].substring(1));//商户订单号
				wechatBill.setTrade_type(billInfos[8].substring(1));//交易类型
				wechatBill.setTrade_state(billInfos[9].substring(1));//交易状态
				wechatBill.setTotal_fee(billInfos[12].substring(1));//总金额
				wechatBill.setProduct_name(billInfos[14].substring(1));//商品名称
			}

			list.add(wechatBill);
			indx++;//行数+1
		}
		
		return list;
	}
	
	//微信退款
	public void wechatRefund(){
		WechatRefundReqData wrrd=new WechatRefundReqData();
		wrrd.setMch_id(MCHID);//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
		wrrd.setAppid(MCHID_APPID);//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
		wrrd.setDevice_info(null);//商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
		wrrd.setTransaction_id(null);//微信订单号
		wrrd.setOut_trade_no("1");//商户订单号，本地订单号
		wrrd.setOut_refund_no("p123456");//商户退款单号，即原微信支付订单号
		wrrd.setTotal_fee(1);//总金额
		wrrd.setRefund_fee(1);//退款金额
		wrrd.setOp_user_id(MCHID);//操作员帐号, 默认为商户号
		Map<String,String> paramMap=wps.wechatRefund(wrrd, MCHID_KEY, MCHID_CERTPATH, MCHID_CERTPASS);//微信退款
		if(paramMap == null){
			logger.error("微信退款失败，请重试");
			return;
		}else if(!"SUCCESS".equals((String)paramMap.get("return_code"))){
			logger.error("微信退款失败，错误信息为："+(String)paramMap.get("return_message"));
			return;
		}else if(!"SUCCESS".equals((String)paramMap.get("result_code"))){
			logger.error("微信退款失败，错误信息为："+(String)paramMap.get("result_message"));
			return;
		}
		
		logger.info("微信退款成功，退款单号为："+(String)paramMap.get("refund_id"));
	}
}