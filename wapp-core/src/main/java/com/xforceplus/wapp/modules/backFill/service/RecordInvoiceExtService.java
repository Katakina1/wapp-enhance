package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.repository.daoExt.TDxRecordInvoiceExtDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Kenny Wong on 2021/10/28.
 * 底账数据服务
 */
@Service
@Slf4j
public class RecordInvoiceExtService extends ServiceImpl<TDxRecordInvoiceExtDao, TDxRecordInvoiceEntity> {

    /**
     * 根据id扣除入参实体的剩余金额
     *
     * @param entityList 实体对象
     */
    public int deductRemainingAmount(TDxRecordInvoiceEntity entityList) {
        return baseMapper.deductRemainingAmount(entityList);
    }

    /**
     * 根据id扣除入参实体的剩余金额
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deductRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList) {
        return deductRemainingAmountById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id扣除入参实体的剩余金额
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deductRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "deductRemainingAmount";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TDxRecordInvoiceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    public int withdrawRemainingAmount(TDxRecordInvoiceEntity entityList) {
        return baseMapper.withdrawRemainingAmount(entityList);
    }


    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList) {
        return withdrawRemainingAmountById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "withdrawRemainingAmount";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TDxRecordInvoiceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
    }

    /**
     * 从底账表按照先进先出的方式获取一张合适的蓝票
     *
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @return
     */
    public TDxRecordInvoiceEntity obtainAvailableInvoice(String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate) {
        // 获取两年前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);
        TDxRecordInvoiceEntity record = getOne(new QueryWrapper<TDxRecordInvoiceEntity>()
                // 只返回第一行数据，否则getOne可能会报错
                .select("top 1  "
                        + TDxRecordInvoiceEntity.INVOICE_NO
                        ,TDxRecordInvoiceEntity.INVOICE_CODE
                        ,TDxRecordInvoiceEntity.INVOICE_AMOUNT
                        ,TDxRecordInvoiceEntity.REMAINING_AMOUNT
                        ,TDxRecordInvoiceEntity.INVOICE_DATE
                        ,TDxRecordInvoiceEntity.ID
                )
                .lambda()
                .eq(TDxRecordInvoiceEntity::getXfTaxNo, sellerTaxNo)
                .eq(TDxRecordInvoiceEntity::getGfTaxNo, purchaserTaxNo)
                .eq(TDxRecordInvoiceEntity::getTaxRate, taxRate)
                // 排除状态异常的发票（只要正常的发票）
                .eq(TDxRecordInvoiceEntity::getInvoiceStatus, "0")
                // 排除非专票（只要增值税专票 和 电子专票）
                .in(TDxRecordInvoiceEntity::getInvoiceType, InvoiceTypeEnum.SPECIAL_INVOICE.getValue(), InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue())
                // 排除可用金额=0的发票
                .and(
                        wrapper1 -> wrapper1
                                // remainingAmount > 0
                                .gt(TDxRecordInvoiceEntity::getRemainingAmount, BigDecimal.ZERO)
                                .or(
                                        wrapper2 -> wrapper2
                                                // remainingAmount is null and invoiceAmount > 0
                                                .isNull(TDxRecordInvoiceEntity::getRemainingAmount)
                                                .gt(TDxRecordInvoiceEntity::getInvoiceAmount, BigDecimal.ZERO)
                                )
                )
                // 开票日期再2年内的
                .gt(TDxRecordInvoiceEntity::getInvoiceDate, calendar.getTime())
                // 排除未完成付款的蓝票(已认证)
                .eq(TDxRecordInvoiceEntity::getRzhYesorno, "1")
                // 按照发票先进先出
                .orderByAsc(TDxRecordInvoiceEntity::getInvoiceDate));

        if (Objects.nonNull(record) && Objects.isNull(record.getRemainingAmount())) {
            record.setRemainingAmount(record.getInvoiceAmount());
        }
        return record;
    }

    /**
     * 从底账表按照先进先出的方式获取一组合适的蓝票
     *
     * @param sellerTaxNo
     * @param purchaserTaxNo
     * @param taxRate
     * @return
     */
    public Page<TDxRecordInvoiceEntity> obtainAvailableInvoices(String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, long pageNo, long pageSize) {
        // 获取两年前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);
        Page<TDxRecordInvoiceEntity> result = baseMapper.selectPage(new Page<>(pageNo, pageSize),
                new QueryWrapper<TDxRecordInvoiceEntity>()
                        .lambda()
                        .eq(TDxRecordInvoiceEntity::getXfTaxNo, sellerTaxNo)
                        .eq(TDxRecordInvoiceEntity::getGfTaxNo, purchaserTaxNo)
                        .eq(TDxRecordInvoiceEntity::getTaxRate, taxRate)
                        // 排除状态异常的发票（只要正常的发票）
                        .eq(TDxRecordInvoiceEntity::getInvoiceStatus, "0")
                        // 排除非专票（只要增值税专票 和 电子专票）
                        .in(TDxRecordInvoiceEntity::getInvoiceType, InvoiceTypeEnum.SPECIAL_INVOICE.getValue(), InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue())
                        // 排除可用金额=0的发票
                        .and(
                                wrapper1 -> wrapper1
                                        // remainingAmount > 0
                                        .gt(TDxRecordInvoiceEntity::getRemainingAmount, BigDecimal.ZERO)
                                        .or(
                                                wrapper2 -> wrapper2
                                                        // remainingAmount is null and invoiceAmount > 0
                                                        .isNull(TDxRecordInvoiceEntity::getRemainingAmount)
                                                        .gt(TDxRecordInvoiceEntity::getInvoiceAmount, BigDecimal.ZERO)
                                        )
                        )
                        // 排除未完成付款的蓝票(已认证)
                        .eq(TDxRecordInvoiceEntity::getRzhYesorno, "1")
                        // 开票日期再2年内的
                        .gt(TDxRecordInvoiceEntity::getInvoiceDate, calendar.getTime())
                        // 按照发票先进先出
                        .orderByAsc(TDxRecordInvoiceEntity::getInvoiceDate));
        // remainingAmount初始化为invoiceAmount
        Optional.ofNullable(result.getRecords()).ifPresent(x->x.forEach(
                record -> {
                    if (Objects.isNull(record.getRemainingAmount())) {
                        record.setRemainingAmount(record.getInvoiceAmount());
                    }
                }
        ));
        return result;
    }

}
