package com.xforceplus.wapp.modules.certification.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 导入认证业务层接口
 * @author Colin.hu
 * @date 4/19/2018
 */
public interface ImportCertificationService {

    /**
     * 导入待认证发票的excel
     * @param file 需要导入的文件
     * @return 结果 成功则包含发票信息集
     */
   Map<String, Object> importEnjoySubsided(String schemaLabel, Long userId, MultipartFile file);

    /**
     * 提交认证，将提交认证的发票状态改为已确认
     * @param param 提交认证的发票信息
     * @return 结果
     */
   Integer submitAuth(String schemaLabel, Map<String, String> param,String userAccount,String userName);
}
