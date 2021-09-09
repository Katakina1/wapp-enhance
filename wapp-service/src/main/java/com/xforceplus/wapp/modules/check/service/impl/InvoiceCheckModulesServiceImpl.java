package com.xforceplus.wapp.modules.check.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.RMBUtils;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.check.dao.InvoiceCheckMapper;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckDetailModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckMainModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import com.xforceplus.wapp.modules.check.export.InvoiceCheckImport;
import com.xforceplus.wapp.modules.check.service.InvoiceCheckModulesService;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.INVOICE_CHECK_CYJG_CODE_SFYZ;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.INVOICE_TYPE;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.INVOICE_TYPE_VEHICLE;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_AUTH_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_EXIST;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_INNER_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_REMOTE_SERVER_ERROR;
import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.RESPONSE_CODE_SUCCESS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验业务
 */
@Service
@Transactional
public class InvoiceCheckModulesServiceImpl implements InvoiceCheckModulesService {

    private final static Logger LOGGER = getLogger(InvoiceCheckModulesServiceImpl.class);

    //日期处理
    private static final String TIME_PATTERN = "yyyy-MM-dd";

//    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

    private SystemConfig systemConfig;
    private InvoiceCheckMapper invoiceCheckMapper;



    public InvoiceCheckModulesServiceImpl(SystemConfig systemConfig, InvoiceCheckMapper invoiceCheckMapper) {
        this.systemConfig = systemConfig;
        this.invoiceCheckMapper = invoiceCheckMapper;
    }


