package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.enums.XFDeductionEnum;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
public class DeductService {
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    private TXfBillDeductItemRefDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    /**
     * 接收索赔明细
     * @param
     * @return
     */
    public boolean receiveData(List<ClaimBillItemData> claimBillItemDataList, String batchNo ) {
//        TXfBillDeductItemEntity
//        for (ClaimBillItemData claimBillItemData : claimBillItemDataList) {
//            tXfBillDeductItemDao.insert()
//        }
        return true;
    }

    /**
     * 接收清洗数据
     * @param deductBillBaseData
     * @return
     */
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseData, String batchNo, XFDeductionEnum deductionEnum) {
        return true;
    }

    /**
     * 某批次完成通知，索赔单保证，明细信息完成后，再进行主信息保存
     * @param batchNo
     * @return
     */
    public boolean receiveDone(String batchNo) {
        return true;
    }

    /**
     * 自动取消和解锁
     * @param businessNo
     * @param deductionEnum
     * @return
     */
    public boolean unlockAndCancel(String businessNo,XFDeductionEnum deductionEnum) {
        return false;
    }

    /**
     * 索赔单匹配 （当月的主信息 明细信息信息匹配）
     * @param businessNo
     * @param deductionEnum
     * @param itemNo 商品编码
     * @return
     */
    public boolean matchClaimBill(String businessNo, XFDeductionEnum deductionEnum, String itemNo) {
        return false;
    }

    /**
     * 商品税编匹配
     * @param businessNo
     * @param deductionEnum
     * @param itemNo 商品编码
     * @return
     */
    public boolean matchTaxCode(String businessNo, XFDeductionEnum deductionEnum, String itemNo) {
        return false;
    }

    /***
     * 结算单匹配蓝票
     * @param businessNo
     * @param amout
     * @param sellerNo
     * @return
     */
    public boolean settlementMatchInvoice(String businessNo, BigDecimal amout,String sellerNo) {
        return false;
    }

    /**
     *  合并协议单、EPD
     * @return
     */
    public boolean mergeBill(XFDeductionEnum deductionEnum) {
        return false;
    }

    /**
     *  协议单、EPD 索赔单 合并结算单, 合并2年内的未匹配的单子
     * @return
     */
    public boolean mergeSettlement(XFDeductionEnum deductionEnum) {
        return false;
    }
}
