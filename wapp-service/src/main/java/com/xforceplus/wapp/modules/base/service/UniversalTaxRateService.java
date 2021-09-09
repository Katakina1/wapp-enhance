package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.UniversalTaxRateEntity;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UniversalTaxRateService {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<UniversalTaxRateEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 批量导入
     * @param multipartFile
     * @return
     */
    public Map<String, Object> parseExcel(MultipartFile multipartFile);


    /**
     * 查询商品分页数据列表
     * @param map
     * @return
     */
    List<UniversalTaxRateEntity> queryCommodity(Map<String, Object> map);

    /**
     * 查询商品列表总数量
     * @param map
     * @return
     */
    Integer queryCommodityCount(Map<String, Object> map);

    /**
     * 保存商品信息
     * @param universalTaxRateEntity
     * @return
     */
    Map<String, Object> saveCommodity(UniversalTaxRateEntity universalTaxRateEntity);

    /**
     * 删除商品
     */
    int deleteCommodity(Long[] ids);

    /**
     * 删除供应商
     * */
    int deleteVendor(String vendorNbr);
}


