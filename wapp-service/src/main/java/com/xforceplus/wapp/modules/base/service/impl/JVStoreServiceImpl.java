package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.JVStoreDao;
import com.xforceplus.wapp.modules.base.entity.JVStoreEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.service.JVStoreService;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class JVStoreServiceImpl implements JVStoreService {
    @Autowired
    JVStoreDao jvStoreDao;
    @Override
    public List<JVStoreEntity> queryList(String schemaLabel, JVStoreEntity entity) {
        return jvStoreDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, JVStoreEntity entity) {
        return jvStoreDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public int delete(String schemaLabel, JVStoreEntity entity) {
        return jvStoreDao.delete(schemaLabel,entity);
    }

    @Override
    public void save(String schemaLabel, JVStoreEntity entity) {
        jvStoreDao.save(schemaLabel,entity);
    }

    @Override
    public void update(String schemaLabel, JVStoreEntity entity) {
        jvStoreDao.update(schemaLabel,entity);
    }

    @Override
    public List<String> queryjv() {
        return jvStoreDao.queryjv();
    }

    @Override
    public Map saveBatchJVStore(List<JVStoreEntity> jvStorelist,String userCode,HttpServletResponse response) {

        Map<String,Integer> result = new HashMap<>();
        //从excel成功读取的结果数量
        Integer successCount = 0;
        Integer failureCount = 0;
//        //从excel行数据读取结果
//        Boolean isSuccess=Boolean.TRUE;
        //成功的数据
        List<JVStoreEntity> jvStoreSuccessList = new ArrayList<>();
        //失败的数据
        List<JVStoreEntity> failureList = new ArrayList<>();
        for (JVStoreEntity entity :
                jvStorelist) {
            if(entity.getJvcode().length() > 255
                    || entity.getStoreCode().length() > 255
                    || entity.getStoreChinese().length() > 255
                    || entity.getJvcodeName().length() > 255
                    || entity.getStoreTax().length() > 255
                    || entity.getTaxpayerCode().length() > 255){
                entity.setFailureReason("数据长度不能超过255");
                //失败的数据
                failureList.add(entity);
            }else {
                jvStoreSuccessList.add(entity);
            }
        }
        successCount = jvStoreSuccessList.size();
        failureCount = failureList.size();
        List<List<JVStoreEntity>> splitProtocolList=splitList(jvStoreSuccessList,100);
        //批量保存协议
        for(List<JVStoreEntity> list : splitProtocolList ){
            for (JVStoreEntity entity :
                    list) {
                jvStoreDao.save("",entity);
            }

        }


        result.put("successIn",successCount);
        result.put("failureIn",failureCount);
//        result.put("fail",jvStorelist.size()-successCount);
        return result;
    }
    /**
     * 分批次导入数据
     * */
    private static  List<List<JVStoreEntity>> splitList(List<JVStoreEntity> sourceList, int  batchCount) {
        List<List<JVStoreEntity>> returnList =  new ArrayList<>();
        int  startIndex =  0 ;  // 从第0个下标开始
        while  (startIndex < sourceList.size()) {
            int  endIndex =  0 ;
            if  (sourceList.size() - batchCount < startIndex) {
                endIndex = sourceList.size();
            }  else  {
                endIndex = startIndex + batchCount;
            }
            returnList.add(sourceList.subList(startIndex, endIndex));
            startIndex = startIndex + batchCount;  // 下一批
        }
        return  returnList;
    }

}
