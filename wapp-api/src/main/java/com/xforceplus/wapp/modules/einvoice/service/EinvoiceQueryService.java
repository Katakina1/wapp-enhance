package com.xforceplus.wapp.modules.einvoice.service;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.modules.einvoice.entity.EinvoiceQueryEntity;
import com.xforceplus.wapp.modules.einvoice.entity.ElectronInvoiceEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author marvin
 * 电票查询业务层接口
 */
public interface EinvoiceQueryService {

    /**
     * 根据条件查询电票信息列表和总条数 封装在数据集合的工具类中
     *
     * @param queryEntity ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @param userId      当前用户的id
     * @return 查询的结果 例如：
     * totalCount：100
     * pageSize：10
     * totalPage：10
     * list：{{
     * id:10,
     * invoiceNo:"1234656"
     * invoiceCode:"1234656"
     * qsStatus:"1"
     * checkCode:"123456"
     * invoiceDate:Date类型
     * gfTaxNo:"BN5641564165"
     * xfTaxNo:"165165165146"
     * invoiceAmount:"1234.23"
     * totalAmount:"123.2"
     * scanId:"123465fhrfh2"
     * uuid:"12346561234656"
     * }}
     */
    PageUtils queryInvoiceMsg(String schemaLabel, EinvoiceQueryEntity queryEntity, Long userId);

    /**
     * 根据条件查询电票信息列表
     *
     * @param queryEntity ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 查询的结果 List<ElectronInvoiceEntity> :[{
     * id:10,
     * invoiceNo:"1234656"
     * invoiceCode:"1234656"
     * qsStatus:"1"
     * checkCode:"123456"
     * invoiceDate:Date类型
     * gfTaxNo:"BN5641564165"
     * xfTaxNo:"165165165146"
     * invoiceAmount:"1234.23"
     * totalAmount:"123.2"
     * scanId:"123465fhrfh2"
     * uuid:"12346561234656"
     * }]
     */
    List<ElectronInvoiceEntity> queryList(String schemaLabel, EinvoiceQueryEntity queryEntity);

    /**
     * 根据条件查询电票信息列表的总条数
     *
     * @param queryEntity ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @return 总条数 例如：100
     */
    int queryTotal(String schemaLabel, EinvoiceQueryEntity queryEntity);

    /**
     * 导出电票
     *
     * @param requestData ：
     *                    gfTaxNo：购方税号 可以为空
     *                    invoiceNo：发票号码 可以为空
     *                    qsStartDate：签收开始日期 可以为空
     *                    qsEndDate：签收结束日期 可以为空
     * @param schemaLabel 当前用户所在的分库名
     * @param userId      当前用户的id
     * @param response    返回数据
     */
    void exportElectronInvoice(String schemaLabel, Map<String, Object> requestData, HttpServletResponse response, Long userId);
}
