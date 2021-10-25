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
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDetailsDao;
import com.xforceplus.wapp.repository.entity.TDxInvoiceDetailsEntity;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
@Slf4j
public class InvoiceServiceImpl extends ServiceImpl<TDxInvoiceDao, TDxInvoiceEntity> {

    @Autowired
    TDxInvoiceDetailsDao tDxInvoiceDetailsDao;
    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    private SettlementService settlementService;

    public Response<InvoiceDto> detail(Long id) {
        TDxInvoiceEntity tDxInvoiceEntity = getBaseMapper().selectById(id);
        if (tDxInvoiceEntity != null) {
            LambdaQueryWrapper<TDxInvoiceDetailsEntity> queryWrapper = new LambdaQueryWrapper<>();
            List<TDxInvoiceDetailsEntity> tXfInvoiceItemEntities = tDxInvoiceDetailsDao.selectList(queryWrapper);

            InvoiceDto invoiceDto = invoiceMapper.entityToInvoiceDto(tDxInvoiceEntity);
            List<InvoiceItemDto> invoiceItemDtos = invoiceMapper.entityToInvoiceItemDtoList(tXfInvoiceItemEntities);
            invoiceDto.setDetails(invoiceItemDtos);
            return Response.ok("查询成功", invoiceDto);
        }

        return Response.ok("查询成功", null);
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxInvoiceEntity> entityList) {
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
    public boolean withdrawRemainingAmountById(List<TDxInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "update t_xf_invoice set remaining_amount = remaining_amount + #{remainingAmount} where id = #{id}";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TDxInvoiceEntity> param = new MapperMethod.ParamMap<>();
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

//        final BigDecimal taxRate = byId.getTaxRate();
//        final String taxRateStr = taxRate.compareTo(BigDecimal.ONE) > 0 ? taxRate.movePointLeft(2).toPlainString() : taxRate.toPlainString();
//        final String sellerNo = byId.getSellerNo();
//        final String sellerTaxNo = byId.getSellerTaxNo();
//        final String purchaserNo = byId.getPurchaserNo();
//        final String purchaserTaxNo = byId.getPurchaserTaxNo();

        LambdaQueryWrapper<TDxInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.ge(TDxInvoiceEntity::getMakeDate,request.getInvoiceDateStart())
                .le(TDxInvoiceEntity::getMakeDate,request.getInvoiceDateEnd())
        ;

        Page<TDxInvoiceEntity> page=new Page<>(request.getPage(),request.getSize());

        final Page<TDxInvoiceEntity> entityPage = super.page(page, wrapper);

        List<InvoiceDto> dtos=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())){
            final List<InvoiceDto> collect = entityPage.getRecords().stream().map(this.invoiceMapper::entityToInvoiceDto).collect(Collectors.toList());
            dtos.addAll(collect);
        }
        return PageResult.of(dtos,entityPage.getTotal(),entityPage.getPages(),entityPage.getSize());
    }
}
