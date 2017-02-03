package com.wasu.bpp.wechat;

import java.io.Serializable;

import com.wasu.bpp.util.StringUtils;

@SuppressWarnings("serial")
public class BillDownReqData implements Serializable {
	//每个字段具体的意思请查看API文档
	private String mch_id;
	private String appid;
	private String nonce_str;
	private String sign;
	private String device_info;
	private String bill_date;
	private String bill_type;
	
	//不带参数构造函数
	public BillDownReqData(){}
	
	public BillDownReqData(String mchId, String mchId_appId, String deviceInfo, String billDate, String billType) {
		this.mch_id=mchId;//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
		this.appid=mchId_appId;//微信分配的公众号ID（开通公众号之后可以获取到）
		this.device_info=deviceInfo;//商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
		this.bill_date=billDate;//下载对账单的日期，格式：yyyyMMdd 例如：20140603
		this.bill_type=billType;//账单类型 ALL，返回当日所有订单信息，默认值 SUCCESS，返回当日成功支付的订单；REFUND，返回当日退款订单；REVOKED，已撤销的订单
	}

	//初始化属性，所有参数赋值后调用(nonce_str、sign除外)：mchId_key 微信支付分配的key（开通公众号之后可以获取到）
    public void initProp(String mchId_key){
    	this.nonce_str=StringUtils.getRandomStr(32);//随机字符串，不长于32 位
		this.sign=StringUtils.getSign(StringUtils.getParamMap(this), mchId_key);//根据API给的签名规则进行签名，并把签名数据设置到Sign这个属性中
    }
    
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getNonce_str() {
		return nonce_str;
	}
	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	
	public String getBill_date() {
		return bill_date;
	}
	public void setBill_date(String bill_date) {
		this.bill_date = bill_date;
	}

	public String getBill_type() {
		return bill_type;
	}
	public void setBill_type(String bill_type) {
		this.bill_type = bill_type;
	}
}