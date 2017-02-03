package com.wasu.bpp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtil {
	private static Logger logger = Logger.getLogger(HttpUtil.class);
	private static final String CONTENTTYPE = "application/json";
	private static final String CONTENTTYPE_XML = "text/xml";
	private static final String CHARSETNAME = "UTF-8";
	private static final String PARAM_CONTENTTYPE_JSON = "text/json";
	private static final String PARAM_CONTENTTYPE="application/x-www-form-urlencoded";
	private static int SOCKETTIMEOUT = 10000;//连接超时时间，默认10秒
	private static int CONNECTTIMEOUT = 30000;//传输超时时间，默认30秒
	
    //发送post请求(JSON)
    public static String sendHttpPost(String url, String jsonStr){
    	logger.info("请求JSON字符串: "+jsonStr);    	
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse=null;
        HttpEntity httpEntity=null;
        try{
        	httpClient=HttpClients.createDefault();//创建默认的httpClient实例
        	HttpPost httpPost=new HttpPost(url);//创建http请求(post方式)：httpPost.getRequestLine() & httpPost.getURI()
        	httpPost.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE);
        	
        	//请求参数
        	StringEntity se = new StringEntity(jsonStr, CHARSETNAME);//URLEncoder.encode(jsonStr, CHARSETNAME)
            se.setContentType(PARAM_CONTENTTYPE_JSON);//设置类型
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENTTYPE));//设置编码方式
            httpPost.setEntity(se);//设置表单实体
            
            //执行请求
            logger.info("executing request：" + httpPost.getRequestLine());
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
            
            return EntityUtils.toString(httpEntity, "UTF-8");//响应内容
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
    	logger.info("请求参数字符串: "+paramStr);    	
        CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse=null;
        HttpEntity httpEntity=null;
        try{
        	httpClient=HttpClients.createDefault();//创建默认的httpClient实例
        	HttpGet httpGet=new HttpGet(url+(isEncode?URLEncoder.encode(paramStr, CHARSETNAME):paramStr));//创建http请求(get方式)
        	httpGet.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE);
        	
            //执行请求
        	logger.info("executing request：" + httpGet.getRequestLine());
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
            
            return EntityUtils.toString(httpEntity, "UTF-8");//响应内容
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
    
	//通过Https往API post xml数据
    public static String sendHttpsPost(String url, String xmlStr) throws IOException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {
    	logger.info("请求XML字符串: "+xmlStr);
    	CloseableHttpClient httpClient=null;
        CloseableHttpResponse httpResponse=null;
        HttpEntity httpEntity=null;
        try{
        	ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(Charset.forName(CHARSETNAME)).build();
    		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(CONNECTTIMEOUT).build();
    		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
    		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
    		registryBuilder.register("http", plainSF);
    		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());//指定信任密钥存储对象和连接套接字工厂
    		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new TrustStrategy(){
    			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    				return true;
    			}
    		}).build();
    		LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    		registryBuilder.register("https", sslSF);			
    		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
    		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);//设置连接管理器
    		connManager.setDefaultConnectionConfig(connConfig);
    		connManager.setDefaultSocketConfig(socketConfig);
    		BasicCookieStore cookieStore = new BasicCookieStore();//指定cookie存储对象
    		httpClient=HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setConnectionManager(connManager).build();//构建客户端
        	HttpPost httpPost=new HttpPost(url);//创建http请求(post方式)：httpPost.getRequestLine() & httpPost.getURI()
        	httpPost.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE_XML);
        	
        	//请求参数
        	StringEntity se = new StringEntity(xmlStr, CHARSETNAME);//得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别：URLEncoder.encode(jsonStr, CHARSETNAME)
        	se.setContentType(PARAM_CONTENTTYPE_JSON);//设置类型
        	se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENTTYPE));//设置编码方式
        	httpPost.setEntity(se);//设置表单实体
        	
        	//执行请求
        	logger.info("executing request：" + httpPost.getRequestLine());
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
        	
        	return EntityUtils.toString(httpEntity, "UTF-8");//响应内容
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
    
    //通过Https往API post xml数据：url,API地址；xmlObj,要提交的XML数据对象；mchId,商户号
    public static String sendHttpsCertPost(String url, String xmlStr, String mchId_certPath, String mchId_certPass) throws Exception {
    	FileInputStream fis=null;
    	CloseableHttpClient httpClient=null;
    	CloseableHttpResponse httpResponse =null;
    	HttpEntity httpEntity=null;
    	try {
    		String certPath=ClassLoader.class.getResource("/").getPath()+mchId_certPath;
    		logger.info("请求XML字符串: "+xmlStr+"、退款证书的路径："+certPath+"、退款证书的密码："+mchId_certPass);
    		fis = new FileInputStream(new File(certPath));//退款证书的路径
    		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
    		keyStore.load(fis, mchId_certPass.toCharArray());
    		SSLContext sslCtx = SSLContexts.custom().loadKeyMaterial(keyStore, mchId_certPass.toCharArray()).build();
    		SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslCtx, new String[] {"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    		httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
    		HttpPost httpPost = new HttpPost(url);//创建http请求(post方式)：httpPost.getRequestLine() & httpPost.getURI()
    		httpPost.addHeader(HTTP.CONTENT_TYPE, CONTENTTYPE_XML);
    		httpPost.setConfig(RequestConfig.custom().setSocketTimeout(SOCKETTIMEOUT).setConnectTimeout(CONNECTTIMEOUT).build());
    		
    		//请求参数
    		StringEntity se = new StringEntity(xmlStr, CHARSETNAME);//得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别：URLEncoder.encode(jsonStr, CHARSETNAME)
    		se.setContentType(PARAM_CONTENTTYPE);//设置类型
    		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, CONTENTTYPE));//设置编码方式
    		httpPost.setEntity(se);//设置表单实体
        	
    		//执行请求
    		logger.info("executing request：" + httpPost.getRequestLine());
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
        	
        	return EntityUtils.toString(httpEntity, "UTF-8");//响应内容
      	}catch(Exception e){
      		logger.error("调用接口"+url+"时出错："+e);
      		return null;
      	} finally {
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
            
            if(fis!=null){
      			fis.close();
      			fis=null;
      		}
      	}
    }
}