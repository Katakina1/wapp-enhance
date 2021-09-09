package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.SignedUncertifiedDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.SignedUncertifiedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author joe.tang
 * @date 2018/4/14
 * 签收未认证统计业务层
 */
@Service
public class SignedUncertifiedServiceImpl implements SignedUncertifiedService{

    @Autowired
    private SignedUncertifiedDao signedUncertifiedDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map,String schemaLabel) {
        return signedUncertifiedDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(Map<String, Object> map,String schemaLabel) {
        return signedUncertifiedDao.queryTotalResult(map,schemaLabel);
    }
    @Override
    public int queryTotal(Map<String, Object> map,String schemaLabel) {
        return signedUncertifiedDao.queryTotal(schemaLabel,map);
    }

    @Override
    public List<OptionEntity> searchGf(Long userId,String schemaLabel) {
        return signedUncertifiedDao.searchGf(userId,schemaLabel);
    }
    @Override
    public List<String> searchXf(Map<String, Object> map,String schemaLabel) {
        return signedUncertifiedDao.searchXf(map,schemaLabel);
    }
}
