package com.wasu.pub.util;

public class Constant {
	
	//分号.不具体到业务，以便共用
    public static final String SEMICOLON_SEPARATOR = ";";
    //冒号:不具体到业务，以便共用
    public static final String COLON_SEPARATOR = ":";
    //逗号,不具体到业务，以便共用
    public static final String COMMA_SEPARATOR = ",";
    //x
  	public static final String X_LINE = "x";
  	
  	public static final String SEPARATOR="/";
	//下划线
	public static final String UNDER_LINE = "_";
	

	//m3u8后缀
	public static final String SUFFIX_M3U8 = ".m3u8";

	//m2u8后缀
	public static final String SUFFIX_MP4 = ".mp4";
	
	//
	public static final String FORMAT_MP4 = "MP4";

	//
	public static final String FORMAT_TS = "TS";
	
	
	public enum ParamKey {
		
		//海报Key
		TERMINAL_TYPE("terminalTypeParams"),
		POSTER_SIZE("posterSize"),
		POSTER_CONVERT("posterConvert"),
		POSTER_SERVER_URL("poster-server-url"),
		
		
		
		CDNURL("cdnurl"),
		PosterRoot("PostRoot"),
		MAX_DATE_LENGTH("MAX_DATE"),
		MAX_TIME_LENGTH("MAX_TIME"),
		MIN_TIME_LENGTH("MIN_TIME"),
		FTP_UPLOAD_URL("UpUrl"),
		FTP_DOWN_URL("DownUrl"),
		FTP_UP_USERNAME("UPNAME"),
		FTP_UP_PASSWD("UPPASSWD"),
		FTP_DOWN_USERNAME("DOWNNAME"),
		FTP_DOWN_PASSWD("DWPASSWD"),

		//A3
		A3_PROTOCOL("protocol"),
		A3_EXPOSE_PROTOCOL("exposeProtocol"),
		A3_TRANSFERBITRATE("transferBitrate"),
		A3_RESPONSE_IP("responseIP"),
		A3_RESPONSE_PORT("responsePort"),
		A3_RESPONSE_PATH("responsePath"),
		A3_OTT_INTERVAL("ottInterval"),	
		A3_OTTVOLUME("ottVolume"),
	    A3_OTTREQUEST_IP("ottRequestIP"),
	    A3_OTTREQUEST_PORT("ottRequestPort"),
	    A3_READ_TIMEOUT("ReadTimeout"),
	    A3_CONNECT_TIMEOUT("ConnectTimeout"),
	    
		
	    //系统功能开关
		PROGAM_AUTO_AUDIT("ProgramAutoAudit"),
		ASSET_AUTO_AUDIT("AssetAutoAudit"),
		ASSETPKG_AUTO_AUDIT("AssetPkgAutoAudit"),
//		ASSETPKG_AUTO_CREATE("AssetPkgAutoCreate"),
		ASSET_AUTO_RELATE_PKG("AssetAutoRelatePkg"),//自动关联资源包
		CHANNEL_AUTO_AUDIT("ChannelAutoAudit"),
		ASSET_AUTO_DISTRIBUTE("AssetAutoDistribute"),
		DISTRIBUTE_ALL_FINISH_SYNC_MSG("DistributeAllFinishSyncMsg"),
		//CIP_SYNC_MSG("CIPSyncMsg"),
//		FUNCTIONSWITCH_RESOURCECODE_SYNC_WAY("ResourceCodeSyncWay"),
//		RECORD_CREATE_TYPE("RecordCreateType"),
//		RECORD_AUTO_CREATE("RecordAutoCreate"),
//		RESOURCE_SYNC_TYPE("ResourceSyncType"),
		IS_SUPPORT_TRANSCODING_TYPE("isSupportTranscoding"),
		IS_OFFLINE_SYSTEM_TYPE("IsOffLineSystemSwitch"),
		CMS_PRE_DISTRIBUTE_SWITCH("CMDPreDistributSwitch"),
		ASSET_FETCH_DVB_RECENT_SWITCH("AssetFetchDVBRecentSwitch"),//DVB 获取最近更新
		ASSET_FETCH_OTT_RECENT_SWITCH("AssetFetchOTTRecentSwitch"),//OTT 获取最近更新
		AssetNameCaptureChapterSwitch("AssetNameCaptureChapterSwitch"),//媒资名称提取集数信息

