package com.wasu.bpp.queue;

import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.wasu.bpp.domain.QueueBean;

//视频接口后台队列
public class SendMsgQueue {
	private static Logger logger = Logger.getLogger(SendMsgQueue.class);
	private static PriorityBlockingQueue<QueueBean> queue = new PriorityBlockingQueue<QueueBean>(1000000);

	//添加元素到队列
	public static void add(QueueBean bean) {
		try {
			queue.add(bean);//添加一个元素
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("add QueueBean to queue timeout..." + e.getMessage());
			}
		}
	}
	
	//从队列中修改元素
	public static void mdf(QueueBean bean) {
		try {
			queue.remove(bean);//删除一个元素
			queue.add(bean);//添加一个元素
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("remove QueueBean from queue timeout..." + e.getMessage());
			}
		}
	}
	
	//从队列中删除元素
	public static void del(QueueBean bean) {
		try {
			queue.remove(bean);//删除一个元素
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("remove QueueBean from queue timeout..." + e.getMessage());
			}
		}
	}

	public static PriorityBlockingQueue<QueueBean> getQueue() {
		return queue;
	}

	public static void setQueue(PriorityBlockingQueue<QueueBean> queue) {
		SendMsgQueue.queue = queue;
	}
}