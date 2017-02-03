package com.wasu.bpp.service;

import com.wasu.pub.Response;
import com.wasu.pub.service.DBCenter;
import com.wasu.bpp.queue.NotifyMsgStatusQueue;
import com.wasu.bpp.queue.SendMsgQueue;
import com.wasu.sid.SysRelate;
import com.wasu.sid.SysUser;
import com.wasu.sid.bpp.MmsMsg;
import com.wasu.pub.util.Page;
import com.wasu.pub.util.StringUtil;

import net.sf.json.JSONArray;

import com.wasu.bpp.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

//消息管理服务
@Component
@Transactional
public class MessManagerService {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private DBCenter dbCenter;

	//初始化或者按照条件查询信息
	public Response getPage(MmsMsg mmsMsg, Page page, SysUser user) {
		StringBuffer selectSql = new StringBuffer();
		selectSql.append("select m.* ").append("from mms_msg m where 1 = 1 ");
		StringBuffer conditions = new StringBuffer();
		if (!StringUtil.isBlank(mmsMsg.getContent())) {
			conditions.append(" and m.content like '%" + mmsMsg.getContent() + "%' ");
		}
		if (!StringUtil.isBlank(mmsMsg.getStbId())) {
			conditions.append(" and m.stbId like '%" + mmsMsg.getStbId() + "%' ");
		}
		if (!StringUtil.isBlank(mmsMsg.getAreaId())) {
			conditions.append(" and m.areaId like '%" + mmsMsg.getAreaId() + "%' ");
		}
		if (!StringUtil.isBlank(mmsMsg.getCustId())) {
			conditions.append(" and m.custId like '%" + mmsMsg.getCustId() + "%' ");
		}
		if (!StringUtil.isBlank(mmsMsg.getStatus())) {
			conditions.append(" and m.status= '" + mmsMsg.getStatus() + "' ");
		} else {
			conditions.append(" and m.status in (0,1,2,3,4) ");
		}
		if (!StringUtil.isBlank(mmsMsg.getDataSrc())) {
			conditions.append(" and m.dataSrc= '" + mmsMsg.getDataSrc() + "' ");
		}
		if (!isAdmin(user)) {
			conditions.append(" and m.sysUserId ='" + user.getId() + "' ");
		}
		selectSql.append(conditions.toString());
		StringBuffer countSql = new StringBuffer("select count(1) from mms_msg m where 1 = 1 ");
		countSql.append(conditions.toString());
		return dbCenter.sqlListCountSqlNoCache(selectSql.toString(), countSql.toString(), page, MmsMsg.class.getName());
	}

	public void saveMessage(MmsMsg mm) {
		SendMsgQueue.add(StringUtils.getQueueBean(mm.getId(), mm));//添加元素到队列
		dbCenter.save(mm, MmsMsg.class.getName());
		dbCenter.refreshCache(MmsMsg.class.getName());
	}

	public MmsMsg getCheckedMsg(Long id) {
		return (MmsMsg) dbCenter.getById(MmsMsg.class.getName(), id).getEntity();
	}

	public void updateMsg(MmsMsg mm) {
		SendMsgQueue.mdf(StringUtils.getQueueBean(mm.getId(), mm));//从队列中修改元素
		dbCenter.update(mm, MmsMsg.class.getName());
		dbCenter.refreshCache(MmsMsg.class.getName());
	}

	public void resendMsg(MmsMsg mm) {
		dbCenter.update(mm, MmsMsg.class.getName());
		SendMsgQueue.add(StringUtils.getQueueBean(mm.getId(), mm));//添加元素到队列
		if (!"0".equals(mm.getDataSrc())) {//调用状态同步接口，数据来源：0-MMS；1-用户中心；2-智能推荐系统；3-酒店管理系统；4-终端升级
			NotifyMsgStatusQueue.putMsgStatus(mm.getDataSrc(), JSONArray.fromObject("[{\"msgId\":\"" + mm.getId() + "\",\"status\":\"" + mm.getStatus() + "\"}]"));//加入通知消息状态队列
		}
	}

	//获取用户的角色id
	public String getUserRoleId(SysUser user) {
		HashMap<String, String> query = new HashMap<>();
		query.put("objectId", user.getId());
		query.put("typeId", user.getOrgId());
		SysRelate re = (SysRelate) dbCenter.queryOne(SysRelate.class.getName(), query).getEntity();
		if (re != null) {
			return re.getRelatedId();
		}
		
		return null;
	}

	//判断消息是否为待发送的消息
	public boolean isWillSend(Long id) {
		MmsMsg mm = (MmsMsg) dbCenter.getById(MmsMsg.class.getName(), id).getEntity();
		if (mm != null && "0".equalsIgnoreCase(mm.getDataSrc()) && ("0".equals(mm.getStatus()) || "2".equals(mm.getStatus())))
			return true;
		return false;
	}

	//判断当前用户是否是管理员
	public boolean isAdmin(SysUser user) {
		String orgId = user.getOrgId();
		String sql = "select a.manager as ISMANAGER from t_sys_organization a where id= '" + orgId + "'";
		List<HashMap> list = dbCenter.sqlListForMap(HashMap.class.getName(), sql);
		return "1".equals(list.get(0).get("ISMANAGER")) ;
	}

	//根据id获取所有对应的消息
	public List<MmsMsg> getAllMsg(List<Long> ids) {
		List<MmsMsg> list = new ArrayList<>();
		for (Long id : ids) {
			MmsMsg mm = (MmsMsg) dbCenter.getById(MmsMsg.class.getName(), id).getEntity();
			if (isWillSend(id)) {
				list.add(mm); //发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			}
		}
		
		return list;
	}

	public void delMsg(List<MmsMsg> list) {
		for (MmsMsg mm : list) {
			SendMsgQueue.del(StringUtils.getQueueBean(mm.getId(), mm));//从队列中删除元素
			dbCenter.delete(mm, MmsMsg.class.getName());
		}
	}

	public Date string2Date(String date, Integer hour) throws Exception {
		Date d = sdf.parse(date);
		if (hour == null)
			return d;
		Long dm = d.getTime() + hour * 60 * 60 * 1000L;
		return new Date(dm);
	}
}