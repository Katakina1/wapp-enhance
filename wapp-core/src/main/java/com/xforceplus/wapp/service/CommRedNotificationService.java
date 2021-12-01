package com.xforceplus.wapp.service;

import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预制发票与红字信息申请作废 相关操作
 */
@Service
@Slf4j
public class CommRedNotificationService {

    @Autowired
    private RedNotificationOuterService redNotificationOuterService;

    /**
     * 申请红字信息
     * 调用沃尔玛接口 申请
     *
     * @param applyProInvoiceRedNotificationDTO 预制发票信息
     * @return
     */
    public void applyAddRedNotification(PreInvoiceDTO applyProInvoiceRedNotificationDTO) {
        RedNotificationInfo redNotificationInfo = convertApplyPreInvoiceRedNotificationDTOToRedNotificationInfo(applyProInvoiceRedNotificationDTO);
        // 申请调用沃尔玛接口申请
        AddRedNotificationRequest request = new AddRedNotificationRequest();
        request.setAutoApplyFlag(1);
        request.setRedNotificationInfoList(Collections.singletonList(redNotificationInfo));
        redNotificationOuterService.add(request);
    }

    /**
     * 申请调用沃尔玛接口作废
     * 调用沃尔玛接口 审核中
     *
     * @param preInvoiceId 预制发票id
     * @return
     */
    public void applyDestroyRedNotification(Long preInvoiceId,String remark) {
        redNotificationOuterService.updateAppliedToWaitAppproveByPid(preInvoiceId,remark);
    }

    /**
     * 直接调用沃尔玛接口撤销（调用税件）
     *
     * @param preInvoiceId
     */
    @Async
    public void confirmDestroyRedNotification(Long preInvoiceId) {
        try {
            Response response = redNotificationOuterService.rollback(preInvoiceId);
            if (response.getCode() == 0) {
                log.error("撤销红字信息失败：" + response.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 直接删除申请红字信息记录
     *
     * @param preInvoiceId
     */
    public void deleteRedNotification(Long preInvoiceId,String remark) {
        redNotificationOuterService.deleteRednotification(Collections.singletonList(preInvoiceId),remark);
    }

    private RedNotificationInfo convertApplyPreInvoiceRedNotificationDTOToRedNotificationInfo(PreInvoiceDTO applyProInvoiceRedNotificationDTO) {

        TXfPreInvoiceEntity preInvoice = applyProInvoiceRedNotificationDTO.getTXfPreInvoiceEntity();
        List<TXfPreInvoiceItemEntity> preInvoiceItemList = applyProInvoiceRedNotificationDTO.getTXfPreInvoiceItemEntityList();

        RedNotificationMain redNotificationMain = new RedNotificationMain();
        //数据转换
        redNotificationMain.setPid(String.valueOf(preInvoice.getId()));
        redNotificationMain.setId(preInvoice.getId());
        redNotificationMain.setRemark(preInvoice.getRemark());
        redNotificationMain.setUserRole(2);
        /// 发票类型转换
        if (InvoiceTypeEnum.SPECIAL_INVOICE.getValue().equals(preInvoice.getInvoiceType())) {
            redNotificationMain.setInvoiceType(InvoiceTypeEnum.SPECIAL_INVOICE.getXfValue());
        } else if (InvoiceTypeEnum.E_SPECIAL_INVOICE.getValue().equals(preInvoice.getInvoiceType())) {
            redNotificationMain.setInvoiceType(InvoiceTypeEnum.E_SPECIAL_INVOICE.getXfValue());
        } else {
            throw new RuntimeException("不支持的发票票种申请红字信息");
        }
        redNotificationMain.setOriginInvoiceType(preInvoice.getOriginInvoiceType());
        redNotificationMain.setOriginalInvoiceNo(preInvoice.getOriginInvoiceNo());
        redNotificationMain.setOriginalInvoiceCode(preInvoice.getOriginInvoiceCode());
        redNotificationMain.setOriginalInvoiceDate(preInvoice.getOriginPaperDrewDate());
        redNotificationMain.setPurchaserTaxNo(preInvoice.getPurchaserTaxNo());
        redNotificationMain.setPurchaserName(preInvoice.getPurchaserName());
        redNotificationMain.setSellerTaxNo(preInvoice.getSellerTaxNo());
        redNotificationMain.setSellerName(preInvoice.getSellerName());
        redNotificationMain.setAmountWithTax(preInvoice.getAmountWithTax());
        redNotificationMain.setAmountWithoutTax(preInvoice.getAmountWithoutTax());
        redNotificationMain.setTaxAmount(preInvoice.getTaxAmount());
        redNotificationMain.setCompanyCode(preInvoice.getSellerNo());
        redNotificationMain.setSpecialInvoiceFlag(0);
        redNotificationMain.setBillNo(preInvoice.getSettlementNo());
        redNotificationMain.setInvoiceOrigin(preInvoice.getSettlementType());
        redNotificationMain.setApplyType(0);
        //redNotificationMain.setPaymentTime();
        //redNotificationMain.setApplyReason();
        //redNotificationMain.setCustomerNo();
        List<RedNotificationItem> redNotificationItemList = preInvoiceItemList.parallelStream().map(preInvoiceItem -> {
            RedNotificationItem redNotificationItem = new RedNotificationItem();
            //TODO
            //redNotificationItem.setDetailNo();
            //redNotificationItem.setTaxConvertCode();
            redNotificationItem.setGoodsName(preInvoiceItem.getCargoName());
            redNotificationItem.setGoodsTaxNo(preInvoiceItem.getGoodsTaxNo());
            redNotificationItem.setTaxPre(Integer.valueOf(preInvoiceItem.getTaxPre()));
            redNotificationItem.setTaxPreCon(preInvoiceItem.getTaxPreCon());
            redNotificationItem.setZeroTax(StringUtils.isEmpty(preInvoiceItem.getZeroTax()) ? null : Integer.valueOf(preInvoiceItem.getZeroTax()));
            redNotificationItem.setModel(preInvoiceItem.getItemSpec());
            redNotificationItem.setUnit(preInvoiceItem.getQuantityUnit());
            redNotificationItem.setNum(preInvoiceItem.getQuantity());
            redNotificationItem.setTaxRate(preInvoiceItem.getTaxRate());
            redNotificationItem.setUnitPrice(preInvoiceItem.getUnitPrice());
            redNotificationItem.setAmountWithoutTax(preInvoiceItem.getAmountWithoutTax());
            redNotificationItem.setAmountWithTax(preInvoiceItem.getAmountWithTax());
            redNotificationItem.setTaxAmount(preInvoiceItem.getTaxAmount());
            return redNotificationItem;
        }).collect(Collectors.toList());

        RedNotificationInfo redNotificationInfo = new RedNotificationInfo();
        redNotificationInfo.setRednotificationMain(redNotificationMain);
        redNotificationInfo.setRedNotificationItemList(redNotificationItemList);
        return redNotificationInfo;
    }

}
