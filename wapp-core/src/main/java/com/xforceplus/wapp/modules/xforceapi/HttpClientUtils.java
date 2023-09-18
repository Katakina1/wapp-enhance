package com.xforceplus.wapp.modules.xforceapi;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {
    final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    final static String DEFAULT_CHARSET = "UTF-8";//默认编码
    final static int DEFAULT_SOCKET_TIMEOUT = 60000;//默认超时时间
    final static int DEFAULT_CONNECT_TIMEOU = 60000;//默认连接超时时间

    /**
     * http get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        return httpGet(url, null, null, DEFAULT_CHARSET, getIsProxy());
    }

    /**
     * http get请求
     *
     * @param url
     * @return
     */
    public static String get(String url, Map<String, String> header, Map<String, String> params) {
        return httpGet(url, params, header, DEFAULT_CHARSET, getIsProxy());
    }

    /**
     * http get请求
     *
     * @param url
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        return httpGet(url, params, null, DEFAULT_CHARSET, getIsProxy());
    }

    /**
     * http post 请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        return httpPost(url, params, null, DEFAULT_CHARSET, getIsProxy());
    }

    /**
     * http post Json
     *
     * @param url
     * @param jsonParams
     * @return
     */
    public static String postJson(String url, String jsonParams) {
        return httpPostJson(url, jsonParams, null, DEFAULT_CHARSET, getIsProxy());
    }

    public static String postJson2(String url, String jsonParams) {
        return httpPostJson2(url, jsonParams, null, DEFAULT_CHARSET, getIsProxy());
    }

    /**
     * http post Json
     *
     * @param url
     * @param jsonParams
     * @return
     */
    public static String postJson(String url, String jsonParams, Map<String, String> header) {
        return httpPostJson(url, jsonParams, header, DEFAULT_CHARSET, getIsProxy());
    }

    private static boolean getIsProxy() {
        String isProxy = "false";
        if (StringUtils.isBlank(isProxy)) {
            return false;
        }
        return Boolean.parseBoolean(isProxy);
    }

    private static RequestConfig getRequestConfig(boolean isProxy) {
        String host = "", port = "";
        if (isProxy && StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port)) {
            logger.info("HttpURLConnectionUtils set proxy host:{},port:{}", host, port);
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);
        }
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).setConnectTimeout(DEFAULT_CONNECT_TIMEOU);
        if (isProxy && StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port)) {
            builder.setProxy(new HttpHost(host, Integer.parseInt(port)));
        }
        return builder.build();
    }

    /**
     * .文件下载并且base64
     *
     * @param fileUrl
     * @return
     * @throws IOException
     */
    public static String fileDownload(String fileUrl) throws IOException {
        if (StringUtils.isBlank(fileUrl)) {
            return "";
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        // 设置超时间为5秒
        connection.setConnectTimeout(5 * 1000);
        // 防止屏蔽程序抓取而返回403错误
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        // 得到输入流
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, len);
        }
        return Base64.encodeBase64String(swapStream.toByteArray());
    }

    public static void main(String[] args) throws IOException {
        System.out.println(fileDownload("http://10.239.188.1:18281/uatscm/ZPImgDownload?fileid=PN1800120110001986118&sheetid=PN1800120110001"));
    }

    /**
     * Post 请求
     *
     * @param url
     * @param params
     * @param header
     * @param charset
     * @return
     */
    public static String httpPost(String url, Map<String, String> params, Map<String, String> header, String charset, boolean isProxy) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpClient = getCloseableHttpClient(url);
            httpPost = new HttpPost(url);
            httpPost.setConfig(getRequestConfig(isProxy));
            //set params post参数
            List<NameValuePair> listParams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                listParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //设置参数到请求对象中
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //设置头
            if (header != null && header.size() > 0) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(listParams, charset));
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException(String.format("request %s error,status: %s,msg：%s", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), charset)));
            }
            return EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeHttp(httpClient, response, httpPost, null);
        }
    }

    /**
     * Post 请求
     *
     * @param url
     * @param params
     * @param header
     * @param charset
     * @return
     */
    public static String httpPostJson(String url, String jsonParams, Map<String, String> header, String charset, boolean isProxy) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpClient = getCloseableHttpClient(url);
            httpPost = new HttpPost(url);
            httpPost.setConfig(getRequestConfig(isProxy));
            //设置参数到请求对象中
            httpPost.setHeader("Content-type", "application/json");
            //httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //设置头
            if (header != null && header.size() > 0) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            logger.info("request url ={}", url);
            StringEntity stringEntity = new StringEntity(jsonParams, ContentType.APPLICATION_JSON);
            stringEntity.setContentEncoding(charset);
            httpPost.setEntity(new StringEntity(jsonParams, ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException(String.format("request json %s error,status: %s,msg：%s", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), charset)));
            }
            String content = EntityUtils.toString(response.getEntity(), charset);
            logger.info("response={}", content);
            return content;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            closeHttp(httpClient, response, httpPost, null);
        }
    }

    public static String httpPostJson2(String url, String jsonParams, Map<String, String> header, String charset, boolean isProxy) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpClient = getCloseableHttpClient(url);
            httpPost = new HttpPost(url);
            httpPost.setConfig(getRequestConfig(isProxy));
            //设置参数到请求对象中
            httpPost.setHeader("Content-type", "application/json");
            //httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //设置头
            if (header != null && header.size() > 0) {
                for (String key : header.keySet()) {
                    httpPost.setHeader(key, header.get(key));
                }
            }
            logger.info("request url ={}", url);
            StringEntity stringEntity = new StringEntity(jsonParams, ContentType.APPLICATION_JSON);
            stringEntity.setContentEncoding(charset);
            httpPost.setEntity(new StringEntity(jsonParams, ContentType.APPLICATION_JSON));
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException(String.format("request json %s error,status: %s,msg：%s", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), charset)));
            }
            String content = EntityUtils.toString(response.getEntity(), charset);
            logger.info("response={}", content);
            return content;
        } catch (Exception e) {
            // throw new RuntimeException(e.getMessage(), e);
            return "";
        } finally {
            closeHttp(httpClient, response, httpPost, null);
        }
    }


    /**
     * Get 请求
     *
     * @param url
     * @param param
     * @param header
     * @param charset
     * @return
     */
    public static String httpGet(String url, Map<String, String> param, Map<String, String> header, String charset, boolean isProxy) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        try {
            httpClient = getCloseableHttpClient(url);
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            httpGet = new HttpGet(builder.build());
            //设置头
            if (header != null && header.size() > 0) {
                for (String key : header.keySet()) {
                    httpGet.setHeader(key, header.get(key));
                }
            }
            httpGet.setConfig(getRequestConfig(isProxy));
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException(String.format("request %s error,status: %s,msg：%s", url, response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(), charset)));
            }
            return EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            closeHttp(httpClient, response, null, httpGet);
        }
    }

    private static CloseableHttpClient getCloseableHttpClient(String url) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        CloseableHttpClient httpClient = null;
        if (url.startsWith("https:")) {
            httpClient = getHttpsClient();
        } else {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }

    private static void closeHttp(CloseableHttpClient httpClient, CloseableHttpResponse response, HttpPost httpPost, HttpGet httpGet) {
        if (httpGet != null) {
            httpGet.releaseConnection();
        }
        if (httpPost != null) {
            httpPost.releaseConnection();
        }
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 创建CloseableHttpClient
     *
     * @return
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static CloseableHttpClient getHttpsClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setConnectionRequestTimeout(10000)
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOU).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();
        //SSL 过滤
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        });
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", trustAllHttpsCertificates()).build();
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registry);
        manager.setMaxTotal(200);

        // http 请求默认设置
        HttpClientBuilder custom = HttpClients.custom();
        custom.setDefaultRequestConfig(globalConfig);
        custom.setSSLSocketFactory(sslConnectionSocketFactory);
        custom.setConnectionManager(manager);
        custom.setConnectionManagerShared(true);
        return custom.build();
    }

    /**
     * SSL https 构建
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLConnectionSocketFactory trustAllHttpsCertificates() throws NoSuchAlgorithmException, KeyManagementException {
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

}