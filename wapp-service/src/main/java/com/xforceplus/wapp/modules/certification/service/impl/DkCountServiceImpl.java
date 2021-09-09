package com.xforceplus.wapp.modules.certification.service.impl;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.certification.dao.DkCountDao;
import com.xforceplus.wapp.modules.certification.dao.ManualCheckDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountDetail;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountLogEntity;
import com.xforceplus.wapp.modules.certification.service.DkCountService;
import com.xforceplus.wapp.modules.certification.service.ManualCheckService;
import com.xforceplus.wapp.modules.job.entity.TAcOrg;
import com.xforceplus.wapp.modules.job.entity.TDxTaxCurrent;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.signin.dao.HandWorkRepository;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 手工勾选
 * @author kevin.wang
 * @date 4/14/2018
 */
@Service
public class DkCountServiceImpl implements DkCountService {
    @Autowired
    private ManualCheckDao manualCheckDao;
    @Autowired
    private HandWorkRepository handWorkRepository;
    @Autowired
    private DkCountDao dkCountDao;

    /**
     * 抵扣统计
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据集
     */
    @Override
    public List<TDxDkCountEntity> queryList(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return dkCountDao.queryList(schemaLabel,map);
    }

    /**
     * 抵扣统计
     * @param map 查询条件
     * @param schemaLabel mycat分库参数
     * @return 可勾选操作的数据数
     */
    @Override
    public ReportStatisticsEntity queryTotal(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return dkCountDao.queryTotal(schemaLabel,map);
    }
    @Override
    public Integer selectBeforeTj(Map<String,Object> map){
        return dkCountDao.selectBeforeTj(map);
    }
    @Override
    public void insertDk(String gfsh,String skssq){
        dkCountDao.insertDk(gfsh,skssq);
    }
    @Override
    public void insertConfirm(String gfsh,String skssq,String ywmm){
        dkCountDao.insertConfirm(gfsh,skssq,ywmm);
    }
    @Override
    public void insertCxtj(String gfsh,String skssq){
        dkCountDao.insertCxtj(gfsh,skssq);
    }
    @Override
    public void insertCxqs(String gfsh,String skssq){
        dkCountDao.insertCxqs(gfsh,skssq);
    }

    @Override
    public List<TDxDkCountEntity> selectDksh(String[] gfsh){
        return dkCountDao.selectDksh(gfsh);
    }
    @Override
    public List<TDxDkCountEntity> checkDksh(Map<String,Object> map){
        return dkCountDao.checkDksh(map);
    }
    @Override
    public void insertDkLog(String[] gfshs,String[] skssqs,String loginName,String operaType){
        List<TDxDkCountLogEntity> logs = new ArrayList<>();
        TDxDkCountLogEntity log = new TDxDkCountLogEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = sdf.format(new Date());
        for (int i=0;i<gfshs.length;i++){
            log.setTaxno(gfshs[i]);
            log.setOperaType(operaType);
            log.setSkssq(skssqs[i]);
            log.setOperaName(loginName);
            log.setCreateDate(createDate);
            logs.add(log);
        }
        if (logs.size()>0){
            dkCountDao.insertDkLog(logs);
        }
    }
    @Override
    public List<TDxDkCountDetail> selectDkDetail(String gfsh, String skssq){
        return dkCountDao.selectDkDetail(gfsh,skssq);
    }

    @Override
    public TAcOrg selectUpgrad(String gfsh) {
        return dkCountDao.selectUpgrad(gfsh);
    }
}
