package com.xforceplus.wapp.modules.invoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.modules.invoice.mapstruct.InvoiceMapper;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.settlement.service.SettlementService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
@Slf4j
public class InvoiceServiceImpl extends ServiceImpl<TXfInvoiceDao, TXfInvoiceEntity> {

    @Autowired
    TXfInvoiceItemDao tXfInvoiceItemDao;
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    private SettlementService settlementService;

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
    public boolean withdrawRemainingAmountById(Collection<TXfInvoiceEntity> entityList) {
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
    public boolean withdrawRemainingAmountById(List<TXfInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "update t_xf_invoice set remaining_amount = remaining_amount + #{remainingAmount} where id = #{id}";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TXfInvoiceEntity> param = new MapperMethod.ParamMap<>();
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

        final BigDecimal taxRate = byId.getTaxRate();
        final String taxRateStr = taxRate.compareTo(BigDecimal.ONE) > 0 ? taxRate.movePointLeft(2).toPlainString() : taxRate.toPlainString();
        final String sellerNo = byId.getSellerNo();
        final String sellerTaxNo = byId.getSellerTaxNo();
        final String purchaserNo = byId.getPurchaserNo();
        final String purchaserTaxNo = byId.getPurchaserTaxNo();

        LambdaQueryWrapper<TXfInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(TXfInvoiceEntity::getSellerTaxNo,sellerTaxNo)
                .eq(TXfInvoiceEntity::getPurchaserTaxNo,purchaserTaxNo)
                .eq(TXfInvoiceEntity::getTaxRate,taxRateStr)
                .ge(TXfInvoiceEntity::getPaperDrewDate,request.getInvoiceDateStart())
                .le(TXfInvoiceEntity::getPaperDrewDate,request.getInvoiceDateEnd())
        ;

        Page<TXfInvoiceEntity> page=new Page<>(request.getPage(),request.getSize());

        final Page<TXfInvoiceEntity> entityPage = super.page(page, wrapper);

        List<InvoiceDto> dtos=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())){
            final List<InvoiceDto> collect = entityPage.getRecords().stream().map(this.invoiceMapper::entityToInvoiceDto).collect(Collectors.toList());
            dtos.addAll(collect);
        }
        return PageResult.of(dtos,entityPage.getTotal(),entityPage.getPages(),entityPage.getSize());
    }
}
