package com.xforceplus.wapp.modules.fixed.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.fixed.dao.InvoiceImportExportDao;
import com.xforceplus.wapp.modules.fixed.export.InvoiceImportExportImport;
import com.xforceplus.wapp.modules.fixed.service.InvoiceImportExportService;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;
import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportImportEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class InvoiceImportExportServiceImpl implements InvoiceImportExportService {
    private static final Logger LOGGER = getLogger(InvoiceImportExportServiceImpl.class);

    @Autowired
    private InvoiceImportExportDao invoiceImportAndExportDao;
    @Override
    public List<InvoiceImportAndExportEntity> invoiceImportAndExportlist(Map<String, Object> map){
        List<InvoiceImportAndExportEntity> list = invoiceImportAndExportDao.invoiceImportAndExportlist(map);
        List<InvoiceImportAndExportEntity> orderlist = new LinkedList();
        for(InvoiceImportAndExportEntity entity:list){
            Long id = entity.getId();
            //通过id获取所有的订单号，然后拼接
            List<String> orderNoList = invoiceImportAndExportDao.selectOrdersById(id);
            String orders = "";
            if(orderNoList!=null && orderNoList.size()>0){
                 orders = listToString2(orderNoList,',');
            }
            entity.setOrders(orders);
            orderlist.add(entity);
        }
        return orderlist;
    }
    @Override
    public List<InvoiceImportAndExportEntity> invoiceImportAndExportlistAll(Map<String, Object> map){
        List<InvoiceImportAndExportEntity> list = invoiceImportAndExportDao.invoiceImportAndExportlistAll(map);
        List<InvoiceImportAndExportEntity> orderlist = new LinkedList();
        for(InvoiceImportAndExportEntity entity:list){
            Long id = entity.getId();
            //通过id获取所有的订单号，然后拼接
            List<String> orderNoList = invoiceImportAndExportDao.selectOrdersById(id);
            String orders = "";
            if(orderNoList!=null && orderNoList.size()>0){
                orders = listToString2(orderNoList,',');
            }
            entity.setOrders(orders);
            orderlist.add(entity);
        }
        return orderlist;
    }

    @Override
    public Integer invoiceImportAndExportlistCount(Map<String, Object> map){
        return invoiceImportAndExportDao.invoiceImportAndExportlistCount(map);
    }

    @Override
    public Map<String, Object> importInvoice(Map<String, Object> params, MultipartFile file) {
        final InvoiceImportExportImport invoiceImport = new InvoiceImportExportImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            List currentList= Lists.newArrayList();
            Map<String , Object> analysisExcelMap=invoiceImport.analysisExcel();
            List<InvoiceImportAndExportImportEntity> certificationEntityList = (List<InvoiceImportAndExportImportEntity>) analysisExcelMap.get("enjoySubsidedList");
            if("98156006248284160".equals(analysisExcelMap.get("row1cellN"))){
                LOGGER.info("导出数据模版有误,信息为："+analysisExcelMap.toString());
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel模版格式有误，请使用导出的模版");
                return map;
            }
            if (certificationEntityList.size()>0) {
                for(InvoiceImportAndExportImportEntity importEntity:certificationEntityList){
                    Map<String,Object> mapps= Maps.newHashMapWithExpectedSize(10);
                    mapps.put("uuid",importEntity.getUuid());
                    mapps.put("sap",importEntity.getsAp());

                    //通过查询判断发票数据是否被修改
                    invoiceImportAndExportDao.invoiceImportAndExportUpdate(mapps);
                }
                map.put("invoiceQueryList",currentList);
                map.put("success", Boolean.TRUE);
                map.put("reason", "批量导入成功！");
            }else {
                LOGGER.info("读取到excel数据格式有误"+analysisExcelMap.toString());
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel数据格式有误！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }


        return map;
    }
    public static String listToString2(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append(separator);
        }
        return list.isEmpty() ? "" : sb.toString().substring(0, sb.toString().length() - 1);
    }



}
