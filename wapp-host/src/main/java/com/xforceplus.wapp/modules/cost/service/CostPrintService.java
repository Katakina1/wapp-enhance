package com.xforceplus.wapp.modules.cost.service;


import com.xforceplus.wapp.modules.report.entity.OptionEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/9 11:28
 */
public interface CostPrintService {


    void costProviderExport(Map<String, Object> params, HttpServletResponse response);


    List<OptionEntity> queryXL(String walmart_rate);
}
