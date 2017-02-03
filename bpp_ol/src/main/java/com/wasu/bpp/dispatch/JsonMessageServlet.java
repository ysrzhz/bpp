package com.wasu.bpp.dispatch;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.wasu.pub.WSException;
import org.apache.log4j.Logger;
public class JsonMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(JsonMessageServlet.class);
    private static JsonActionDispatcher dispatcher;
    public  void  init() throws ServletException {
	    try{
	    	if(dispatcher==null){
	    		ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	    		dispatcher = new JsonActionDispatcher(applicationContext);
	    	}
	    }catch(Exception ex){
	    	logger.error(ex);
	    }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	long startTime =System.currentTimeMillis();
		String uri=req.getRequestURI();
		if(uri.indexOf(".")>=0){//判断uri是否是接口调用
			throw new WSException("接口调用出错，请求地址不正确","");
		}
		
		dispatcher.execute(req, resp);
		logger.info("请求接口["+uri+"]执行时间为："+(System.currentTimeMillis()-startTime)+" ms");
    }
}