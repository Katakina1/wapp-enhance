package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.KnowledgeFileEntity;
import com.xforceplus.wapp.modules.base.entity.UniversalTaxRateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UniversalTaxRateDao {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<UniversalTaxRateEntity> queryList(@Param("map") Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(@Param("map") Map<String, Object> map);

    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<UniversalTaxRateEntity> queryCommodity(@Param("map") Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCommodityCount(@Param("map") Map<String, Object> map);

    /**
     * 保存上传文件的信息
     * @param knowledgeFileEntity 文件信息
     */
    void saveKnowFile(KnowledgeFileEntity knowledgeFileEntity);

    /**
     * 批量导入
     * @param universalTax
     */
    void insertUniversalTaxRate(@Param("universalTax") UniversalTaxRateEntity universalTax);

    /**
     * 保存商品信息
     *
     * @param query
     */
    int saveCommodity(@Param("entity") UniversalTaxRateEntity query);

    /**
     * 删除商品信息
     */
    int deleteCommodity(@Param("ids") Long[] ids);

    int selectCont(UniversalTaxRateEntity tax);

    void updateUniversalTaxRate(UniversalTaxRateEntity tax);

    int getCount(UniversalTaxRateEntity universalTaxRateEntity);

    //删除供应商
    int deleteVendor(String vendorNbr);
}
