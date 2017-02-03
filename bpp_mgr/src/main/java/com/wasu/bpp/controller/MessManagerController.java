package com.wasu.bpp.controller;

import com.google.gson.internal.LinkedTreeMap;
import com.wasu.bpp.service.MessManagerService;
import com.wasu.pub.controller.BaseController;
import com.wasu.pub.util.*;
import com.wasu.sid.SysUser;
import com.wasu.sid.bpp.MmsMsg;
import com.wasu.pub.util.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

//消息管理
@Controller
@RequestMapping("/messManager")
public class MessManagerController extends BaseController<MmsMsg> {
	public final static String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	private static final String AREA_URL = Configuration.readConfigString("IUC_AREAURL", "/conf/env");
	private static final String STBID_URL = Configuration.readConfigString("IUC_STBIDURL", "/conf/env");
	private static final String TREELEVEL = "treelevel";
	private static final String PARENTID = "parentid";
	private static final String REGIONID = "regionId";
	private static final int THRTH = 30000;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String PRO = "application/json;charset=UTF-8";
	private static final String SUC = "success";
	private static final String MSS = "message";
	@Autowired
	private MessManagerService mmsservice;

	//获取初始化页面及初始化信息
	@RequestMapping(value = "/list", produces = { PRO })
	@ResponseBody
	public ResponseEntity list(final MmsMsg mMsg, final @RequestParam(defaultValue = "1") int page, final @RequestParam(defaultValue = "100") int rows) {
		Page<MmsMsg> pager = null;
		HttpSession session = request.getSession();
		SysUser user = (SysUser) session.getAttribute("loginUser");
		try {
			pager = new Page<MmsMsg>(rows, page);
			pager = (Page) mmsservice.getPage(mMsg, pager, user).getEntity();
		} catch (Exception e) {
			logger.error("获取消息列表不正常", e);
		}
		
		return createPageResponseEntity(pager);
	}

	//新增用户服务
	@RequestMapping(value = "/save", produces = { PRO })
	@ResponseBody
	public Map save(final HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		MmsMsg mmsMsg = new MmsMsg();
		mmsMsg.setTitle(request.getParameter("title"));
		mmsMsg.setContent(request.getParameter("content"));
		String stbid = "";
		String area = "";
		String custid = "";
		if (!StringUtils.isBlank(request.getParameter("stbId"))) {
			stbid = request.getParameter("stbId").replaceAll("\r\n", ",");
			mmsMsg.setStbId(stbid);
		}
		if (!StringUtils.isBlank(request.getParameter("areaId"))) {
			area = request.getParameter("areaId").replaceAll("\r\n", ",");
			mmsMsg.setAreaId(area);
		}
		if (!StringUtils.isBlank(request.getParameter("custId"))) {
			custid = request.getParameter("custId").replaceAll("\r\n", ",");
			mmsMsg.setCustId(custid);
		}
		mmsMsg.setScope(request.getParameter("scope"));
		mmsMsg.setVtype(request.getParameter("vtype"));
		HttpSession session = request.getSession();
		SysUser user = (SysUser) session.getAttribute("loginUser");
		String sysRoleId = mmsservice.getUserRoleId(user);
		mmsMsg.setSysRoleId(sysRoleId);
		mmsMsg.setSysUserId(user.getId());
		mmsMsg.setCreateTime(new Date());
		mmsMsg.setLastUpdateTime(new Date());
		mmsMsg.setStatus("0");
		mmsMsg.setDataSrc("0");
		try {
			String sendTime = request.getParameter("sendTime");
			if (!StringUtils.isBlank(sendTime)) {
				mmsMsg.setSendTime(mmsservice.string2Date(sendTime, 0));
			} else {
				mmsMsg.setSendTime(new Date());
			}
			String vaildTime = request.getParameter("validTime");
			if (!StringUtils.isBlank(vaildTime)) {
				mmsMsg.setValidTime(mmsservice.string2Date(vaildTime, 0));
			} else {
				mmsMsg.setValidTime(mmsservice.string2Date(vaildTime, 24));
			}
			mmsservice.saveMessage(mmsMsg);
			result.put(SUC, true);
			result.put(MSS, "添加成功");
			return result;
		} catch (Exception e) {
			logger.info(e.getMessage());
			result.put(SUC, false);
			result.put(MSS, "添加失败");
			return result;
		}
	}

	//将要删除消息的消息（状态为0-待发送）状态改为8
	@RequestMapping(value = "/del", produces = { PRO })
	@ResponseBody
	public Map del(final HttpServletRequest request, final String ids) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Long> idList = convertIdsStr2Long(ids, Constant.COMMA_SEPARATOR);
		List<MmsMsg> mms = mmsservice.getAllMsg(idList);
		if (mms.size() != idList.size()) {
			result.put(SUC, false);
			result.put(MSS, "只能删除数据来源为MMS的待发送、发送失败的消息");
			return result;
		}
		
