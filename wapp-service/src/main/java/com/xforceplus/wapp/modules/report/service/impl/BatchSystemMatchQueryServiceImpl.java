package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.posuopei.entity.MatchExcelEntity;
import com.xforceplus.wapp.modules.report.dao.BatchSystemMatchQueryDao;
import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryEntity;
import com.xforceplus.wapp.modules.report.entity.BatchSystemMatchQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.GfOptionEntity;
import com.xforceplus.wapp.modules.report.entity.MatchEntity;
import com.xforceplus.wapp.modules.report.service.BatchSystemMatchQueryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BatchSystemMatchQueryServiceImpl implements BatchSystemMatchQueryService {
    @Autowired
    private BatchSystemMatchQueryDao batchSystemMatchQueryDao;
    @Override
    public List<BatchSystemMatchQueryEntity> matchlists(Map<String, Object> map){
        return batchSystemMatchQueryDao.matchlists(map);
    }
    @Override
    public List<BatchSystemMatchQueryEntity> matchlistAll(Map<String, Object> map){
        return batchSystemMatchQueryDao.matchlistAll(map);
    }
    @Override
    public Integer matchlistCounts(Map<String, Object> map){
        return batchSystemMatchQueryDao.matchlistCounts(map);
    }
    @Override
    public List<GfOptionEntity> searchGf(){
        return batchSystemMatchQueryDao.searchGf();
    }
    @Override
    public List<BatchSystemMatchQueryExcelEntity> transformExcle(List<BatchSystemMatchQueryEntity> list){
        List<BatchSystemMatchQueryExcelEntity> matchExcelEntities=new ArrayList<>();
        for (int i=0; i<list.size();i++) {
            BatchSystemMatchQueryEntity entity = list.get(i);
            BatchSystemMatchQueryExcelEntity matchExcelEntity = new BatchSystemMatchQueryExcelEntity();


            //序号
            matchExcelEntity.setRownumber(String.valueOf(i + 1));
           matchExcelEntity.setMatchNo(  entity.getMatchNo());

           matchExcelEntity.setJv(  entity.getJv());
            //
           matchExcelEntity.setVender(  entity.getVender());
            //
           matchExcelEntity.setInvTotal(  entity.getInvTotal());
            //
           matchExcelEntity.setTaxAmount(  entity.getTaxAmount());
           matchExcelEntity.setTaxRate(  entity.getTaxRate());
            //
           matchExcelEntity.setInv( entity.getInv());
            //
           matchExcelEntity.setYymmdd(  (entity.getYy()+"-"+entity.getMm()+"-"+entity.getDd()));
            //
           matchExcelEntity.setYy1mm1dd1(  (entity.getYy1()+"-"+entity.getMm1()+"-"+entity.getDd1()));
            //
           matchExcelEntity.setTaxRate2(  entity.getTaxRate());
            //
            //导入日期
           matchExcelEntity.setCreateDate(  formatDate(entity.getCreateDate()));
            
            
            


            matchExcelEntities.add(matchExcelEntity);
        }
        return matchExcelEntities;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
    private String HostStatus(String status){
        return null==status ? "" :
                "0".equals(status) ? "未处理" :
                        "1".equals(status) ? "已处理" : "";
    }
}