    /**
     * 发票历史-查验
     *
     * @param params
     */
    @Override
    public Map<String, Object> doInvoiceCheck(String schemaLabel, Map<String, Object> params, String currentUser) {
        LOGGER.info("发票查验,params:{}", params);

        //定义返回值
        final Map<String, Object> resultMap = Maps.newHashMap();

        //获取查验人员
        final String checkUser = invoiceCheckMapper.getCheckUser(schemaLabel, valueOf(params.get("invoiceCode")), valueOf(params.get("invoiceNo")));
        //判断人员是否有查验权限 即发票发票是否有其他人处理过
        if(StringUtils.isNotEmpty(checkUser) && !currentUser.equals(checkUser)) {
            resultMap.put("RCode", RESPONSE_CODE_AUTH_ERROR);
            return resultMap;
        }

        final InvoiceCheckMainModel checkModel = new InvoiceCheckMainModel();
        final String uuidString = valueOf(params.get("invoiceCode")) + valueOf(params.get("invoiceNo"));
        checkModel.setUuid(uuidString);
        /*发票查验成功唯一性检测：一张发票查验能且仅能成功一次*/
        final String checkValue = invoiceCheckMapper.doInvoiceCheckMainUniqueCheck(schemaLabel, checkModel);
        //发票在查验表存在 且code为0001则说明查验成功 无需重复查验
        if (!(StringUtils.isNotEmpty(checkValue) && INVOICE_CHECK_CYJG_CODE_SFYZ.equals(checkValue))) {
            final ResponseInvoice responseInvoice = null;

            /**
             * 验证成功 新增-查验历史、查验主表、查验明细表
             */
            if (responseInvoice != null) {
                if (!StringUtils.isEmpty(responseInvoice.getResultCode())) {
                    InvoiceCheckModel data = assignInvoiceCheckFailHistoryAddData(responseInvoice, currentUser);
                    data.setDetailId(invoiceCheckMapper.getInvoiceIdByUuId(schemaLabel, uuidString));
                    resultMap.put("data", data);
                    if (INVOICE_CHECK_CYJG_CODE_SFYZ.equals(responseInvoice.getResultCode())) {
                        Boolean logFlag = invoiceCheckMapper.doInvoiceCheckLogAdd(schemaLabel, assignInvoiceCheckHistoryAddData(responseInvoice, currentUser)) > 0;
                        data.setResponseInvoice(responseInvoice);
                        data.setStringTotalAmount(RMBUtils.getRMBCapitals(Math.round(Double.valueOf(responseInvoice.getTotalAmount())*100)));
                        resultMap.put("data", data);
                        if (!logFlag) {
                            resultMap.put("RCode", RESPONSE_CODE_INNER_ERROR);
                            return resultMap;
                        }
                        Boolean delFlag = Boolean.TRUE;
                        if(StringUtils.isNotEmpty(checkValue)) {
                            delFlag = invoiceCheckMapper.deleteCheckInvoice(schemaLabel, uuidString) > 0;
                        }
                        Boolean mainFlag = invoiceCheckMapper.doInvoiceCheckMainAdd(schemaLabel, assignInvoiceCheckMainAddData(responseInvoice)) > 0;
                        if (!(mainFlag && delFlag)) {
                            resultMap.put("RCode", RESPONSE_CODE_INNER_ERROR);
                            return resultMap;
                        }
                        Boolean detailFlag = assignInvoiceCheckDetailAddData(schemaLabel, responseInvoice);
                        if (!detailFlag) {
                            resultMap.put("RCode", RESPONSE_CODE_INNER_ERROR);
                            return resultMap;
                        }
                        if (INVOICE_TYPE_VEHICLE.equals(responseInvoice.getInvoiceType())) {
                            Boolean vehicleFlag = invoiceCheckMapper.doInvoiceCheckVehicleDetailAdd(schemaLabel, assignInvoiceCheckVehicleDetailAddData(responseInvoice)) > 0;
                            if (!vehicleFlag) {
                                resultMap.put("RCode", RESPONSE_CODE_INNER_ERROR);
                                return resultMap;
                            }

                        }
                    } else {
                        //查验失败保存记录
                        Boolean logFlag = invoiceCheckMapper.doInvoiceCheckLogAdd(schemaLabel, assignInvoiceCheckFailHistoryAddData(responseInvoice, currentUser)) > 0;
                        Boolean delFlag = Boolean.TRUE;
                        if(StringUtils.isNotEmpty(checkValue)) {
                            delFlag = invoiceCheckMapper.deleteCheckInvoice(schemaLabel, uuidString) > 0;
                        }
                        //保存主表
                        Boolean mainFlag = invoiceCheckMapper.doInvoiceCheckMainAdd(schemaLabel, assignInvoiceCheckMainAddData(responseInvoice)) > 0;
                        if (!(logFlag && mainFlag && delFlag)) {
                            resultMap.put("RCode", RESPONSE_CODE_INNER_ERROR);
                            return resultMap;
                        }

                    }
                    resultMap.put("RCode", RESPONSE_CODE_SUCCESS);
                    resultMap.put("msg", responseInvoice.getResultTip());

                } else {
                    resultMap.put("RCode", RESPONSE_CODE_REMOTE_SERVER_ERROR);
                }


            } else {
                resultMap.put("RCode", RESPONSE_CODE_REMOTE_SERVER_ERROR);
            }


        } else {
            resultMap.put("RCode", RESPONSE_CODE_EXIST);
        }
        return resultMap;
    }

    /**
     * 查验历史列表
     *
     * @param params
     * @return
     */
    @Override
    public PagedQueryResult<InvoiceCheckModel> getInvoiceCheckHistoryList(String schemaLabel, Map<String, Object> params) {
        LOGGER.info("查验历史列表,params:{}", params);

        final PagedQueryResult<InvoiceCheckModel> result = new PagedQueryResult<>();
        List<InvoiceCheckModel> resultList = newArrayList();
        final ReportStatisticsEntity r = invoiceCheckMapper.getInvoiceCheckHistoryListCount(schemaLabel, params);
        if (r.getTotalCount() > 0) {
            resultList = invoiceCheckMapper.getInvoiceCheckHistoryList(schemaLabel, params);
        }
        result.setTotalCount(r.getTotalCount());
        result.setResults(resultList);
        result.setTotalAmount(r.getTotalAmount());
        result.setTotalTax(r.getTotalTax());
        return result;
    }