		//单片缺省价格
		ASSERT_DEFAULT_PRICE("AssertDefaultPrice"),
		

		
		//蓝讯内容刷新，预分发IP
		CHINACACHE_CONTENT_CUST_ID("custId"),
		CHINACACHE_CONTENT_PUBLISH_URL("publishUrl"),
		CHINACACHE_CONTENT_PRELOAD_OP("preloadOp"),
		CHINACACHE_CONTENT_CANCEL_OP("cancelOp"),
		CHINACACHE_CONTENT_GETURL_SPEED("GetUrlSpeed"),
		CHINACACHE_CONTENT_WHETHER_REFRESH("WhetherRefresh"),
		CHINACACHE_CONTENT_ENCRYPT_STR("encryptStr"),
		CHINACACHE_CONTENT_OP_PRELOAD("preloadOp"),
		
		//封装格式
		CONTAINER_FORMAT_MP4("mp4"),
		CONTAINER_FORMAT_TS("ts"),
		
		//转码设置
		TANSCODING_LIMIT_VALUE("TranscodingLimitValue"),
	    TANSCODING_VALUE("TranscodingValue"),
	    TANSCODING_SUPPORT_FORMAT("SupportTranscodingFormat"),
	    TANSCODING_TO_FORMAT("TranscodingToFormat"),
	    TANSCODING_TO_MP4_VALUE("TransToFormat_MP4"),
	    TANSCODING_TO_TS_VALUE("TransToFormat_TS"),
	    TANSCODING_CDN_URL("CDN_URL"),
	    BLUE_TRANS_DEFAULT_BITRATE("BlueTransDefaultBitrate"),
		//CP信息同步其它系统
		CP_SYN_OTHER_SYSTEM("CPInfoTargetSystem"),
		//内容刷新同步其它系统
		REFRESH_CONTENT_SYTEM("RefreshContentTargetSystem"),
		//内容预分发同步其它系统
		PRELOAD_CONTENT_SYTEM("ContentPreDistributeTargetSystem"),
		//CMS内容预分发信息同步其它系统
		CMS_PRE_DISTRIBUTE_SYN_OTHER_SYSTEM("CMSPreDistributeTargetSystem"),
		
		//审核参数设置
		AUDIT_PARAM_AUDITPERIOD("AudiPeriod"),
		
		//DVB区域同步其它系统
		DVB_AREA_TARGETSYSTEM("DvbAreaTargetSystem"),
		
		//海报不存在或最大张数据小于3张 则新增,否则默认丢失
		POSTER_CHECK_NUM("PosterCheckNum"),
		
		//url配置地址
		url_cms_crawler_whole("cmsCrawlerUrlWhole"),
		url_cms_crawler_recent("cmsCrawlerUrlRecent"),
		url_cms_crawler_detail("cmsCrawlerUrlDetail"),
		url_cms_crawler_interval("interval"),
		url_cms_crawler_dvbOrigins("dvbOrigins"),
		
		url_cms_crawler_channelFetchUrl("cmsCrawlerChannelFetchUrl"),
		;
		
		private String paraCode;
		
		ParamKey(String paraCode) {
			this.paraCode = paraCode;
		}
		
		public String toString() {
			return this.paraCode;
		}
	}
	
	public enum DictKey {
		
		//海报参数
		PosterParams("posterParams"),
		
		//同步其它系统
		SynOtherSystem("syncSystem"),
		//蓝讯接口
		Chinacache("chinacache"),
		
		//TW项目拓展的枚举参数值
		Container_Format("containerFormat");
		
		private String typeCode;

		DictKey(String typeCode) {
			this.typeCode = typeCode;
		}

		public String toString() {
			return this.typeCode;
		}
	}

	
	
	//第三方CDN厂商编码
    public static final String PARTNER_CODE="";//TODO
    //蓝讯分发opCode
    public static final String COSHIP_CONTENT_PRELOAD="COSHIP_CONTENT_PRELOAD";
    //蓝讯刷新opCode
    public static final String COSHIP_FRESH_CONTENT="COSHIP_FRESH_CONTENT";
    
	
	
	public static enum VideoDistrbuteState{
		
		DEFINED("defined","定义"),
		SENDING("sending","发送"),
		COMPLETE("complete","完成");
		
		public String key;
		public String name;
		
	
		VideoDistrbuteState(String key,String name)
		{
			this.key = key;
			this.name = name;
		}


		public String getKey() {
			return key;
		}


		public void setKey(String key) {
			this.key = key;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}

	}
	
	
	public static enum VideoDistrbuteType{
		
		REFRESH("refresh","刷新"),
		PRELOAD("preload","预分发"),
		CANCEL("cancel","取消预分发");
		
		public String key;
		public String name;
		
	
		VideoDistrbuteType(String key,String name)
		{
			this.key = key;
			this.name = name;
		}


		public String getKey() {
			return key;
		}


		public void setKey(String key) {
			this.key = key;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}

	}
	
	//锁定rank固定值
	public final static int SORT_LOCK_RANK = 10000000;

}
