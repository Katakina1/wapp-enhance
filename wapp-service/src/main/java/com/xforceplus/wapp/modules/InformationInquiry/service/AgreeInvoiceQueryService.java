package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;

import java.util.Map;

/**
 * Created by 1 on 2018/11/14 20:26
 */
public interface AgreeInvoiceQueryService {
    PagedQueryResult<MatchEntity> getMatchList(Map<String, Object> params);
}
