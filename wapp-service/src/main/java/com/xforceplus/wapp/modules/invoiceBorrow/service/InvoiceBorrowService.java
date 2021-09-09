package com.xforceplus.wapp.modules.invoiceBorrow.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.invoiceBorrow.entity.BorrowEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.FpghExcelEntity;
import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.xforceplus.wapp.modules.report.entity.JyjlcxExcelEntity;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 发票借阅业务层接口
 */
public interface InvoiceBorrowService {

    /**
     * 获得发票借阅集合
     * @param map 查询条件
     * @return 发票借阅集合
     */
   PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceBorrowList(Map<String, Object> map);

    /**
     * 保存借阅或归还记录
     * @param borrowEntity 借阅人，借阅时间，借阅原因
     * @return
     */
   void save(BorrowEntity borrowEntity);

    /**
     * 获得发票借阅记录集合
     * @param map 查询条件
     * @return 发票借阅记录集合
     */
    PagedQueryResult<BorrowEntity> queryBorrowRecordList(Map<String, Object> map);


    List<FpjyExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> borrowInvoiceList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    List<FpghExcelEntity> transformExcle2(List<ComprehensiveInvoiceQueryEntity> borrowInvoiceList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    List<JyjlcxExcelEntity> transformExcle3(List<BorrowEntity> recordList) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    Map<String, Object> parseExcel(MultipartFile multipartFile);
    Map<String, Object> parseExcelGh(MultipartFile multipartFile);
}
