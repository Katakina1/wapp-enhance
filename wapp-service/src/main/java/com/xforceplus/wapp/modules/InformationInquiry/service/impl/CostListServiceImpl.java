package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.modules.InformationInquiry.dao.CostListDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.ScanningDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.CostListService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CostListServiceImpl implements CostListService {
    @Autowired
    private CostListDao costListDao;
    @Override
    public List<ScanningEntity> scanningList(Map<String, Object> map){
        return costListDao.scanningList(map);
    }
    @Override
    public Integer scanningCount(Map<String, Object> map){
        return costListDao.scanningCount(map);
    }
}
