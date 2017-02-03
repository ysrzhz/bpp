package com.wasu.bpp.wechat;

import java.io.Serializable;

import com.wasu.bpp.util.StringUtils;

@SuppressWarnings("serial")
public class WechatRefundReqData implements Serializable{
	private String mch_id;//商户号
	private String appid;//公众账号ID
	private String device_info;//商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
	private String nonce_str;//随机字符串
	private String sign;//签名
	private String transaction_id;//微信订单号
	private String out_trade_no;//商户订单号，本地订单号
	private String out_refund_no;//商户退款单号，即原微信支付订单号
	private int total_fee;//总金额
	private int refund_fee;//退款金额
	private String refund_fee_type;//货币种类，默认人民币：CNY
	private String op_user_id;//操作员
    
	//不带参数构造函数
	public WechatRefundReqData(){}
	
	//带所有参数的构造函数(nonce_str、sign除外)
    public WechatRefundReqData(String mch_id, String appid, String device_info, String transaction_id, String out_trade_no, String out_refund_no, int total_fee, int refund_fee, String op_user_id){
    	this.mch_id=mch_id;//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
    	this.appid=appid;//微信分配的公众号ID（开通公众号之后可以获取到）
        this.device_info=device_info;//商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
    	this.transaction_id=transaction_id;//微信订单号
    	this.out_trade_no=out_trade_no;//商户订单号
    	this.out_refund_no=out_refund_no;//商户退款单号
    	this.total_fee=total_fee;//总金额
    	this.refund_fee=refund_fee;//退款金额
    	this.op_user_id=op_user_id;//操作员
    }
    
    //初始化属性，所有参数赋值后调用(nonce_str、sign、refund_fee_type除外)
    public void initProp(String mchId_key, String refund_fee_type){
        this.nonce_str=StringUtils.getRandomStr(32);//随机字符串，不长于32位
        this.sign=StringUtils.getSign(StringUtils.getParamMap(this), mchId_key);//根据API给的签名规则进行签名，并把签名数据设置到Sign这个属性中
    	this.refund_fee_type=StringUtils.isEmpty(refund_fee_type)?"CNY":refund_fee_type;//货币种类，默认人民币：CNY
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

	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
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

	public String getTransaction_id() {
		return transaction_id;
	}
	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}
	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public int getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}

	public int getRefund_fee() {
		return refund_fee;
	}
	public void setRefund_fee(int refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getRefund_fee_type() {
		return refund_fee_type;
	}
	public void setRefund_fee_type(String refund_fee_type) {
		this.refund_fee_type = refund_fee_type;
	}

	public String getOp_user_id() {
		return op_user_id;
	}
	public void setOp_user_id(String op_user_id) {
		this.op_user_id = op_user_id;
	}
}