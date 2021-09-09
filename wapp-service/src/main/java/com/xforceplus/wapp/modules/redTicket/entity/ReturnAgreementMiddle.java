package com.xforceplus.wapp.modules.redTicket.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退货、协议匹配中间表
 */
public class ReturnAgreementMiddle extends AbstractBaseDomain {

    private static final long serialVersionUID = 8256738348973517368L;
    private Long redTicketMatchingAssociation;//红票匹配关联字段
    private Long returnAgreementAssociation;//退货、协议关联字段

    public Long getRedTicketMatchingAssociation() {
        return redTicketMatchingAssociation;
    }

    public void setRedTicketMatchingAssociation(Long redTicketMatchingAssociation) {
        this.redTicketMatchingAssociation = redTicketMatchingAssociation;
    }

    public Long getReturnAgreementAssociation() {
        return returnAgreementAssociation;
    }

    public void setReturnAgreementAssociation(Long returnAgreementAssociation) {
        this.returnAgreementAssociation = returnAgreementAssociation;
    }

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
