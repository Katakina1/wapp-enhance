package com.xforceplus.wapp.repository.daoExt;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface TDxRecordInvoiceExtDao extends BaseMapper<TDxRecordInvoiceEntity> {

    @Update("update t_dx_record_invoice set remaining_amount = remaining_amount + #{et.remainingAmount} where id = #{et.id}")
    int withdrawRemainingAmount(@Param(Constants.ENTITY) TDxRecordInvoiceEntity tDxRecordInvoiceEntity);
}
