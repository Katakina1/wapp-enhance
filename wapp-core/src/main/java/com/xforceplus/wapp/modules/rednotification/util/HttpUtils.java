package com.xforceplus.wapp.modules.rednotification.util;

import com.xforceplus.apollo.client.utils.ChineseUtil;
import com.xforceplus.apollo.utils.ErrorUtil;
import com.xforceplus.apollo.utils.JacksonUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public  class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;

    private static final int MAX_TIMEOUT = 8000;

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private static  List<String> whitelist = new ArrayList<>();


    public static String host;

    @Value("${wapp.integration.host.http}")
    public  void setHost(String host) {
        HttpUtils.host = host;
        //添加白名单
        whitelist.add(host);
    }

    static {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", createSSLConnSocketFactory())
                .build();
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager(registry);
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();

        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    private static void checkWhitelist(String url) throws RuntimeException{
        if(!whitelist.contains(url)){
            throw new RuntimeException("访问地址不在白名单内");
        }
    }

    private static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    public static String doPost(String url) throws IOException {

        return doPost(url, null);
    }

    public static String doGet(String url, boolean json) throws IOException {

        return doGet(url, null, json);
    }

    public static String doPost(String url, Map<String, Object> params) throws IOException {
        List<NameValuePair> pairList = convertToPair(params);
        return doPostl(url,pairList);
    }

    /**
     *
     * @param params
     * @author xuchuanhou
     */
    private static List<NameValuePair> convertToPair(Map<String, Object> params) {
        List<NameValuePair> pairList = new ArrayList<>();
        if (MapUtils.isEmpty(params)) {
            return pairList;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            pairList.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
        }

        return pairList;
    }


    public static String doPostl(String url, List<NameValuePair> pairs) throws IOException {
        String result = null;
        InputStream instream = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(pairs, DEFAULT_CHARSET);
            httpPost.setEntity(uefEntity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        } finally {
            if (null != instream) {
                instream.close();
            }
        }

        return result;
    }


    public static String doGet(String url, Map<String, String> params, boolean json) throws IOException {
            return doGet(url,params,json,null);
    }

    public static String doGet(String url, Map<String, String> params, boolean json, Map<String, String> headers) throws IOException {
        String result = null;
        InputStream instream = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(buildQueryUrlWithParams(url,params));
            httpget.setConfig(requestConfig);
            if (json) {
                httpget.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            }
            if(MapUtils.isNotEmpty(headers)){
                for (Map.Entry<String,String> entry: headers.entrySet()) {
                    httpget.addHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        } finally {
          // IOUtil.closeQuietly(instream);
        }
        return result;
    }


    public static String doGet(String url, Map<String, String> params, int timeout) throws IOException {
        String result = null;
        InputStream instream = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet(buildQueryUrlWithParams(url,params));

            RequestConfig requestConf = RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .build();
            httpget.setConfig(requestConf);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        } finally {
          //  IOUtil.closeQuietly(instream);
        }
        return result;
    }


    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder query = new StringBuilder();
        boolean hasParam = false;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            // 忽略参数名或参数值为空的参数
            if (!StringUtils.isEmpty(entry.getKey())&&!StringUtils.isEmpty(entry.getValue()) ) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append( entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), charset));
            }
        }

        return query.toString();
    }


    public static String getStreamAsString(InputStream stream, String charset) throws IOException {
        String result = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(stream, charset));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();

        } finally {
            //IOUtil.closeQuietly(stream);
          //  IOUtil.closeQuietly(in);
        }
        return result;
    }

    public static String http(String url, Map<String, String> params) {
        URL u = null;
        HttpURLConnection con = null;
        // 构建请求参数
        StringBuffer sb = new StringBuffer();
        if (params != null) {
            for (Map.Entry<String, String> e : params.entrySet()) {
                sb.append(e.getKey());
                sb.append("=");
                sb.append(e.getValue());
                sb.append("&");
            }
            sb.substring(0, sb.length() - 1);
        }

        // 尝试发送请求
        try {
            u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            //// POST 只能为大写，严格限制，post会不识别
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), DEFAULT_CHARSET);
            osw.write(sb.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            //ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        // 读取返回内容
        StringBuffer buffer = new StringBuffer();
        try {
            //一定要有返回值，否则无法把请求发送给server端。
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "GBK"));
            String temp;
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        } catch (Exception e) {
           // ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
        }

        return buffer.toString();
    }


    public static String doPostJson(String url, String json) throws IOException  {
        return doPostJson(url,json,null);
    }

    public static String doPostJson(String url, String json,Map<String,String> headers) throws IOException  {
       return doPostJson(url,json,headers,null);
    }

    public static String doPostJson(String url, String json,Map<String,String> headers,Map<String,String> params) throws IOException {
        String result = null;
        InputStream instream = null;
        try {
            // 将JSON进行UTF-8编码,以便传输中文
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(buildQueryUrlWithParams(url,params));
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            if(MapUtils.isNotEmpty(headers)){
                for (Map.Entry<String,String> entry: headers.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            if(StringUtils.isNotEmpty(json)){
                StringEntity se = new StringEntity(json, DEFAULT_CHARSET);
                se.setContentType(CONTENT_TYPE_TEXT_JSON);
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, DEFAULT_CHARSET));

                httpPost.setEntity(se);
            }

            httpPost.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        }catch (Exception e){
            logger.info("调用postJson异常",e);
        }finally {
            if (instream!=null){
                instream.close();
            }
        }
        return result;
    }





    public static String doPostJsonSkipSsl(String url, String json) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        String result = null;
        InputStream instream = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);

            StringEntity se = new StringEntity(json, DEFAULT_CHARSET);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
          //  ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
            return null;
        } finally {
            if (null != instream)
                try {
                    instream.close();
                } catch (IOException e) {
                  //  ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
                }
        }
        return result;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(DEFAULT_CHARSET)));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, DEFAULT_CHARSET);
        } catch (Exception e) {
           // ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                   // ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     *
     * @param apiUrl API接口URL
     * @param json   JSON对象
     * @return
     */
    public static String doPostSSL(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), DEFAULT_CHARSET);//解决中文乱码问题
            stringEntity.setContentEncoding(DEFAULT_CHARSET);
            stringEntity.setContentType(APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, DEFAULT_CHARSET);
        } catch (Exception e) {
           // ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                   // ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
                }
            }
        }
        return httpStr;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {

            //采用绕过验证的方式处理https请求
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null,
                    new TrustStrategy() {
                        // 信任所有
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();

            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            //ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
        }
        return sslsf;
    }



    private static void outputFoot(String boundary, OutputStream os) throws IOException {
        os.write(("--" + boundary + "--" + "\r\n").getBytes());
        os.flush();
    }

    public static String buildQueryUrlWithParams(String url,Map<String,String> params) throws IOException {
        if(MapUtils.isEmpty(params)){
            return url;
        }

        StringBuilder urlSb = new StringBuilder(url);
        String urlParam = buildQuery(params, DEFAULT_CHARSET);
        if (url.contains("?") && url.contains("=")) {
            urlSb.append("&");
        } else {
            urlSb.append("?");
        }
        urlSb.append(urlParam);
        logger.info("buildQueryUrlWithParams:{}",urlSb.toString());
        return urlSb.toString();
    }

    public static String doPutHttpRequest(String url, Map<String, String> headerMap,String requestBody) {
        checkWhitelist(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String entityStr = null;
         CloseableHttpResponse response = null;
         try {
                 HttpPut post = new HttpPut(url);
                 //添加头部信息
                 for (Map.Entry<String, String> header : headerMap.entrySet()) {
                         post.addHeader(header.getKey(), header.getValue());
                     }
                 HttpEntity entity = new StringEntity(requestBody,"Utf-8");

                 post.setEntity(entity);
                 response = httpClient.execute(post);
                 // 获得响应的实体对象
                 HttpEntity httpEntity = response.getEntity();
                // 使用Apache提供的工具类进行转换成字符串
                 entityStr = EntityUtils.toString(httpEntity, "UTF-8");

             } catch (ClientProtocolException e) {
                 logger.error("Http协议出现问题",e);

             } catch (ParseException e) {
                 logger.error("解析错误",e);
             } catch (IOException e) {
                 logger.error("IO异常",e);
             }
         return entityStr;
     }

    /**
     * 集成平台压缩header
      * @param headerMap
     * @param action
     * @param authentication
     * @return
     */
    public static boolean pack(Map<String, String> headerMap, String action,String authentication) {
        boolean result = true;
        if (StringUtils.isBlank(action)) {
            logger.error("action信息不能为空");
            result = false;
        }
        if (result) {
            Map<String, String> extInfoMap = new HashMap();
            headerMap.forEach((key, value) -> {
                if (ChineseUtil.hasChinese(value)) {
                    try {
                        extInfoMap.put(key, URLEncoder.encode(value, "utf-8"));
                    } catch (UnsupportedEncodingException var4) {
                        logger.error(ErrorUtil.getStackMsg(var4));
                    }
                }

            });
            if (!extInfoMap.isEmpty()) {
                headerMap.put("extInfo", JacksonUtil.getInstance().toJson(extInfoMap));
                headerMap.keySet().removeIf((key) -> {
                    return extInfoMap.containsKey(key);
                });
            }

            headerMap.put("httpMethod", "put");
            headerMap.put("action", action);
            headerMap.put("Authentication", authentication);
            headerMap.put("rpcType", "http");
        }

        return result;
    }


    public static String doPutHttpRequest(String url, String json,Map<String,String> headers,Map<String,String> params) throws IOException {
        String result = null;
        InputStream instream = null;
        try {
            // 将JSON进行UTF-8编码,以便传输中文
            CloseableHttpClient httpClient =HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(buildQueryUrlWithParams(url,params));
            httpPut.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            if(MapUtils.isNotEmpty(headers)){
                for (Map.Entry<String,String> entry: headers.entrySet()) {
                    httpPut.addHeader(entry.getKey(), entry.getValue());
                }
            }
            if(StringUtils.isNotEmpty(json)){
                StringEntity se = new StringEntity(json, DEFAULT_CHARSET);
                se.setContentType(CONTENT_TYPE_TEXT_JSON);
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, DEFAULT_CHARSET));
                httpPut.setEntity(se);
            }
            httpPut.setConfig(requestConfig);
            CloseableHttpResponse response  = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        }catch (Exception e){
            logger.info("调用postJson异常",e);
        }finally {
            if (instream!=null){
                instream.close();
            }
        }
        return result;
    }




    public static String doPutJsonSkipSsl(String url,Map<String,String> headers ,String json) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        String result = null;
        InputStream instream = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPut.addHeader(entry.getKey(), entry.getValue());
            }


            StringEntity se = new StringEntity(json, DEFAULT_CHARSET);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPut.setEntity(se);
            HttpResponse response = httpClient.execute(httpPut);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                instream = entity.getContent();
                result = getStreamAsString(instream, DEFAULT_CHARSET);
            }
        } catch (Exception e) {
            //  ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
            return null;
        } finally {
            if (null != instream)
                try {
                    instream.close();
                } catch (IOException e) {
                    //  ApolloLoggerFactory.getFactory().loggerError(ErrorUtil.holdExceptionMsg(e));
                }
        }
        return result;
    }

}
