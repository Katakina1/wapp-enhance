package com.xforceplus.wapp.modules.pack.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.pack.dao.InputPackNumberDao;

import com.xforceplus.wapp.modules.pack.entity.InputPackNumberEntity;
import com.xforceplus.wapp.modules.pack.entity.InputPackNumberExcelEntity;
import com.xforceplus.wapp.modules.pack.export.InputPackNumberImport;
import com.xforceplus.wapp.modules.pack.service.InputPackNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Service
@SuppressWarnings("ALL")
public class InputPackNumberServiceImpl implements InputPackNumberService {

    @Autowired
    private InputPackNumberDao inputPackNumberDao;

    @Override
    public List<InputPackNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return inputPackNumberDao.queryList(schemaLabel,map);
    }

    @Override
    public List<InputPackNumberEntity> getListAll(String schemaLabel, Map<String, Object> map) {
        return inputPackNumberDao.getListAll(schemaLabel,map);
    }

    @Override
    public int queryTotalResult(String schemaLabel,Map<String, Object> map) {

        return inputPackNumberDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<InputPackNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return inputPackNumberDao.queryListAll(schemaLabel,map);
    }

    @Override
    public void inputpackingno(String schemaLabel, String[] bbindingNos,String packingNo) {
        inputPackNumberDao.inputpackingno(schemaLabel, null, packingNo,null);
    }

    @Override
    public int querypackingno(String schemaLabel,String packingNo) {
         return inputPackNumberDao.querypackingno(schemaLabel,packingNo);
    }

    @Override
    public List<InputPackNumberEntity> getBindingnoList(Map<String, Object> params) {
        return inputPackNumberDao.getBindingnoList(params);
    }

    @Override
    public Integer getBindingnoListCount(Map<String, Object> params) {
        return inputPackNumberDao.getBindingnoListCount(params);
    }

    @Override
    public List<InputPackNumberExcelEntity> transformExcle(List<InputPackNumberEntity> inputPackNumberEntity) {
        List<InputPackNumberExcelEntity> list2=new ArrayList<>();
        for(int i = 0 ; i < inputPackNumberEntity.size() ; i++) {
            InputPackNumberEntity entity = inputPackNumberEntity.get(i);
            InputPackNumberExcelEntity supplierInformationSearchExcelEntity = new InputPackNumberExcelEntity();
            supplierInformationSearchExcelEntity.setIndexNo( String.valueOf(i+1));
            supplierInformationSearchExcelEntity.setBbindingDate(entity.getBbindingDate());
            supplierInformationSearchExcelEntity.setBbindingNo(entity.getBbindingNo());
            supplierInformationSearchExcelEntity.setEpsNo(entity.getEpsNo());
            supplierInformationSearchExcelEntity.setInvoiceSerialNo(entity.getInvoiceSerialNo());
list2.add(supplierInformationSearchExcelEntity);
        }
        return list2;
    }
    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        final InputPackNumberImport enterPackageNumberImport = new InputPackNumberImport(multipartFile);
        Map<String, Object> map = newHashMap();
        try {
            map = enterPackageNumberImport.analysisExcel();
            if(map.get("status").equals("false")){
                map.put("success", Boolean.FALSE);
                map.put("reason", "导入数据不能超过5000条！");
            }
            List<InputPackNumberEntity> redInvoiceList = (List<InputPackNumberEntity>)map.get("enjoySubsidedList");

            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<InputPackNumberEntity>> entityMap =RedInvoiceImportData(redInvoiceList);
                map.put("reason", entityMap.get("successEntityList").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            e.printStackTrace();
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<InputPackNumberEntity>> RedInvoiceImportData(List<InputPackNumberEntity> redInvoiceList){
        final Map<String, List<InputPackNumberEntity>> map = newHashMap();
        final List<InputPackNumberEntity> successEntityList = newArrayList();
        final List<InputPackNumberEntity> errorEntityList = newArrayList();
        final List<InputPackNumberEntity> errorEntityList1 = newArrayList();

        redInvoiceList.forEach(redInvoiceData -> {
            Long rebateNo = redInvoiceData.getId();
            String bindNumber = redInvoiceData.getPackingNo();
            if(bindNumber.isEmpty()){
                errorEntityList.add(redInvoiceData);
            } else {
                successEntityList.add(redInvoiceData);
            }
        });

        for(InputPackNumberEntity red: successEntityList){
            inputPackNumberDao.inputpackingno(null,red.getBbindingNo(),red.getPackingNo(),red.getPackingAddress());
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }
}
