package com.xforceplus.wapp.modules.certification.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 导入认证业务层接口
 * @author Colin.hu
 * @date 4/19/2018
 */
public interface ImportCheckService {

    /**
     * 提交认证，将提交认证的发票状态改为已确认
     * @param param 提交认证的发票信息
     * @return 结果
     */
    Integer submit(String schemaLabel,Map<String, String> param,String loginName,String userName);
}
