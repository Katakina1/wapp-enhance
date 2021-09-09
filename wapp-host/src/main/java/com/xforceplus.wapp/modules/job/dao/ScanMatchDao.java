package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.posuopei.entity.MatchEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScanMatchDao {

    List<RedInvoiceData> getAllDatas();

    List<String> selectInvoicesBySerialNumber(@Param("serialNumber") String serialNumber);

    List<String> getCountNotSign(@Param("list") List<String> list);

    void updateMatchInfo(@Param("data") RedInvoiceData data);

    void updateRecordInvoice(@Param("list") List<String> invoices, @Param("status") String status);

    void updateRecordInvoices(@Param("list") List<String> invoices,@Param("status") String status, @Param("epsNo") String epsNo);

    List<RedTicketMatch> selectOuterRedInvoice();

    void updateOutMatch(@Param("id") Long id);

    void updateRecordInvoiceByRedNotice(@Param("list") List<String> list);

    List<MatchEntity> findpoMatchDatas();

    List<String> findInvoicesById(@Param("id") Integer id);

    void updatePoMatchById(@Param("id") Integer id, @Param("status") String status);

    List<SettlementEntity> findNotScanCost();

    List<String> getInvoiceByCostNo(@Param("costNo") String costNo);

    void updateCostMatchById(@Param("costNo") String costNo, @Param("status") String status);

    void updateStatusAndReason(@Param("data") RedInvoiceData data);

    void updatePoScanFailReason(@Param("id") Integer id, @Param("status") String status, @Param("reason") String reason);

    void updateCostFailReason(@Param("costNo") String costNo, @Param("status") String status, @Param("reason") String reason);

    void updateCostScan(@Param("uuId") String uuId, @Param("status") String status, @Param("reason") String reason);

    void updateRecordReason(@Param("list") List<String> list, @Param("reason") String reason);

    List<RecordInvoiceEntity> findInvoicesByCostNo(@Param("costNo") String costNo);

    List<RecordInvoiceEntity> findScanInvoices(@Param("costNo") String costNo);

    Integer findMatchCount();

    List<MatchEntity> findpoMatchPageDatas(@Param("map")Map<String, Object> query);

    List<RecordInvoiceEntity> findInvoicesByCostNos(@Param("costNo") String costNo);

    void underWay(@Param("costNo") String costNo);
}
