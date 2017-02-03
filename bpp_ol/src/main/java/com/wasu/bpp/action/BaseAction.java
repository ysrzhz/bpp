package com.wasu.bpp.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wasu.pub.WSException;
import com.wasu.bpp.annoation.SkipJSon;
import com.wasu.bpp.util.JsonUtil;
import com.wasu.bpp.util.LowerCaseFieldNamingStrategy;

/**
 * @author 906324
 */
public abstract class BaseAction {
	private static Logger logger = Logger.getLogger(BaseAction.class);
	protected static GsonBuilder gsonBuilder;

	public abstract void execute(HttpServletRequest request, HttpServletResponse response);

	static {
		init();
	}

	private static void init() {

		gsonBuilder = new GsonBuilder();
		ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}

			public boolean shouldSkipField(FieldAttributes f) {
				return f.getAnnotation(SkipJSon.class) != null;
			}
		};
		gsonBuilder.setPrettyPrinting()
		//.serializeNulls()
				.setExclusionStrategies(new ExclusionStrategy[] { exclusionStrategy }).setDateFormat("yyyy-MM-dd HH:mm:ss").setFieldNamingStrategy(new LowerCaseFieldNamingStrategy());

	}

	protected <T> T getEntityFromRequest(String json, Class<T> clazz) throws IOException {
		if (logger.isDebugEnabled())
			logger.debug("request content:\t{}" + json);
		if (StringUtils.isBlank(json)) {
			throw new WSException("request content is empty!!!", "");
		}
		Gson gson = gsonBuilder.create();
		T t = gson.fromJson(json, clazz);
		if (t != null) {
			vaildObject(t, null);
		}
		return t;
	}

	protected String getJson(Object o) {
		//Gson gson = gsonBuilder.create();
		//return gson.toJson(o);
		return JsonUtil.fromObject(o);
	}

	protected static void vaildObject(Object t, String profile) {
		Validator validator = new Validator();
		if (StringUtils.isNotBlank(profile)) {
			validator.disableAllProfiles();
			validator.enableProfile("default");
			validator.enableProfile(profile);
		}
		//TODO 添加具体的校验文件
		List<ConstraintViolation> validates = validator.validate(t);
		if ((validates != null) && (validates.size() > 0)) {
			throw new WSException("校验没有通过！！！！", "");
		}
	}

	/*protected void output(HttpServletResponse response, String jsonResult) {
		PrintWriter pw = null;
		try {
			response.setContentType("application/json; charset=UTF-8");
			response.setContentLength(jsonResult.getBytes("UTF-8").length);
			if (logger.isDebugEnabled()) {
				logger.debug("response json: {}" + jsonResult);
			}

			pw = response.getWriter();
			pw.write(jsonResult);
			pw.flush();

		} catch (Exception e) {
			logger.error("Error output json data to the client!!!orginal json={}" + jsonResult, e);
		} finally {
			if (pw != null) {
				IOUtils.closeQuietly(pw);
			}
		}
	}*/
	
	protected void output(HttpServletResponse response, String jsonResult, String encoding) {
		PrintWriter pw = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("response json: {}" + jsonResult);
			}
			
			if(encoding==null || encoding.trim().length()==0){
				encoding="UTF-8";
			}
			
			response.setContentType("application/json; charset="+encoding);
			response.setContentLength(jsonResult.getBytes(encoding).length);
			pw = response.getWriter();
			pw.write(jsonResult);
			pw.flush();
		} catch (Exception e) {
			logger.error("Error output json data to the client!!!orginal json={}" + jsonResult, e);
		} finally {
			if (pw != null) {
				IOUtils.closeQuietly(pw);
			}
		}
	}
}