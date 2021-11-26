package com.xforceplus.wapp.modules.settlement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendListRequest;
import com.xforceplus.wapp.modules.deduct.dto.InvoiceRecommendResponse;
import com.xforceplus.wapp.modules.deduct.mapstruct.InvoiceRecommendMapper;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.recordinvoice.mapstruct.InvoiceDtoMapper;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.daoExt.RecordInvoiceDetailExtDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    private RecordInvoiceDetailExtDao recordInvoiceDetailExtDao;
    @Autowired
    private TXfSettlementExtDao settlementDao;

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    @Autowired
    private InvoiceDtoMapper invoiceMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private InvoiceRecommendMapper invoiceRecommendMapper;

    @Autowired
    private TXfSettlementDao tXfSettlementDao;


    public List<TXfSettlementEntity> querySettlementByStatus(Long id, Integer status, Integer limit ) {
        return settlementDao.querySettlementByStatus(status, id, limit);
    }

    public Map<String,Integer> getSettlementStatus(List<String> settlementNos){
        final LambdaQueryWrapper<TXfSettlementEntity> wrapper = Wrappers.lambdaQuery(TXfSettlementEntity.class).in(TXfSettlementEntity::getSettlementNo, settlementNos).select(TXfSettlementEntity::getSettlementStatus, TXfSettlementEntity::getSettlementNo);
        final List<TXfSettlementEntity> entities = this.tXfSettlementDao.selectList(wrapper);
        Map<String,Integer> result = new HashMap<>();
        Optional.ofNullable(entities).ifPresent(x->{
            for (TXfSettlementEntity tXfSettlementEntity : x) {
                result.put(tXfSettlementEntity.getSettlementNo(),tXfSettlementEntity.getSettlementStatus());
            }
        });
        return result;
    }



    public TXfSettlementEntity getById(Long id){
        return settlementDao.selectById(id);
    }

    public PageResult<InvoiceRecommendResponse> recommend(InvoiceRecommendListRequest request) {
        log.info("userCode:{}", UserUtil.getUser().getUsercode());

//        final TXfSettlementEntity byId = getById(settlementId);
//        if (byId == null) {
//            throw new EnhanceRuntimeException("结算单:[" + settlementId + "]不存在");
//        }

        BigDecimal taxRate = request.getTaxRate();
        taxRate=taxRate.compareTo(BigDecimal.ONE) < 0 ? taxRate.movePointRight(2) : taxRate;
        taxRate=AgreementBillService.convertTaxRate(TXfDeductionBusinessTypeEnum.EPD_BILL,taxRate);
        final String taxRateStr = taxRate.toPlainString();
        final String sellerNo = request.getSellerNo();
        final TAcOrgEntity purchaserOrg = companyService.getByOrgCode(request.getPurchaserNo(), false);
        final String purchaserTaxNo = Optional.ofNullable(purchaserOrg).map(TAcOrgEntity::getTaxNo)
                .orElseThrow(()->new EnhanceRuntimeException("购方公司:["+request.getPurchaserNo()+"]不存在"));

        final TAcOrgEntity sellerOg = companyService.getByOrgCode(sellerNo, true);
        final String sellerTaxNo = sellerOg.getTaxNo();

        LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity::getXfTaxNo,sellerTaxNo)
                .eq(TDxRecordInvoiceEntity::getGfTaxNo,purchaserTaxNo)
                .eq(TDxRecordInvoiceEntity::getTaxRate,taxRateStr)
                .ge(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateStart())
                .le(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateEnd())
                .eq(TDxRecordInvoiceEntity::getRzhYesorno,1).
        and(x->{
            x.gt(TDxRecordInvoiceEntity::getRemainingAmount,0).or(
                    s->s.isNull(TDxRecordInvoiceEntity::getRemainingAmount
            ));
        });
        wrapper.orderByAsc(TDxRecordInvoiceEntity::getInvoiceDate);

        Page<TDxRecordInvoiceEntity> page=new Page<>(request.getPage(),request.getSize());

        final Page<TDxRecordInvoiceEntity> entityPage = tDxRecordInvoiceDao.selectPage(page, wrapper);

        List<InvoiceRecommendResponse> dtos=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityPage.getRecords())){
            final List<InvoiceRecommendResponse> collect = new ArrayList<>();
            for (TDxRecordInvoiceEntity entity : entityPage.getRecords()) {
                InvoiceRecommendResponse x = invoiceRecommendMapper.toDto(entity);
                final List<TDxRecordInvoiceDetailEntity> details = this.recordInvoiceDetailExtDao.selectTopGoodsName(5, x.getInvoiceCode() + x.getInvoiceNo());
                final String goodsName = details.stream().map(TDxRecordInvoiceDetailEntity::getGoodsName).collect(Collectors.joining(","));
                x.setGoodsName(goodsName);
                collect.add(x);
            }
            dtos.addAll(collect);
        }
        return PageResult.of(dtos,entityPage.getTotal(),entityPage.getPages(),entityPage.getSize());
    }


}