    /**
     * 查验历史详情
     *
     * @param params
     * @return
     */
    @Override
    public PagedQueryResult<InvoiceCheckModel> getInvoiceCheckHistoryDetail(String schemaLabel, Map<String, Object> params) {
        LOGGER.info("查验历史详情,params:{}", params);
        final PagedQueryResult<InvoiceCheckModel> result = new PagedQueryResult<>();
        List<InvoiceCheckModel> resultList = newArrayList();
        final Integer count = invoiceCheckMapper.getInvoiceCheckHistoryDetailCount(schemaLabel, params);
        if (count > 0) {
            resultList = invoiceCheckMapper.getInvoiceCheckHistoryDetail(schemaLabel, params);
        }
        result.setResults(resultList);
        result.setTotalCount(count);
        return result;
    }

    /**
     * 查验历史删除
     *
     * @param params
     * @return
     */
    @Override
    public Boolean getInvoiceCheckHistoryDelete(String schemaLabel, Map<String, Object> params) {
        LOGGER.info("查验历史删除,params:{}", params);
        invoiceCheckMapper.getInvoiceCheckHistoryDelete(schemaLabel, params);
        return TRUE;
    }

    /**
     * 查验统计
     *
     * @param params
     * @return
     */
    @Override
    public PagedQueryResult<Map<String, Object>> getInvoiceStatistics(String schemaLabel, Map<String, Object> params) {
        LOGGER.info("查验统计,params:{}", params);

        final PagedQueryResult<Map<String, Object>> result = new PagedQueryResult<>();
        List<Map<String, Object>> resultList = newArrayList();
        final Integer count = invoiceCheckMapper.getInvoiceStatisticsCount(schemaLabel, params);
        if (count > 0) {
            resultList = invoiceCheckMapper.getInvoiceStatistics(schemaLabel, params);
            resultList.forEach(map -> {
                final Integer cgCount = invoiceCheckMapper.getInvoiceStatisticsCountByMonth(schemaLabel, map.get("checkDate").toString(), String.valueOf(params.get("userAccount")));
                map.put("cgCount", String.valueOf(cgCount));
                map.put("sbCount", String.valueOf(Integer.valueOf(map.get("totalCount").toString()) - cgCount));
            });
        }
        result.setTotalCount(count);
        result.setResults(resultList);
        return result;
    }

    @Override
    public Boolean deleteCheckInvoice(String schemaLabel, String uuid) {
        return invoiceCheckMapper.deleteCheckInvoice(schemaLabel, uuid) > 0;
    }


    /**
     * 查验成功-历史新增
     */

    private InvoiceCheckModel assignInvoiceCheckHistoryAddData(ResponseInvoice responseInvoice, String currentUser) {

        InvoiceCheckModel invoiceCheckHistoryModel = new InvoiceCheckModel();

        invoiceCheckHistoryModel.setInvoiceCode(responseInvoice.getInvoiceCode());
        invoiceCheckHistoryModel.setInvoiceNo(responseInvoice.getInvoiceNo());
        invoiceCheckHistoryModel.setHandleDate(getCurrentDate());
        invoiceCheckHistoryModel.setCheckMassege(responseInvoice.getResultTip());
        invoiceCheckHistoryModel.setCheckUser(currentUser);
        //日期
        invoiceCheckHistoryModel.setInvoiceDate(responseInvoice.getInvoiceDate());
        invoiceCheckHistoryModel.setBuyerName(responseInvoice.getBuyerName());
        invoiceCheckHistoryModel.setTotalAmount(Double.valueOf(responseInvoice.getTotalAmount()));
        invoiceCheckHistoryModel.setInvoiceAmount(Double.valueOf(responseInvoice.getInvoiceAmount()));
        invoiceCheckHistoryModel.setTaxAmount(Double.valueOf(responseInvoice.getTaxAmount()));
        invoiceCheckHistoryModel.setInvoiceType(responseInvoice.getInvoiceType());
        invoiceCheckHistoryModel.setHandleCode(responseInvoice.getResultCode());
        invoiceCheckHistoryModel.setCheckCode(responseInvoice.getCheckCode());
        return invoiceCheckHistoryModel;
    }

    /**
     * 查验成功主表新增
     */

