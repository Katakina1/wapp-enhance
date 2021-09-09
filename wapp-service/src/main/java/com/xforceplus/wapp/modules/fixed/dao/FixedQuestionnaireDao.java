package com.xforceplus.wapp.modules.fixed.dao;

import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FixedQuestionnaireDao {
    List<MatchQueryEntity> queryList(Map<String, Object> map);
    int queryCount(Map<String, Object> map);

    List<RecordInvoiceEntity> getInvoiceDetail(Long matchId);
    List<OrderEntity> getOrderDetail(Long matchId);
    List<FileEntity> getFileDetail(Long matchId);

    FileEntity getFileInfo(Long id);

    Integer deleteFile(Long id);
}
