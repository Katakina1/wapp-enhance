package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.EnterPackageNumberDao;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.export.EnterPackageNumberImport;
import com.xforceplus.wapp.modules.scanRefund.service.EnterPackageNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

@Service
public class EnterPackageNumberServiceImpl implements EnterPackageNumberService {

    private static final Logger LOGGER= getLogger(EnterPackageNumberServiceImpl.class);

    @Autowired
    private EnterPackageNumberDao enterPackageNumberDao;

    @Override
    public List<EnterPackageNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return enterPackageNumberDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return enterPackageNumberDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<EnterPackageNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return enterPackageNumberDao.queryListAll(schemaLabel,map);
    }

    @Override
    public void inputrebateExpressno(String schemaLabel, String[] rebateNos,String rebateExpressno,String mailDate,String mailCompany) {
        enterPackageNumberDao.inputrebateExpressno(schemaLabel, rebateNos, rebateExpressno,mailDate,mailCompany);
    }

    @Override
    public int queryrebateexpressno(String rebateExpressno) {
        return enterPackageNumberDao.queryrebateexpressno(rebateExpressno);
    }

    /**
     * 解析excel数据，解析保存入库
     * @return
     */
    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName) {
        //进入解析excel方法
        final EnterPackageNumberImport enterPackageNumberImport = new EnterPackageNumberImport(multipartFile);
        Map<String, Object> map = newHashMap();
        try {
            //读取excel
//            final List<EnterPackageNumberEntity> redInvoiceList = enterPackageNumberImport.analysisExcel();
            map = enterPackageNumberImport.analysisExcel();
            List<EnterPackageNumberEntity> redInvoiceList = (List<EnterPackageNumberEntity>)map.get("enjoySubsidedList");
            Set set = (Set)map.get("set");
            Set<String> set1 = new HashSet<>();
            Set<String> set2 = new HashSet<>();
            Iterator e = set.iterator();
            while (e.hasNext()){
                String s = (String) e.next();
                //去掉邮包号不能重复的限制
                //int count = enterPackageNumberDao.queryrebateexpressno(s);
               // if(count == 0){
                    set1.add(s);
               // }else {
                //    set2.add(s);
               // }
            }
            if(set2.size()>0){
                Iterator e1 = set2.iterator();
                while (e1.hasNext()) {
                    String s = (String) e1.next();
                    map.put("success", Boolean.FALSE);
                    map.put("reason", s+"此邮包号已存在，请重新输入该邮包号");
                    return  map;
                }
            }
            List<EnterPackageNumberEntity> list = new LinkedList();
            for(EnterPackageNumberEntity entity : redInvoiceList){
                if(!set2.contains(entity.getRebateExpressno())){
                    list.add(entity);
                }
            }
            redInvoiceList = list;

            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<EnterPackageNumberEntity>> entityMap =RedInvoiceImportData(redInvoiceList,logingName);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
                map.put("errorCount1", entityMap.get("errorEntityList1").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
//            if(set2.size()>0){
//                Iterator e1 = set2.iterator();
//                while (e1.hasNext()) {
//                    String s = (String) e1.next();
//                    map.put("success", Boolean.FALSE);
//                    map.put("reason", s+"此邮包号已存在，请重新输入该邮包号,其他邮包号上传成功");
//                    return  map;
//                }
//            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }
    private Map<String, List<EnterPackageNumberEntity>> RedInvoiceImportData(List<EnterPackageNumberEntity> redInvoiceList,String loginname){
        //返回值
        final Map<String, List<EnterPackageNumberEntity>> map = newHashMap();
        //导入成功的数据集
        final List<EnterPackageNumberEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<EnterPackageNumberEntity> errorEntityList = newArrayList();
        //导入失败的数据集1
        final List<EnterPackageNumberEntity> errorEntityList1 = newArrayList();

        redInvoiceList.forEach(redInvoiceData -> {
            String rebateNo = redInvoiceData.getRebateNo();
            String rebateExpressno = redInvoiceData.getRebateExpressno();
            String mailDate = redInvoiceData.getMailDate();
            String mailCompany = redInvoiceData.getMailCompany();
            int count = enterPackageNumberDao.queryrebateNo(rebateNo);
            if ( rebateNo.isEmpty() ||  rebateExpressno.isEmpty() || count == 0 ) {

                errorEntityList.add(redInvoiceData);
            } else if(mailDate.isEmpty()||mailCompany.isEmpty()){
                errorEntityList1.add(redInvoiceData);
            } else {
                successEntityList.add(redInvoiceData);
            }
        });

        //如果都校验通过，保存入库
        for(EnterPackageNumberEntity red: successEntityList){
            enterPackageNumberDao.inputrebateExpressnoBatch(red.getRebateNo(),red.getRebateExpressno(),red.getMailCompany(),red.getMailDate());
        }

        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        map.put("errorEntityList1", errorEntityList1);

        return map;
    }
}
