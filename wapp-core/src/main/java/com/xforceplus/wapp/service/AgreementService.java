package com.xforceplus.wapp.service;

/**
 * 协议单相关逻辑操作
 */
public class AgreementService {


    /**
     * 撤销协议单 撤销结算单 蓝票释放额度 如果有预制发票 撤销预制发票
     * 协议单还可以再次使用
     * @param settlementId 结算单id
     * @return
     */
    public boolean repealAgreementSettlement(Long settlementId){
        return true;
    }

}
