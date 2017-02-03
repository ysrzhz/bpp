package com.wasu.bpp.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.wasu.bpp.domain.NmsQueueBean;

import net.sf.json.JSONArray;

//通知消息状态队列
public class NotifyMsgStatusQueue {
	private static Logger logger = Logger.getLogger(NotifyMsgStatusQueue.class);
	private static LinkedBlockingQueue<NmsQueueBean> queue = new LinkedBlockingQueue<NmsQueueBean>(1000000);

	public NotifyMsgStatusQueue() {
	}

	// 直接添加操作缓存
	public static void putMsgStatus(String dataSrc, JSONArray jsonArr) {
		try {
			NmsQueueBean bean = new NmsQueueBean();
			bean.setDataSrc(dataSrc);
			bean.setJsonArr(jsonArr);
			queue.add(bean);//添加一个元素
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("offer QueueBean to queue timeout..." + e.getMessage());
			}
		}
	}

	public static LinkedBlockingQueue<NmsQueueBean> getQueue() {
		return queue;
	}

	public static void setQueue(LinkedBlockingQueue<NmsQueueBean> queue) {
		NotifyMsgStatusQueue.queue = queue;
	}
}