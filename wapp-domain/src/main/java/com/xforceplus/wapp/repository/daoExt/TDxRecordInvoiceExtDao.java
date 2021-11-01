package com.xforceplus.wapp.repository.daoExt;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface TDxRecordInvoiceExtDao extends BaseMapper<TDxRecordInvoiceEntity> {

    /**
     * 扣除发票上的剩余金额，必须保证剩余金额>=0, 并且<=不含税金额
     *
     * @param tDxRecordInvoiceEntity
     * @return
     */
    @Update("update t_dx_record_invoice set remaining_amount = IIF(remaining_amount is null , invoice_amount - #{et.remainingAmount}  , remaining_amount - #{et.remainingAmount} )" +
            "where id = #{et.id} " +
            "and IIF(remaining_amount is null ,invoice_amount-#{et.remainingAmount}, remaining_amount - #{et.remainingAmount}) >= 0 "
//            + "and IIF(remaining_amount is null ,remaining_amount - #{et.remainingAmount}) <= invoice_amount"
    )
    int deductRemainingAmount(@Param(Constants.ENTITY) TDxRecordInvoiceEntity tDxRecordInvoiceEntity);

    /**
     * 补充发票上的剩余金额，必须保证剩余金额>=0, 并且<=不含税金额
     *
     * @param tDxRecordInvoiceEntity
     * @return
     */
    @Update("update t_dx_record_invoice set remaining_amount = IIF(remaining_amount is null , invoice_amount - #{et.remainingAmount}  ,  remaining_amount + #{et.remainingAmount} )" +
            "where id = #{et.id} " +
            "and IIF(remaining_amount is null , invoice_amount - #{et.remainingAmount}  , remaining_amount + #{et.remainingAmount}) >= 0 ")
            // "and (remaining_amount + #{et.remainingAmount}) <= invoice_amount")
    int withdrawRemainingAmount(@Param(Constants.ENTITY) TDxRecordInvoiceEntity tDxRecordInvoiceEntity);
}
