package com.xforceplus.wapp.modules.job.dao;

import java.util.Date;
import java.util.List;

import com.xforceplus.wapp.modules.job.entity.TAcOrg;
import com.xforceplus.wapp.modules.job.entity.TDxApplyRecord;
import com.xforceplus.wapp.modules.job.entity.TDxHttpLog;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoice;
import com.xforceplus.wapp.modules.job.pojo.LegalizeResult;
import com.xforceplus.wapp.modules.job.pojo.TaxCurrent;
import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConfirmDao extends BaseDao<TAcOrg> {

    List getTaxno();

    int qryTaxCurrent(@Param("taxno") String taxno);

    void insertCurrent(@Param("taxCurrent") TaxCurrent taxCurrent);

    void updateCurrent(@Param("taxCurrent") TaxCurrent taxCurrent);

    List<String> getBatchNo();

    void updateRecord(@Param("result") LegalizeResult result, @Param("uuid") String uuid, @Param("rzhDate") Date rzhDate);

    void updateApply(@Param("uuid")String uuid);


    void saveLog(@Param("tDxHttpLog") TDxHttpLog tDxHttpLog);

    List<TDxRecordInvoice> getApplyInvoice();

    void insertApplyRecord(@Param("tDxApplyRecord") TDxApplyRecord tDxApplyRecord, @Param("batchNo") String batchNo);

    int getApplyInvoiceByCount();

    void updateInvoiceRecord(@Param("tDxApplyRecord") TDxApplyRecord tDxApplyRecord);

    List<TAcOrg> getLink(@Param("linkName")String linkName);
}