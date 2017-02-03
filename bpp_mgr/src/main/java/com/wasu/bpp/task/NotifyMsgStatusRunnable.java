package com.wasu.bpp.task;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.wasu.bpp.domain.NmsQueueBean;
import com.wasu.bpp.queue.NotifyMsgStatusQueue;
import com.wasu.bpp.util.HttpUtil;

//通知消息状态
@Service
public class NotifyMsgStatusRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(NotifyMsgStatusRunnable.class);
	@Resource
	HashMap<String, String> dataSrcUrlMap;//数据来源dataSrc对应消息状态同步接口地址dataSrcUrl
	
	//初始化(构造方法之后初始化方法之前)
	@PostConstruct
	private void init() {
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			try {
				LinkedBlockingQueue<NmsQueueBean> queue = NotifyMsgStatusQueue.getQueue();
				NmsQueueBean bean = queue.poll(2, TimeUnit.SECONDS);// 取出一个QueueBean，若QueueBean为空，等到队列有QueueBean为止(获取并移除此队列的头部)
				if (bean != null) {
					HttpUtil.notifyMsgStatus(dataSrcUrlMap.get(bean.getDataSrc()), bean.getJsonArr().toString());
					continue;
				}
			} catch (InterruptedException e) {
				logger.error("poll QueueBean from queue occur Exception" + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}