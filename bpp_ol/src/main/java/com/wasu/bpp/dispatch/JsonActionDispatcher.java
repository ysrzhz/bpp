package com.wasu.bpp.dispatch;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wasu.pub.WSException;
import com.wasu.bpp.action.BaseAction;
import com.wasu.bpp.annoation.SkipJSon;
import com.wasu.bpp.util.BeanUtil;
import com.wasu.bpp.util.LowerCaseFieldNamingStrategy;
import com.wasu.bpp.util.Streams;

public class JsonActionDispatcher {

	private ApplicationContext applicationContext;

	public JsonActionDispatcher(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 处理所有的数据
	 *
	 * @param req
	 * @param resp
	 */
	private static Logger logger = Logger.getLogger(JsonActionDispatcher.class);
	protected static GsonBuilder gsonBuilder;

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
			.serializeNulls()
			.setExclusionStrategies(new ExclusionStrategy[] { exclusionStrategy })
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.setFieldNamingStrategy(new LowerCaseFieldNamingStrategy());
	}

	public void execute(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI().toString();
		String beanName = BeanUtil.getMethodName(uri);
		BaseAction baseActon = (BaseAction) applicationContext.getBean(beanName);
		if (baseActon == null) {
			throw new WSException("获取请求失败", "");
		}
		
		baseActon.execute(request, response);
	}

	protected <T> T getEntityFromRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
		String json = Streams.asString(request.getInputStream(), "UTF-8");
		if (logger.isDebugEnabled()){
			logger.debug("request content:\t{}" + json);
		}
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
}