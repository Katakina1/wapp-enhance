package com.xforceplus.wapp.modules.signin.service.impl;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.dao.HandWorkRepository;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDetailEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.HandWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * CreateBy leal.liang on 2018/4/12.
 **/
@Service
@Transactional
public class HandWorkServiceImpl implements HandWorkService {


    private HandWorkRepository handWorkRepository;

    @Autowired
    public HandWorkServiceImpl(HandWorkRepository handWorkRepository) {
        this.handWorkRepository = handWorkRepository;
    }


    @Override
    public List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query) {
        return handWorkRepository.selectInvoiceList(schemaLabel,query);
    }

    @Override
    public int queryTotal(String schemaLabel, Query query) {
        return handWorkRepository.getInvoiceTotal(schemaLabel,query);
    }

    @Override
    public List<RecordInvoiceEntity> queryList(String schemaLabel, Map<String, Object> query) {
        return handWorkRepository.queryList(schemaLabel,query);
    }

    @Override
    public Boolean receiptInvoice(String schemaLabel, Long[] ids, UserEntity user) {
        Boolean a=true;
        for (Long  id:ids){
            //手工签收-更改抵账表数据
            a=handWorkRepository.receiptInvoice(schemaLabel,id);
            //根据id获取发票的信息
            RecordInvoiceDataEntity r= handWorkRepository.getInvoiceDataById(schemaLabel,id);
            r.setUserName(user.getUsername());
            r.setUserNum(user.getLoginname());
            //保存发票信息到扫描表
            handWorkRepository.saveData(schemaLabel,r);
        }
        return a;
    }

    @Override
    public RecordInvoiceDetailEntity getRecordIncoiceDetail(String schemaLabel, String invoiceNo) {
        return handWorkRepository.getInvoiceDetail(schemaLabel,invoiceNo);
    }

    @Override
    public List<OptionEntity> searchGf(String schemaLabel, Long userId) {
        return handWorkRepository.searchGf(schemaLabel,userId);
    }
}
