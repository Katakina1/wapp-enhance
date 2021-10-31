package com.xforceplus.wapp.modules.invoice.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductInvoiceBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfSettlementItemFlagEnum;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.invoice.mapstruct.InvoiceMapper;
import com.xforceplus.wapp.modules.settlement.dto.InvoiceMatchedRequest;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
@Slf4j
public class InvoiceServiceImpl extends ServiceImpl<TDxRecordInvoiceDao, TDxRecordInvoiceEntity> {

    @Autowired
    InvoiceMapper invoiceMapper;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private BlueInvoiceService blueInvoiceService;
    @Autowired
    private IDSequence idSequence;
    @Autowired
    private DeductService deductService;


    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(List<TDxRecordInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "update t_dx_record_invoice set remaining_amount = remaining_amount + #{remainingAmount} where id = #{id}";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TDxRecordInvoiceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
    }


    /**
     * 保存结算单匹配的蓝票
     */
    @Transactional
    public void saveSettlementMatchedInvoice(Long settlementId, InvoiceMatchedRequest request) {
        TXfSettlementEntity tXfSettlementEntity = settlementService.getById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if (CollectionUtils.isEmpty(request.getInvoiceList())) {
            throw new EnhanceRuntimeException("结算单匹配蓝票为空");
        }
        //校验数据
        checkSettlementMatchedInvoice(tXfSettlementEntity, request);
        //释放结算单明细、匹配的蓝票
        releaseSettlementItemAndInvoice(tXfSettlementEntity);
        //保存匹配结果
        AtomicReference<BigDecimal> totalUseAmount = new AtomicReference<>(BigDecimal.ZERO);
        request.getInvoiceList().parallelStream().forEach(invoice -> {
            BigDecimal settlementAmountWithoutTax = tXfSettlementEntity.getAmountWithoutTax();
            if (totalUseAmount.get().compareTo(settlementAmountWithoutTax) >= 0) {
                log.info("结算单id={},匹配多余的发票={}", settlementId, JSON.toJSONString(invoice));
                return;
            }
            //底账数据
            LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo())
                    .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode());
            TDxRecordInvoiceEntity tDxInvoice = this.baseMapper.selectOne(wrapper);
            //默认使用底账金额
            BigDecimal useAmount = tDxInvoice.getRemainingAmount();
            //默认底账剩余额度
            BigDecimal remainingAmount = BigDecimal.ZERO;
            //匹配一张发票后明细总额
            totalUseAmount.set(totalUseAmount.get().add(tDxInvoice.getRemainingAmount()));
            //判断明细总额与结算单总额的差额
            BigDecimal exceedAmount = totalUseAmount.get().subtract(settlementAmountWithoutTax);
            //如果超额 则取底账部分金额
            if (exceedAmount.compareTo(BigDecimal.ZERO) > 0) {
                useAmount = tDxInvoice.getRemainingAmount().subtract(exceedAmount);
                remainingAmount = exceedAmount;
            }
            //处理结算单蓝票
            dealSettlementBillDeductInvoice(tXfSettlementEntity, tDxInvoice, useAmount, remainingAmount);
            //处理结算单明细
            dealSettlementItem(tXfSettlementEntity, tDxInvoice, useAmount);
        });
    }

    private void releaseSettlementItemAndInvoice(TXfSettlementEntity tXfSettlementEntity) {
        LambdaQueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new LambdaQueryWrapper<>();
        tXfBillDeductInvoiceWrapper
                .eq(TXfBillDeductInvoiceEntity::getBusinessNo, tXfSettlementEntity.getSettlementNo())
                .eq(TXfBillDeductInvoiceEntity::getBusinessType, TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceEntityList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceEntityList.parallelStream().forEach(tXfBillDeductInvoiceEntity -> {
            //释放匹配蓝票
            TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoiceEntity.getId());
            updateTXfBillDeductInvoiceEntity.setStatus(1);
            tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
            //还原底账蓝票额度
            LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TDxRecordInvoiceEntity::getInvoiceNo, tXfBillDeductInvoiceEntity.getInvoiceNo())
                    .eq(TDxRecordInvoiceEntity::getInvoiceCode, tXfBillDeductInvoiceEntity.getInvoiceCode());
            TDxRecordInvoiceEntity tDxInvoice = this.baseMapper.selectOne(wrapper);
            if (tDxInvoice != null) {
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoice.getId());
                updateTDxInvoiceEntity.setRemainingAmount(tDxInvoice.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
                this.baseMapper.updateById(updateTDxInvoiceEntity);
            }
        });
        //删除结算单明细
        QueryWrapper<TXfSettlementItemEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSettlementItemEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        tXfSettlementItemDao.delete(queryWrapper);
    }

    private void dealSettlementBillDeductInvoice(TXfSettlementEntity tXfSettlementEntity,
                                                 TDxRecordInvoiceEntity tDxInvoice,
                                                 BigDecimal useAmount, BigDecimal remainingAmount) {
        //保存的匹配蓝票
        TXfBillDeductInvoiceEntity newTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
        newTXfBillDeductInvoiceEntity.setInvoiceNo(tDxInvoice.getInvoiceNo());
        newTXfBillDeductInvoiceEntity.setInvoiceCode(tDxInvoice.getInvoiceCode());
        newTXfBillDeductInvoiceEntity.setThridId(tXfSettlementEntity.getId());
        newTXfBillDeductInvoiceEntity.setBusinessNo(tXfSettlementEntity.getSettlementNo());
        newTXfBillDeductInvoiceEntity.setBusinessType(TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());
        newTXfBillDeductInvoiceEntity.setStatus(0);
        newTXfBillDeductInvoiceEntity.setUseAmount(useAmount);
        newTXfBillDeductInvoiceEntity.setCreateTime(new Date());
        newTXfBillDeductInvoiceEntity.setUpdateTime(new Date());
        tXfBillDeductInvoiceDao.insert(newTXfBillDeductInvoiceEntity);
        //使用底账蓝票额度
        TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
        updateTDxInvoiceEntity.setId(tDxInvoice.getId());
        updateTDxInvoiceEntity.setRemainingAmount(remainingAmount);
        this.baseMapper.updateById(updateTDxInvoiceEntity);
    }

    private void dealSettlementItem(TXfSettlementEntity tXfSettlementEntity,
                                    TDxRecordInvoiceEntity tDxInvoice,
                                    BigDecimal useAmount) {
        //匹配计算单明细
        List<TDxRecordInvoiceDetailEntity> tDxRecordInvoiceDetailList = blueInvoiceService.obtainAvailableItems(tDxInvoice.getInvoiceNo() + tDxInvoice.getInvoiceCode(),
                tDxInvoice.getInvoiceAmount(), tDxInvoice.getRemainingAmount(), useAmount);
        //保存匹配的结算单明细
        tDxRecordInvoiceDetailList.forEach(invoiceItem -> {
            TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
            tXfSettlementItemEntity.setUnitPrice(new BigDecimal(Optional.ofNullable(invoiceItem.getUnitPrice()).orElse("0")));
            tXfSettlementItemEntity.setTaxAmount(new BigDecimal(Optional.ofNullable(invoiceItem.getTaxAmount()).orElse("0")));
            tXfSettlementItemEntity.setGoodsTaxNo(invoiceItem.getGoodsNum());
            tXfSettlementItemEntity.setTaxRate(new BigDecimal(Optional.ofNullable(invoiceItem.getTaxRate()).orElse("0")));
            tXfSettlementItemEntity.setAmountWithoutTax(new BigDecimal(Optional.ofNullable(invoiceItem.getDetailAmount()).orElse("0")));
            tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
            tXfSettlementItemEntity.setQuantity(new BigDecimal(Optional.ofNullable(invoiceItem.getNum()).orElse("0")));
            tXfSettlementItemEntity.setUnitPrice(new BigDecimal(Optional.ofNullable(invoiceItem.getUnitPrice()).orElse("0")));
            tXfSettlementItemEntity.setUnitPriceWithTax(new BigDecimal(Optional.ofNullable(invoiceItem.getUnitPrice()).orElse("0")));
            tXfSettlementItemEntity.setAmountWithTax(new BigDecimal(Optional.ofNullable(invoiceItem.getDetailAmount()).orElse("0"))
                    .add(new BigDecimal(Optional.ofNullable(invoiceItem.getTaxAmount()).orElse("0"))));
            tXfSettlementItemEntity.setCreateUser(0l);
            tXfSettlementItemEntity.setUpdateUser(0l);
            tXfSettlementItemEntity.setId(idSequence.nextId());
            tXfSettlementItemEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
            tXfSettlementItemEntity.setCreateTime(new Date());
            tXfSettlementItemEntity.setUpdateTime(new Date());
            tXfSettlementItemEntity.setItemCode(invoiceItem.getGoodsNum());
            tXfSettlementItemEntity.setThridId(invoiceItem.getId());
            tXfSettlementItemEntity.setItemName(invoiceItem.getGoodsName());
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
            tXfSettlementItemEntity.setGoodsNoVer("33.0");
            tXfSettlementItemEntity.setZeroTax(StringUtils.EMPTY);
            tXfSettlementItemEntity.setTaxPre(StringUtils.EMPTY);
            tXfSettlementItemEntity.setTaxPreCon(StringUtils.EMPTY);
            tXfSettlementItemEntity = deductService.checkItem(tXfSettlementItemEntity);
            tXfSettlementItemDao.insert(tXfSettlementItemEntity);
        });
    }

    /**
     * 判断底账匹配额度
     *
     * @param tXfSettlementEntity
     * @param request
     */
    public void checkSettlementMatchedInvoice(TXfSettlementEntity tXfSettlementEntity, InvoiceMatchedRequest request) {
        List<CompletableFuture<BigDecimal>> addCompletableFutureList = request.getInvoiceList().stream().map(invoice ->
                CompletableFuture.supplyAsync(() -> {
                    LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo())
                            .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode());
                    TDxRecordInvoiceEntity tDxInvoice = this.baseMapper.selectOne(wrapper);
                    if(tDxInvoice == null){
                        return BigDecimal.ZERO;
                    }
                    return tDxInvoice.getRemainingAmount();
                })
        ).collect(Collectors.toList());
        BigDecimal addAmount = addCompletableFutureList.parallelStream().map(f -> {
            try {
                return f.get();
            } catch (Exception e) {
            }
            return BigDecimal.ZERO;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (tXfSettlementEntity.getAmountWithoutTax().compareTo(addAmount) > 0) {
            throw new EnhanceRuntimeException("匹配额度少于结算需要的额度");
        }
    }
}
