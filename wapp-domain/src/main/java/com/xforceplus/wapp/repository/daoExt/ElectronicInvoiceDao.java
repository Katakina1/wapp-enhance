package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.OrgEntity;
import com.xforceplus.wapp.repository.entity.TDxInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxMatchEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ElectronicInvoiceDao {

    /**
     * 保存上传的电票
     *
     * @param invoice 电票信息
     * @return
     */
    int saveElectronInvoice(@Param("invoice") TDxInvoiceEntity invoice);
//    /**
//     * 批量保存上传的电票
//     *
//     * @param invoices     电票信息
//     * @return
//     */
//    int batchSaveElectronInvoice(@Param("invoice") List<ElectronInvoiceEntity> invoices);


    TDxInvoiceEntity selectElectronInvoiceByUUid(@Param("uuid") String uuid);

    List<TDxInvoiceEntity> selectByIds(@Param("ids") List<Long> ids);

    int updateInvoiceMatch(Map<String, Object> map);

    void updateRebateyesorno(@Param("id") Long id, @Param("rebateyesorno") String rebateyesorno);

    Integer insertMatch(@Param("entity") TDxMatchEntity matchEntity);

    OrgEntity selectGfByJvCode(@Param("jvCode") String jvCode);

    /**
     * 底账发票插入
     *
     * @param map
     * @return
     */
    Integer saveInvoice(Map<String, Object> map);

    /**
     * 插入普纸
     *
     * @param map
     * @return
     */
    Integer saveInvoicePP(Map<String, Object> map);

    void updateNoDeduction(@Param("uuid") List<String> uuid);


}