		mmsservice.delMsg(mms);
		baseService.refreshPlaceAndAllRelateCache();
		result.put(SUC, true);
		result.put(MSS, "处理成功！");
		return result;
	}

	//跳转编辑页面
	@RequestMapping(value = "/edit")
	public ModelAndView edit(final Long id, final boolean isEdit, final boolean isMMS) {
		ModelAndView mv = new ModelAndView();
		boolean b = isEdit && isMMS; //仅消息系统的待发送消息可以进行编辑
		mv.setViewName("/messManager/" + (b ? "messManager_edit" : "showMessInfo"));
		mv.addObject("id", id);
		return mv;
	}

	@RequestMapping(value = "/resend", produces = { PRO })
	@ResponseBody
	public Map resend(final String ids) {
		Map<String, Object> result = new HashMap<>();
		List<Long> idList = convertIdsStr2Long(ids, Constant.COMMA_SEPARATOR);
		try {
			for (Long id : idList) {
				MmsMsg mm = mmsservice.getCheckedMsg(id);
				mm.setStatus("0");
				mm.setLastUpdateTime(new Date());
				mmsservice.resendMsg(mm);
			}
			
			result.put(SUC, true);
			result.put(MSS, "重新发送成功");
			return result;
		} catch (Exception e) {
			logger.info(e.getMessage());
			result.put(SUC, false);
			result.put(MSS, "重新发送失败");
			return result;
		}
	}

	//得到需要更新的用户服务信息
	@RequestMapping(value = "/get/{id}", produces = { PRO })
	@ResponseBody
	public String get(final @PathVariable Long id) {
		MmsMsg mm = mmsservice.getCheckedMsg(id);
		String send = null;
		String valid = null;
		if (mm.getSendTime() != null) {
			send = sdf.format(mm.getSendTime());
		}
		
		if (mm.getValidTime() != null) {
			valid = sdf.format(mm.getValidTime());
		}
		
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("id", mm.getId());
		hm.put("title", mm.getTitle());
		hm.put("content", mm.getContent());
		if (!StringUtils.isBlank(mm.getStbId())) {
			String stbId = mm.getStbId().replaceAll(",", "\r\n");
			hm.put("stbId", stbId);
		}
		
		if (!StringUtils.isBlank(mm.getAreaId())) {
			String areaId = mm.getAreaId().replaceAll(",", "\r\n");
			hm.put("areaId", areaId);
		}
		
		if (!StringUtils.isBlank(mm.getCustId())) {
			String custId = mm.getCustId().replaceAll(",", "\r\n");
			hm.put("custId", custId);
		}
		
		hm.put("scope", mm.getScope());
		hm.put("vtype", mm.getVtype());
		hm.put("status", mm.getStatus());
		hm.put("dataSrc", mm.getDataSrc());
		hm.put("sendTime", send);
		hm.put("validTime", valid);
		return getJson(hm);
	}

	//更新用户服务信息
	@RequestMapping(value = "/updat", produces = { PRO })
	@ResponseBody
	public Map updat(final HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		try {
			String id = request.getParameter("id");
			if (!mmsservice.isWillSend(Long.parseLong(id))) { //根据id查询消息是否为待发送状态
				result.put(SUC, false);
				result.put(MSS, "只能修改数据来源为MMS的待发送消息");
				return result;
			}
			
			MmsMsg mmsMsg = mmsservice.getCheckedMsg(Long.parseLong(id));
			mmsMsg.setTitle(request.getParameter("title"));
			mmsMsg.setContent(request.getParameter("content"));
			String stbId = request.getParameter("stbId").replaceAll("\r\n", ",");
			mmsMsg.setStbId(stbId);
			String areaId = request.getParameter("areaId").replaceAll("\r\n", ",");
			mmsMsg.setAreaId(areaId);
			String custId = request.getParameter("custId").replaceAll("\r\n", ",");
			mmsMsg.setCustId(custId);
			String sendTime = request.getParameter("sendTime");
			if (!StringUtils.isBlank(sendTime)) {
				mmsMsg.setSendTime(DateUtil.str2Date(sendTime));
			} else {
				mmsMsg.setSendTime(new Date());
			}
			
			String vaildTime = request.getParameter("validTime");
			if (!StringUtils.isBlank(vaildTime)) {
				mmsMsg.setValidTime(DateUtil.str2Date(vaildTime));
			} else {
				mmsMsg.setValidTime(DateUtil.getAddHourOfDate(new Date(), 24));
			}
			
			mmsMsg.setScope(request.getParameter("scope"));
			mmsMsg.setVtype(request.getParameter("vtype"));
			mmsMsg.setLastUpdateTime(new Date());
			mmsservice.updateMsg(mmsMsg);
			result.put(SUC, true);
			result.put(MSS, "修改成功");
			return result;
		} catch (Exception e) {
			logger.info(e.getMessage());
			result.put(SUC, false);
			result.put(MSS, "修改失败");
			return result;
		}
	}

	//加载区域树
	@RequestMapping(value = "/getTree", produces = { PRO })
	@ResponseBody
	public String getRegionInfo(final String bosstype, final String parentId, final String name) {
		String level = "7";
		String parent = parentId;
		if (!StringUtils.isBlank(bosstype)) {
			parent = bosstype;
		}
		
		String strURL = AREA_URL + TREELEVEL + "=" + level + "&" + PARENTID + "=" + parent;
		StringBuffer strResponse = connToUserCeneter(strURL);
		HashMap hm = JsonUtil.json2Object(strResponse.toString(), HashMap.class);
		List<LinkedTreeMap> list = (List) hm.get("list");
		List<HashMap> area = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (LinkedTreeMap ltm : list) {
				HashMap<String, Object> node = new HashMap<>();
				String pid = (String) ltm.get("pid");
				String ids = (String) ltm.get("ids");
				boolean isParent = (boolean) ltm.get("isParent");
				String id = ids.substring(ids.lastIndexOf("^") + 1);
				node.put("id", id);
				node.put("text", (String) ltm.get("name"));
				HashMap<String, Object> attr = new HashMap<>();
				attr.put("pid", pid);
				attr.put("ids", ids);
				attr.put("isParent", isParent);
				node.put("attributes", attr);
				node.put("state", "closed");
				area.add(node);
			}
		}
		
		return JsonUtil.fromObject(area);
	}

	//根据区域id加载所有stbid
	@RequestMapping(value = "/getStbids", produces = { PRO })
	@ResponseBody
	public String getStbIds(final String regionId) {
		Integer rows = 300; //每页记录数
		Integer page = 1;
		String strURL = STBID_URL + REGIONID + "=" + regionId;
		String req = strURL + "&rows=" + rows + "&page=" + page;
		StringBuffer strResponse = connToUserCeneter(req); //首次加载，获取首页记录及总页数
		HashMap hm = JsonUtil.json2Object(strResponse.toString(), HashMap.class);
		LinkedTreeMap lk = (LinkedTreeMap) hm.get("page");
		List<LinkedTreeMap> list = (List) lk.get("results");
		Double pageCount = (Double) lk.get("pageCount"); //获取总页数
		List<HashMap> area = new ArrayList<>(); //封装节点
		if (list != null && list.size() > 0) {
			for (LinkedTreeMap h : list) {
				HashMap<String, Object> node = new HashMap<>();
				String bill_id = (String) h.get("bill_id");
				node.put("id", bill_id);
				node.put("text", bill_id);
				node.put("state", "open");
				area.add(node);
			}
		}
		
		for (int i = 2; i < pageCount; i++) { //根据页数从第二页请求每页记录
			req = strURL + "&rows=" + rows + "&page=" + i;
			strResponse = connToUserCeneter(req);
			List l = getStbidNode(strResponse); //获取当前页封装的stbid节点
			area.addAll(l);
		}

		return JsonUtil.fromObject(area);
	}

	//封装stbid节点
	public static List getStbidNode(StringBuffer strResponse) {
		HashMap hm = JsonUtil.json2Object(strResponse.toString(), HashMap.class);
		LinkedTreeMap lk = (LinkedTreeMap) hm.get("page");
		List<LinkedTreeMap> list = (List) lk.get("results");
		List<HashMap> area = new ArrayList<>();
		if (list != null && list.size() > 0) {
			for (LinkedTreeMap h : list) {
				HashMap<String, Object> node = new HashMap<>();
				String bill_id = (String) h.get("bill_id");
				node.put("id", bill_id);
				node.put("text", bill_id);
				node.put("state", "open");
				area.add(node);
			}
		}
		
		return area;
	}

	public StringBuffer connToUserCeneter(String strURL) {
		BufferedReader reader = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(strURL);
			connection = (HttpURLConnection) url.openConnection();//打开URL连接
			connection.setDoOutput(true);//http正文内，因此需要设为true, 默认情况下是false;
			connection.setDoInput(true);//设置是否从httpUrlConnection读入，默认情况下是true;
			connection.setUseCaches(false);//Post 请求不能使用缓存
			connection.setRequestMethod("GET");//设置URL的请求方式
			connection.setConnectTimeout(THRTH);//setConnectTimeout：设置连接主机超时（单位：毫秒）
			connection.setReadTimeout(THRTH);//setReadTimeout：设置从主机读取数据超时（单位：毫秒）
			String strLine = "";
			StringBuffer strResponse = new StringBuffer();
			InputStream in = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while ((strLine = reader.readLine()) != null) {
				strResponse = strResponse.append(strLine).append("\n");
			}
			
			return strResponse;
		} catch (Exception e) {
			logger.error("用户中心服务器连接异常", e);
			return null;
		} finally {
			try {
				if (connection != null) {
					connection.disconnect();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}