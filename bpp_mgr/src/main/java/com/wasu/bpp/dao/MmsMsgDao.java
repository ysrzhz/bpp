package com.wasu.bpp.dao;

import com.wasu.pub.dao.BaseDao;
import com.wasu.sid.bpp.MmsMsg;

import org.springframework.stereotype.Component;
import java.util.List;

//消息数据访问对象
@Component
public class MmsMsgDao extends BaseDao<MmsMsg> {
    //未发送列表查询
    @SuppressWarnings("unchecked")
	public List<MmsMsg> getNoSendList() throws Exception {
    	StringBuffer sb = new StringBuffer("select a.id, a.createTime, a.lastUpdateTime, a.title, a.sendTime, a.content, a.stbId, a.areaId, a.custId, a.cronExpr, a.validTime, a.scope, a.vtype, a.status, a.sysUserId, a.sysRoleId, a.dataSrc FROM mms_msg a where")
			.append(" a.status='0'")//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			.append(" order by a.id asc");
    	return (List<MmsMsg>)dbCenter.getSqlList(MmsMsg.class.getName(), sb.toString());
    }
    
    //队列同步列表查询
    @SuppressWarnings("unchecked")
	public List<MmsMsg> getQueueSyncList() throws Exception {
    	//"select x.id, x.createTime, x.lastUpdateTime, x.title, x.sendTime, x.content, x.stbId, x.areaId, x.custId, x.cronExpr, x.validTime, x.scope, x.vtype, x.status, x.sysUserId, x.sysRoleId, x.dataSrc from ("
    	StringBuffer sb = new StringBuffer("select a.id, a.createTime, a.lastUpdateTime, a.title, a.sendTime, a.content, a.stbId, a.areaId, a.custId, a.cronExpr, a.validTime, a.scope, a.vtype, a.status, a.sysUserId, a.sysRoleId, a.dataSrc FROM mms_msg a where")//, row_number() over(order by a.id asc) row_id
			.append(" a.dataSrc!='0'")//数据来源：0-MMS；1-用户中心；2-智能推荐系统；3-酒店管理系统；4-终端升级
			.append(" and a.status='99'")//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			.append(" order by a.id asc limit 0,10000");//") x where x.row_id >= 0 and x.row_id < 10000"
    	return (List<MmsMsg>)dbCenter.getSqlList(MmsMsg.class.getName(), sb.toString());
    }
    
    //状态同步列表查询
    @SuppressWarnings("unchecked")
	public List<MmsMsg> getStatusSyncList(long qryBeginPos, long qryNum) throws Exception {
    	//"select x.id, x.createTime, x.lastUpdateTime, x.title, x.sendTime, x.content, x.stbId, x.areaId, x.custId, x.cronExpr, x.validTime, x.scope, x.vtype, x.status, x.sysUserId, x.sysRoleId, x.dataSrc from ("
    	StringBuffer sb = new StringBuffer("select a.id, a.createTime, a.lastUpdateTime, a.title, a.sendTime, a.content, a.stbId, a.areaId, a.custId, a.cronExpr, a.validTime, a.scope, a.vtype, a.status, a.sysUserId, a.sysRoleId, a.dataSrc FROM mms_msg a where")//, row_number() over(order by a.dataSrc asc, a.id asc) row_id
			.append(" a.status='1'")//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			.append(" order by a.dataSrc asc, a.id asc limit "+qryBeginPos+","+qryNum);//") x where x.row_id >= to_number("+qryBeginPos+") and x.row_id < to_number("+qryBeginPos+") + to_number("+qryNum+")"
    	return (List<MmsMsg>)dbCenter.getSqlList(MmsMsg.class.getName(), sb.toString());
    }
}