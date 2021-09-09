package com.xforceplus.wapp.modules.check.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.check.dao.InvoiceCheckMapper;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckDetailModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckMainModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import com.xforceplus.wapp.modules.check.service.InvoiceCheckModulesService;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.google.common.collect.Maps;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.check.InvoiceCheckConstants.*;
import static com.google.common.collect.Lists.newArrayList;
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


     
       final ResponseInvoice responseInvoice = null;

         resultMap.put("responseInvoice", responseInvoice);
          
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
        final Integer count = invoiceCheckMapper.getInvoiceCheckHistoryListCount(schemaLabel, params);
        if (count > 0) {
            resultList = invoiceCheckMapper.getInvoiceCheckHistoryList(schemaLabel, params);
        }
        result.setTotalCount(count);
        result.setResults(resultList);
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        final DateTime nowDate = new DateTime();
//        return nowDate.toString("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date());
    }


    /**
     * 参数初始化
     */

    private RequestData initRequestData(Map<String, Object> params) {
        final RequestData requestData = new RequestData();
         /*购方税号*/
        requestData.setBuyerTaxNo(systemConfig.getBuyerTaxNo());
       // requestData.setBuyerTaxNo(valueOf(params.get("gfTaxNo")));
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
//        return source == null ? "" : (new DateTime(source.getTime())).toString(FORMATTER);
        return source == null ? "":new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(source);
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
}
