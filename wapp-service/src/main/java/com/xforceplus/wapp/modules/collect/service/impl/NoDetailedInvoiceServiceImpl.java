package com.xforceplus.wapp.modules.collect.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.ReturnInfoEnum;
import com.xforceplus.wapp.config.SystemConfig;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import com.xforceplus.wapp.modules.collect.dao.NoDetailedInvoiceDao;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.entity.NoDetailInvoiceExcelEntity;
import com.xforceplus.wapp.modules.collect.entity.RecordInvoiceStatistics;
import com.xforceplus.wapp.modules.collect.pojo.InvoiceDetail;
import com.xforceplus.wapp.modules.collect.pojo.RequestData;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.collect.service.NoDetailedInvoiceService;
import com.xforceplus.wapp.modules.job.dao.RecordInvoiceStatisticsDao;
import com.xforceplus.wapp.modules.job.dao.TDxRecordInvoiceDetailDao;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoiceStatistics;
import com.xforceplus.wapp.modules.signin.enumflord.InvoiceTypeEnum;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.Constant.DETAIL_YES_OR_NO;
import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMap;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 未补明细发票业务层实现
 *
 * @author Colin.hu
 * @date 4/11/2018
 */
@Service
@Transactional
public class NoDetailedInvoiceServiceImpl implements NoDetailedInvoiceService {

    private final NoDetailedInvoiceDao noDetailedInvoiceDao;


    private final SystemConfig systemConfig;

    private final TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao;

    private RecordInvoiceStatisticsDao recordInvoiceStatisticsDao;

