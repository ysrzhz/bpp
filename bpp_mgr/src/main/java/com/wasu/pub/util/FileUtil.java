package com.wasu.pub.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class FileUtil {
	private static final Logger logger = Logger.getLogger(FileUtil.class);
	private final static String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

	/**
	 * 返回数据格式为 宽*高
	 * 
	 * @param imageFile
	 * @return
	 */
	public static int[] readPicRatio(String imageFile) {
		BufferedImage image = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imageFile);
			image = javax.imageio.ImageIO.read(fis);
		} catch (FileNotFoundException e) {
			logger.error("can't find imageFile ,url=[" + imageFile + "]", e);
			return null;
		} catch (IOException e) {
			logger.error("can't find imageFile ,url=[" + imageFile + "]", e);
			return null;
		} finally {
			IOUtils.closeQuietly(fis);
		}
		int[] array = new int[2];
		array[1] = image.getHeight();
		array[0] = image.getWidth();

		return array;
	}

	public static boolean renameFile(File sourceFile, File destFile) {

		//判断父文件夹是否存储
		//if (!destFile.getParentFile().exists()) {
		//destFile.getParentFile().mkdirs();
		//}
		//判断子文件是否存在,存在则删除
		//if (destFile.exists()) {
		//destFile.deleteOnExit();
		//}

		boolean isSucc = false;
		try {
			FileUtils.copyFile(sourceFile, destFile);
			isSucc = true;
		} catch (IOException e) {
			logger.error("文件拷贝失败", e);
		}

		logger.info(sourceFile.getAbsolutePath() + File.separator
				+ sourceFile.getName() + " --> copy to --> " + destFile
				+ " 文件拷贝结果:" + isSucc);
		sourceFile.deleteOnExit();
		return isSucc;
	}

	/**
	 * 检查海报真实尺寸 1.如果尺寸为空 或不存在，则默认取其真实尺寸； 2.有尺寸则校验尺寸大小,不匹配抛出错误
	 * 
	 * @param file
	 * @param posterType
	 * @return
	 */
	public static String checkImageFileSize(File file) {
		BufferedImage image = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			image = javax.imageio.ImageIO.read(fis);
		} catch (FileNotFoundException e) {
			logger.error("not find file.", e);
			return null;
		} catch (IOException e) {
			logger.error("read file error.", e);
			return null;
		} finally {
			IOUtils.closeQuietly(fis);
		}

		Integer height = image.getHeight();
		Integer width = image.getWidth();
		return width + "x" + height;
	}

	/**
	 * 保存xml文本内容到本地 如果文件名未提供，则以当前时间作为文件名.xml
	 * 
	 * @param dir
	 * @param fileName
	 * @param message
	 * @return
	 */
	public static boolean saveXML(String message, String dir, String fileName) {
		if (null == fileName || fileName.trim().isEmpty()) {
			fileName = DateUtil.date2Str(new Date(), YYYYMMDDHHMMSSSSS) + ".xml";
		}
		
		File file = new File(dir + File.separator + fileName);
		File parentDir = file.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			IOUtils.write(message, fos);
			return true;
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			IOUtils.closeQuietly(fos);
		}
		return false;
	}

	/**
	 * 文件下载，存放当前项目的根目录
	 * 
	 * @param httpUrl
	 *            文件URL
	 * @param maxSize
	 *            文件大小 字节 小于0 则下载不限制大小
	 * @return
	 */
	public static File downloadFile(String httpUrl, String toPath, long maxSize) {
		File resultFile = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		HttpURLConnection httpconn = null;
		try {
			/**
			 * 重构 关闭HTTP连接
			 */
			int HttpResult = 0; //服务器返回的状态
			URL postHttpUrl = new URL(httpUrl); //创建URL
			URLConnection urlconn = postHttpUrl.openConnection(); //试图连接并取得返回状态码urlconn.connect();
			urlconn.setConnectTimeout(10000);//10秒超时
			urlconn.setReadTimeout(30000);//30秒读超时
			urlconn.setDoOutput(true);

			httpconn = (HttpURLConnection) urlconn;
			HttpResult = httpconn.getResponseCode();

			if (HttpResult != HttpURLConnection.HTTP_OK) { //不等于HTTP_OK说明连接不成功
				logger.error(httpUrl + "：连接不成功");
				return null;
			} else {
				long filesize = urlconn.getContentLength(); //取数据长度
				logger.debug("海报filesize = " + filesize);

				//maxSize大于0则进行文件下载校验,小于0不校验大小
				if (filesize > maxSize && maxSize > 0) {
					logger.error("要下载的海报文件太大，超过maxSize=" + maxSize + "chars");
					return null;
				}
				bis = new BufferedInputStream(urlconn.getInputStream());
				if (StringUtils.isNotEmpty(toPath)) {
					if (!toPath.endsWith("/")) {
						toPath += "/";
					}
					String fileName = httpUrl.substring(httpUrl
							.lastIndexOf("/") + 1);
					String suffix = fileName.indexOf(".") > -1 ? fileName
							: fileName + ".jpg";
					File dirs = new File(toPath);
					if (!dirs.exists()) {
						dirs.mkdirs();
					}
					resultFile = new File(toPath + EncoderByMd5.md5(httpUrl)
							+ suffix);
				} else {
					resultFile = new File(httpUrl.substring(httpUrl
							.lastIndexOf("/") + 1));
				}

				bos = new BufferedOutputStream(new FileOutputStream(resultFile));
				byte[] buffer = new byte[1024]; //创建存放输入流的缓冲
				int num = 0; //读入的字节数
				num = bis.read(buffer);
				while (num != -1) {
					bos.write(buffer, 0, num);
					bos.flush();
					num = bis.read(buffer); //读入到缓冲区
				}
			}
		} catch (Exception e) {
			logger.error("下载文件失败， 地址为： " + httpUrl, e);
			return null;
		} finally {
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(bis);

			//关闭http连接
			if (httpconn != null) {
				httpconn.disconnect();
			}
		}

		return resultFile;
	}

	public static String txt2String(File file) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
				result = result + "\n" + s;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
