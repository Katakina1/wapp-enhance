package com.xforceplus.wapp.modules.businessData.constant;

public final class BusinessDataConstant {
    private  BusinessDataConstant(){

    }
    private static final String MODULES_ROOT= "/modules/";
    /**
     * 退货查询
     */
    public static final String BUSINESSDATA_RETURNGOODS_QUERY=MODULES_ROOT+"businessData/returngoods/query";
    /**
     * 协议查询
     */
    public static final String BUSINESSDATA_AGREEMENT_QUERY=MODULES_ROOT+"businessData/agreement/query";
    /**
     * 退货明细查询
     */
    public static final String BUSINESSDATA_RETURNGOODSDET_QUERY=MODULES_ROOT+"businessData/returngoodsdet/query";
    /**
     * 根据业务类型查询
     */
    public static final String BUSINESSDATA_RETURNORAGREEMENT_QUERY=MODULES_ROOT+"businessData/returnoragreement/query";
    /**
     * 未红冲退货查询
     */
    public static final String BUSINESSDATA_RETURNGOODSBY_QUERY=MODULES_ROOT+"businessData/returngoodsby/query";
    /**
     * 未红冲协议查询
     */
    public static final String BUSINESSDATA_AGREEMENTBy_QUERY=MODULES_ROOT+"businessData/agreementby/query";
}