    @Autowired
    public NoDetailedInvoiceServiceImpl(NoDetailedInvoiceDao noDetailedInvoiceDao,  SystemConfig systemConfig,TDxRecordInvoiceDetailDao tDxRecordInvoiceDetailDao,RecordInvoiceStatisticsDao recordInvoiceStatisticsDao) {
        this.noDetailedInvoiceDao = noDetailedInvoiceDao;
        this.systemConfig = systemConfig;
        this.tDxRecordInvoiceDetailDao=tDxRecordInvoiceDetailDao;
        this.recordInvoiceStatisticsDao = recordInvoiceStatisticsDao;
    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> selectNoDetailedInvoice(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = noDetailedInvoiceDao.getNoDetailInvoiceCount(map);

        //需要返回的集合
        List<InvoiceCollectionInfo> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = noDetailedInvoiceDao.selectNoDetailedInvoice(map);
            final Map<String, BigDecimal> totalMap = noDetailedInvoiceDao.getNoDetailSumAmount(map);
            pagedQueryResult.setSummationTotalAmount(totalMap.get("summationTotalAmount"));
            pagedQueryResult.setSummationTaxAmount(totalMap.get("summationTaxAmount"));
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public Map<String, String> manualInspection(Map<String, String> params) {
        final String schemaLabel = params.get("schemaLabel");
        //定义返回值
        final Map<String, String> map = Maps.newHashMapWithExpectedSize(2);

        //构建外不请求值
        final RequestData requestData = buildRequestData(params);

        //调用接口验证
        final ResponseInvoice responseInvoice = null;

        //判断接口返回值是否为空 如果为空则直接返回，业务错误信息
        if (responseInvoice == null) {
            map.put("resultCode", ReturnInfoEnum.SYS_ERROR.getResultCode());
            map.put("resultTip", ReturnInfoEnum.SYS_ERROR.getResultTip());
            return map;
        }
        //如果不为空，但返回code不是0001 则查验不一致，直接返回错误信息
        if (!ReturnInfoEnum.CHECK_SUCCESS.getResultCode().equals(responseInvoice.getResultCode())) {
            map.put("resultCode", responseInvoice.getResultCode());
            map.put("resultTip", responseInvoice.getResultTip());
            return map;
        }
        //如果等于0001 则查验成功，获取明细 做数据库操作
        final List<InvoiceDetail> detailList = responseInvoice.getDetailList();

        //如果不为机动车统计销售发票
        if (!InvoiceTypeEnum.MOTOR_INVOICE.getResultCode().equals(responseInvoice.getInvoiceType())) {
            //如果明细不为空则保存明细,保存明细统计，更新主表
            final List<InvoiceDetailInfo> invoiceDetailInfoList = buildDetailList(responseInvoice);
            if (detailList != null && detailList.size() > 0) {
                //保存明细表    之前先删除
                for (InvoiceDetailInfo invoiceDetail : invoiceDetailInfoList) {
                    noDetailedInvoiceDao.deleteDetail(schemaLabel, invoiceDetail);
                }
            }
            //构建抵账表发票明细

                noDetailedInvoiceDao.insertNoDetailedInvoice(schemaLabel, invoiceDetailInfoList);

                //构建统计表发票明细
                final List<RecordInvoiceStatistics> recordInvoiceStatisticsList = buildRecordInvoiceStatisticsList(responseInvoice);
                if (recordInvoiceStatisticsList.size() > 0) {
                    //保存抵账统计明细
                    for(RecordInvoiceStatistics recordInvoiceStatistics: recordInvoiceStatisticsList){
                        tDxRecordInvoiceDetailDao.delDetailTaxRate(recordInvoiceStatistics.getInvoiceCode(),recordInvoiceStatistics.getInvoiceNo(),schemaLabel);
                    }

                }
            for(RecordInvoiceStatistics recordInvoiceStatistics: recordInvoiceStatisticsList){
                Integer integer = noDetailedInvoiceDao.selectStatisticsList(recordInvoiceStatistics);
                if(integer>0){
                        noDetailedInvoiceDao.updateStatisticsList(recordInvoiceStatistics);
                }else {
                    noDetailedInvoiceDao.insertStatisticsList(recordInvoiceStatistics);
                }
            }

            //构建主表实体
                InvoiceCollectionInfo invoiceCollectionInfo = buildInvoiceCollectionInfo(responseInvoice, params.get("buyerTaxNo"));
                invoiceCollectionInfo.setSchemaLabel(schemaLabel);

                int countRate = tDxRecordInvoiceDetailDao.getTaxRate(invoiceCollectionInfo.getInvoiceCode(),invoiceCollectionInfo.getInvoiceNo(),schemaLabel);
                int countdetail = tDxRecordInvoiceDetailDao.getDetail(invoiceCollectionInfo.getInvoiceCode()+invoiceCollectionInfo.getInvoiceNo(),schemaLabel);
                //更新主表
                if(countRate>0&&countdetail>0) {
                    invoiceCollectionInfo.setDetailYesorno("1");
                }else{
                    invoiceCollectionInfo.setDetailYesorno("0");
                }
                noDetailedInvoiceDao.updateRecordInvoice(invoiceCollectionInfo);
        } else {
            //机动车统一销售发票  构建机动车统一销售发票明细
            List<InvoiceCheckVehicleDetailModel> vehicleDetailList = buildVehicleDetailList(responseInvoice);
            if (vehicleDetailList.size() > 0) {
                for (InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel:vehicleDetailList) {
                    tDxRecordInvoiceDetailDao.delDetailJd(invoiceCheckVehicleDetailModel.getUuid(),schemaLabel);

                }

            }
//            noDetailedInvoiceDao.insertVehicleDetailList(schemaLabel, vehicleDetailList);
            for(InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel: vehicleDetailList){
                Integer integer = noDetailedInvoiceDao.selectVehicleDetailList(invoiceCheckVehicleDetailModel);
                if(integer>0){
                    noDetailedInvoiceDao.updateVehicleDetailList(invoiceCheckVehicleDetailModel);
                }else {
                    noDetailedInvoiceDao.insertVehicleDetailList(invoiceCheckVehicleDetailModel);
                }
            }
            //构建统计表发票明细
//            final List<RecordInvoiceStatistics> recordInvoiceStatisticsList = buildRecordInvoiceStatisticsList(responseInvoice);
//            if (recordInvoiceStatisticsList.size() > 0) {
//                for(RecordInvoiceStatistics recordInvoiceStatistics: recordInvoiceStatisticsList){
                    tDxRecordInvoiceDetailDao.delDetailTaxRate(responseInvoice.getInvoiceCode(),responseInvoice.getInvoiceNo(),schemaLabel);
                    //保存抵账统计明细
                    //设置税额统计表字段值
                    TDxRecordInvoiceStatistics tDxRecordInvoiceStatistics = new TDxRecordInvoiceStatistics();
                    tDxRecordInvoiceStatistics.setInvoiceCode(responseInvoice.getInvoiceCode());
                    tDxRecordInvoiceStatistics.setInvoiceNo(responseInvoice.getInvoiceNo());
                    tDxRecordInvoiceStatistics.setDetailAmount(new BigDecimal(responseInvoice.getInvoiceAmount()));
                    if(responseInvoice.getTaxRate().contains("%")){
                        responseInvoice.setTaxRate(responseInvoice.getTaxRate().replace("%",""));
                    }
                    tDxRecordInvoiceStatistics.setTaxRate(new BigDecimal(responseInvoice.getTaxRate()));
                    tDxRecordInvoiceStatistics.setTaxAmount(new BigDecimal(responseInvoice.getTaxAmount()));
                    tDxRecordInvoiceStatistics.setTotalAmount(new BigDecimal(responseInvoice.getTotalAmount()));
                    recordInvoiceStatisticsDao.saveStatistics(tDxRecordInvoiceStatistics,schemaLabel);
//                }



//            }

//            noDetailedInvoiceDao.insertStatisticsList(schemaLabel, recordInvoiceStatisticsList);
            //构建主表实体
            InvoiceCollectionInfo invoiceCollectionInfo = buildInvoiceCollectionInfo(responseInvoice, params.get("buyerTaxNo"));
            invoiceCollectionInfo.setSchemaLabel(schemaLabel);

            int countRate = tDxRecordInvoiceDetailDao.getTaxRate(invoiceCollectionInfo.getInvoiceCode(),invoiceCollectionInfo.getInvoiceNo(),schemaLabel);
            int countdetail = tDxRecordInvoiceDetailDao.getDetailJd(invoiceCollectionInfo.getInvoiceCode()+invoiceCollectionInfo.getInvoiceNo(),schemaLabel);
            //更新主表
            if(countRate>0&&countdetail>0) {
                invoiceCollectionInfo.setDetailYesorno("1");
            }else{
                invoiceCollectionInfo.setDetailYesorno("0");
            }
            //更新主表
            noDetailedInvoiceDao.updateRecordInvoice(invoiceCollectionInfo);
        }

        map.put("resultCode", responseInvoice.getResultCode());
        map.put("resultTip", responseInvoice.getResultTip());
        return map;
    }

    @Override
    public List<Map<String, String>> getParamMapByType(Map<String, String> params) {
        return noDetailedInvoiceDao.getParamMapByType(params);
    }

    @Override
    public Boolean inspectionProcess(String schemaLabel, List<ResponseInvoice> responseInvoiceList) {
        //导入excel时所有需要保存主表数据
        final List<InvoiceCollectionInfo> infoList = newArrayList();
        //所有需要保存的明细数据
        final List<InvoiceDetailInfo> inspectionDetailInfoList = newArrayList();
        //所有需要保存的统计数据
        final List<RecordInvoiceStatistics> inspectionStatisticsList = newArrayList();
        //机动车发票明细
        final List<InvoiceCheckVehicleDetailModel> inspectionVehicleDetailList = newArrayList();
        //遍历
        responseInvoiceList.forEach(responseInvoice -> {
            //如果为机动车发票
            if (InvoiceTypeEnum.MOTOR_INVOICE.getResultCode().equals(responseInvoice.getInvoiceType())) {
                //机动车统一销售发票  构建机动车统一销售发票明细
                final List<InvoiceCheckVehicleDetailModel> vehicleDetailList = buildVehicleDetailList(responseInvoice);
                //集合合并
                inspectionVehicleDetailList.addAll(vehicleDetailList);
            } else {
                //构建抵账表发票明细
                final List<InvoiceDetailInfo> invoiceDetailInfoList = buildDetailList(responseInvoice);
                //集合合并
                inspectionDetailInfoList.addAll(invoiceDetailInfoList);
            }
            //构建统计表发票明细
            final List<RecordInvoiceStatistics> recordInvoiceStatisticsList = buildRecordInvoiceStatisticsList(responseInvoice);
            //集合合并
            inspectionStatisticsList.addAll(recordInvoiceStatisticsList);
            //构建主表实体
            InvoiceCollectionInfo invoiceCollectionInfo = buildInvoiceCollectionInfo(responseInvoice, "");
            //添加集合
            infoList.add(invoiceCollectionInfo);
        });

        if(inspectionDetailInfoList.size() > 0) {
            //保存明细表
            noDetailedInvoiceDao.insertNoDetailedInvoice(schemaLabel, inspectionDetailInfoList);
        }

        if (inspectionStatisticsList.size() > 0) {
            //保存抵账统计明细
            for(RecordInvoiceStatistics recordInvoiceStatistics: inspectionStatisticsList){
                Integer integer = noDetailedInvoiceDao.selectStatisticsList(recordInvoiceStatistics);
                if(integer>0){
                    noDetailedInvoiceDao.updateStatisticsList(recordInvoiceStatistics);
                }else {
                    noDetailedInvoiceDao.insertStatisticsList(recordInvoiceStatistics);
                }
            }
            //noDetailedInvoiceDao.insertStatisticsList(schemaLabel, inspectionStatisticsList);
        }

        //保存机动车明细
        if (inspectionVehicleDetailList.size() > 0) {
            for(InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel: inspectionVehicleDetailList){
                Integer integer = noDetailedInvoiceDao.selectVehicleDetailList(invoiceCheckVehicleDetailModel);
                if(integer>0){
                    noDetailedInvoiceDao.updateVehicleDetailList(invoiceCheckVehicleDetailModel);
                }else {
                    noDetailedInvoiceDao.insertVehicleDetailList(invoiceCheckVehicleDetailModel);
                }
            }
//            noDetailedInvoiceDao.insertVehicleDetailList(schemaLabel, inspectionVehicleDetailList);

        }

        //保存主表
        noDetailedInvoiceDao.insertRecordInvoice(schemaLabel, infoList);
        return Boolean.TRUE;
    }

    private RequestData buildRequestData(Map<String, String> params) {
        //定义请求数据
        final RequestData requestData = new RequestData();

        return requestData;
    }

    /**
     * 构建抵账表发票明细
     *
     * @param responseInvoice 响应实体
     * @return 抵账表发票明细
     */
    private List<InvoiceDetailInfo> buildDetailList(ResponseInvoice responseInvoice) {
        //构建返回值
        final List<InvoiceDetailInfo> invoiceDetailInfoList = newArrayList();

        //明细
        final List<InvoiceDetail> invoiceDetailList = responseInvoice.getDetailList();

        invoiceDetailList.forEach(invoiceDetail -> {
            final InvoiceDetailInfo invoiceDetailInfo = new InvoiceDetailInfo();
            //税额
            invoiceDetailInfo.setTaxAmount(invoiceDetail.getTaxAmount());
            //货物或应税劳务名称
            invoiceDetailInfo.setGoodsName(invoiceDetail.getGoodsName());
            //发票号码
            invoiceDetailInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            invoiceDetailInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
            //数量
            invoiceDetailInfo.setNum(invoiceDetail.getNum());
            //明细序号
            invoiceDetailInfo.setDetailNo(invoiceDetail.getDetailNo());
            //单价
            invoiceDetailInfo.setUnitPrice(invoiceDetail.getUnitPrice());
            //类型
            invoiceDetailInfo.setLx(invoiceDetail.getLx());
            //uuid唯一标识(发票代码+发票号码)
            invoiceDetailInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
            //通行日期起
            invoiceDetailInfo.setTxrqq(invoiceDetail.getTxrqq());
            //通行日期止
            invoiceDetailInfo.setTxrqz(invoiceDetail.getTxrqz());
            //税率
            invoiceDetailInfo.setTaxRate(invoiceDetail.getTaxRate());
            //单位
            invoiceDetailInfo.setUnit(invoiceDetail.getUnit());
            //金额
            invoiceDetailInfo.setDetailAmount(invoiceDetail.getDetailAmount());
            //规格型号
            invoiceDetailInfo.setModel(invoiceDetail.getSpecificationModel());
            //车牌号
            invoiceDetailInfo.setCph(invoiceDetail.getCph());
            //放入集合
            invoiceDetailInfoList.add(invoiceDetailInfo);
        });
        return invoiceDetailInfoList;
    }

    /**
     * 构建统计实体集
     *
     * @param responseInvoice 响应实体
     * @return 抵账统计实体
     */
    private List<RecordInvoiceStatistics> buildRecordInvoiceStatisticsList(ResponseInvoice responseInvoice) {
        //定义返回值
        final List<RecordInvoiceStatistics> recordInvoiceStatisticsList = newArrayList();

        //明细
        List<InvoiceDetail> invoiceDetailList;

        //如果为机动车统计销售发票
        if (InvoiceTypeEnum.MOTOR_INVOICE.getResultCode().equals(responseInvoice.getInvoiceType())) {
            invoiceDetailList = motorVehicleDetail(responseInvoice);
        } else {
            //不为机动车统计销售发票
            invoiceDetailList = responseInvoice.getDetailList();
        }

        // 最终要的结果
        final Map<String, List<InvoiceDetail>> resultMap = newHashMap();
        //按税率分组
        invoiceDetailList.forEach(dataItem -> {
            if (!dataItem.getGoodsName().equals("原价合计") && !dataItem.getGoodsName().equals("折扣额合计")&& !dataItem.getGoodsName().equals("(详见销货清单)")&&StringUtils.isNotEmpty(dataItem.getTaxRate())) {
                if (resultMap.containsKey(dataItem.getTaxRate())) {
                    resultMap.get(dataItem.getTaxRate()).add(dataItem);
                } else {
                    final List<InvoiceDetail> list = newArrayList();
                    list.add(dataItem);
                    resultMap.put(dataItem.getTaxRate(), list);
                }
            }
        });

        //循环遍历map获取每组明细，并循环遍历每组里的明细组装抵账统计数据
        for (Map.Entry<String, List<InvoiceDetail>> entry : resultMap.entrySet()) {
            //获取每组明细
            final List<InvoiceDetail> detailList = entry.getValue();
            //定义抵账统计实体
            final RecordInvoiceStatistics recordInvoiceStatistics = new RecordInvoiceStatistics();
            //税额
            Double taxAmount = 0.0;
            //金额
            Double detailAmount = 0.0;
            //循环赋值
            for (InvoiceDetail invoiceDetail : detailList) {
                    taxAmount = taxAmount + Double.valueOf(invoiceDetail.getTaxAmount());
                    detailAmount = detailAmount + Double.valueOf(invoiceDetail.getDetailAmount());

            }
            //价税合计
            final Double totalAmount = taxAmount + detailAmount;
            //税率
            recordInvoiceStatistics.setTaxRate(Double.valueOf(entry.getKey()));
            //发票号码
            recordInvoiceStatistics.setInvoiceNo(responseInvoice.getInvoiceNo());
            //发票代码
            recordInvoiceStatistics.setInvoiceCode(responseInvoice.getInvoiceCode());
            //税额
            recordInvoiceStatistics.setTaxRate(taxAmount);
            //金额
            recordInvoiceStatistics.setDetailAmount(detailAmount);
            //价税合计
            recordInvoiceStatistics.setTotalAmount(totalAmount);
            //放入集合
            recordInvoiceStatisticsList.add(recordInvoiceStatistics);
        }
        //返回
        return recordInvoiceStatisticsList;
    }

    /**
     * 构建抵账主体数据
     *
     * @param responseInvoice 抵账主体数据
     * @return 主体数据
     */
    private InvoiceCollectionInfo buildInvoiceCollectionInfo(ResponseInvoice responseInvoice, String buyerTaxNo) {
        //定义返回值
        final InvoiceCollectionInfo invoiceCollectionInfo = new InvoiceCollectionInfo();
        //购方税号
        invoiceCollectionInfo.setGfTaxNo(responseInvoice.getBuyerTaxNo());
        //购方名称
        invoiceCollectionInfo.setGfName(responseInvoice.getBuyerName());
        //发票类型
        invoiceCollectionInfo.setInvoiceType(responseInvoice.getInvoiceType());
        //发票代码
        invoiceCollectionInfo.setInvoiceCode(responseInvoice.getInvoiceCode());
        //发票号码
        invoiceCollectionInfo.setInvoiceNo(responseInvoice.getInvoiceNo());
        //价税合计
        invoiceCollectionInfo.setTotalAmount(responseInvoice.getTotalAmount());
        //金额
        invoiceCollectionInfo.setInvoiceAmount(responseInvoice.getInvoiceAmount());
        //税额
        invoiceCollectionInfo.setTaxAmount(responseInvoice.getTaxAmount());
        //销方税号
        invoiceCollectionInfo.setXfTaxNo(responseInvoice.getSalerTaxNo());
        //销方名称
        invoiceCollectionInfo.setXfName(responseInvoice.getSalerName());
        //销方地址
        invoiceCollectionInfo.setXfAddressAndPhone(responseInvoice.getSalerAddressPhone());
        //销方银行帐号
        invoiceCollectionInfo.setXfBankAndNo(responseInvoice.getSalerAccount());
        //购方银行帐号
        invoiceCollectionInfo.setGfBankAndNo(responseInvoice.getBuyerAccount());
        //购方地址
        invoiceCollectionInfo.setGfAddressAndPhone(responseInvoice.getBuyerAddressPhone());
        //开票日期
        final DateTime invoiceDateTime = DateTimeFormat.forPattern(SHORT_DATE_FORMAT).parseDateTime(responseInvoice.getInvoiceDate());
        invoiceCollectionInfo.setInvoiceDate(invoiceDateTime.toDate());
        //备注
        invoiceCollectionInfo.setRemark(responseInvoice.getRemark());
        //校验码
        invoiceCollectionInfo.setCheckCode(responseInvoice.getCheckCode());
        //有明细
        invoiceCollectionInfo.setDetailYesorno(DETAIL_YES_OR_NO);
        //uuid
        invoiceCollectionInfo.setUuid(responseInvoice.getInvoiceCode() + responseInvoice.getInvoiceNo());
        //更新前的购方税号，放置不一致
        invoiceCollectionInfo.setBuyerTaxNo(buyerTaxNo);
        //发票状态
        if ("N".equals(responseInvoice.getIsCancelled())) {
            invoiceCollectionInfo.setInvoiceStatus("0");
        } else {
            invoiceCollectionInfo.setInvoiceStatus("2");
        }
        //机器编号
        invoiceCollectionInfo.setMachinecode(responseInvoice.getMachineNo());
        //返回
        return invoiceCollectionInfo;
    }

    /**
     * 机动车统一销售发票明细
     * 这种发票没有detailList 故明细信息需从主体信息中获取
     *
     * @param responseInvoice 查验响应实体
     * @return 机动车统一销售发票明细
     */
    private List<InvoiceDetail> motorVehicleDetail(ResponseInvoice responseInvoice) {
        final List<InvoiceDetail> invoiceDetailList = newArrayList();
        final InvoiceDetail invoiceDetail = new InvoiceDetail();
        //税率
        invoiceDetail.setTaxRate(responseInvoice.getTaxRate());
        //税额
        invoiceDetail.setTaxAmount(responseInvoice.getTaxAmount());
        //金额
        invoiceDetail.setDetailAmount(responseInvoice.getInvoiceAmount());

        invoiceDetailList.add(invoiceDetail);
        return invoiceDetailList;
    }

    /**
     * 构建机动车销售明细集
     *
     * @param responseInvoice 查验响应实体
     * @return 机动车销售明细集
     */
    private List<InvoiceCheckVehicleDetailModel> buildVehicleDetailList(ResponseInvoice responseInvoice) {
        final List<InvoiceCheckVehicleDetailModel> vehicleDetailModelList = newArrayList();
        final InvoiceCheckVehicleDetailModel invoiceCheckVehicleDetailModel = new InvoiceCheckVehicleDetailModel();
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

        vehicleDetailModelList.add(invoiceCheckVehicleDetailModel);
        return vehicleDetailModelList;
    }
    @Override
    public List<NoDetailInvoiceExcelEntity> toExcel(List<InvoiceCollectionInfo> list){
        List<NoDetailInvoiceExcelEntity> list2=new ArrayList<>();
        for (InvoiceCollectionInfo ic:list) {
            NoDetailInvoiceExcelEntity ne=new NoDetailInvoiceExcelEntity();
            ne.setInvoiceCode(ic.getInvoiceCode());
            ne.setInvoiceNo(ic.getInvoiceNo());
            ne.setInvoiceAmount(ic.getInvoiceAmount());
            ne.setGfName(ic.getGfName());
            ne.setXfName(ic.getXfName());
            ne.setCheckCode(ic.getCheckCode());
            ne.setTaxAmount(ic.getTaxAmount());
            ne.setCreateDate(formatDate(ic.getCreateDate()));
            ne.setInvoiceDate(formatDate(ic.getInvoiceDate()));
            list2.add(ne);
        }
        return list2;
    }
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }
}
