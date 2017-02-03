package com.wasu.pub.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

public class EncoderByMd5 {
	
	/**日志记录对象*/	
	private static Logger log = Logger.getLogger(EncoderByMd5.class);
	
	/**
	 * 密码加密
	 * 
	 * @return
	 */
	public String encoderByMd5(String str){
		String newstr = "";
		try{
	        MessageDigest md5=MessageDigest.getInstance("MD5");
	        BASE64Encoder base64en = new BASE64Encoder();
	        //加密后的字符串
	        newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
	       
		}catch(Exception e){
			e.printStackTrace();
		}
		 return newstr;
	}

	/**
	 * 
	 * md5的加密算法
	 * 
	 * @param str 要加密的字符串
	 * @return 返回已经加密过的字符串
	 * 
	 * @return String 返回已经加密过的字符串
	 */
	public static String md5(String str) {  
        MessageDigest messageDigest = null;  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.reset();  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
        	log.error( "md5 error!", e );
        } catch (UnsupportedEncodingException e) {  
        	log.error( "md5 error!", e );
        }  
        
        byte[] byteArray = messageDigest.digest();  
        StringBuffer md5StrBuff = new StringBuffer();  
  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
  
        return md5StrBuff.toString();  
    }
}
