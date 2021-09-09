package com.xforceplus.wapp.modules.InformationInquiry.service;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.DirectAuthQueryExcelEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface DirectAuthService {
    List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map);
    int queryCount(Map<String, Object> map);
    R submit(List<ConfirmInvoiceEntity> list);
    R submitBatch(MultipartFile file, Long userId);
    List<DirectAuthQueryExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> list);
}

