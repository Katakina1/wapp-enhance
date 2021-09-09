package com.xforceplus.wapp.modules.job.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.AuthenticationQueryDao;
import com.xforceplus.wapp.modules.job.dao.WalmartApiDao;
import com.xforceplus.wapp.modules.job.entity.WalmartApiEntity;
import com.xforceplus.wapp.modules.job.service.WalmartApiService;
import com.xforceplus.wapp.modules.report.dao.ComprehensiveInvoiceQueryDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.ComprehensiveInvoiceQueryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WalmartApiServiceImpl implements WalmartApiService {

    @Autowired
    private WalmartApiDao walmartApiDao;

    @Override
    public List<WalmartApiEntity> searchGf() {
        return walmartApiDao.searchGf();
    }

}
