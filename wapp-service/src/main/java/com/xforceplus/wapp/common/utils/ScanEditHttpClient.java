package com.xforceplus.wapp.common.utils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.http.*;


import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;


import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import net.sf.json.JSONObject;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
public class ScanEditHttpClient {
    protected static Logger log = Logger.getLogger(ScanEditHttpClient.class);



//    public static void upload(String url,byte[] fileByte,String filename) {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        try {
//            HttpPost httppost = new HttpPost(url);
//            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build();
//            httppost.setConfig(requestConfig);
//            HttpEntity req = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
//                    .addPart("file", fileByte)
//                    .build();
//            httppost.setEntity(req);
//
//            System.out.println("executing request " + httppost.getRequestLine());
//            CloseableHttpResponse response = httpclient.execute(httppost);
//            try {
//                System.out.println(response.getStatusLine());
//                HttpEntity resEntity = response.getEntity();
//                if (resEntity != null) {
//                    String responseEntityStr = EntityUtils.toString(response.getEntity());
//                    System.out.println(responseEntityStr);
//                }
//                EntityUtils.consume(resEntity);
//            } finally {
//                response.close();
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                httpclient.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
    /**

     * post请求

     * @param url         url地址

     * @param jsonParam     参数

     * @param noNeedResponse    不需要返回结果

     * @return

     */

    public static JSONObject httpPostAuth(String url,JSONObject jsonParam, boolean noNeedResponse,String auth){

        //post请求返回结果

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        JSONObject jsonResult = null;

        HttpPost method = new HttpPost(url);
        method.addHeader("Authorization", "Basic " + auth);
        try {

            if (null != jsonParam) {

                //解决中文乱码问题

                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");

                entity.setContentEncoding("UTF-8");

                entity.setContentType("application/json");

                method.setEntity(entity);

            }

            HttpResponse result = httpClient.execute(method);

            url = URLDecoder.decode(url, "UTF-8");

            /**请求发送成功，并得到响应**/

            if (result.getStatusLine().getStatusCode() == 200) {

                String str = "";

                try {

                    /**读取服务器返回过来的json字符串数据**/

                    str = EntityUtils.toString(result.getEntity());

                    if (noNeedResponse) {

                        return null;

                    }

                    /**把json字符串转换成json对象**/

                    jsonResult = JSONObject.fromObject(str);

                } catch (Exception e) {

                    log.error("post请求提交失败:" + url, e);

                }

            }

        } catch (IOException e) {

            log.error("post请求提交失败:" + url, e);

        }

        return jsonResult;

    }

    /**

     * post请求

     * @param url         url地址

     * @param jsonParam     参数

     * @param noNeedResponse    不需要返回结果

     * @return

     */

    public static JSONObject httpPost(String url,JSONObject jsonParam, boolean noNeedResponse){

        //post请求返回结果

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        JSONObject jsonResult = null;

        HttpPost method = new HttpPost(url);

        try {

            if (null != jsonParam) {

                //解决中文乱码问题

                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");

                entity.setContentEncoding("UTF-8");

                entity.setContentType("application/json");

                method.setEntity(entity);

            }

            HttpResponse result = httpClient.execute(method);

            url = URLDecoder.decode(url, "UTF-8");

            /**请求发送成功，并得到响应**/

            if (result.getStatusLine().getStatusCode() == 200) {

                String str = "";

                try {

                    /**读取服务器返回过来的json字符串数据**/

                    str = EntityUtils.toString(result.getEntity());

                    if (noNeedResponse) {

                        return null;

                    }

                    /**把json字符串转换成json对象**/

                    jsonResult = JSONObject.fromObject(str);

                } catch (Exception e) {

                    log.error("post请求提交失败:" + url, e);

                }

            }

        } catch (IOException e) {

            log.error("post请求提交失败:" + url, e);

        }

        return jsonResult;

    }

}
