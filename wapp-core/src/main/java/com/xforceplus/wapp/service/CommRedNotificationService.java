package com.xforceplus.wapp.service;

import com.xforceplus.wapp.dto.ApplyProInvoiceRedNotificationDTO;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationInfo;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationItem;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 预制发票与红字信息申请撤销 相关操作
 */
@Service
public class CommRedNotificationService {

    /**
     * 申请红字信息
     * 调用沃尔玛接口 申请
     * @param applyProInvoiceRedNotificationDTO 预制发票信息
     * @return
     */
    public void applyAddRedNotification(ApplyProInvoiceRedNotificationDTO applyProInvoiceRedNotificationDTO) {
        RedNotificationInfo redNotificationInfo = convertApplyPreInvoiceRedNotificationDTOToRedNotificationInfo(applyProInvoiceRedNotificationDTO);
        //TODO 申请调用沃尔玛接口申请

    }

    /**
     * 撤销红字信息
     * 调用沃尔玛接口 审核中
     *
     * @param proInvoiceId 预制发票id
     * @return
     */
    public void applyCancelRedNotification(Long proInvoiceId) {
        //TODO 申请调用沃尔玛接口撤销

    }

    /**
     * 直接调用撤销红字信息
     * @param proInvoiceId
     */
    public void confirmCancelRedNotification(Long proInvoiceId) {
        //TODO 直接调用沃尔玛接口撤销

    }


    private RedNotificationInfo convertApplyPreInvoiceRedNotificationDTOToRedNotificationInfo(ApplyProInvoiceRedNotificationDTO applyProInvoiceRedNotificationDTO) {

        TXfPreInvoiceEntity preInvoice = applyProInvoiceRedNotificationDTO.getTXfPreInvoiceEntity();
        List<TXfPreInvoiceItemEntity> preInvoiceItemList = applyProInvoiceRedNotificationDTO.getTXfPreInvoiceItemEntityList();

        RedNotificationMain redNotificationMain = new RedNotificationMain();
        //数据转换
        redNotificationMain.setId(preInvoice.getId());
        redNotificationMain.setRemark(preInvoice.getRemark());
        redNotificationMain.setUserRole(2);
        redNotificationMain.setInvoiceType(preInvoice.getInvoiceType());
        redNotificationMain.setOriginInvoiceType(preInvoice.getOriginInvoiceType());
        redNotificationMain.setOriginalInvoiceNo(preInvoice.getOriginInvoiceNo());
        redNotificationMain.setOriginalInvoiceCode(preInvoice.getOriginInvoiceCode());
        redNotificationMain.setOriginalInvoiceDate(preInvoice.getOriginPaperDrawDate());
        redNotificationMain.setPurchaserTaxNo(preInvoice.getPurchaserTaxNo());
        redNotificationMain.setPurchaserName(preInvoice.getPurchaserName());
        redNotificationMain.setSellerTaxNo(preInvoice.getSellerTaxNo());
        redNotificationMain.setSellerName(preInvoice.getSellerName());
        redNotificationMain.setAmountWithTax(preInvoice.getAmountWithTax());
        redNotificationMain.setAmountWithoutTax(preInvoice.getAmountWithoutTax());
        redNotificationMain.setTaxAmount(preInvoice.getTaxAmount());
        redNotificationMain.setCompanyCode(preInvoice.getPurchaserNo());
        redNotificationMain.setSpecialInvoiceFlag(0);
        redNotificationMain.setBillNo(preInvoice.getSettlementNo());
        redNotificationMain.setInvoiceOrigin(preInvoice.getSettlementType());
        redNotificationMain.setApplyType(0);
        //redNotificationMain.setPaymentTime();
        //redNotificationMain.setApplyReason();
        //redNotificationMain.setCustomerNo();
        List<RedNotificationItem> redNotificationItemList = preInvoiceItemList.stream().map(preInvoiceItem -> {
            RedNotificationItem redNotificationItem = new RedNotificationItem();
            //TODO
            //redNotificationItem.setDetailNo();
            //redNotificationItem.setTaxConvertCode();
            redNotificationItem.setGoodsTaxNo(preInvoiceItem.getGoodsTaxNo());
            redNotificationItem.setTaxPre(Integer.valueOf(preInvoiceItem.getTaxPre()));
            redNotificationItem.setTaxPreCon(preInvoiceItem.getTaxPreCon());
            redNotificationItem.setZeroTax(Integer.valueOf(preInvoiceItem.getZeroTax()));
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
