package com.xforceplus.wapp.modules.fixed.service;

import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface QuestionnaireService {
    List<MatchQueryEntity> queryList(Map<String, Object> map);
    int queryCount(Map<String, Object> map);

    List<RecordInvoiceEntity> getInvoiceDetail(Long matchId);
    List<OrderEntity> getOrderDetail(Long matchId);
    List<FileEntity> getFileDetail(Long matchId);

    void viewImg(Long id, HttpServletResponse response);
    void downloadFile(Long id, HttpServletResponse response);

    void fileConfirm(MatchQueryEntity match);
}
