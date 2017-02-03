package com.wasu.pub.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wasu.pub.WSException;
import com.wasu.pub.service.BaseService;
import com.wasu.pub.service.UrmService;
import com.wasu.pub.util.Page;

public class BaseController<T> {
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	@Autowired
	protected BaseService baseService;
	private String entityName = "";
	private Class entityClass;

	protected static GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();;

	protected ObjectMapper mapper = new ObjectMapper();

	protected Logger logger = Logger.getLogger(getClass());

	@RequestMapping(value = "/detail/{id}")
	public ModelAndView detail(@PathVariable Long id) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/" + entityName + "/" + entityName + "_detail");
		T entity = (T) baseService.getById(entityClass.getName(), id);
		logger.debug("获取详情ID[" + id + "] 内容[" + entity);
		mv.addObject(entityName, entity);
		return mv;
	}

	@InitBinder
	private void stringBinder(WebDataBinder binder) {
		StringTrimmerEditor editor = new StringTrimmerEditor(false);
		binder.registerCustomEditor(String.class, editor);
	}

	public void responseByError(HttpServletResponse response, int code, String message) {
		try {
			response.sendError(400, "response content is null.");
		} catch (IOException e) {
			logger.error("处理消息返回失败", e);
		}
	}

	/**
	 * 拼装错误消息
	 * @return
	 */
	public void createErrorMsg(StringBuffer buffer, String value) {
		if (buffer.length() == 0) {
			buffer.append(value);
		} else {
			if (null == value || value.trim().isEmpty()) {
			} else {
				buffer.append(",").append(value);
			}
		}
	}

	/**
	 * 回馈消息
	 * @param response
	 * @param message
	 */
	public void responseMsg(HttpServletResponse response, String message) {
		PrintWriter out = null;
		try {
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.println(message);
			out.flush();
			out.close();
		} catch (IOException e) {
			logger.error("处理消息返回失败", e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	protected String getJson(Object o) {
		Gson gson = gsonBuilder.create();
		String json = gson.toJson(o).replace("null", "\"\""); //统一null转""
		return json;
	}

	/**
	 * 提供json返回拼装封装
	 * @param t
	 * @return
	 */
	protected <T> ResponseEntity<T> createResponseEntity(T t) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<T>(t, headers, HttpStatus.OK);
	}

	/**
	 * 通用分页json返回处理,携带异常
	 * @param pager 分页条件
	 * @param 错误码
	 * @param errorMsg 错误信息
	 * @return
	 */
	public ResponseEntity<Map<String, Object>> createCommonResponseEntity(boolean succ, int code, String message) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", succ); //兼容之前
		resultMap.put("retCode", code);//返回页面的错误码，200 ok,其他视为异常
		resultMap.put("message", message);//自定义的业务异常
		return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.OK);
	}

	/**
	 * 外部成功通用（适用于增删改等操作，返回处理结果）
	 * @return
	 */
	protected ResponseEntity<Map<String, Object>> createResponseEntity4success() {
		return this.createCommonResponseEntity(true, 200, "操作成功");
	}

	/**
	 * 外部成功通用（适用于增删改等操作，返回处理结果）
	 * @return
	 */
	protected ResponseEntity<Map<String, Object>> createResponseEntity4special(boolean succ, String message, Exception ex) {
		return this.createCommonResponseEntity(succ, 400, getMessageByException(ex, message));
	}

	/**
	 * @param successOrFailure
	 * @param message
	 * @return
	 */
	protected Map<String, Object> result(boolean successOrFailure, Object message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", successOrFailure);
		result.put("message", message);
		return result;
	}

	/**
	 * @param ex
	 * @param defaultMessage
	 * @return
	 */
	private String getMessageByException(Exception ex, String defaultMessage) {
		String message = null;
		if (null == ex) {
			return defaultMessage;
		}

		if (ex instanceof WSException) {
			message = ex.getMessage();
			if (defaultMessage != null) {
				message = defaultMessage + "--" + message;
			}
		} else if (ex instanceof NullPointerException) {
			message = "代码逻辑-空指针异常!";
		} else if (ex instanceof DataAccessException || ex instanceof SQLException) {
			message = "数据库异常";//
		} else {
			message = "未知异常";//
		}

		return message;
	}

	/**
	 * 提供json返回拼装封装
	 * @param t
	 * @return
	 */
	protected <T> ResponseEntity<T> createResponseEntity(T t, int code, String msg) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<T>(t, headers, HttpStatus.OK);
	}

	/**
	 * id字符串转数字型list.为空判断上层调用这决定
	 * @param idsStr
	 * @return
	 */
	protected List<Long> convertIdsStr2Integer(String idsStr, String regex) {
		String[] rightArray = idsStr.split(regex);
		List<Long> list = new ArrayList<Long>();
		for (String rightId : rightArray) {
			list.add(Long.valueOf(rightId));
		}
		
		return list;
	}

	/**
	 * id字符串转数字型list.为空判断上层调用这决定
	 * @param idsStr
	 * @return
	 */
	protected List<Long> convertIdsStr2Long(String idsStr, String regex) {
		if (null == idsStr || idsStr.trim().isEmpty()) {
			throw new WSException("020002", "idsStr不能为空！");
		}

		String[] rightArray = idsStr.split(regex);
		List<Long> list = new ArrayList<Long>();
		for (String rightId : rightArray) {
			list.add(Long.valueOf(rightId));
		}
		
		return list;
	}

	/**
	 * 通用分页json返回处理,携带异常
	 * @param pager 分页条件
	 * @param errorMsg 错误信息
	 * @return
	 */
	public <T> ResponseEntity<Map<String, Object>> createPageResponseEntity(Page<T> pager, String errorMsg) {
		return createPageResponseEntity(pager, 200, errorMsg);
	}

	/**
	 * 通用分页json返回处理
	 * @param pager
	 * @return
	 */
	public <T> ResponseEntity<Map<String, Object>> createPageResponseEntity(Page<T> pager) {
		if (null == pager) {
			if (logger.isDebugEnabled()) {
				logger.debug("response data:" + pager);
			}
			
			return this.createPageResponseEntity(pager, 400, "发生未知异常");
		}
		
		return this.createPageResponseEntity(pager, 200, null);
	}

	/**
	 * 通用分页json返回处理,携带异常
	 * @param pager 分页条件
	 * @param 错误码 错误信息
	 * @param message 错误信息
	 * @return
	 */
	private <T> ResponseEntity<Map<String, Object>> createPageResponseEntity(Page<T> pager, int code, String message) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total", pager.getTotalCount());
		resultMap.put("rows", pager.getResults());
		resultMap.put("retCode", code);//返回页面的错误码，200 ok其他异常
		resultMap.put("message", message);//自定义的业务异常
		return new ResponseEntity<Map<String, Object>>(resultMap, headers, HttpStatus.OK);
	}

	/**
	 * 通用分页json返回处理
	 * @param pager
	 * @return
	 */
	public <T> ResponseEntity<Map<String, Object>> createPageResponseEntity(Page<T> pager, Exception ex) {
		if (null == pager) {
			return this.createPageResponseEntity(pager, 400, "分页参数未初始化");
		}

		String message = null;
		if (ex != null) {
			if (ex instanceof WSException) {
				message = ex.getMessage();
			} else if (ex instanceof DataAccessException || ex instanceof SQLException) {
				message = "数据库异常";//
			}

			return this.createPageResponseEntity(pager, 400, message);
		}

		return this.createPageResponseEntity(pager);
	}

	public void writeJsonValue(HttpServletResponse response, Object object) {
		try {
			response.setContentType("application/json;charset=UTF-8");
			mapper.writeValue(response.getOutputStream(), object);
		} catch (JsonGenerationException e) {
			logger.error("处理消息返回失败", e);
		} catch (JsonMappingException e) {
			logger.error("处理消息返回失败", e);
		} catch (IOException e) {
			logger.error("处理消息返回失败", e);
		}
	}

	public static String success() {
		return success(0, "处理成功！");
	}

	public static String success(Object obj) {
		return success(0, obj);
	}

	public static String error(Object obj) {
		return success(1, obj);
	}

	public static String success(int code, Object obj) {
		try {
			Map map = new HashMap();
			map.put("ret", String.valueOf(code));
			map.put("retInfo", obj);
			return JSON.toJSONString(map);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public UrmService getUrmClient() {
		return UrmService.get(request.getSession());
	}

	public BaseService getBaseService() {
		return baseService;
	}

	public void setBaseService(BaseService baseService) {
		this.baseService = baseService;
	}

	public String getEntityName() {
		return entityName;
	}

	public Class getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class entityClass) {
		this.entityClass = entityClass;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}