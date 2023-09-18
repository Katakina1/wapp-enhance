package com.xforceplus.wapp.modules.agreement.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xforceplus.apollo.core.domain.settlementstatus.SettlementStatus;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.dto.*;
import com.xforceplus.wapp.modules.deduct.service.AgreementBillService;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxTaxCurrentDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.*;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class AgreementService {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TDxTaxCurrentDao tDxTaxCurrentDao;

    public InvoiceRecommendDetailListResponse recommend(InvoiceRecommendDetailListRequest request) {
        //0.处理入参批次号和期望返回条数
        if (request.getLastBatchNum() == null){
            request.setLastBatchNum(0);
        }
        if (request.getExpectNum() == null){
            request.setExpectNum(20);
        }
        //1.计算推荐发票税率
        BigDecimal taxRate = AgreementBillService.switchToTargetTaxRate(request.getTaxRate(),false,request.getTaxCode());

        String taxRateStr = taxRate.toPlainString();
        //2.获取购销对信息
        String sellerNo = request.getSellerNo();
        TAcOrgEntity purchaserOrg = companyService.getByOrgCode(request.getPurchaserNo(), false);
        String purchaserTaxNo = Optional.ofNullable(purchaserOrg).map(TAcOrgEntity::getTaxNo)
                .orElseThrow(()->new EnhanceRuntimeException("购方公司:["+request.getPurchaserNo()+"]不存在"));
        TAcOrgEntity sellerOg = companyService.getByOrgCode(sellerNo, true);
        String sellerTaxNo = sellerOg.getTaxNo();
        //根据购方税号获取当前征期
        String currentTaxPeriod = null;
        QueryWrapper<TDxTaxCurrentEntity> taxCurrentQueryWrapper = new QueryWrapper<TDxTaxCurrentEntity>();
        taxCurrentQueryWrapper.eq(TDxTaxCurrentEntity.TAXNO,purchaserTaxNo);
        TDxTaxCurrentEntity taxCurrentEntity = tDxTaxCurrentDao.selectOne(taxCurrentQueryWrapper);
        if (taxCurrentEntity != null && StringUtils.isNotBlank(taxCurrentEntity.getCurrentTaxPeriod())){
          currentTaxPeriod = taxCurrentEntity.getCurrentTaxPeriod();
        }
        //3.组装查询条件
        LambdaQueryWrapper<TDxRecordInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(TDxRecordInvoiceEntity::getXfTaxNo,sellerTaxNo)
                .eq(TDxRecordInvoiceEntity::getGfTaxNo,purchaserTaxNo)
                .eq(TDxRecordInvoiceEntity::getTaxRate,taxRateStr)
                .ge(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateStart())
                .le(TDxRecordInvoiceEntity::getInvoiceDate,request.getInvoiceDateEnd())
                .eq(TDxRecordInvoiceEntity::getRzhYesorno,1).
                and(x->{
                    x.gt(TDxRecordInvoiceEntity::getRemainingAmount,1)
                    .or(s->s.isNull(TDxRecordInvoiceEntity::getRemainingAmount));
                });

        if (StringUtil.isNotBlank(currentTaxPeriod)){
          //蓝票当前税款所属期 需小于 当前购方税号当前税款所属期
          wrapper.lt(TDxRecordInvoiceEntity::getRzhBelongDate, currentTaxPeriod);
        }

        wrapper.orderByDesc(TDxRecordInvoiceEntity::getInvoiceDate);

        //4.按预期值获取推荐明细
        List<MatchedInvoiceDetailBean> detailBeanList = new ArrayList<>();
        do {
            Page<TDxRecordInvoiceEntity> page=new Page<>(request.getLastBatchNum()+1,1);
            Page<TDxRecordInvoiceEntity> entityPage = tDxRecordInvoiceDao.selectPage(page, wrapper);
            if (CollectionUtils.isEmpty(entityPage.getRecords())){
                log.info("已无可推荐发票记录");
                break;
            }
            TDxRecordInvoiceEntity invoiceEntity = entityPage.getRecords().get(0);
            request.setLastBatchNum(request.getLastBatchNum()+1);
            //获取发票明细
            List<MatchedInvoiceDetailBean> detailList = deductBlueInvoiceService.gainInvoiceRecommendDetailList(invoiceEntity);
            if (CollectionUtils.isNotEmpty(detailList)){
                detailBeanList.addAll(detailList);
            }
        }while (detailBeanList.size()<request.getExpectNum());

        //5.返回结果
        InvoiceRecommendDetailListResponse recommendResponse = new InvoiceRecommendDetailListResponse();
        recommendResponse.setLastBatchNum(request.getLastBatchNum());
        recommendResponse.setExpectNum(request.getExpectNum());
        recommendResponse.setActualNum(detailBeanList.size());
        recommendResponse.setDetailList(detailBeanList);
        return recommendResponse;
    }

    /**
     * 结算单是否待确认状态
     * @param settlementEntity 结算单
     */
    public boolean checkSettlementWaitConfirm(TXfSettlementEntity settlementEntity) {
        return TXfSettlementStatusEnum.isWaitConfirm(settlementEntity.getSettlementStatus());
    }

    /**
     * 结算单是否处于待开票状态，且预制发票未申请红字信息表
     * @param settlementEntity 结算单信息
     */
    public boolean checkSettlementWaitUploadInvoiceAndNoApplyRedNo(TXfSettlementEntity settlementEntity) {
        if (TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(settlementEntity.getSettlementStatus())) {
            // 待开票
            LambdaQueryWrapper<TXfPreInvoiceEntity> queryWrapper = Wrappers.lambdaQuery(TXfPreInvoiceEntity.class)
                    .eq(TXfPreInvoiceEntity::getSettlementId, settlementEntity.getId())
                    .notIn(TXfPreInvoiceEntity::getPreInvoiceStatus, Lists.newArrayList(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode(), TXfPreInvoiceStatusEnum.DESTROY.getCode()));
            List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
                return preInvoiceEntityList.stream().allMatch(entity -> {
                    // 未申请红字信息表
                    boolean noApply = TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode().equals(entity.getPreInvoiceStatus());
                    // 无需申请红字信息表
                    boolean noNeedApply = TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(entity.getPreInvoiceStatus()) && BigDecimal.ZERO.compareTo(entity.getTaxRate()) == 0;
                    return noApply || noNeedApply;
                });
            }
        }
        return false;
    }
}
