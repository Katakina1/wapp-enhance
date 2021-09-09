package com.xforceplus.wapp.modules.posuopei.service.impl;

import com.xforceplus.wapp.modules.posuopei.dao.SubmitOutstandingReportDao;
import com.xforceplus.wapp.modules.posuopei.entity.SubmitOutstandingReportEntity;
import com.xforceplus.wapp.modules.posuopei.service.SubmitOutstandingReportService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.LoggerFactory.getLogger;

@Service("submitOutstandingReportService")
public class SubmitOutstandingReportServiceImpl implements SubmitOutstandingReportService{


   private static final Logger LOGGER= getLogger(SubmitOutstandingReportServiceImpl.class);
    @Autowired
    SubmitOutstandingReportDao submitOutstandingReportDao;

   @Transactional
   @Override
   public Integer insertSubmitOutstandingReport(SubmitOutstandingReportEntity submitOutstandingReportEntity){
        return submitOutstandingReportDao.insertSubmitOutstandingReport(submitOutstandingReportEntity);
   }
}
