package com.xforceplus.wapp.modules.cost.service;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementMatchEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CostMatchService {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<SettlementMatchEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 查询明细
     * @param costNo
     * @return
     */
    List<CostEntity> queryDetail(String costNo);

    /**
     * 查询明细
     * @param costNoArray
     * @return
     */
    List<CostEntity> querySelectDetail(String[] costNoArray);

    /**
     * 获取符合条件的发票,用于验证发票是否重复
     * @param entity
     * @return
     */
    int selectInvoice(RecordInvoiceEntity entity);

    /**
     * 解析导入的excel
     * @param multipartFile
     * @return
     */
    Map<String,Object> parseExcel(MultipartFile multipartFile);
}
