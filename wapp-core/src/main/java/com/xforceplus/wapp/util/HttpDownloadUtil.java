package com.xforceplus.wapp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 下载网络文件
 *
 */
public class HttpDownloadUtil {

  public static final int cache = 10 * 1024;

  public static void main(String[] args) {
    String url = "https://test-litc.xforceplus.com/litchi/pdf/einvoice.pdf?request=FVLHJPWACXgsLXA9MfwgCEULxj6jKpRLzR46otYsf8uSZzWFyOgBHtPBs9Y%2F9RDDrHj6bAh0mCyyi1%2FN3UTCT6WaYpwV2b%2B8joBNoVhN0c0ZK0jbbMaXoA%3D%3D";
    HttpResponse response = getFileResponse(url);
    String fileName = getFileName(response,url);
    byte[] fileBytes = getFileBytes(response);
    System.out.println("fileName:"+fileName);
    System.out.println("fileBytes.length:"+fileBytes.length);
  }

  public static HttpResponse getFileResponse(String url){
    try {
      HttpClient client = HttpClients.createDefault();
      HttpGet httpGet = new HttpGet(url);
      // 加入Referer,防止防盗链
      httpGet.setHeader("Referer", url);
      return client.execute(httpGet);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] getFileBytes(HttpResponse response){
    byte[] fileBytes = null;
    try {
      HttpEntity entity = response.getEntity();
      InputStream is = entity.getContent();
      ByteArrayOutputStream baos =new ByteArrayOutputStream();
      byte[] buffer = new byte[cache];
      int ch = 0;
      while ((ch = is.read(buffer)) != -1) {
        baos.write(buffer, 0, ch);
      }
      fileBytes = baos.toByteArray();
      is.close();
      baos.flush();
      baos.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileBytes;
  }

  /**
   * 获取response header中Content-Disposition中的filename值
   * @param response
   * @param url
   * @return
   */
  public static String getFileName(HttpResponse response,String url) {
    Header contentHeader = response.getFirstHeader("Content-Disposition");
    String filename = null;
    if (contentHeader != null) {
      // 如果contentHeader存在
      HeaderElement[] values = contentHeader.getElements();
      if (values.length == 1) {
        NameValuePair param = values[0].getParameterByName("filename");
        if (param != null) {
          try {
            filename = param.getValue();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }else{
      // 正则匹配后缀
      filename = getSuffix(url);
    }

    return StringUtils.isNotBlank(filename)?filename:String.valueOf(System.currentTimeMillis());
  }

  /**
   * 获取文件名后缀
   * @param url
   * @return
   */
  public static String getSuffix(String url) {
    // 正则表达式“.+/(.+)$”的含义就是：被匹配的字符串以任意字符序列开始，后边紧跟着字符“/”，
    // 最后以任意字符序列结尾，“()”代表分组操作，这里就是把文件名做为分组，匹配完毕我们就可以通过Matcher
    // 类的group方法取到我们所定义的分组了。需要注意的这里的分组的索引值是从1开始的，所以取第一个分组的方法是m.group(1)而不是m.group(0)。
    String regEx = ".+/(.+)$";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(url);
    if (!m.find()) {
      // 格式错误，则随机生成个文件名
      return String.valueOf(System.currentTimeMillis());
    }
    return m.group(1);

  }
}
