package com.xforceplus.wapp.modules.businessData.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.posuopei.entity.ClaimEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ClaimExcelEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;

import java.util.List;
import java.util.Map;

public interface ClaimService {
    PagedQueryResult<ClaimEntity> claimQueryList(Map<String, Object> map);
    List<ClaimExcelEntity> transformExcle(List<ClaimEntity> list);
}
