package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface XfRecordInvoiceDao {

    int saveRecordInvoiceDetail(@Param("detailList") List<TDxRecordInvoiceDetailEntity> detailList);

    TDxInvoiceEntity selectByUuid(@Param("uuid") String uuid);

    int update(TDxInvoiceEntity recordInvoice);

}
