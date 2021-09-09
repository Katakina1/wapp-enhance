package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.einvoice.service.EinvoiceUploadService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.SignInqueryMRXYDao;
import com.xforceplus.wapp.modules.signin.dao.SignatureProcessingDao;
import com.xforceplus.wapp.modules.signin.dao.SweepCodeDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.enumflord.SignInEnum;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryMRXYService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@Service
@Transactional
public class SignInqueryServiceMRXYImpl implements SignInInqueryMRXYService {

    private SignInqueryMRXYDao signInqueryDao;

    private final String code="0001";

    private final static String GF_TAX_NO="91110108MA004CPN95";

    private SignatureProcessingDao signatureProcessingDao;



    private final EinvoiceUploadService einvoiceUploadService;

    private SweepCodeDao sweepCodeDao;

    private final SystemConfig systemConfig;

    @Autowired
    public SignInqueryServiceMRXYImpl(SignInqueryMRXYDao signInqueryDao,SignatureProcessingDao signatureProcessingDao,  EinvoiceUploadService einvoiceUploadService, SweepCodeDao sweepCodeDao, SystemConfig systemConfig) {
        this.signatureProcessingDao = signatureProcessingDao;
        this.einvoiceUploadService = einvoiceUploadService;
        this.sweepCodeDao = sweepCodeDao;
        this.systemConfig = systemConfig;
        this.signInqueryDao = signInqueryDao;
    }


    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return signInqueryDao.searchGf(schemaLabel,userId);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return signInqueryDao.queryTotal(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return signInqueryDao.getRecordIncoiceList(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params) {
        return signInqueryDao.queryAllList(schemaLabel,params);
    }

    @Override
    public Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query) {
        return signInqueryDao.getSumAmount(schemaLabel,query);
    }

    @Override
    public Boolean checkedInvoice(String schemaLabel, Map<String, Object> params) {

        String uuid= (String)params.get("invoiceCode")+params.get("invoiceNo");
        RecordInvoiceEntity rq=sweepCodeDao.getIncoiceData(schemaLabel,uuid);
        if(rq!=null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean updateInvoice(String schemaLabel, String uuid, UserEntity user, Map<String ,Object> params) throws RRException {

        RecordInvoiceEntity rq=sweepCodeDao.getIncoiceData(schemaLabel, uuid);
        //只有查验成功数据才能去判断是否有权限签收
        Boolean checkPowerResult=einvoiceUploadService.checkUserTaxNoPower(schemaLabel, Long.valueOf(user.getUserid()),rq.getGfTaxNo());
        if(checkPowerResult){
            comparisonData(params, rq);
            rq.setQs(SignInEnum.NUMBER_ONE.getValue());
            rq.setQsStatus(SignInEnum.NUMBER_ONE.getValue());
            //更新抵账表签收数据
            signatureProcessingDao.updateInvoice(schemaLabel,uuid);
            //更新扫描表签收信息
            signatureProcessingDao.updateDxInvoiceData(schemaLabel,uuid,rq);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
    private void comparisonData(Map<String, Object> params, RecordInvoiceEntity rq) throws RRException {
        //开票日期
        final String invoiceDate = new DateTime(rq.getInvoiceDate()).toString(SHORT_DATE_FORMAT);
        rq.setNotes("签收成功");
        if(!(params.get("invoiceDate").equals(invoiceDate))) {
            rq.setNotes("开票日期与实际不符");
            throw new RRException("开票日期与实际不符");

        }

        //购方税号
        if(!(params.get("gfTaxNo").equals(rq.getGfTaxNo()))) {
            rq.setNotes("购方税号与实际不符");
            throw new RRException("购方税号与实际不符");

        }

        //销方税号
        if(!(params.get("xfTaxNo").equals(rq.getXfTaxNo()))) {
            rq.setNotes("销方税号与实际不符");
            throw new RRException("销方税号与实际不符");

        }

        //发票金额
        if(new BigDecimal(String.valueOf(params.get("invoiceAmount"))).compareTo(rq.getInvoiceAmount()) != 0) {
            rq.setNotes("发票金额与实际不符");
            throw new RRException("发票金额与实际不符");

        }

        //发票税额
        if(new BigDecimal(String.valueOf(params.get("taxAmount"))).compareTo(rq.getTaxAmount()) != 0) {
            rq.setNotes("发票税额与实际不符");
            throw new RRException("发票税额与实际不符");

        }

        //价税合计金额
        if(new BigDecimal(String.valueOf(params.get("totalAmount"))).compareTo(BigDecimal.valueOf(rq.getTotalAmount())) != 0) {
            rq.setNotes("价税合计金额与实际不符");
            throw new RRException("价税合计金额与实际不符");

        }
    }


    public int batchUpdate(String schemaLabel,UserEntity user,Set<String> uuIdSet) {
        int tbNum=0;
        for (String uuid : uuIdSet) {
            RecordInvoiceEntity rq=sweepCodeDao.getIncoiceData(schemaLabel, uuid);
            //只有查验成功数据才能去判断是否有权限签收
            Boolean checkPowerResult=einvoiceUploadService.checkUserTaxNoPower(schemaLabel, Long.valueOf(user.getUserid()),rq.getGfTaxNo());
            Map<String, Object> params=new HashMap<>();
            params.put("invoiceDate",rq.getInvoiceDate());
            params.put("gfTaxNo",rq.getGfTaxNo());
            params.put("xfTaxNo",rq.getXfTaxNo());
            params.put("invoiceAmount",rq.getInvoiceAmount());
            params.put("taxAmount",rq.getTaxAmount());
            params.put("totalAmount",rq.getTotalAmount());
            if(checkPowerResult){
                comparisonData(params, rq);
                rq.setQs(SignInEnum.NUMBER_ONE.getValue());
                rq.setQsStatus(SignInEnum.NUMBER_ONE.getValue());
                //更新抵账表签收数据
                signatureProcessingDao.updateInvoice(schemaLabel,uuid);
                //更新扫描表签收信息
                signatureProcessingDao.updateDxInvoiceData(schemaLabel,uuid,rq);
                tbNum++;
            }
        }
        return tbNum;
    }
}
