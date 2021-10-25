package com.xforceplus.wapp.modules.invoice.service;

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
        if (!CollectionUtils.isEmpty(request.getRemoved())) {
            request.getRemoved().forEach(invoice -> {
                //查询底账数据
                LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo())
                        .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode());
                TDxRecordInvoiceEntity tDxInvoice = this.baseMapper.selectOne(wrapper);
                //查询已匹配蓝票数据
                LambdaQueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new LambdaQueryWrapper<>();
                tXfBillDeductInvoiceWrapper
                        .eq(TXfBillDeductInvoiceEntity::getBusinessNo, tXfSettlementEntity.getSettlementNo())
                        .eq(TXfBillDeductInvoiceEntity::getBusinessType, TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType())
                        .eq(TXfBillDeductInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo())
                        .eq(TXfBillDeductInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode());
                TXfBillDeductInvoiceEntity tXfBillDeductInvoice = tXfBillDeductInvoiceDao.selectOne(tXfBillDeductInvoiceWrapper);
                //释放匹配蓝票
                TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
                updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoice.getId());
                updateTXfBillDeductInvoiceEntity.setStatus(1);
                tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
                //还原底账蓝票额度
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoice.getId());
                updateTDxInvoiceEntity.setRemainingAmount(tDxInvoice.getRemainingAmount().add(updateTXfBillDeductInvoiceEntity.getUseAmount()));
                this.baseMapper.updateById(updateTDxInvoiceEntity);
            });
        }
        if (!CollectionUtils.isEmpty(request.getAdded())) {
            request.getAdded().forEach(invoice -> {
                //底账数据
                LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TDxRecordInvoiceEntity::getInvoiceNo, invoice.getInvoiceNo())
                        .eq(TDxRecordInvoiceEntity::getInvoiceCode, invoice.getInvoiceCode());
                TDxRecordInvoiceEntity tDxInvoice = this.baseMapper.selectOne(wrapper);
                //最后一张可能需要特殊处理
                BigDecimal amountWithoutTax = tXfSettlementEntity.getAmountWithoutTax();
                //查询已匹配蓝票数据金额
                QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper<>();
                tXfBillDeductInvoiceWrapper
                        .select("SUM(use_amount) as totalUseAmount")
                        .eq(TXfBillDeductInvoiceEntity.BUSINESS_NO, tXfSettlementEntity.getSettlementNo())
                        .eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());
                Map<String,Object> totalUseAmountMap = tXfBillDeductInvoiceDao.selectMaps(tXfBillDeductInvoiceWrapper).stream().findAny().orElse(new HashMap<>());
                BigDecimal totalUseAmount = new BigDecimal(totalUseAmountMap.get("totalUseAmount").toString());
                //TODO
                //匹配蓝票
                TXfBillDeductInvoiceEntity newTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
                newTXfBillDeductInvoiceEntity.setInvoiceNo(tDxInvoice.getInvoiceNo());
                newTXfBillDeductInvoiceEntity.setInvoiceCode(tDxInvoice.getInvoiceCode());
                newTXfBillDeductInvoiceEntity.setThridId(settlementId);
                newTXfBillDeductInvoiceEntity.setBusinessNo(tXfSettlementEntity.getSettlementNo());
                newTXfBillDeductInvoiceEntity.setBusinessType(TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());
                newTXfBillDeductInvoiceEntity.setStatus(0);
                newTXfBillDeductInvoiceEntity.setUseAmount(tDxInvoice.getRemainingAmount());
                tXfBillDeductInvoiceDao.insert(newTXfBillDeductInvoiceEntity);
                //使用底账蓝票额度
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoice.getId());
                updateTDxInvoiceEntity.setRemainingAmount(BigDecimal.ZERO);
                this.baseMapper.updateById(updateTDxInvoiceEntity);
            });
        }

    }
}
