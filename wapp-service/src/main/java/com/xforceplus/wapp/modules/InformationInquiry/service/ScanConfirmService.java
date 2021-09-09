package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.SpthpExcelEntity;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ScanConfirmService {
    List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map);
    int queryCount(Map<String, Object> map);
    List<SelectionOptionEntity> getJV(String taxNo);
    List<SelectionOptionEntity> getVender();
    boolean submit(ConfirmInvoiceEntity entity);
    R submitBatch(MultipartFile file, Long userId);

    List<SpthpExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
