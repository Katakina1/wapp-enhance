package com.xforceplus.wapp.modules.protocol.dao;

import com.xforceplus.wapp.modules.protocol.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProtocolDao {
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
     * 查询导入失败的协议分页数据列表
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
    List<ProtocolDetailEntity> queryDetailList(@Param("venderId") String venderId, @Param("protocolNo") String protocolNo, @Param("amount") BigDecimal amount, @Param("caseDate") Date caseDate);

    /**
     * 发票明细查询
     * @param caseDate 定案日期
     * @param protocolNo 协议号
     * @return 明细
     */
    List<ProtocolInvoiceDetailEntity> queryInvoiceDetailList(@Param("caseDate")String caseDate,@Param("protocolNo") String protocolNo,@Param("venderName") String venderName);
    /**
     * 	查询协议号和供应商号是否已存在
     */
    int queryProtocolAndVenderId( @Param("entity") ProtocolEntity entity);

    /**
     * 	查询协议号和供应商号在明细表是否已存在
     */
    int queryProtocolAndVenderIdDetail( @Param("entity") ProtocolEntity entity);

    /**
     * 	删除协议根据供应商号和协议号
     */
    int deleteByProtocolAndVenderId( @Param("venderId") String venderId,@Param("protocolNo") String costType);

    /**
     * 	删除协议明细根据供应商号和协议号
     */
    int deleteByProtocolAndVenderIdDetail( @Param("venderId") String venderId,@Param("protocolNo") String costType);

    /**
     * 	保存协议
     */
    int save( @Param("list") List<ProtocolEntity> entity);

    /**
     * 	保存导入失败的协议
     */
    int saveFailure( @Param("list") List<ProtocolEntity> list,@Param("userCode") String userCode);

    /**
     * 	保存导入失败的发票明细
     */
    int saveFailureInvoiceDetail( @Param("list") List<ProtocolInvoiceDetailEntity> list);//int saveFailureInvoiceDetail( @Param("entity") ProtocolInvoiceDetailEntity entity);
    /**
     * 	保存发票明细
     */
    int saveInvoiceDetail( @Param("entity") ProtocolInvoiceDetailEntity entity);

    /**
     * 	保存协议明细
     */
    int saveDetail( @Param("list") List<ProtocolEntity> entity);

    /**
     * 	查询发票明细是否已存在
     */
    Long queryInvoiceDetailExist( @Param("entity") ProtocolInvoiceDetailEntity entity);

    int updateInvoiceDetail(@Param("entity") ProtocolInvoiceDetailEntity entity);

    /**
     * 删除协议
     */
    void deleteProtocol(@Param("ids") Long[] ids);

    /**
     * 删除协议明细
     */
    void deleteProtocolDetail(@Param("ids") Long[] ids);
    /**
     * 根据id查询协议
     * @param id
     * @return
     */
    ProtocolEntity queryProtocolById(@Param("id") Long id);

    /**
     * 清空协议失败表中所有数据
     */
    void emptyFailureProtocol();


    /**
     * 清空协议发票明细失败表中所有数据
     */
    void emptyFailureInvoiceDetail();

   void deleteByProtocolAndUserCode(@Param("userCode") String userCode);

   Long[] queryProtocolIds(Map<String, Object> map);

    Long[] queryProtocolDetailIds(@Param("ids") Long[] ids);
}
