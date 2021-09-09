package com.xforceplus.wapp.modules.fixed.service;



import com.xforceplus.wapp.modules.lease.entity.InvoiceImportAndExportEntity;

import java.util.List;
import java.util.Map;

public interface SapUnconfirmService {

    /**
     * 查询sap待确认信息
     * @param map
     * @return
     */
    List<InvoiceImportAndExportEntity> sapList(Map<String, Object> map);

    Integer sapCount(Map<String, Object> map);

    /**
     * sap匹配修改为成功
     * @return 是否修改成功
     */
    boolean sapSuccess(Long id);

    /**
     * 退票
     * @param param
     * @return
     */
    void refund(Map<String,Object> param);
}
