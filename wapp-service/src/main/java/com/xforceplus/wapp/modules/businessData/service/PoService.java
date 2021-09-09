package com.xforceplus.wapp.modules.businessData.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoExcelEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface PoService {
    PagedQueryResult<PoEntity> poQueryList(Map<String, Object> map);

    Integer queryTotalResult(Map map);

    List<PoEntity> queryList(Query query1);

    List<PoExcelEntity> transformExcle(List<PoEntity> invoiceEntityList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
