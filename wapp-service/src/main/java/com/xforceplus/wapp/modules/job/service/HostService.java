package com.xforceplus.wapp.modules.job.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.job.entity.HostTaskEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface HostService {
    PagedQueryResult<HostTaskEntity> getTaskList(Map<String,Object> params);

    PagedQueryResult<MatchEntity> getMatchEntityLists(Map<String,Object> params);
    List<ScreenExcelEntity> transformExcle(List<MatchEntity> list);
    String postApi(Long[] ids);
    List<InvoiceEntity> queryInvoiceList(String matchno);
}
