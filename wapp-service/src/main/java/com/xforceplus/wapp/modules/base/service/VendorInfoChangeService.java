package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.UserEntity;

import java.util.List;
import java.util.Map;

public interface VendorInfoChangeService {
    /**
     * 供应商信息变更提交
     * @return
     */
    void submit(UserEntity userEntity);

    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<UserEntity> queryVendorInfoChangeList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryVendorInfoChangeCount(Map<String, Object> map);

    /**
     * 供应商变更信息通过
     * @param ids
     */
    void auditAgree(Long[] ids);
}
