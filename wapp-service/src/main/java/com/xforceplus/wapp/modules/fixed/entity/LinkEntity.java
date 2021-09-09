package com.xforceplus.wapp.modules.fixed.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;

public class LinkEntity extends AbstractBaseDomain {
    //匹配表id
    private Long matchId;
    //单据类型 1-订单 2-发票
    private String docType;
    //单据id
    private Long docId;

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }
}
