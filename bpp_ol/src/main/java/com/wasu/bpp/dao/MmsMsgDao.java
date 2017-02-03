package com.wasu.bpp.dao;

import com.wasu.bpp.domain.MsgRequest;
import com.wasu.bpp.util.StringUtils;
import com.wasu.sid.bpp.MmsMsg;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//消息发送
@Component
public class MmsMsgDao extends BaseDao<MmsMsg> {
	private static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	private static final String SYSUSERID = "system";
	private static final String SYSROLEID = "system";
	
    //消息发送
    @Transactional
    public String sendMsg(MsgRequest req) throws Exception {
    	MmsMsg mmsMsg=new MmsMsg();
    	mmsMsg.setTitle(req.getTitle());
    	mmsMsg.setSendTime(new SimpleDateFormat(yyyyMMddHHmmss).parse(req.getSendTime()));
    	mmsMsg.setContent(req.getContent());
    	mmsMsg.setStbId(req.getStbId());
    	mmsMsg.setAreaId(req.getAreaId());
    	mmsMsg.setCustId(req.getCustId());
    	mmsMsg.setCronExpr(req.getCronExpr());
    	mmsMsg.setValidTime(new SimpleDateFormat(yyyyMMddHHmmss).parse(req.getValidTime()));
    	mmsMsg.setScope(req.getScope());
    	mmsMsg.setVtype(req.getVtype());
    	mmsMsg.setStatus("99");
    	mmsMsg.setSysUserId(SYSUSERID);
    	mmsMsg.setSysRoleId(SYSROLEID);
    	mmsMsg.setDataSrc(req.getDataSrc());
    	mmsMsg.setCreateTime(new Date());
    	mmsMsg.setLastUpdateTime(new Date());
    	Long id=dbCenter.saveEntity(mmsMsg, MmsMsg.class.getName());
    	if(id==0){//如果返回0，则入库失败
			logger.error("入库失败[" + req.toString() + "]，请重试");
            return StringUtils.getRetStr("-1", "入库失败，请重试", req.getEncoding());
		}
    	
		return StringUtils.getRetStr("0", "消息发送成功", req.getEncoding(), "msgId", String.valueOf(id));
    }
    
    //消息状态列表查询
    @SuppressWarnings("unchecked")
	public List<MmsMsg> getMsgStatusList(String msgIds, String dataSrc) throws Exception {
    	StringBuffer sb = new StringBuffer("SELECT a.id, a.createTime, a.lastUpdateTime, a.title, a.sendTime, a.content, a.stbId, a.areaId, a.custId, a.cronExpr, a.validTime, a.scope, a.vtype, a.status, a.sysUserId, a.sysRoleId, a.dataSrc FROM bpp_msg a where")
			.append(" a.dataSrc='"+dataSrc+"'")//数据来源：1-用户中心；2-智能推荐系统；3-酒店管理系统；4-终端升级
			.append(" and find_in_set(a.id, '"+msgIds+"')")//发送状态：0-待发送；1-发送成功；2-发送失败；3-已达；4-已读；5-已删除
			.append(" order by a.id asc");
    	return (List<MmsMsg>)dbCenter.getSqlList(MmsMsg.class.getName(), sb.toString());
    }
}