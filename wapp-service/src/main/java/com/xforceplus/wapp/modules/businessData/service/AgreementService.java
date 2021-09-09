package com.xforceplus.wapp.modules.businessData.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.businessData.entity.AgreementEntity;

import java.util.List;
import java.util.Map;

public interface AgreementService {
    /**
     * 获取协议信息
     * @param map 参数
     * @return
     */
    List<AgreementEntity> getAgreementList(Map<String, Object> map);
    /**
     * 查询有多少条信息
     */
    Integer agreementQueryCount(Map<String, Object> map);
    /**
     * 获取未红冲协议信息
     * @param map
     * @return
     */
    List<AgreementEntity> getAgreementListBy(Map<String, Object> map);
    /**
     * 查询有多少条未红冲协议信息
     */
    Integer agreementQueryRedCount(Map<String, Object> map);
}
