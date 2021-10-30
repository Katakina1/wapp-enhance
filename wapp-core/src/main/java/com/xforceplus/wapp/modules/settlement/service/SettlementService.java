package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.recordinvoice.mapstruct.InvoiceDtoMapper;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类描述：
 *
 * @ClassName SettlementService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 13:59
 */
@Service
@Slf4j
public class SettlementService {
    @Autowired
    private TXfSettlementExtDao settlementDao;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private InvoiceDtoMapper invoiceMapper;


    public List<TXfSettlementEntity> querySettlementByStatus(Long id, Integer status, Integer limit ) {
        return settlementDao.querySettlementByStatus(status, id, limit);
    }



    public TXfSettlementEntity getById(Long id){
        return settlementDao.selectById(id);
    }

    public PageResult<InvoiceDto> recommend(Long settlementId, InvoiceRecommendListRequest request) {
        log.info("userCode:{}", UserUtil.getUser().getUsercode());

        final TXfSettlementEntity byId = getById(settlementId);
        if (byId == null) {
            throw new EnhanceRuntimeException("结算单:[" + settlementId + "]不存在");
        }

        final BigDecimal taxRate = byId.getTaxRate();
        final String taxRateStr = taxRate.compareTo(BigDecimal.ONE) < 0 ? taxRate.movePointRight(2).toPlainString() : taxRate.toPlainString();
        final String sellerNo = byId.getSellerNo();
        final String sellerTaxNo = byId.getSellerTaxNo();
        final String purchaserNo = byId.getPurchaserNo();
        final String purchaserTaxNo = byId.getPurchaserTaxNo();

        LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity::getXfTaxNo,sellerTaxNo)
                .eq(TDxRecordInvoiceEntity::getGfTaxNo,purchaserTaxNo)
                .eq(TDxRecordInvoiceEntity::getTaxRate,taxRateStr)
                .ge(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateStart())
                .le(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateEnd())
        ;

        Page<TDxRecordInvoiceEntity> page=new Page<>(request.getPage(),request.getSize());

        final Page<TDxRecordInvoiceEntity> entityPage = tDxRecordInvoiceDao.selectPage(page, wrapper);

        List<InvoiceDto> dtos=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())){
            final List<InvoiceDto> collect = entityPage.getRecords().stream().map(this.invoiceMapper::toDto).collect(Collectors.toList());
            dtos.addAll(collect);
        }
        return PageResult.of(dtos,entityPage.getTotal(),entityPage.getPages(),entityPage.getSize());
    }


}
