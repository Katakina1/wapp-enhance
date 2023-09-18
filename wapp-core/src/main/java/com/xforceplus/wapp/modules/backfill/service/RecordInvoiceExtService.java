package com.xforceplus.wapp.modules.backfill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.repository.dao.TDxTaxCurrentDao;
import com.xforceplus.wapp.repository.daoExt.TDxRecordInvoiceExtDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxTaxCurrentEntity;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TDxTaxCurrentDao tDxTaxCurrentDao;

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
     * @param notQueryOil
     * @param invoiceDateOrder DESC | ASC
     * @return
     */
    public TDxRecordInvoiceEntity obtainAvailableInvoice(List<Long> preIdList, String sellerTaxNo, String purchaserTaxNo
            , BigDecimal taxRate, boolean notQueryOil,String invoiceDateOrder) {
        log.info("obtainAvailableInvoice, sellerTaxNo:{},purchaserTaxNo:{},taxRate:{} 是否查询成品油发票：{}", sellerTaxNo, purchaserTaxNo, taxRate, !notQueryOil);
        //根据购方税号获取当前征期
        String currentTaxPeriod = null;
        QueryWrapper<TDxTaxCurrentEntity> taxCurrentQueryWrapper = new QueryWrapper<TDxTaxCurrentEntity>();
        taxCurrentQueryWrapper.eq(TDxTaxCurrentEntity.TAXNO,purchaserTaxNo);
        TDxTaxCurrentEntity taxCurrentEntity = tDxTaxCurrentDao.selectOne(taxCurrentQueryWrapper);
        if (taxCurrentEntity != null && StringUtils.isNotBlank(taxCurrentEntity.getCurrentTaxPeriod())){
          currentTaxPeriod = taxCurrentEntity.getCurrentTaxPeriod();
        }

        // 获取两年前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);
        // 只返回第一行数据，否则getOne可能会报错
        QueryWrapper<TDxRecordInvoiceEntity> queryWrapper = new QueryWrapper<TDxRecordInvoiceEntity>();
		queryWrapper.select("top 1  "
                + TDxRecordInvoiceEntity.INVOICE_NO
                ,TDxRecordInvoiceEntity.INVOICE_CODE
                ,TDxRecordInvoiceEntity.INVOICE_AMOUNT
                ,TDxRecordInvoiceEntity.REMAINING_AMOUNT
                ,TDxRecordInvoiceEntity.INVOICE_DATE
                ,TDxRecordInvoiceEntity.ID
                ,TDxRecordInvoiceEntity.IS_OIL
        )
        .lambda()
        .notIn(TDxRecordInvoiceEntity::getId, preIdList)
        .eq(TDxRecordInvoiceEntity::getXfTaxNo, sellerTaxNo)
        .eq(TDxRecordInvoiceEntity::getGfTaxNo, purchaserTaxNo)
        .eq(TDxRecordInvoiceEntity::getTaxRate, taxRate)
        // 排除状态异常的发票（只要正常的发票）
        .eq(TDxRecordInvoiceEntity::getInvoiceStatus, "0")
        .eq(TDxRecordInvoiceEntity::getFlowType, "1")
        // 排除非专票（只要增值税专票 和 电子专票）
        .in(TDxRecordInvoiceEntity::getInvoiceType, InvoiceTypeEnum.SPECIAL_INVOICE.getValue(), InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue())
        // 排除可用金额<=1的发票
        .and(
                wrapper1 -> wrapper1
                        // remainingAmount > 1
                        .gt(TDxRecordInvoiceEntity::getRemainingAmount, BigDecimal.ONE)
                        .or(
                                wrapper2 -> wrapper2
                                        // remainingAmount is null and invoiceAmount > 1
                                        .isNull(TDxRecordInvoiceEntity::getRemainingAmount)
                                        .gt(TDxRecordInvoiceEntity::getInvoiceAmount, BigDecimal.ONE)
                        )
        )
        // 开票日期再2年内的
        .gt(TDxRecordInvoiceEntity::getInvoiceDate, calendar.getTime())
        // 排除未完成付款的蓝票(已认证)
        .eq(TDxRecordInvoiceEntity::getRzhYesorno, "1")
        //判断是否查询成品油
        .ne(notQueryOil, TDxRecordInvoiceEntity::getIsOil, "1");

		    if (StringUtil.isNotBlank(currentTaxPeriod)){
		      //蓝票当前税款所属期 需小于 当前购方税号当前税款所属期
          queryWrapper.lt(TDxRecordInvoiceEntity.RZH_BELONG_DATE, currentTaxPeriod);
        }

        // 按照发票先进先出  2022-08-23 新增 协议匹配蓝票时，假设期间是202007-202207，之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
		//https://jira.xforceplus.com/browse/PRJCENTER-10272
		if ("DESC".equals(invoiceDateOrder)) {
			queryWrapper.orderByDesc(TDxRecordInvoiceEntity.INVOICE_DATE);
		}else {
			queryWrapper.orderByAsc(TDxRecordInvoiceEntity.INVOICE_DATE);
		}
        queryWrapper.orderByAsc(TDxRecordInvoiceEntity.ID);
                
        TDxRecordInvoiceEntity record = getOne(queryWrapper);
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
     * @param notQueryOil
     * @return
     */
    public Page<TDxRecordInvoiceEntity> obtainAvailableInvoices(String sellerTaxNo, String purchaserTaxNo, BigDecimal taxRate, long pageNo, long pageSize, boolean notQueryOil, 
    		TXfDeductionBusinessTypeEnum deductionEnum) {
        log.info("是否查询成品油发票：{}", !notQueryOil);
        // 获取两年前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);
        QueryWrapper<TDxRecordInvoiceEntity> queryWrapper = new QueryWrapper<TDxRecordInvoiceEntity>();
        queryWrapper.lambda()
        .ne(notQueryOil, TDxRecordInvoiceEntity::getIsOil, "1")
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
        .gt(TDxRecordInvoiceEntity::getInvoiceDate, calendar.getTime());
        // 按照发票先进先出  2022-08-23 新增 协议匹配蓝票时，假设期间是202007-202207，之前是从2020年的发票开始匹配，现在要改成先从2022年发票开始匹配，匹配近两年供应商蓝票规则不变。
 		//https://jira.xforceplus.com/browse/PRJCENTER-10272
 		if (deductionEnum == TXfDeductionBusinessTypeEnum.AGREEMENT_BILL) {
 			queryWrapper.orderByDesc(TDxRecordInvoiceEntity.INVOICE_DATE);
 		}else {
 			queryWrapper.orderByAsc(TDxRecordInvoiceEntity.INVOICE_DATE);
 		}
		queryWrapper.orderByAsc(TDxRecordInvoiceEntity.ID);
		Page<TDxRecordInvoiceEntity> result = baseMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
		// remainingAmount初始化为invoiceAmount
		Optional.ofNullable(result.getRecords()).ifPresent(x -> x.forEach(record -> {
			if (Objects.isNull(record.getRemainingAmount())) {
				record.setRemainingAmount(record.getInvoiceAmount());
			}
		}));
		return result;
    }

}
