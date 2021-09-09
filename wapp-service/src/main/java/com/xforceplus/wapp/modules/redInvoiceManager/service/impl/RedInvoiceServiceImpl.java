package com.xforceplus.wapp.modules.redInvoiceManager.service.impl;


import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.redInvoiceManager.dao.RedInvoiceDao;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redInvoiceManager.export.RedInvoiceImport;
import com.xforceplus.wapp.modules.redInvoiceManager.service.RedInvoiceService;

import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class RedInvoiceServiceImpl implements RedInvoiceService {
   private static final Logger LOGGER= getLogger(RedInvoiceServiceImpl.class);
   @Autowired
   private RedInvoiceDao redInvoiceDao;

   /**
    * 解析excel数据，解析保存入库
    * @param params
    * @param logingName
    * @return
    */
   @Override
   public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName) {
      //进入解析excel方法
      final RedInvoiceImport redInvoiceImport = new RedInvoiceImport(multipartFile);
      final Map<String, Object> map = newHashMap();
      try {
         //读取excel
         final List<RedInvoiceData> redInvoiceList = redInvoiceImport.analysisExcel();
         List<String> list  = redInvoiceDao.findDict();
         for(RedInvoiceData data :redInvoiceList){
            String rate = data.getTaxRate();
            String jv = data.getJvCode();
            String invoiceDate = data.getInvoiceDate();
            String invoiceAmount = data.getInvoiceAmount();
            if(redInvoiceDao.selectIsExists(jv,invoiceDate,invoiceAmount)>0){
               LOGGER.info("数据已存在"+ jv+"--"+invoiceDate+"--"+invoiceAmount);
               map.put("success", Boolean.FALSE);
               map.put("reason", "数据已存在 jv:"+jv+"-开票月份："+invoiceDate+"--开票金额："+invoiceAmount);
               return map;
            }
            if(list.contains(rate)){
               continue;
            }else{
               LOGGER.info("excel数据税率错误");
               map.put("success", Boolean.FALSE);
               map.put("reason", "请检查税率是否正确！");
               return map;
            }

         }
         if(redInvoiceList.size()>500){
            LOGGER.info("excel数据不能超过500条");
            map.put("success", Boolean.FALSE);
            map.put("reason", "excel数据不能超过500条！");
            return map;
         }
         if (!redInvoiceList.isEmpty()) {
            map.put("success", Boolean.TRUE);
            Map<String, List<RedInvoiceData>> entityMap =RedInvoiceImportData(redInvoiceList,logingName);
            map.put("reason", entityMap.get("successEntityList"));
            map.put("errorCount", entityMap.get("errorEntityList").size());
         } else {
            LOGGER.info("读取到excel无数据");
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取到excel无数据！");
         }
      } catch (ExcelException e) {
         LOGGER.error("读取excel文件异常:{}", e);
         map.put("success", Boolean.FALSE);
         map.put("reason", "读取excel文件异常！");
      }
      return map;
   }

      private Map<String, List<RedInvoiceData>> RedInvoiceImportData(List<RedInvoiceData> redInvoiceList,String loginname){
         //返回值
         final Map<String, List<RedInvoiceData>> map = newHashMap();
         //导入成功的数据集
         final List<RedInvoiceData> successEntityList = newArrayList();
         //导入失败的数据集
         final List<RedInvoiceData> errorEntityList = newArrayList();

         redInvoiceList.forEach(redInvoiceData -> {
            String invoiceAmount = redInvoiceData.getInvoiceAmount();
            String invoiceDate = redInvoiceData.getInvoiceDate();
            String invoiceType = redInvoiceData.getInvoiceType();
            String store = redInvoiceData.getStore();
            String taxAmount = redInvoiceData.getTaxAmount();
            String jvcode = redInvoiceData.getJvCode();
            String taxrate = redInvoiceData.getTaxRate();


            if (!jvcode.isEmpty() && !invoiceAmount.isEmpty() && !invoiceDate.isEmpty() && !invoiceType.isEmpty()&& !taxAmount.isEmpty()&&!taxrate.isEmpty()) {
               successEntityList.add(redInvoiceData);
            } else {
               errorEntityList.add(redInvoiceData);
            }
         });
         if(errorEntityList.size()==0){
            //设置序列号
            String curr = new SimpleDateFormat("yyyyMMdd").format(new Date());


            //如果都校验通过，保存入库
            for(RedInvoiceData red: successEntityList){
               red.setCreateBy(loginname);
              /*String store = redInvoiceDao.findTaxMD(red.getJvCode());//查询税务承担店号
               if(store!=null){
                  store=store.replace(".0","");
               }
               red.setStore(store);*/
               String serial = redInvoiceDao.selectLastRedInvoiceData();
                  if(serial==null){
                     AtomicInteger atomicNum = new AtomicInteger();
                     int newNum = atomicNum.incrementAndGet();
                     String newStrNum = String.format("%04d", newNum);
                     String serial_num=curr+newStrNum;
                     red.setSerialNumber(serial_num);
                  }else{
                      if(serial.contains(curr)){
                         BigDecimal decimal = new BigDecimal(serial);
                         String add = String.valueOf(decimal.add(new BigDecimal(1)));
                         red.setSerialNumber(add);
                      }else{
                         AtomicInteger atomicNum = new AtomicInteger();
                         int newNum = atomicNum.incrementAndGet();
                         String newStrNum = String.format("%04d", newNum);
                         String serial_num =curr+newStrNum;
                         red.setSerialNumber(serial_num);
                      }
                  }
               redInvoiceDao.insertRedInvoiceData(red);
            }

         }
         map.put("successEntityList", successEntityList);
         map.put("errorEntityList", errorEntityList);

         return map;
      }
}