    private InvoiceCheckMainModel assignInvoiceCheckMainAddData(ResponseInvoice responseInvoice) {
        InvoiceCheckMainModel invoiceCheckMainModel = new InvoiceCheckMainModel();

        invoiceCheckMainModel.setCyjgCode(responseInvoice.getResultCode());
        invoiceCheckMainModel.setCyjgTip(responseInvoice.getResultTip());
        invoiceCheckMainModel.setFpzl(responseInvoice.getInvoiceType());
        invoiceCheckMainModel.setFpdm(responseInvoice.getInvoiceCode());
        invoiceCheckMainModel.setFphm(responseInvoice.getInvoiceNo());
        invoiceCheckMainModel.setCycs(responseInvoice.getCheckCount());
        invoiceCheckMainModel.setXfsh(responseInvoice.getSalerTaxNo());
        invoiceCheckMainModel.setXfmc(responseInvoice.getSalerName());
        invoiceCheckMainModel.setXfdzdh(responseInvoice.getSalerAddressPhone());
        invoiceCheckMainModel.setXfyhzh(responseInvoice.getSalerAccount());
        invoiceCheckMainModel.setGfsh(responseInvoice.getBuyerTaxNo());
        invoiceCheckMainModel.setGfmc(responseInvoice.getBuyerName());
        invoiceCheckMainModel.setGfdzdh(responseInvoice.getBuyerAddressPhone());
        invoiceCheckMainModel.setGfyhzh(responseInvoice.getBuyerAccount());
        //日期
        invoiceCheckMainModel.setKprq(responseInvoice.getInvoiceDate());
        invoiceCheckMainModel.setJe(responseInvoice.getInvoiceAmount());
        invoiceCheckMainModel.setSe(responseInvoice.getTaxAmount());
        invoiceCheckMainModel.setJshj(responseInvoice.getTotalAmount());
        invoiceCheckMainModel.setBz(responseInvoice.getRemark());
        invoiceCheckMainModel.setJqbh(responseInvoice.getMachineNo());
        invoiceCheckMainModel.setZfbz(responseInvoice.getIsCancelled());
        invoiceCheckMainModel.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        invoiceCheckMainModel.setCreateDate(getCurrentDate());
        invoiceCheckMainModel.setCyrmc(responseInvoice.getCarrierTaxName());
        invoiceCheckMainModel.setCyrsbh(responseInvoice.getCarrierTaxNum());
        invoiceCheckMainModel.setSpfmc(responseInvoice.getAcceptTaxName());
        invoiceCheckMainModel.setSpfsbh(responseInvoice.getAcceptTaxNum());
        invoiceCheckMainModel.setShrmc(responseInvoice.getReceiverName());
        invoiceCheckMainModel.setShrsbh(responseInvoice.getReceiverTaxNum());
        invoiceCheckMainModel.setFhrmc(responseInvoice.getShipperName());
        invoiceCheckMainModel.setFhrsbh(responseInvoice.getShipperTaxNum());
        invoiceCheckMainModel.setQyd(responseInvoice.getWayInfo());
        invoiceCheckMainModel.setYshwxx(responseInvoice.getTransportInfo());
        invoiceCheckMainModel.setCzch(responseInvoice.getVehicleNum());
        invoiceCheckMainModel.setCcdw(responseInvoice.getVehicleTonnage());
        invoiceCheckMainModel.setSfzhm(responseInvoice.getBuyerIDNum());
        invoiceCheckMainModel.setCllx(responseInvoice.getVehicleType());
        invoiceCheckMainModel.setCpxh(responseInvoice.getFactoryModel());
        invoiceCheckMainModel.setCd(responseInvoice.getProductPlace());
        invoiceCheckMainModel.setHgzs(responseInvoice.getCertificate());
        invoiceCheckMainModel.setJkzmsh(responseInvoice.getCertificateImport());
        invoiceCheckMainModel.setSjdh(responseInvoice.getInspectionNum());
        invoiceCheckMainModel.setFdjhm(responseInvoice.getEngineNo());
        invoiceCheckMainModel.setCjhm(responseInvoice.getVehicleNo());
        invoiceCheckMainModel.setJym(responseInvoice.getCheckCode());

        return invoiceCheckMainModel;
    }


