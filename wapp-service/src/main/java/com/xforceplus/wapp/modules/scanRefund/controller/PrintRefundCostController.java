package com.xforceplus.wapp.modules.scanRefund.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.modules.job.utils.FileZip;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.export.PrintRefundCostExcel;
import com.xforceplus.wapp.modules.scanRefund.export.PrintRefundExcel;
import com.xforceplus.wapp.modules.scanRefund.export.PrintRefundInformationExcel;
import com.xforceplus.wapp.modules.scanRefund.service.CostPrintRefundInformationService;
import com.xforceplus.wapp.modules.scanRefund.service.PrintRefundInformationService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.collect.Lists;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;


import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import static org.joda.time.DateTime.now;

/**
 * 发票综合查询
 */
@RestController
public class PrintRefundCostController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintRefundController.class);
    private static final Log log = LogFactory.getLog(ZipUtil.class);

    /**
     * 本地文件存放路径
     */
    @Value("${filePathConstan.tempDir}")
    private String tempdir;

    @Autowired
    private PrintRefundInformationService printRefundInformationService;
    @Autowired
    private CostPrintRefundInformationService costPrintRefundInformationService;


    @SysLog("查询退单发票")
    @RequestMapping(value = "/export/printQueryPayCostList")
    public void printQuery(@RequestParam("matchnoList") String matchnoList, @RequestParam("refundType") String refundType, @RequestParam("refundNo") String refundNo, @RequestParam("refundReason") String refundReason, @RequestParam("refundRemark") String refundRemark, HttpServletResponse response) {
        LOGGER.info("查询条件为:{}", matchnoList);
        JSONArray arr = JSONArray.fromObject(matchnoList);
        List<EnterPackageNumberEntity> list1 = new ArrayList<>();
        List<Long> list2 = new ArrayList<>();
        for(int i = 0;i<arr.size();i++){
            Long id = Long.valueOf(String.valueOf(arr.get(i)));
            EnterPackageNumberEntity enterPackageNumberEntity = printRefundInformationService.queryRefundList(id);
            list1.add(enterPackageNumberEntity);
        }
        Map<String,List<Long>> idEntity = new HashMap();
        for (int i =0;i<list1.size();i++){

            EnterPackageNumberEntity entity = list1.get(i);
            String epsNo = entity.getEpsNo();
            if(!idEntity.containsKey(epsNo)){
                List<Long> entityList = new LinkedList();
                entityList.add(entity.getId());
                idEntity.put(epsNo,entityList);
            }else{
                List<Long> entityList = (List<Long>)idEntity.get(epsNo);
                entityList.add(entity.getId());
                idEntity.put(epsNo,entityList);
            }
        }

        try {

//            //本地临时文件夹路径
//            String timeString = "" + new Date().getTime();
//            String localTempDir = tempdir + timeString + File.separator;
//            //创建临时文件夹
//            File dir = new File(localTempDir);
//            dir.mkdirs();

//        final Map<String, Object> map = newHashMapWithExpectedSize(10);
        Set set = idEntity.keySet();
        Iterator iter = set.iterator();
        List<Map<String, Object>> mapList = Lists.newArrayList();
        while (iter.hasNext()){
            final Map<String, Object> map = newHashMapWithExpectedSize(10);
            List<EnterPackageNumberEntity> list = new ArrayList<EnterPackageNumberEntity>();
             String vendor = (String)iter.next();
            List<Long>  entityList1 = idEntity.get(vendor);
            for(Long id : entityList1){
                //得到id
                EnterPackageNumberEntity enterPackageNumberEntity = printRefundInformationService.queryRefundList(id);
                enterPackageNumberEntity.setRefundNo("");
//                enterPackageNumberEntity.setRefundReason(refundReason);
//                enterPackageNumberEntity.setRefundType(refundType);
                enterPackageNumberEntity.setRefundRemark("");
                if(enterPackageNumberEntity.getFlowType().equals("1")){
                    enterPackageNumberEntity.setFlowType("商品");
                }else if(enterPackageNumberEntity.getFlowType().equals("2")) {
                    enterPackageNumberEntity.setFlowType("费用");
                } else if(enterPackageNumberEntity.getFlowType().equals("3")){
                    enterPackageNumberEntity.setFlowType("外部红票");
                } else if(enterPackageNumberEntity.getFlowType().equals("4")){
                    enterPackageNumberEntity.setFlowType("内部红票");
                } else if(enterPackageNumberEntity.getFlowType().equals("5")){
                    enterPackageNumberEntity.setFlowType("供应商红票");
                } else if(enterPackageNumberEntity.getFlowType().equals("6")){
                    enterPackageNumberEntity.setFlowType("租赁");
                } else if(enterPackageNumberEntity.getFlowType().equals("7")){
                    enterPackageNumberEntity.setFlowType("直接认证");
                }else if(enterPackageNumberEntity.getFlowType().equals("8")){
                    enterPackageNumberEntity.setFlowType("Ariba");
                }else {
                    enterPackageNumberEntity.setFlowType("");
                }
                list.add(enterPackageNumberEntity);
            }
            Long id = entityList1.get(0);
            EnterPackageNumberEntity enterPackageNumberEntity1 = printRefundInformationService.queryPostType(id);
            map.put("printRefundList", list);
            map.put("total", entityList1.size());
            if (enterPackageNumberEntity1==null||enterPackageNumberEntity1.getPostType() == null || enterPackageNumberEntity1.getPostType().equals("")) {
                map.put("postType", "");
            } else {
                map.put("postType", enterPackageNumberEntity1.getPostType());
            }
            map.put("vendor",vendor);
//            final PrintRefundExcel excelView = new PrintRefundExcel(map, "export/scanRefund/", "PrintRefundList.xlsx",vendor);
//            excelView.writeBD(localTempDir,vendor+".xlsx");
            mapList.add(map);
        }
            final PrintRefundCostExcel excelView = new PrintRefundCostExcel(mapList, "export/scanRefund/PrintRefundCostList.xlsx", "printRefundList");
            final String excelNameSuffix = String.valueOf(new Date().getTime());
            excelView.write(response,"printRefundList"+excelNameSuffix);


//        //打包
//        dir = new File(localTempDir);
//        File[] files = dir.listFiles();
//        String zipName = localTempDir + "exportImgs_" + timeString + ".zip";
//        FileZip.zipFiles(files, new File(zipName));
//
//        //下载
//        File zipFile = new File(zipName);
//        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(zipFile.getPath()));
//        byte[] buffer = new byte[fis.available()];
//        fis.read(buffer);
//        fis.close();
//
//        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment;filename=" + new String(zipFile.getName().getBytes("UTF-8"), "ISO-8859-1"));
//        toClient.write(buffer);
//        toClient.flush();
//        toClient.close();
//
//        //删除临时文件夹
//        FileUtil fileUtil = new FileUtil();
//        fileUtil.deleteDirectory(localTempDir);

        }catch (Exception e){
            e.printStackTrace();
            logger.error("下载图片出错:"+e);
        }

    }

    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("退货查询导出")
    @RequestMapping(value = "/export/refundCostExport")
    public void comprehensiveInvoiceQueryCostExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        List<EnterPackageNumberExcelEntity> list = costPrintRefundInformationService.queryListForExcel(schemaLabel,params);

        try {
            ExcelUtil.writeExcel(response,list,"打印退单封面（费用）","sheet1", ExcelTypeEnum.XLSX,EnterPackageNumberExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("printRefundInformationList", list);
//        //生成excel
//        final PrintRefundInformationExcel excelView = new PrintRefundInformationExcel(map, "export/scanRefund/PrintRefundCostInformationList.xlsx", "printRefundInformationList");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "printRefundInformationList" + excelNameSuffix);
    }

}
