package com.xforceplus.wapp.modules.exceptionreport.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.converter.PageResultConverter;
import com.xforceplus.wapp.modules.exceptionreport.dto.ExceptionReportRequest;
import com.xforceplus.wapp.modules.exceptionreport.service.ExceptionReportService;
import com.xforceplus.wapp.repository.dao.TXfExceptionReportDao;
import com.xforceplus.wapp.repository.entity.TXfExceptionReportEntity;
import org.springframework.stereotype.Service;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 20:42
 **/
@Service
public class ExceptionReportServiceImpl extends ServiceImpl<TXfExceptionReportDao,TXfExceptionReportEntity> implements ExceptionReportService {
    @Override
    public void add4Claim(TXfExceptionReportEntity entity) {

    }

    @Override
    public void add4Agreement(TXfExceptionReportEntity entity) {

    }

    @Override
    public void add4EPD(TXfExceptionReportEntity entity) {

    }

    @Override
    public Page<TXfExceptionReportEntity> getPage(ExceptionReportRequest request) {
        Page<TXfExceptionReportEntity> page=new Page<>();
        page.setSize(request.getSize());
        page.setCurrent(request.getPage());

        final Page<TXfExceptionReportEntity> result = this.page(page);
        return null;
    }
}
