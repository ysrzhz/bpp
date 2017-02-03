package com.wasu.pub.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.wasu.pub.util.Constant;

public abstract class EntityController<T> extends BaseController {
	public abstract String getEntityName();
	public abstract String getEntityClassName();

	@RequestMapping(value = "/add")
	public String add() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		result.put("message", "处理成功！");
		return getEntityName() + "/" + getEntityName() + "_edit";
	}

	@RequestMapping(value = "/del", produces = { "application/json;charset=UTF-8" })
	public @ResponseBody Map del(HttpServletRequest request, String ids) {
		List<Long> idList = convertIdsStr2Long(ids, Constant.COMMA_SEPARATOR);
		baseService.delete(getEntityClassName(), idList);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		result.put("message", "处理成功！");
		return result;
	}

	@RequestMapping(value = "/edit/{id}")
	public ModelAndView edit(@PathVariable Integer id) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(getEntityName() + "/" + getEntityName() + "_edit");
		mv.addObject("id", id);
		return mv;
	}

	@RequestMapping(value = "/get/{id}", produces = { "application/json;charset=UTF-8" })
	public @ResponseBody String get(@PathVariable Long id) {
		T policy = (T) baseService.getById(getEntityClassName(), id);
		return getJson(policy);
	}

	protected String getJson(Object o) {
		Gson gson = gsonBuilder.create();
		String json = gson.toJson(o).replace("null", "\"\""); //统一null转""
		return json;
	}
}