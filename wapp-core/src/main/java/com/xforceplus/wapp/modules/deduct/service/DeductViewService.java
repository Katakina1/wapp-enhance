package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.claim.mapstruct.DeductMapper;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-15 18:58
 **/
@Service
public class DeductViewService extends ServiceImpl<TXfBillDeductExtDao,TXfBillDeductEntity> {

    @Autowired
    private DeductMapper deductMapper;

    public Map<String, Object> summary(DeductListRequest request, XFDeductionBusinessTypeEnum typeEnum) {
        TXfBillDeductEntity deductEntity=new TXfBillDeductEntity();
        deductEntity.setBusinessNo(request.getBillNo());
        deductEntity.setPurchaserNo(request.getPurchaserNo());
        deductEntity.setBusinessType(typeEnum.getType());



        LambdaQueryWrapper<TXfBillDeductEntity> wrapper= Wrappers.lambdaQuery(deductEntity);
        //扣款日期>>Begin
        final String deductDateBegin = request.getDeductDateBegin();
        if (StringUtils.isNotBlank(deductDateBegin)){
            wrapper.gt(TXfBillDeductEntity::getDeductDate,deductDateBegin);
        }

        //扣款日期>>End
        String deductDateEnd = request.getDeductDateEnd();
        if (StringUtils.isNotBlank(deductDateEnd)){
            final String format = DateUtils.addDayToYYYYMMDD(deductDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity::getDeductDate,format);
        }
        // ===============================
        //定案、入账日期 >> begin
        final String verdictDateBegin = request.getVerdictDateBegin();
        if (StringUtils.isNotBlank(verdictDateBegin)){
            wrapper.gt(TXfBillDeductEntity::getVerdictDate,deductDateBegin);
        }

        //定案、入账日期 >> end
        String verdictDateEnd = request.getVerdictDateEnd();
        if (StringUtils.isNotBlank(verdictDateEnd)){
            final String format = DateUtils.addDayToYYYYMMDD(verdictDateEnd, 1);
            wrapper.lt(TXfBillDeductEntity::getVerdictDate,format);
        }
        wrapper.select(TXfBillDeductEntity::getTaxRate,o-> "count(1) as count");


        final Map<String, Object> map = this.getMap(wrapper.groupBy(TXfBillDeductEntity::getTaxRate));
        return map;
    }
}
