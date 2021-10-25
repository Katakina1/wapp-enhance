package com.xforceplus.wapp.modules.invoice.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductInvoiceBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.mapstruct.InvoiceMapper;
import com.xforceplus.wapp.modules.settlement.dto.InvoiceMatchedRequest;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
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

//    public Response<InvoiceDto> detail(Long id) {
//        TXfInvoiceEntity tXfInvoiceEntity = getBaseMapper().selectById(id);
//        if (tXfInvoiceEntity != null) {
//            LambdaQueryWrapper<TXfInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
//            List<TXfInvoiceItemEntity> tXfInvoiceItemEntities = tXfInvoiceItemDao.selectList(queryWrapper);
//
//            InvoiceDto invoiceDto = invoiceMapper.entityToInvoiceDto(tXfInvoiceEntity);
//            List<InvoiceItemDto> invoiceItemDtos = invoiceMapper.entityToInvoiceItemDtoList(tXfInvoiceItemEntities);
//            invoiceDto.setDetails(invoiceItemDtos);
//            return Response.ok("查询成功", invoiceDto);
//        }
//
//        return Response.ok("查询成功", null);
//    }

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

    public PageResult<InvoiceDto> recommend(Long settlementId, InvoiceRecommendListRequest request) {
        log.info("userCode:{}", UserUtil.getUser().getUsercode());

        final TXfSettlementEntity byId = settlementService.getById(settlementId);
        if (byId == null) {
            throw new EnhanceRuntimeException("结算单:[" + settlementId + "]不存在");
        }

        LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(TDxRecordInvoiceEntity::getInvoiceDate, request.getInvoiceDateStart())
                .le(TDxRecordInvoiceEntity::getInvoiceDate, request.getInvoiceDateEnd());

        Page<TDxRecordInvoiceEntity> page = new Page<>(request.getPage(), request.getSize());

        final Page<TDxRecordInvoiceEntity> entityPage = super.page(page, wrapper);

        List<InvoiceDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())) {
            final List<InvoiceDto> collect = entityPage.getRecords().stream().map(this.invoiceMapper::entityToInvoiceDto).collect(Collectors.toList());
            dtos.addAll(collect);
        }
        return PageResult.of(dtos, entityPage.getTotal(), entityPage.getPages(), entityPage.getSize());
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
        checkSettlementMatchedInvoice(tXfSettlementEntity, request);
        //查询已匹配蓝票数据
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
            TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
            updateTDxInvoiceEntity.setId(tDxInvoice.getId());
            updateTDxInvoiceEntity.setRemainingAmount(tDxInvoice.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
            this.baseMapper.updateById(updateTDxInvoiceEntity);
        });
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
            //匹配蓝票
            TXfBillDeductInvoiceEntity newTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            newTXfBillDeductInvoiceEntity.setInvoiceNo(tDxInvoice.getInvoiceNo());
            newTXfBillDeductInvoiceEntity.setInvoiceCode(tDxInvoice.getInvoiceCode());
            newTXfBillDeductInvoiceEntity.setThridId(settlementId);
            newTXfBillDeductInvoiceEntity.setBusinessNo(tXfSettlementEntity.getSettlementNo());
            newTXfBillDeductInvoiceEntity.setBusinessType(TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());
            newTXfBillDeductInvoiceEntity.setStatus(0);
            newTXfBillDeductInvoiceEntity.setUseAmount(useAmount);
            tXfBillDeductInvoiceDao.insert(newTXfBillDeductInvoiceEntity);
            //使用底账蓝票额度
            TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
            updateTDxInvoiceEntity.setId(tDxInvoice.getId());
            updateTDxInvoiceEntity.setRemainingAmount(remainingAmount);
            this.baseMapper.updateById(updateTDxInvoiceEntity);
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
