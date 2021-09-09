package com.xforceplus.wapp.modules.signin.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

//import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图片上传
 *
 * @author jingsong.mao
 * @date 7/30/2018
 */
@Api(tags = "客户端-图片上传")
@RestController
@RequestMapping("/onlineUpload")
public class ImportSignHttpOnlineController extends AbstractController {

    private final static Logger LOGGER = getLogger(ImportSignHttpOnlineController.class);

    private final ImportSignService importSignService;



    @Autowired
    public ImportSignHttpOnlineController(ImportSignService importSignService) {
        this.importSignService = importSignService;

    }

    @Autowired


    /**
     * 图片上传
     */
    @ApiOperation("ofd上传转图片")
    @SysLog("ofd上传转图片")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value = "elUpload")
    public String elInvoice(@RequestBody String requestBody) throws Exception {

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(requestBody);

        String path="file";
        String jsonString="{\"path\":\""+path+"\"}";
        return jsonString;
    }


    /**
     * 图片上传
     */
    @ApiOperation("图片上传")
    @SysLog("图片上传")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    public String importSignImgHttp(String userId, String token, String ocrMapJson, @RequestParam("file") MultipartFile file) {
        logger.info("发票信息：" + ocrMapJson);
        String jsonString = ocrMapJson;

        JSONObject jasonObject = JSONObject.fromObject(jsonString);

        Map<String, String> ocrMap = (Map) jasonObject;
        String scanId = file.getOriginalFilename();//scanId就是客户端传过来的文件名称,去掉后面的.jpg

        try {
            scanId = URLDecoder.decode(scanId.substring(0, scanId.lastIndexOf('.')), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ocrMap.put("scanId", scanId);


        JSONObject json = new JSONObject();


        //执行上传
        try {


            //判断金额是否为null,若为null按0算
            if (StringUtils.isBlank(ocrMap.get("invoiceAmount"))) {
                ocrMap.put("invoiceAmount", "0");
            }
            //先判断未退票的代码号码金额开票日期，判断是否重复，若未签收，则进行签收处理，进行覆盖处理，否则不进行签收处理信息驳回
            Integer integer = importSignService.getInvoiceDouble(ocrMap);
            if (integer > 0) {
                String uuid = ocrMap.get("invoiceCode") + ocrMap.get("invroiceNo");
                json.put("uuid", uuid);
                json.put("success", false);
                json.put("message", "重复数据并且已签收，不予处理");
                json.put("userId", userId);
                json.put("fileName", file.getOriginalFilename());
            } else {
                //使用线程池执行签收
                ExportEntity entity = buildExportEntity();
                LOGGER.info("在线上传------------------------------------" + userId);
                String uuid = importSignService.onlyUploadImg(entity, file, ocrMap);
                json.put("uuid", uuid);
                json.put("success", uuid != null && !uuid.equals(""));
                json.put("message", (uuid != null && !uuid.equals("")) ? "上传成功" : "上传失败");
                json.put("userId", userId);
                json.put("fileName", file.getOriginalFilename());
                //先上传再签收
                //使用线程池执行签收
                ScanThread scanThread = new ScanThread(entity, file, ocrMap, importSignService);
                scanThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.put("message", "上传失败：服务器发生异常，请联系管理员！\n异常原因：" + e.getMessage());
            json.put("uuid", "");
            json.put("success", "false");
            json.put("userId", userId);
            json.put("fileName", file.getOriginalFilename());
        }


        return json.toString();
    }
    @Autowired
    private SystemConfig systemConfig;
    @SysLog("查验")
    @RequestMapping("/check")
    @ResponseBody
    public String check(@RequestBody String requestBody) {

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(requestBody);
        RequestData requestData = jsonObject.toJavaObject(RequestData.class);
        requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());

        ResponseInvoice responseInvoice = null;
        return JSON.toJSONString(responseInvoice);
    }




    /**
     * 预览pdf文件
     * @param fileName
     */
    @RequestMapping(value = "/pdf", method = RequestMethod.GET)
    public void pdfStreamHandler(String fileName,HttpServletRequest request,HttpServletResponse response) {

        Resource resource = new ClassPathResource("test/"+fileName);

            byte[] data = null;
            try {
                InputStream input =resource.getInputStream();
                data = new byte[input.available()];
                input.read(data);
                response.getOutputStream().write(data);
                input.close();
            } catch (Exception e) {
                logger.error("pdf文件处理异常：" + e.getMessage());
            }


    }

        /**
         * 稿源周报excel表格下载
         * @return
         */
    @RequestMapping(value = "/ofd", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String downExcel(HttpServletResponse response,String fileName) throws UnsupportedEncodingException {



                // 配置文件下载
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                // 实现文件下载
                byte[] buffer = new byte[1024];
                InputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    Resource resource = new ClassPathResource("test/"+fileName);
                    fis = resource.getInputStream();
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("Download  successfully!");
                    return "successfully";

                } catch (Exception e) {
                    System.out.println("Download  failed!");
                    return "failed";

                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


//        return "";
    }

    @SysLog("扫描修改签收")
    @RequestMapping("/updateInvoice")
    @ResponseBody
    public R updateInvoice(@RequestBody String requestBody, HttpServletRequest request) {

        JSONObject jsonObject = JSONObject.fromObject(requestBody);
        logger.info("发票信息：" + jsonObject.toString());
        Map<String, Object> invoices = jsonObject;
        final ExportEntity exportEntity = new ExportEntity();

        exportEntity.setSchemaLabel((String) invoices.get("exportEntitySchemaLabel"));
        //人员id
        exportEntity.setUserId(Long.valueOf((Integer) invoices.get("exportEntityUserId")));
        //帐号
        exportEntity.setUserAccount((String) invoices.get("exportEntityUserAccount"));
        //人名
        exportEntity.setUserName((String) invoices.get("exportEntityUserName"));
        try {
            final Map<String, Object> recordInvoiceEntityMap = importSignService.getUpdateRecordInvoiceEntity(exportEntity, invoices);
            return R.ok().put("page", recordInvoiceEntityMap.get("list"));
        } catch (RRException e) {
            logger.error("扫描签收失败，excel失败:{}", e);
            return R.error(9999, e.getMessage());
        }
//        //返回请求结果
//
//        JSONObject result= new JSONObject();
//
//        result.put("success", "true");

//        return null;

    }


//    public R updateInvoice(Map<String, Object> invoices){
//        System.out.println("");
//        //通过id获取修改前数据，并构建修改后要进行查验的内容
//
////        try {
////            final Map<String,Object> recordInvoiceEntityMap = importSignService.getUpdateRecordInvoiceEntity(exportEntity,invoices);
////            return R.ok().put("page",recordInvoiceEntityMap);
////        } catch ( RRException e) {
////            logger.error("扫描签收失败，excel失败:{}", e);
////            return R.error(9999, e.getMessage());
////        }
//        return null;
//    }


    /**
     * 构建实体
     *
     * @return 实体
     */
    private ExportEntity buildExportEntity() {
        final ExportEntity exportEntity = new ExportEntity();
        exportEntity.setSchemaLabel(getCurrentUserSchemaLabel());
        //人员id
        exportEntity.setUserId(getUserId());
        //帐号
        exportEntity.setUserAccount(getUser().getLoginname());
        //人名
        exportEntity.setUserName(getUserName());
        return exportEntity;
    }


}