    /**
     * 查验成功明细表(mycat不支持多行插入，只好改单个了)
     */
    private Boolean assignInvoiceCheckDetailAddData(String schemaLabel, ResponseInvoice responseInvoice) {
        Boolean result;

        InvoiceCheckDetailModel invoiceCheckDetailModel;
        final List<InvoiceDetail> detailList = responseInvoice.getDetailList();
        if (detailList != null && detailList.size() > 0) {
            for (InvoiceDetail detail : responseInvoice.getDetailList()) {
                invoiceCheckDetailModel = new InvoiceCheckDetailModel();
                invoiceCheckDetailModel.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
                invoiceCheckDetailModel.setDetailNo(detail.getDetailNo());
                invoiceCheckDetailModel.setGoodsName(detail.getGoodsName());
                invoiceCheckDetailModel.setModel(detail.getSpecificationModel());
                invoiceCheckDetailModel.setUnit(detail.getUnit());
                invoiceCheckDetailModel.setNum(detail.getNum());
                invoiceCheckDetailModel.setUnitPrice(detail.getUnitPrice());
                invoiceCheckDetailModel.setDetailAmount(detail.getDetailAmount());
                invoiceCheckDetailModel.setTaxRate(detail.getTaxRate());
                invoiceCheckDetailModel.setTaxAmount(detail.getTaxAmount());
                invoiceCheckDetailModel.setCph(detail.getCph());
                invoiceCheckDetailModel.setLx(detail.getLx());
                invoiceCheckDetailModel.setTxrqq(detail.getTxrqq());
                invoiceCheckDetailModel.setTxrqz(detail.getTxrqz());
                invoiceCheckDetailModel.setGoodsNum(detail.getNum());

                result = invoiceCheckMapper.doInvoiceCheckDetailAdd(schemaLabel, invoiceCheckDetailModel) > 0;

                if (!result) {
                    return Boolean.FALSE;
                }
            }
        }
        return true;
    }

    /**
     * 查验成功03明细表
     */
    private InvoiceCheckVehicleDetailModel assignInvoiceCheckVehicleDetailAddData(ResponseInvoice responseInvoice) {
        InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel;

        invoiceCheckVehicleDetailModel = new InvoiceCheckVehicleDetailModel();
        invoiceCheckVehicleDetailModel.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        invoiceCheckVehicleDetailModel.setBuyerIdNum(responseInvoice.getBuyerIDNum());
        invoiceCheckVehicleDetailModel.setVehicleType(responseInvoice.getVehicleType());
        invoiceCheckVehicleDetailModel.setFactoryModel(responseInvoice.getFactoryModel());
        invoiceCheckVehicleDetailModel.setProductPlace(responseInvoice.getProductPlace());
        invoiceCheckVehicleDetailModel.setCertificate(responseInvoice.getCertificate());
        invoiceCheckVehicleDetailModel.setCertificateImport(responseInvoice.getCertificateImport());
        invoiceCheckVehicleDetailModel.setInspectionNum(responseInvoice.getInspectionNum());
        invoiceCheckVehicleDetailModel.setEngineNo(responseInvoice.getEngineNo());
        invoiceCheckVehicleDetailModel.setVehicleNo(responseInvoice.getVehicleNo());
        invoiceCheckVehicleDetailModel.setTaxRate(responseInvoice.getTaxRate());
        invoiceCheckVehicleDetailModel.setTaxBureauName(responseInvoice.getTaxBureauName());
        invoiceCheckVehicleDetailModel.setTaxBureauCode(responseInvoice.getTaxBureauCode());
        invoiceCheckVehicleDetailModel.setTaxRecords(responseInvoice.getTaxRecords());
        invoiceCheckVehicleDetailModel.setLimitPeople(responseInvoice.getLimitPeople());
        invoiceCheckVehicleDetailModel.setTonnage(responseInvoice.getTonnage());
        invoiceCheckVehicleDetailModel.setCreateDate(getCurrentDate());
        return invoiceCheckVehicleDetailModel;
    }

    /**
     * 查验失败历史新增-赋值
     */

