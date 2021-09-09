package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.modules.report.dao.SupplierIssueInvoiceQuantityandRatioDao;
import com.xforceplus.wapp.modules.report.entity.InvoiceQuestionQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.QuestionInvoiceQuantityAndRatioEntity;
import com.xforceplus.wapp.modules.report.service.SupplierIssueInvoiceQuantityandRatioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SupplierIssueInvoiceQuantityandRatioServiceImpl implements SupplierIssueInvoiceQuantityandRatioService {
    @Autowired
    private SupplierIssueInvoiceQuantityandRatioDao supplierIssueInvoiceQuantityandRatioDao;
    @Override
    public List<QuestionInvoiceQuantityAndRatioEntity> problemInvoice(Map<String, Object> map){
        return supplierIssueInvoiceQuantityandRatioDao.problemInvoice(map);
    }
    @Override
    public List<InvoiceQuestionQueryExcelEntity> toExcel(List<QuestionInvoiceQuantityAndRatioEntity> list){
        List<InvoiceQuestionQueryExcelEntity> list2=new ArrayList<>();
        for (QuestionInvoiceQuantityAndRatioEntity qe:list){
            InvoiceQuestionQueryExcelEntity ie=new InvoiceQuestionQueryExcelEntity();
            ie.setRownumber(qe.getRownumber());
            ie.setNormalInvoice(ie.getNormalInvoice());
            ie.setProblemInvoice(qe.getProblemInvoice().toString());
            ie.setProblemInvoiceRatio(qe.getProblemInvoiceRatio());
            ie.setVenderId(qe.getVenderId());
            ie.setVenderName(qe.getVendername());
            list2.add(ie);
        }
        return list2;
    }
}
