package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.pojo.RecordInvoice;
import com.xforceplus.wapp.modules.job.pojo.advance.DetailData;
import com.xforceplus.wapp.modules.job.pojo.advance.InvoiceData;
import com.xforceplus.wapp.modules.job.pojo.advance.MainData;
import com.xforceplus.wapp.modules.job.pojo.advance.RecordInvoiceData;
import com.xforceplus.wapp.modules.job.pojo.question.FilePo;
import com.xforceplus.wapp.modules.job.pojo.question.QuestionDetail;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdvanceDao {


    List<DictPo> findDict();

    int findByBindID(@Param("bindid") String bindid);

    void insertAdvance(@Param("mainData") MainData mainData);

    void updateAdvance(@Param("mainData")MainData mainData);

    int findDetail(@Param("bindid")String bindid);

    void deleteDetail(@Param("bindid")String bindid);

    void insertDetail(@Param("list") List<DetailData> detailDataList);

    int findCost(String bindid);

    int findCostDetail(String bindid);

    int findInvoice(String bindid);

    void insertCostMain(@Param("mainData")MainData mainData, @Param("costno") String costno,@Param("bpms")String bpms);

    int findInoviceByNo(String invoiceNo);

    int insertInvoice(@Param("entity") InvoiceData entity, @Param("costno") String costno);

    void insertBpmsDetail(@Param("list") List<DetailData> detailDataList, @Param("id") String id);

    void deleteCostDetail(String bindid);

    void insertInvoiceMain(@Param("record") RecordInvoiceData record, @Param("costno") String costno);

    RecordInvoice findRecordInvoice(@Param("uuid") String uuid);

    void insertRecordInvoice(@Param("invoice") RecordInvoiceData invoiceData);

    void insertBindInvoice(@Param("invoice") RecordInvoiceData invoiceData, @Param("costno") String costno);
}