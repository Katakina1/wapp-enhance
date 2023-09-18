package com.xforceplus.wapp.modules.discountRateSetting.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.modules.discountRateLog.dto.DiscountRateLogDto;
import com.xforceplus.wapp.modules.discountRateLog.dto.OrgDto;
import com.xforceplus.wapp.modules.discountRateSetting.dto.OrgExportRequest;
import com.xforceplus.wapp.repository.entity.OrgLogEntity;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;

import java.util.List;

public interface OrgService extends IService<TAcOrgEntity> {
    Page<TAcOrgEntity> paged(OrgDto vo);
    List<TAcOrgEntity> getByBatchIds(List<Long> includes);
    R orgExport(List<TAcOrgEntity> resultList, OrgExportRequest request, String type);
}
