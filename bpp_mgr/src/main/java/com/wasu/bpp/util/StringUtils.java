package com.wasu.bpp.util;

import com.wasu.bpp.domain.QueueBean;
import com.wasu.sid.bpp.MmsMsg;

public class StringUtils {
	public static boolean isNotEmpty(String str) {
		if (null == str || str.trim().length() == 0) {
			return false;
		}

		return true;
	}

	public static boolean isEmpty(String str) {
		return !isNotEmpty(str);
	}

	//获取QueueBean
    public static QueueBean getQueueBean(Long id, MmsMsg mmsMsg) {
		QueueBean bean=new QueueBean();
		bean.setId(id);
		bean.setTitle(mmsMsg.getTitle());
		bean.setSendTime(mmsMsg.getSendTime());
		bean.setContent(mmsMsg.getContent());
		bean.setStbId(mmsMsg.getStbId());
		bean.setAreaId(mmsMsg.getAreaId());
		bean.setCustId(mmsMsg.getCustId());
		bean.setCronExpr(mmsMsg.getCronExpr());
		bean.setValidTime(mmsMsg.getValidTime());
		bean.setScope(mmsMsg.getScope());
		bean.setVtype(mmsMsg.getVtype());
		bean.setDataSrc(mmsMsg.getDataSrc());
		return bean;
    }
}