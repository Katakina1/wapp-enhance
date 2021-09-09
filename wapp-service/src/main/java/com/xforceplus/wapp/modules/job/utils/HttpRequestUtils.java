package com.xforceplus.wapp.modules.job.utils;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequestUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class); // 日志记录

	/**
	 * 
	 * @Description https请求对象
	 * @return CloseableHttpClient  
	 * @author X Yang
	 * @date 2017年6月28日 下午1:27:47
	 */
	@SuppressWarnings("deprecation")
	private static CloseableHttpClient getHttpClient() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
		ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
		registryBuilder.register("http", plainSF);
		// 指定信任密钥存储对象和连接套接字工厂
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			// 信任任何链接
			TrustStrategy anyTrustStrategy = new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] arg0, String arg1) {return true;}
			};
			SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, anyTrustStrategy).build();
			LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			registryBuilder.register("https", sslSF);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Registry<ConnectionSocketFactory> registry = registryBuilder.build();
		// 设置连接管理器
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
		// 构建客户端
		return HttpClientBuilder.create().setConnectionManager(connManager).build();
	}
	/**
	 * 
	 * @Description https post请求，参数为字符串
	 * @param param
	 * @param url
	 * @param socketTimeout 毫秒数量
	 * @return String
	 * @throws Exception   
	 * @author X Yang
	 * @date 2017年6月28日 下午1:28:30
	 */
	public static String httpPost(String param, String url,Integer socketTimeout) throws Exception {
		CloseableHttpClient httpclient = getHttpClient();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(param, "utf-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			RequestConfig requestConfig = RequestConfig.custom().setProxy(new HttpHost("wmtproxy.homeoffice.cn.wal-mart.com",8080,"http")).setSocketTimeout(socketTimeout).setConnectTimeout(600000).build();
			// 设置请求和传输超时时间
			request.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(request);
			url = URLDecoder.decode(url, "UTF-8");
			logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的字符串数据 **/
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (Exception e) {
					logger.error("post请求提交失败:" + url, e);
				} finally {
					response.close();
				}
			} else {
				result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
			}
		} catch (Exception e) {
			logger.error("post请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}
		return result;
	}
	/**
	 * 
	 * @Description https get请求,请求头为键值对的形式
	 * @param url
	 * @param httpHeader
	 * @return String
	 * @throws IOException    
	 * @author X Yang
	 * @date 2017年6月28日 下午1:26:00
	 */
	public static String httpGet(String url, Map<String, String> httpHeader) throws IOException {
		// get请求返回结果
		CloseableHttpClient httpclient = getHttpClient();
		String strResult = null;
		try {
			// 发送get请求
			HttpGet request = new HttpGet(url);
			// 设置http头参数
			if (httpHeader != null && httpHeader.size() > 0) {
				for (Iterator<Entry<String, String>> ies = httpHeader.entrySet().iterator(); ies.hasNext();) {
					Entry<String, String> entry = ies.next();
					String key = entry.getKey();
					String value = entry.getValue();
					request.addHeader(key, value);
				}
			}
			CloseableHttpResponse response = httpclient.execute(request);
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的json字符串数据 **/
					strResult = EntityUtils.toString(response.getEntity(), "UTF-8");
					url = URLDecoder.decode(url, "UTF-8");
				} catch (Exception e) {
					logger.error("get请求提交失败:" + url);
				} finally {
					response.close();
				}
			}
		} catch (IOException e) {
			logger.error("get请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}
		return strResult;
	}
	/**
	 * 
	 * @Description https post请求,参数为字符串,请求头为键值对
	 * @param param
	 * @param url
	 * @param httpHead
	 * @return String  
	 * @throws Exception   
	 * @author X Yang
	 * @date 2017年6月28日 下午1:24:30
	 */
	public static String httpPost(String param, String url,Map<String, String> httpHead) throws Exception {
		CloseableHttpClient httpclient = getHttpClient();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(param, "utf-8");
			request.setEntity(entity);
			// 设置http头参数
			if (httpHead != null && httpHead.size() > 0)
				for (Iterator<Entry<String, String>> ies = httpHead.entrySet().iterator(); ies.hasNext();) {
					Entry<String, String> entry = ies.next();
					String key = entry.getKey();
					String value = entry.getValue();
					request.addHeader(key, value);
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
			// 设置请求和传输超时时间
			request.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(request);
			url = URLDecoder.decode(url, "UTF-8");
			logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的字符串数据 **/
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (Exception e) {
					logger.error("post请求提交失败:" + url, e);
				} finally {
					response.close();
				}
			} else {
				result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
			}
		} catch (Exception e) {
			logger.error("post请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}
		return result;
	}
	/**
	 * 
	 * @Description https请求,参数为键值对的形式
	 * @param url
	 * @param params
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IOException   
	 * @author X Yang
	 * @date 2017年6月28日 下午1:22:48
	 */
	public static String  requestPost(String url,Map<String, String> params) throws ClientProtocolException, IOException{
		String result = null;
		CloseableHttpClient httpclient = getHttpClient();
		HttpPost post =new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(toNameValuePairs(params),"utf-8"));  
        CloseableHttpResponse response=httpclient.execute(post);
        StatusLine statusLine=response.getStatusLine();
        if(statusLine.getStatusCode() == 200){  
            HttpEntity httpEntity = response.getEntity();  
            result = EntityUtils.toString(httpEntity);  
        } else{
        	logger.error("请求失败！请求链接为："+url+"，返回的请求代码为："+
        			statusLine.getStatusCode()+""+"，错误原因为:"+statusLine.getReasonPhrase());
        }
		return result;
	}
	/**
	 * 
	 * <p>将map集合中的数据转为http请求需要的请求键值对</p>
	 *                       
	 * @return List<NameValuePair>
	 * @author: 赵
	 * @date: Created on 2016年5月30日 上午10:05:01
	 */
	public static List <NameValuePair> toNameValuePairs(Map<String, String> map){
		List <NameValuePair> params = new ArrayList<NameValuePair>();
		
		for(Entry<String, String> entry: map.entrySet()){
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
		}
        return params;
	}
	/**
	 *
	 * @Description https post请求，参数为字符串
	 * @param param
	 * @param url
	 * @return String
	 * @throws Exception
	 * @author X Yang
	 * @date 2017年6月28日 下午1:28:30
	 */
	public static String doPost(String param, String url) throws Exception {
		CloseableHttpClient httpclient = getHttpClient();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(param, "utf-8");
			request.setEntity(entity);
			CloseableHttpResponse response = httpclient.execute(request);
			url = URLDecoder.decode(url, "UTF-8");
			logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的字符串数据 **/
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (Exception e) {
					logger.error("post请求提交失败:" + url, e);
				} finally {
					response.close();
				}
			} else {
				result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
			}
		} catch (Exception e) {
			logger.error("post请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}

		return result;
	}

	/**
	 *
	 * @Description https post请求，参数为字符串
	 * @param param
	 * @param url
	 * @return String
	 * @throws Exception
	 * @author X Yang
	 * @date 2017年6月28日 下午1:28:30
	 */
	public static String doXMLPost(String param, String url) throws Exception {
		CloseableHttpClient httpclient = getHttpClient();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(param, "utf-8");
			request.addHeader("Content-Type","application/soap+xml;charset=UTF-8");
			request.setEntity(entity);

			CloseableHttpResponse response = httpclient.execute(request);
			url = URLDecoder.decode(url, "UTF-8");
			logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的字符串数据 **/
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (Exception e) {
					logger.error("post请求提交失败:" + url, e);
				} finally {
					response.close();
				}
			} else {
				result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
			}
		} catch (Exception e) {
			logger.error("post请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}

		return result;
	}

	/**
	 *
	 * @Description https post请求，参数为字符串 采购问题单获取城市
	 * @param param
	 * @param url
	 * @return String
	 * @throws Exception
	 * @author X Yang
	 * @date 2017年6月28日 下午1:28:30
	 */
	public static String doXMLCityPost(String param, String url) throws Exception {
		CloseableHttpClient httpclient = getHttpClient();
		String result = null;
		try {
			HttpPost request = new HttpPost(url);
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(param, "utf-8");
			request.addHeader("Content-Type","text/xml; charset=utf-8");
			request.setEntity(entity);

			CloseableHttpResponse response = httpclient.execute(request);
			url = URLDecoder.decode(url, "UTF-8");
			logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					/** 读取服务器返回过来的字符串数据 **/
					result = EntityUtils.toString(response.getEntity(), "UTF-8");
				} catch (Exception e) {
					logger.error("post请求提交失败:" + url, e);
				} finally {
					response.close();
				}
			} else {
				result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
			}
		} catch (Exception e) {
			logger.error("post请求提交失败:" + url, e);
		} finally {
			httpclient.close();
		}

		return result;
	}

    /**
     * 调用API写屏接口
     * @param param
     * @param url
     * @return
     * @throws Exception
     */
    public static String httpPostApi(String param, String url,Integer socketTimeout) throws Exception {
        CloseableHttpClient httpclient = getHttpClient();
        String result = null;
        try {
            HttpPost request = new HttpPost(url);
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(param, "utf-8");
            entity.setContentType("application/json");
            request.setEntity(entity);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(3600000).build();
             //设置请求和传输超时时间
            request.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(request);
            url = URLDecoder.decode(url, "UTF-8");
            logger.info("请求接口：" + url + ",执行状态：" + response.getStatusLine().getStatusCode());
            /** 请求发送成功，并得到响应 **/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                try {
                    /** 读取服务器返回过来的字符串数据 **/
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                } finally {
                    response.close();
                }
            } else {
                result = response.getStatusLine().getStatusCode() + "-" + response.getStatusLine().getReasonPhrase();
            }
        } catch (Exception e) {
            logger.error("post请求提交失败:" + url, e);
        } finally {
            httpclient.close();
        }
        return result;
    }

}