    private InvoiceCheckModel assignInvoiceCheckFailHistoryAddData(ResponseInvoice responseInvoice,  String currentUser) {
        InvoiceCheckModel invoiceCheckModel = new InvoiceCheckModel();
        invoiceCheckModel.setCheckUser(currentUser);
        invoiceCheckModel.setInvoiceType(responseInvoice.getInvoiceType());
        invoiceCheckModel.setCheckMassege(responseInvoice.getResultTip());
        invoiceCheckModel.setInvoiceCode(responseInvoice.getInvoiceCode());
        invoiceCheckModel.setInvoiceNo(responseInvoice.getInvoiceNo());
        if (INVOICE_TYPE.contains(responseInvoice.getInvoiceType())) {
            invoiceCheckModel.setCheckCode(responseInvoice.getCheckCode());
        }
        invoiceCheckModel.setInvoiceAmount(Double.valueOf(responseInvoice.getInvoiceAmount() != null ? responseInvoice.getInvoiceAmount() : "0"));
        invoiceCheckModel.setInvoiceDate(responseInvoice.getInvoiceDate());
        invoiceCheckModel.setHandleDate(getCurrentDate());
        invoiceCheckModel.setHandleCode(responseInvoice.getResultCode());
        return invoiceCheckModel;
    }


    /**
     * 获取当前时间
     */
    private String getCurrentDate() {
//        final DateTime nowDate = new DateTime();
//        return nowDate.toString("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat abcv=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return abcv.format(new Date());
    }


    /**
     * 参数初始化
     */

    private RequestData initRequestData(Map<String, Object> params) {
        final RequestData requestData = new RequestData();
         /*购方税号*/
        requestData.setBuyerTaxNo("");
               /*发票类型*/
        requestData.setInvoiceType(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))));
                  /* 发票代码*/
        requestData.setInvoiceCode(valueOf(params.get("invoiceCode")));
        /*发票号码*/
        requestData.setInvoiceNo(valueOf(params.get("invoiceNo")));
        /*开票日期*/
        requestData.setInvoiceDate(valueOf(params.get("invoiceDate")));

        if (INVOICE_TYPE.contains(CommonUtil.getFplx(valueOf(params.get("invoiceCode"))))) {
            requestData.setCheckCode(valueOf((params.get("invoiceAmount"))));

        } else {
            requestData.setInvoiceAmount(valueOf(params.get("invoiceAmount")));
        }


        return requestData;
    }

    /**
     * 时间格式化处理，返回：YYYY-DD-MM 格式
     *
     * @param source
     * @return
     */
    public static String formateDates(final Date source) {
        SimpleDateFormat abcv=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return source == null ? "" : abcv.format(source);
    }

    /**
     * 时间格式化处理，返回：YYYY-DD-MM 格式
     *
     * @param str
     * @return
     */
    public static java.util.Date getDateType(String str) {
        if (str == null) {
            return null;
        }
        java.util.Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-DD-MM");
        try {
            date = new java.util.Date();
            date = dateFormat.parse(str);
        } catch (Exception e) {

        }
        return date;
    }

    @Override
    public Map<String, Object> importEnjoySubsided(String schemaLabel, Long userId, MultipartFile file) {
        final InvoiceCheckImport invoiceCheckImport = new InvoiceCheckImport(file);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<ImportCertificationEntity> certificationEntityList = invoiceCheckImport.analysisExcel();
            if (!certificationEntityList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<ImportCertificationEntity>> entityMap = checkImportData(certificationEntityList);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<ImportCertificationEntity>> checkImportData(List<ImportCertificationEntity> certificationEntityList) {
        //返回值
        final Map<String, List<ImportCertificationEntity>> map = newHashMap();
        //导入成功的数据集
        final List<ImportCertificationEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<ImportCertificationEntity> errorEntityList = newArrayList();

        certificationEntityList.forEach(importCertificationEntity -> {
            String invoiceCode = importCertificationEntity.getInvoiceCode();
            String invoiceNo = importCertificationEntity.getInvoiceNo();
            String invoiceDate = importCertificationEntity.getInvoiceDate();

            if (!invoiceCode.isEmpty() && !invoiceNo.isEmpty() && !invoiceDate.isEmpty()) {
                successEntityList.add(importCertificationEntity);
            } else {
                errorEntityList.add(importCertificationEntity);
            }
        });
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }
}
