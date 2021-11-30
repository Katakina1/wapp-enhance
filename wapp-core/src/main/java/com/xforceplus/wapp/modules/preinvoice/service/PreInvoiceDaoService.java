package com.xforceplus.wapp.modules.preinvoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.common.dto.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.modules.preinvoice.dto.ApplyOperationRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.PreInvoiceItem;
import com.xforceplus.wapp.modules.preinvoice.dto.SplitAgainRequest;
import com.xforceplus.wapp.modules.preinvoice.mapstruct.PreInvoiceMapper;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.service.CommAgreementService;
import com.xforceplus.wapp.service.CommEpdService;
import com.xforceplus.wapp.service.CommSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreInvoiceDaoService extends ServiceImpl<TXfPreInvoiceDao, TXfPreInvoiceEntity> {
    @Autowired
    CommAgreementService commAgreementService;
    @Autowired
    CommEpdService commEpdService;
    @Autowired
    CommSettlementService commSettlementService;
    @Autowired
    RedNotificationOuterService redNotificationOuterService;
    @Autowired
    PreInvoiceItemDaoService preInvoiceItemDaoService;
    @Autowired    PreInvoiceMapper  preInvoiceMapper;
    @Autowired
    TXfSettlementDao tXfSettlementDao;


    public R<List<PreInvoiceItem>> applyOperation(ApplyOperationRequest request) {
        int applyOperationType = request.getApplyOperationType();

        // 操作类型 1 修改税编 2 修改限额 3不做任何修改 4 修改商品明细
        switch (applyOperationType){
            case 1:
                //获取重新作废明细重新拆票，重新申请红字信息
                return retryApplyRednotification(request);
            case 2:
            case 3:
                //限额 和 不做任何修改
                try {
                    commSettlementService.againSplitSettlementPreInvoice(Long.parseLong(request.getSettlementId()));
                } catch (EnhanceRuntimeException e) {
                    log.error("重新拆票错误,失败原因:{}",e.getMessage());
                    return R.fail(e.getMessage());
                }
                return R.ok(null,"重新拆票成功");
            case 4:
                //撤销结算单
                return rollBackSettlement(request);
            default:
                return R.fail("操作类型不正确, 1 修改税编 2 修改限额 3 修改商品明细");
        }
    }

    /**
     * 判断是否有已存在的红票
     * @param request
     * @return
     */
//    public Response<ExistRedInvoiceResult> existRedInvoice(ApplyOperationRequest request){
//        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(request.getSettlementId());
//        if (tXfSettlementEntity == null) {
//            Response.failed("结算单不存在");
//        }
//
//        ExistRedInvoiceResult existRedInvoiceResult = new ExistRedInvoiceResult();
//        existRedInvoiceResult.setExistRedInvoice(false);
//        String message = "点击确定，本结算单将被撤销，已勾选协议单，已关联蓝票及明细会被释放。你可以重新勾选协议单，再次匹配需要的发票及明细，请确认是否处理？";
//
//        if (tXfSettlementEntity.getSettlementStatus()== TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getValue()
//          || tXfSettlementEntity.getSettlementStatus()== TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getValue()
//        ){
//            existRedInvoiceResult.setExistRedInvoice(true);
//            message ="本结算单已经上传了红字发票，请全部删除";
//        }
//
//        return Response.ok(message ,existRedInvoiceResult);
//
//    }



    // 结算单类型:1索赔单,2:协议单；3:EPD单
    private R<List<PreInvoiceItem>> rollBackSettlement(ApplyOperationRequest request) {
        try {
            switch (request.getSettlementType()) {
                case 2:
                    commAgreementService.destroyAgreementSettlement(Long.parseLong(request.getSettlementId()));
                case 3:
                    commEpdService.destroyEpdSettlement(Long.parseLong(request.getSettlementId()));
            }
        }catch (Exception e){
            log.error("释放结算单异常",e);
            return R.fail(e.getMessage());
        }

        return  R.ok(Lists.newArrayList());
    }

    private R<List<PreInvoiceItem>> retryApplyRednotification(ApplyOperationRequest request) {
        // 操作类型 1 修改税编 2 修改限额 3 修改商品明细
        LambdaQueryWrapper<TXfPreInvoiceEntity> tXfPreInvoiceEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tXfPreInvoiceEntityLambdaQueryWrapper.eq(TXfPreInvoiceEntity::getSettlementId,request.getSettlementId())
                .eq(TXfPreInvoiceEntity::getPreInvoiceStatus, 5);
        //获取作废的
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = getBaseMapper().selectList(tXfPreInvoiceEntityLambdaQueryWrapper);
        List<Long> abandonList = tXfPreInvoiceEntities.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        log.info("获取作废预制发票数量:{},结算单号:{}", abandonList.size(),request.getSettlementNo());
        //获取待审核的
        List<Long> waitApplyPreIds = redNotificationOuterService.getWaitApplyPreIds(request.getSettlementNo());
        log.info("获取待申请的预制发票数量:{},结算单号:{}", waitApplyPreIds.size(),request.getSettlementNo());

        //删除待审核红字信息
        if (!CollectionUtils.isEmpty(waitApplyPreIds)){
            redNotificationOuterService.deleteRednotification(waitApplyPreIds,null);
        }

        // 获取明细重新拆票
        abandonList.addAll(waitApplyPreIds);
        //
        if (CollectionUtils.isEmpty(abandonList)){
            return R.fail("未查询到待重新拆票的明细");
        }

        LambdaQueryWrapper<TXfPreInvoiceItemEntity> itemEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemEntityLambdaQueryWrapper.in(TXfPreInvoiceItemEntity::getPreInvoiceId,abandonList);
        List<TXfPreInvoiceItemEntity> invoiceItemEntities = preInvoiceItemDaoService.getBaseMapper().selectList(itemEntityLambdaQueryWrapper);

        List<PreInvoiceItem> preInvoiceItems = preInvoiceMapper.entityToPreInvoiceItemDtoList(invoiceItemEntities);
        return R.ok(preInvoiceItems);
    }


    public Response<String> splitAgain(SplitAgainRequest request) {
        //如果不做任何修改
        if (request.getApplyOperationType() != null && request.getApplyOperationType() == 1){
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = preInvoiceMapper.itemToPreInvoiceEntityList(request.getDetails());
            try {
                switch (request.getSettlementType()) {
                    case 2:
                        commAgreementService.againSplitPreInvoice(request.getSettlementId(),tXfPreInvoiceItemEntities);
                        break;
                    case 3:
                        commEpdService.againSplitPreInvoice(request.getSettlementId(),tXfPreInvoiceItemEntities);
                        break;
                }
            }catch (Exception e){
                log.error("重新拆票异常",e);
                return Response.failed(e.getMessage());
            }
        }else {



        }



        return Response.ok("重新拆票成功");
    }
}
