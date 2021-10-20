package com.xforceplus.wapp.modules.exportlog.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TDxExcelExportlogDao;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import org.springframework.stereotype.Service;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 14:39
 **/
@Service
public class ExcelExportLogService extends ServiceImpl<TDxExcelExportlogDao, TDxExcelExportlogEntity> {

    public static final String REQUEST = "1";
    public static final String OK = "2";
    public static final String FAIL = "3";


    public static final Long SERVICE_TYPE=66L;


}
