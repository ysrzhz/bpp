package com.wasu.bpp.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.wasu.pub.service.DBCenter;
import com.wasu.bpp.dao.MmsMsgDao;
import com.wasu.bpp.queue.SendMsgQueue;
import com.wasu.bpp.util.StringUtils;
import com.wasu.sid.bpp.MmsMsg;

//队列同步
@Service
public class QueueSyncRunnable implements Runnable {
	private static Logger logger = Logger.getLogger(QueueSyncRunnable.class);
	private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	@Resource
    private MmsMsgDao mmsMsgDao;
	@Resource
	DBCenter dbCenter;
	
	//初始化(构造方法之后初始化方法之前)
	@PostConstruct
	private void init() {
		long time = System.currentTimeMillis();
		logger.info("开始加载未发送列表");
		loadData();//加载数据
		logger.info("未发送列表加载完毕,共用了" + (System.currentTimeMillis() - time) + "ms");
		new Thread(this).start();
	}
	
	//加载数据
	public void loadData() {
		try{
			List<MmsMsg> list=mmsMsgDao.getNoSendList();//未发送列表查询
			if(list!=null && list.size()>0){
				for(MmsMsg mmsMsg: list){
					SendMsgQueue.add(StringUtils.getQueueBean(mmsMsg.getId(), mmsMsg));//添加元素到队列
				}
			}
		} catch (Exception e) {
			logger.error("未发送列表加载错误："+e.getMessage());
		}
	}

	public void run() {
		while (true) {
			try {
				List<MmsMsg> list=mmsMsgDao.getQueueSyncList();//队列同步列表查询
	    		if(list!=null && list.size()>0){
	    			for(MmsMsg mmsMsg: list){
	    				queueSync(mmsMsg);//队列同步
		    		}
	    		}
			} catch (Exception e) {
				logger.error("队列同步列表查询错误："+e.getMessage());
			}
		}
	}
	
	//队列同步
	private void queueSync(MmsMsg mmsMsg) {
		SendMsgQueue.add(StringUtils.getQueueBean(mmsMsg.getId(), mmsMsg));//添加元素到队列
		Map<String, String> paramMap=new HashMap<String, String>();
		paramMap.put("status", "0");//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
		paramMap.put("lastUpdateTime", new SimpleDateFormat(yyyyMMddHHmmss).format(new Date()));
        dbCenter.update(MmsMsg.class.getName(), mmsMsg.getId(), paramMap);//更新状态
    }
}