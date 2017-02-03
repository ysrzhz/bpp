package com.wasu.bpp.util;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class);
	private static final String CONTENTTYPE = "application/json";
	private static final String CHARSETNAME = "UTF-8";
	private static final String PARAM_CONTENTTYPE = "text/json";
	
    //发送post请求(JSON)
    public static String sendHttpPost(String url, String jsonStr){
    	if(logger.isInfoEnabled()){
    		logger.info("请求JSON字符串: "+jsonStr);
    	}
    	
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse=null;
        HttpEntity httpEntity=null;
        try{
        	httpClient=HttpClients.createDefault();//创建默认的httpClient实例
        	HttpPost httpPost=new HttpPost(url);//创建http请求(post方式)：httpPost.getRequestLine() & httpPost.getURI()
        	httpPost.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE);
        	
        	//请求参数
        	StringEntity se = new StringEntity(jsonStr, CHARSETNAME);//URLEncoder.encode(jsonStr, CHARSETNAME)
            se.setContentType(PARAM_CONTENTTYPE);//设置类型
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENTTYPE));//设置编码方式
            httpPost.setEntity(se);//设置表单实体
            
            //执行请求
            httpResponse=httpClient.execute(httpPost);
            StatusLine sl=httpResponse.getStatusLine();
    		if(sl==null || sl.getStatusCode()!=200){
    			logger.error("Http request error: "+sl);
    			return null;
    		}
            
            //获取数据
            httpEntity=httpResponse.getEntity();
            if(httpEntity==null){
            	return null;
            }
            
            String retJsonStr=EntityUtils.toString(httpEntity, "UTF-8");//响应内容
            if(logger.isInfoEnabled()){
        		logger.info("返回JSON字符串: "+retJsonStr);
        	}
            
            return retJsonStr;
        }catch(Exception e){
        	logger.error("Http request error: "+e.getMessage());
            return null;
        }finally{
            try{
                if(httpEntity!=null){
                	EntityUtils.consume(httpEntity);
                	httpEntity=null;
	            }
                
                if(httpResponse!=null){
                	httpResponse.close();
                	httpResponse=null;
                }
                
                if(httpClient!=null){
            		httpClient.close();//关闭连接,释放资源
            		httpClient=null;
            	}
            }catch(Exception e){
            }
        }
    }
    
    //发送get请求(JSON)：isEncode,是否加密
    public static String sendHttpGet(String url, String paramStr, boolean isEncode){
    	if(logger.isInfoEnabled()){
    		logger.info("请求参数字符串: "+paramStr);
    	}
    	
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse=null;
        HttpEntity httpEntity=null;
        try{
        	httpClient=HttpClients.createDefault();//创建默认的httpClient实例
        	HttpGet httpGet=new HttpGet(url+(isEncode?URLEncoder.encode(paramStr, CHARSETNAME):paramStr));//创建http请求(get方式)
        	httpGet.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE);
        	
            //执行请求
            httpResponse=httpClient.execute(httpGet);
            StatusLine sl=httpResponse.getStatusLine();
    		if(sl==null || sl.getStatusCode()!=200){
    			logger.error("Http request error: "+sl);
    			return null;
    		}
            
            //获取数据
            httpEntity=httpResponse.getEntity();
            if(httpEntity==null){
            	return null;
            }
            
            String retJsonStr=EntityUtils.toString(httpEntity, "UTF-8");//响应内容
            if(logger.isInfoEnabled()){
        		logger.info("返回JSON字符串: "+retJsonStr);
        	}
            
            return retJsonStr;
        }catch(Exception e){
        	logger.error("Http request error: "+e.getMessage());;
            return null;
        }finally{
            try{
                if(httpEntity!=null){
                	EntityUtils.consume(httpEntity);
                	httpEntity=null;
	            }
                
                if(httpResponse!=null){
                	httpResponse.close();
                	httpResponse=null;
                }
                
                if(httpClient!=null){
            		httpClient.close();//关闭连接,释放资源
            		httpClient=null;
            	}
            }catch(Exception e){
            }
        }
    }
    
    //调用状态同步接口
    public static void notifyMsgStatus(String dataSrcUrl, String paramStr){
        if(StringUtils.isNotBlank(dataSrcUrl)){//消息同步接口地址不为空
        	String jsonStr=sendHttpGet(dataSrcUrl, paramStr, true);//发送get请求(JSON)
        	JSONObject retObj=JSONObject.fromObject(jsonStr);
        	if(retObj==null || retObj.isNullObject()){//接口返回错误
        		logger.error("调用状态同步接口失败，请重试");
        		return;
        	}
        	
        	if(!"0".equals(retObj.getString("retCode"))){//接口返回错误
        		logger.error("调用状态同步接口失败："+retObj.getString("retMsg"));
        		return;
        	}
        }
    }
    
    //调用终端列表查询接口
    public static JSONObject getStbList(String getStbListUrl, String areaId, String custId, long currPage, long showNum) throws Exception{
  		String jsonStr=sendHttpGet(getStbListUrl, "regionId="+areaId+"&custCode="+custId+"&page="+currPage+"&rows="+showNum, false);//发送get请求(JSON)
  		JSONObject retObj=JSONObject.fromObject(jsonStr);
  		if(retObj==null || retObj.isNullObject()){//接口返回错误
  			logger.error("调用终端列表查询接口失败，请重试");
  			throw new Exception("调用终端列表查询接口失败，请重试");
  		}
  		
  		if(!"0".equals(retObj.getString("result"))){//接口返回错误
  			logger.error("调用终端列表查询接口失败："+retObj.getString("desc"));
  			throw new Exception("调用终端列表查询接口失败："+retObj.getString("desc"));
  		}
  		
  		return retObj.getJSONObject("page");
  	}
}