package com.xforceplus.wapp.modules.check.service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验业务层
 */
public interface InvoiceCheckModulesService {


    /**
     * 发票查验
     *
     * @param params
     * @param currentUser
     * @return
     */
    Map<String, Object> doInvoiceCheck(String schemaLabel, Map<String, Object> params, String currentUser);


    /**
     * 查验历史列表
     *
     * @param params
     * @return
     */
    PagedQueryResult<InvoiceCheckModel> getInvoiceCheckHistoryList(String schemaLabel, Map<String, Object> params);


    /**
     * 查验历史详情
     *
     * @param params
     * @return
     */
    PagedQueryResult<InvoiceCheckModel> getInvoiceCheckHistoryDetail(String schemaLabel, Map<String, Object> params);

    /**
     * 查验历史删除
     *
     * @param params
     * @return
     */
    Boolean getInvoiceCheckHistoryDelete(String schemaLabel, Map<String, Object> params);

    /**
     * 查验统计
     *
     * @param params
     * @return
     */
    PagedQueryResult<Map<String, Object>> getInvoiceStatistics(String schemaLabel, Map<String, Object> params);

    /**
     * 发票查验删除
     * @param schemaLabel 分库标识
     * @param uuid 发票代码+发票号码
     * @return 删除标识 true 成功 false 失败
     */
    Boolean deleteCheckInvoice(String schemaLabel, String uuid);

    /**
     * 导入待认证发票的excel
     * @param file 需要导入的文件
     * @return 结果 成功则包含发票信息集
     */
    Map<String, Object> importEnjoySubsided(String schemaLabel, Long userId, MultipartFile file);
}
