package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.ReturnInfoEnum;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.signin.dao.SignImportDao;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import com.xforceplus.wapp.modules.signin.service.ImportSignService;
import com.xforceplus.wapp.modules.Constant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;


@Component
@Async
public class CheckScheduled {
    private final static Logger log = getLogger(InvoiceCheckModel.class);
    @Autowired
    private SignImportDao signImportDao;
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private ImportSignService importSignService;
    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat(Constant.SHORT_DATE_FORMAT);
//    @Scheduled(cron = "0/60 * * * * *")
    public void scheduled(){
        //查询底账表里采集无明细的发票数据
        List<RecordInvoiceDataEntity> recordInvoiceDataEntityList = signImportDao.getRecordDataByDetailYesornoAndSourceSystem();
        for (RecordInvoiceDataEntity recordInvoiceDataEntity :recordInvoiceDataEntityList){
            int checkIndex=3;

            //查验
            final RequestData requestData = new RequestData();
            //购方税号
            requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
            //发票类型
            requestData.setInvoiceType(recordInvoiceDataEntity.getInvoiceType());
            if(StringUtils.isNotBlank(recordInvoiceDataEntity.getInvoiceCode())){
                String fplx=   CommonUtil.getFplx(recordInvoiceDataEntity.getInvoiceCode());
                if(StringUtils.isNotBlank(fplx)){
                    recordInvoiceDataEntity.setInvoiceType(fplx);
                }
            }
            //发票代码
            requestData.setInvoiceCode(recordInvoiceDataEntity.getInvoiceCode());
            //发票号码
            requestData.setInvoiceNo(recordInvoiceDataEntity.getInvoiceNo());


            //开票时间
            final String invoiceDate = simpleDateFormat.format(recordInvoiceDataEntity.getInvoiceDate());
            requestData.setInvoiceDate(invoiceDate);
            //校验码
            requestData.setCheckCode(recordInvoiceDataEntity.getCheckCode());
            //金额
            if (recordInvoiceDataEntity.getInvoiceAmount() != null) {
                requestData.setInvoiceAmount(String.valueOf(recordInvoiceDataEntity.getInvoiceAmount()));
            } else {
                requestData.setInvoiceAmount(StringUtils.EMPTY);
            }
            ResponseInvoice responseInvoice = null;
            try {
                responseInvoice = null;
            }catch (Exception e) {
                log.info(recordInvoiceDataEntityList.toString()+"查验失败");
                break;
            }
            if (responseInvoice != null && ReturnInfoEnum.CHECK_SUCCESS.getResultCode().equals(responseInvoice.getResultCode())) {
                if(recordInvoiceDataEntity.getGfTaxNo().length()>responseInvoice.getBuyerTaxNo().length()){
                    recordInvoiceDataEntity.setNewGfTaxno(recordInvoiceDataEntity.getGfTaxNo());
                }else{
                    recordInvoiceDataEntity.setNewGfTaxno(responseInvoice.getBuyerTaxNo());
                }
                //更改底账明细
                //查验成功的底账集
                final List<ResponseInvoice> responseInvoiceList = newArrayList();
                responseInvoiceList.add(responseInvoice);
                importSignService.inspectionProcess(null, responseInvoiceList, false,recordInvoiceDataEntity.getGfTaxNo());
            }else {
                //查验不一致 赋值 签收失败
                log.info(recordInvoiceDataEntityList.toString()+"查验失败");
                break;
            }

        }

        log.info("=====>>>>>使用cron  {}",System.currentTimeMillis());
    }
}
