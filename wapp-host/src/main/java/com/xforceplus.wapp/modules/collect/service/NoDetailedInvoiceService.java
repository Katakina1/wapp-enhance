package com.xforceplus.wapp.modules.collect.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;

import java.util.List;
import java.util.Map;

/**
 * 未补明细发票业务层接口
 * @author Colin.hu
 * @date 4/11/2018
 */
public interface NoDetailedInvoiceService {

    /**
     * 获得未补明细发票集合
     * @param map 查询条件(createDate-采集时间,gfName-购方名称,invoiceNo-发票号码,invoiceType-发票类型)
     * @return 未补明细发票集合
     */
   PagedQueryResult<InvoiceCollectionInfo> selectNoDetailedInvoice(Map<String, Object> map);

    /**
     * 手动验证 调用验证接口 成功则保存获取的发票明细，失败则返回失败原因
     * @param params 参数 税号，发票类型
     * @return 验证结果
     */
    Map<String, String> manualInspection(Map<String, String> params);

    /**
     * 根据类型查询数据字典表获取对应明细
     * @param params 参数 type
     * @return 参数名和code
     */
    List<Map<String, String>> getParamMapByType(Map<String, String> params);

    /**
     * 查验业务处理 仅供导入签收普票查验成功使用
     * @param responseInvoice 查验响应数据
     * @return 结果
     */
    Boolean inspectionProcess(String schemaLabel, List<ResponseInvoice> responseInvoice);
}
