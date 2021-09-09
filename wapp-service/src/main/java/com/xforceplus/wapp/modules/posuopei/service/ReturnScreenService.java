package com.xforceplus.wapp.modules.posuopei.service;


import com.xforceplus.wapp.modules.posuopei.entity.*;

import java.util.List;
import java.util.Map;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;

public interface ReturnScreenService {
    public Integer insertReturnScreen(HostReturnScreenEntity hostReturnScreenEntity);
    public PagedQueryResult<HostReturnScreenEntity> getReturnScreenList(Map<String,Object> params);
}
