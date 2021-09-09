package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.InvoiceAuthMonthlyReportDao;
import com.xforceplus.wapp.modules.report.entity.DailyReportEntity;
import com.xforceplus.wapp.modules.report.service.InvoiceAuthMonthlyReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class InvoiceAuthMonthlyReportServiceImpl implements InvoiceAuthMonthlyReportService {

    @Autowired
    private InvoiceAuthMonthlyReportDao invoiceAuthMonthlyReportDao;

    @Override
    public List<DailyReportEntity> getList(String schemaLabel,Map<String, Object> map) {
        return invoiceAuthMonthlyReportDao.getList(schemaLabel,map);
    }

    /**
     * 修正月报数据,使得列表上没有缺失月份
     * @param list
     * @param year
     * @return
     */
    public List<DailyReportEntity> fixList(List<DailyReportEntity> list, String year){

        String[] monthArray = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};

        final List<DailyReportEntity> resultList = newArrayList();

        Date now = new Date();
        //获取当前年,月
//        String nowYear = now.toString("yyyy");
//        String nowMonth = now.toString("MM");

        int totalMonth = 12;
//        if(year.compareTo(nowYear)<0){
//            //过去的年
//            totalMonth = 12;
//        }else{
//            //今年
//            totalMonth = Integer.valueOf(nowMonth);
//        }

        int index = 0;
        for(DailyReportEntity entity : list){
            String month = entity.getRzhDate().substring(4,6);
            while(monthArray[index].compareTo(month)<0){
                resultList.add(new DailyReportEntity(year+monthArray[index]));
                index++;
            }
            resultList.add(entity);
            index++;
        }
        for(;index<totalMonth;index++){
            resultList.add(new DailyReportEntity(year+monthArray[index]));
        }

        return resultList;
    }
}
