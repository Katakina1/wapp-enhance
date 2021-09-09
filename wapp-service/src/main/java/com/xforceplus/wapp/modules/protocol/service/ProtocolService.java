package com.xforceplus.wapp.modules.protocol.service;

import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolExcelEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProtocolService {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<ProtocolEntity> queryList(Map<String, Object> map);

    List<ProtocolEntity> queryListExport(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);
    Integer queryCountExport(Map<String, Object> map);

    /**
     * 查询导入失败的协议数据列表
     * @param map
     * @return
     */
    List<ProtocolEntity> queryFailureList(Map<String, Object> map);

    /**
     * 查询导入失败的协议列表总数量
     * @param map
     * @return
     */
    Integer queryFailureCount(Map<String, Object> map);

    /**
     * 查询导入失败的发票明细列表
     * @param map
     * @return
     */
    List<ProtocolInvoiceDetailEntity> queryInvoiceDetailFailureList(Map<String, Object> map);

    /**
     * 协议明细查询
     * @param venderId 供应商号
     * @param protocolNo 协议号
     * @return 明细
     */
    List<ProtocolDetailEntity> queryDetailList(String venderId, String protocolNo, BigDecimal detailAmount, Date caseDate);

    /**
     * 发票明细查询
     * @param caseDate 定案日期
     * @param protocolNo 协议号
     * @return 明细
     */
    List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(String caseDate, String protocolNo,String venderName);

    /**
     * 批量保存协议
     * @param protocolList 协议列表
     * @return 成功数量
     */
    Map saveBatchProtocol(List<ProtocolEntity> protocolList,String userCode,HttpServletResponse response);

    /**
     * 批量保存发票明细
     * @param invoiceDetailList 发票明细列表
     * @return 成功数量
     */
    Integer saveBatchInvoiceDetail(List<ProtocolInvoiceDetailEntity> invoiceDetailList);

    /**
     * 按查询条件删除协议和协议明细
     */
    void deletePorotocol( Map<String, Object> map);

    /**
     * 清空协议失败表中所有数据
     */
    void emptyFailureProtocol();

    /**
     * 清空协议发票明细失败表中所有数据
     */
    void emptyFailureInvoiceDetail();

    ProtocolEntity queryProtocolById(String id);

    void deleteByProtocolAndUserCode(String userCode);

    List<ProtocolExcelEntity> selectExcelpolist(Map<String, Object> params);

    List<ProtocolExcelEntity> transformExcle(List<ProtocolEntity> params);
}
