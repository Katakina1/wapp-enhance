package com.xforceplus.wapp.modules.InformationInquiry.service;


import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;

import java.util.List;
import java.util.Map;

public interface ClaimInquiryService {

    /**
     * 查询索赔信息
     * @param map
     * @return
     */
    List<ClaimEntity> claimlist(Map<String, Object> map);

    /**
     * 查询索赔信息条数
     * @param map
     * @return
     */
    Integer claimlistCount(Map<String, Object> map);

    List<ClaimExcelEntity> selectExcelClaimlist(Map<String,Object> params);
}
