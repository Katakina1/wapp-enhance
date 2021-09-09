package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 查询认证数据层接口
 * @author Colin.hu
 * @date 4/13/2018
 */
@Mapper
public interface CertificationQueryDao {

    /**
     * 获取认证总数
     * @param map 查询参数
     * @return 总数
     */
    Integer getCertificationListCount(Map<String, Object> map);

    /**
     * 获取认证发票信息集
     * @param map 查询参数
     * @return 认证发票信息集
     */
    List<InvoiceCollectionInfo> selectCertificationList(Map<String, Object> map);
}
