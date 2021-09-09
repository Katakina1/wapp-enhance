package com.xforceplus.wapp.modules.index.service;

import java.util.Map;

public interface IndexMainVendorService {
    /**
     * 获取供应商首页所需信息
     * @param userid
     * @param usercode
     * @return
     */
    Map<String, Object> getIndexMainInfo(Long userid, String usercode);

    /**
     * 获取首页收货信息
     * @return
     */
    Map<String, Object> getReceiptInfo();
}
