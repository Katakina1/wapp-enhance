package com.xforceplus.wapp.common.utils;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;  
import java.util.Map;  
import java.util.Map.Entry;  
import javax.imageio.ImageIO;  
import javax.imageio.ImageReader;  
import javax.imageio.stream.ImageInputStream;  
    
public class FileTypeUtil    
{    
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();    
        
    private FileTypeUtil(){}    
    static{    
        getAllFileType();  //初始化文件类型信息    
    }


    protected static Logger log = Logger.getLogger(FileTypeUtil.class);
    /**  
     * Created on 2010-7-1   
     * <p>Discription:[getAllFileType,常见文件头信息]</p>  
     * @author:[shixing_11@sina.com]  
     */    
    private static void getAllFileType()    
    {
        //Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("pdf", "255044462D312E");

    }


    public static void main(String[] args) throws Exception
    {

//        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//
//        JSONObject jsonResult = null;
//
//        HttpGet method = new HttpGet("http://test-sapdctm.wal-mart.com:80/assap/archivelink/dmgbsap_tst?get&pVersion=0046&contRep=E3&docId=000D3A7B55E51EDAA9AF905D2C88E029&accessMode=r&authId=CN=DR3,OU=I0020289037,OU=SAPWebAS,O=SAPTrustCommunity,C=DE&expiration=20200603114145");
//
//        byte[] filByte = null;
//        String fileType = null;
//        try {
//            HttpResponse result = httpClient.execute(method);
//            HttpEntity entityResponse = result.getEntity();
//            filByte=readInputStream(entityResponse.getContent());
//
//
//
//            if (filByte == null) {
//                throw new Exception("文件获取失败，get.getResponseBody()后未byte为null");
//            } else {
//                fileType = FileTypeUtil.getFileTypeByStream(filByte);
//
//                if (StringUtils.isBlank(fileType)) {
//                    fileType = "ofd";
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



//        File file = new File("E://ofd");
//
//        File [] files = file.listFiles();
//        for (File file1:files){
//            byte[] stringbyte= File2byte(file1);
//            System.out.println("--------------------------------");
//            System.out.println(new String(stringbyte,"ISO8859-1"));
//        }
//        File f = new File("c://aaa.gif");
//        if (f.exists())
//        {
//            String filetype1 = getImageFileType(f);
//            System.out.println(filetype1);
//            String filetype2 = getFileByFile(f);
//            System.out.println(filetype2);
//        }

    }

    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    /**
     * 将文件转换成byte数组
     * @param filePath
     * @return
     */
    public static byte[] File2byte(File tradeFile){
        byte[] buffer = null;
        try
        {
            FileInputStream fis = new FileInputStream(tradeFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }
    /**  
     * Created on 2010-7-1   
     * <p>Discription:[getImageFileType,获取图片文件实际类型,若不是图片则返回null]</p>  
     * @param File  
     * @return fileType  
     * @author:[shixing_11@sina.com]  
     */    
    public final static String getImageFileType(File f)    
    {    
        if (isImage(f))  
        {  
            try  
            {  
                ImageInputStream iis = ImageIO.createImageInputStream(f);  
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);  
                if (!iter.hasNext())  
                {  
                    return null;  
                }  
                ImageReader reader = iter.next();  
                iis.close();  
                return reader.getFormatName();  
            }  
            catch (IOException e)  
            {  
                return null;  
            }  
            catch (Exception e)  
            {  
                return null;  
            }  
        }  
        return null;  
    }    
    
    /**  
     * Created on 2010-7-1   
     * <p>Discription:[getFileByFile,获取文件类型,包括图片,若格式不是已配置的,则返回null]</p>  
     * @param file  
     * @return fileType  
     * @author:[shixing_11@sina.com]  
     */    
    public final static String getFileByFile(File file)    
    {    
        String filetype = null;    
        byte[] b = new byte[50];    
        try    
        {    
            InputStream is = new FileInputStream(file);    
            is.read(b);    
            filetype = getFileTypeByStream(b);    
            is.close();    
        }    
        catch (FileNotFoundException e)    
        {    
            e.printStackTrace();    
        }    
        catch (IOException e)    
        {    
            e.printStackTrace();    
        }    
        return filetype;    
    }    
        
    /**  
     * Created on 2010-7-1   
     * <p>Discription:[getFileTypeByStream]</p>  
     * @param b  
     * @return fileType  
     * @author:[shixing_11@sina.com]  
     */    
    public final static String getFileTypeByStream(byte[] b)    
    {    
        String filetypeHex = String.valueOf(getFileHexString(b));
        log.info("FileByteIO："+b);
        log.info("ExtensionsHeader："+filetypeHex);
        Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();    
        while (entryiterator.hasNext()) {    
            Entry<String,String> entry =  entryiterator.next();    
            String fileTypeHexValue = entry.getValue();    
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {    
                return entry.getKey();    
            }    
        }    
        return null;    
    }    
        
    /** 
     * Created on 2010-7-2  
     * <p>Discription:[isImage,判断文件是否为图片]</p> 
     * @param file 
     * @return true 是 | false 否 
     * @author:[shixing_11@sina.com] 
     */  
    public static final boolean isImage(File file){  
        boolean flag = false;  
        try  
        {  
            BufferedImage bufreader = ImageIO.read(file);  
            int width = bufreader.getWidth();  
            int height = bufreader.getHeight();  
            if(width==0 || height==0){  
                flag = false;  
            }else {  
                flag = true;  
            }  
        }  
        catch (IOException e)  
        {  
            flag = false;  
        }catch (Exception e) {  
            flag = false;  
        }  
        return flag;  
    }  
      
    /**  
     * Created on 2010-7-1   
     * <p>Discription:[getFileHexString]</p>  
     * @param b  
     * @return fileTypeHex  
     * @author:[shixing_11@sina.com]  
     */    
    public final static String getFileHexString(byte[] b)    
    {    
        StringBuilder stringBuilder = new StringBuilder();    
        if (b == null || b.length <= 0)    
        {    
            return null;    
        }    
        for (int i = 0; i < b.length; i++)    
        {    
            int v = b[i] & 0xFF;    
            String hv = Integer.toHexString(v);    
            if (hv.length() < 2)    
            {    
                stringBuilder.append(0);    
            }    
            stringBuilder.append(hv);    
        }    
        return stringBuilder.toString();    
    }    
}  