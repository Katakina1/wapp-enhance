package com.xforceplus.wapp.modules.blue.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.backFill.model.BackFillVerifyBean;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBlueRelationDao;
import com.xforceplus.wapp.repository.entity.TXfBlueRelationEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 19:53
 **/
@Service
public class BlueInvoiceRelationServiceImpl extends ServiceImpl<TXfBlueRelationDao, TXfBlueRelationEntity> implements BlueInvoiceRelationService {
   @Autowired
   private IDSequence idSequence;

    /**
     * 插入（批量）
     *
     */
    @Override
    public boolean saveBatch(String originInvoiceNo, String originInvoiceCode, List<BackFillVerifyBean> blueInvoices) {
        final List<TXfBlueRelationEntity> collect = blueInvoices.stream().map(x -> {
            TXfBlueRelationEntity entity = new TXfBlueRelationEntity();
            entity.setBlueInvoiceCode(x.getInvoiceCode());
            entity.setBlueInvoiceNo(x.getInvoiceNo());
            entity.setRedInvoiceNo(originInvoiceNo);
            entity.setRedInvoiceCode(originInvoiceCode);
            entity.setCreateTime(new Date());
            entity.setId(idSequence.nextId());
            entity.setCreateUser(UserUtil.getLoginName());
            return entity;
        }).collect(Collectors.toList());
        return super.saveBatch(collect);
    }
}
