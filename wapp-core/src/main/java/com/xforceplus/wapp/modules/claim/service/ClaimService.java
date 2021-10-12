package com.xforceplus.wapp.modules.claim.service;

import com.xforceplus.wapp.enums.XfPreInvoiceEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
public class ClaimService {

    /**
     * 申请索赔单不定案
     * @param settlementId 结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean applyClaimVerdict(Long settlementId, List<Long> billDeductIdList){
        return true;
    }

    /**
     * 驳回申请索赔单不定案
     * @param settlementId 结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean rejectClaimVerdict(Long settlementId, List<Long> billDeductIdList){
        return true;
    }

    /**
     * 通过申请索赔单不定案
     * @param settlementId 结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean agreeClaimVerdict(Long settlementId, List<Long> billDeductIdList){
        return true;
    }

    /**
     * 作废结算单 结算单不能再次匹配 明细释放额度 蓝票释放额度
     * @param settlementId 结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean repealClaimSettlement(Long settlementId, List<Long> billDeductIdList){
        return true;
    }

    /**
     * 撤销结算单 结算单能再次匹配 明细释放额度 蓝票释放额度
     * @param settlementId 结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean backOutClaimSettlement(Long settlementId, List<Long> billDeductIdList){
        return true;
    }


    /**
     * 申请红字信息
     * @return
     */
    public boolean applyClaimRedNotification(){
        return true;
    }

    /**
     * 撤销红字信息
     * @return
     */
    public boolean repealClaimRedNotification(){
        return true;
    }


    /**
     * 修改预制发票状态
     * 撤销预制发票需要撤销红字信息
     * @param xfPreInvoiceId
     * @param xfPreInvoiceEnum
     * @return
     */
    public boolean updateClaimPreInvoice(Long xfPreInvoiceId, XfPreInvoiceEnum xfPreInvoiceEnum){
        return true;
    }

}
