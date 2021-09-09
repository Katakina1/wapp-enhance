package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.job.entity.TDxHttpLog;
import com.xforceplus.wapp.modules.job.pojo.question.FilePo;
import com.xforceplus.wapp.modules.job.pojo.question.QuestionDetail;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseOrderDao {


    List<QuestionPaperEntity> selectOrders();

    List<QuestionDetail> selectQuestionDetails(@Param("id") Integer id);

    List<DictPo> selectParam();

    List<FilePo> findFilePath(@Param("id")Integer id);

    void updateStatus(@Param("id") Integer id, @Param("processId") String processId);

}