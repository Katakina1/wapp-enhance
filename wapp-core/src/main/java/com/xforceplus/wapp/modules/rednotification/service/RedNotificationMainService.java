package com.xforceplus.wapp.modules.rednotification.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.*;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.listener.ExcelListener;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ExportItemInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.modules.rednotification.util.DownloadUrlUtils;
import com.xforceplus.wapp.modules.rednotification.validator.CheckMainService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfRedNotificationDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemInvoiceDetailDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommSettlementService;
import com.xforceplus.wapp.service.CommonMessageService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

@Service
@Slf4j
@Data
public class RedNotificationMainService extends ServiceImpl<TXfRedNotificationDao, TXfRedNotificationEntity> {

    @Autowired
    RedNotificationMainMapper redNotificationMainMapper;
    @Autowired
    RedNotificationItemService redNotificationItemService;
    @Autowired
    RedNotificationLogService redNotificationLogService;
    @Autowired
    @Lazy
    TaxWareService taxWareService;
    @Autowired
    IDSequence iDSequence;
    @Autowired
    @Lazy
    CommSettlementService commSettlementService;
    @Autowired
    ExportCommonService exportCommonService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    CheckMainService checkMainService;
    @Autowired
    ThreadPoolExecutor redNotificationThreadPool;
    @Autowired
    DownloadUrlUtils downloadUrlUtils;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private DefaultSettingServiceImpl defaultSettingService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private RedNotificationAssistService redNotificationAssistService;
    @Autowired
    private TXfSettlementItemInvoiceDetailDao tXfSettlementItemInvoiceDetailDao;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;

    private static final Integer MAX_PDF_RED_NO_SIZE = 100;

    private static final String GENERATE_PDF_KEY = "generate_pdf_key";
    private static final String APPLY_REDNOTIFICATION_KEY = "apply_rednotification_key";
    private static final String EXPORT_REDNOTIFICATION_KEY = "export_rednotification_key";

    //    @Value("${wapp.rednotification.maxApply}")
    private Integer maxApply = 50;
    @Value("${wapp.rednotification.authUser:admin,test01,aqli,j0z01jq}")
    private String redAuthUser;

    public static final int MAX_DETAIL_SIZE = 8;

    /**
     * @Description 新增红字信息表
     * @Author pengtao
     * @return
    **/
    public Response add(AddRedNotificationRequest request) {
        log.info("新增红字信息请求:{}", JsonUtil.toJsonStr(request));
        // 保存红字信息 进入待审核
        List<TXfRedNotificationEntity> listMain = Lists.newLinkedList();
        List<TXfRedNotificationDetailEntity> listItem = Lists.newLinkedList();
        List<RedNotificationInfo> redNotificationInfoList = request.getRedNotificationInfoList();
        redNotificationInfoList.stream().forEach(info -> {

            TXfRedNotificationEntity tXfRedNotificationEntity =
                    redNotificationMainMapper.mainInfoToEntity(info.getRednotificationMain());
            //根据结算单号查询成品油原号码代码类型
            getInvoiceMsg(tXfRedNotificationEntity);
            Long id = iDSequence.nextId();
            tXfRedNotificationEntity.setId(id);
            tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
            tXfRedNotificationEntity.setStatus(1);
            tXfRedNotificationEntity.setLockFlag(LockFlag.NORMAL.getValue());
            tXfRedNotificationEntity.setApproveStatus(ApproveStatus.OTHERS.getValue());
            tXfRedNotificationEntity.setCreateDate(new Date());
            tXfRedNotificationEntity.setUpdateDate(new Date());
            //https://jira.xforceplus.com/browse/WALMART-1663
            //成品油已启用OriginInvoiceDate字段，没必要暂用invioceDate ，这是红字信息表申请成功时间
            if (tXfRedNotificationEntity.getOriginInvoiceDate() != null) {
                tXfRedNotificationEntity.setInvoiceDate(null);
            }
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities =
                    redNotificationMainMapper.itemInfoToEntityList(info.getRedNotificationItemList());
            tXfRedNotificationDetailEntities.stream().forEach(item -> {
                item.setApplyId(id);
            });
            listMain.add(tXfRedNotificationEntity);
            listItem.addAll(tXfRedNotificationDetailEntities);
        });

        if (!CollectionUtils.isEmpty(listMain)) {
            //保存红字信息表
            saveBatch(listMain);
            redNotificationItemService.saveBatch(listItem);
        }

        //判断是否自动申请
        if (request.getAutoApplyFlag() == 1) {
            // 申请请求
            RedNotificationApplyReverseRequest applyRequest = new RedNotificationApplyReverseRequest();

            RedNotificationMain rednotificationMain =
                    request.getRedNotificationInfoList().get(0).getRednotificationMain();
            // 获取在线终端
            GetTerminalResponse terminalResponse = taxWareService.getTerminal(rednotificationMain.getPurchaserTaxNo());
            log.info("获取在线终端结果:{}",terminalResponse);
            if (Objects.equals(TaxWareCode.SUCCESS, terminalResponse.getCode())) {
                //WALMART-3053 固定指定税号下盘号
                List<String> terminalList = taxWareService.getAppointTerminalList(rednotificationMain.getPurchaserTaxNo());
                if(!CollectionUtils.isEmpty(terminalList)){
                    log.info("获取到指定税号下设备列表信息,terminalUn:{}",JSON.toJSON(terminalList));
                    for (GetTerminalResponse.ResultDTO.TerminalListDTO item : terminalResponse.getResult().getTerminalList()) {
                        if(terminalList.contains(item.getTerminalUn())){
                            GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO =
                                    !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0)
                                            : null;
                            if (deviceDTO != null) {
                                applyRequest.setDeviceUn(deviceDTO.getDeviceUn());
                                applyRequest.setTerminalUn(item.getTerminalUn());
                                break;
                            }
                        }
                    }
                }else{
                    //不是指定的税号走原来逻辑
                    for (GetTerminalResponse.ResultDTO.TerminalListDTO item : terminalResponse.getResult().getTerminalList()) {
                        GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO =
                                !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0)
                                        : null;
                        if (deviceDTO != null) {
                            applyRequest.setDeviceUn(deviceDTO.getDeviceUn());
                            applyRequest.setTerminalUn(item.getTerminalUn());
                            break;
                        }
                    }
                }

            }
            if (!StringUtils.isEmpty(applyRequest.getDeviceUn())) {
                QueryModel queryModel = new QueryModel();
                List<Long> pidList =
                        request.getRedNotificationInfoList().stream().map(item -> Long.parseLong(item.getRednotificationMain().getPid())).collect(Collectors.toList());
                queryModel.setPidList(pidList);
                applyRequest.setQueryModel(queryModel);
            } else {
                // 终端不在线更新到红字信息表
                List<Long> redIdList = listMain.stream().map(item -> item.getId()).collect(Collectors.toList());
                TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                record.setApplyRemark("自动申请，税盘不在线");
                record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(TXfRedNotificationEntity::getId, redIdList);
                getBaseMapper().update(record, updateWrapper);
                throw new RRException(String.format("未获取税号[%s]的在线终端", rednotificationMain.getPurchaserTaxNo()));
            }
            //申请
            return applyByPage(applyRequest, true);
        }
        return Response.ok("新增成功");
    }

    /**
     * @Description 查询发票号码代码类型
     * @Author pengtao
     * @return
    **/
    public void getInvoiceMsg(TXfRedNotificationEntity tXfRedNotificationEntity){
        log.info("获取底账发票信息，业务单号:{}",tXfRedNotificationEntity.getBillNo());
        QueryWrapper<TXfSettlementItemInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfSettlementItemInvoiceDetailEntity.SETTLEMENT_NO,tXfRedNotificationEntity.getBillNo());
        List<TXfSettlementItemInvoiceDetailEntity> tXfSettlementItemInvoiceDetailEntities = tXfSettlementItemInvoiceDetailDao.selectList(wrapper);
        if(!CollectionUtils.isEmpty(tXfSettlementItemInvoiceDetailEntities)){
            TXfSettlementItemInvoiceDetailEntity entity = tXfSettlementItemInvoiceDetailEntities.stream().findFirst().get();
            //查询底账数据
            QueryWrapper<TDxRecordInvoiceEntity> wrapper2 = new QueryWrapper<>();
            wrapper2.eq(TDxRecordInvoiceEntity.INVOICE_NO,entity.getInvoiceNo());
            wrapper2.eq(TDxRecordInvoiceEntity.INVOICE_CODE,entity.getInvoiceCode());
            List<TDxRecordInvoiceEntity> tDxRecordInvoiceEntitys = tDxRecordInvoiceDao.selectList(wrapper2);
            if(!CollectionUtils.isEmpty(tDxRecordInvoiceEntitys)){
                TDxRecordInvoiceEntity tDxRecordInvoiceEntity = tDxRecordInvoiceEntitys.stream().findFirst().get();

                log.info("获取底账发票信息结果，业务单号:{}，发票号码:{}，发票代码:{}，开票日期:{},发票类型:{}",tXfRedNotificationEntity.getBillNo()
                ,tDxRecordInvoiceEntity.getInvoiceNo(),tDxRecordInvoiceEntity.getInvoiceCode(),tDxRecordInvoiceEntity.getInvoiceDate(),tDxRecordInvoiceEntity.getInvoiceType());
                //成品油才进行更新
                if(Objects.nonNull(tDxRecordInvoiceEntity.getIsOil())&&tDxRecordInvoiceEntity.getIsOil()==1){
                    tXfRedNotificationEntity.setOriginInvoiceNo(tDxRecordInvoiceEntity.getInvoiceNo());
                    tXfRedNotificationEntity.setOriginInvoiceCode(tDxRecordInvoiceEntity.getInvoiceCode());
                    //发票类型转换数字转字母类型,暂不转换
//                    InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.getByCodeValue(tDxRecordInvoiceEntity.getInvoiceType());
//                    if (invoiceTypeEnum != null) {
//                        tXfRedNotificationEntity.setOriginInvoiceType(invoiceTypeEnum.getXfValue());
//                    }
                    tXfRedNotificationEntity.setOriginInvoiceType(tDxRecordInvoiceEntity.getInvoiceType());
                    tXfRedNotificationEntity.setOriginInvoiceDate(DateUtils.format(tDxRecordInvoiceEntity.getInvoiceDate(),"yyyyMMdd"));
                }
            }
        }
    }

    public Response applyByPage(RedNotificationApplyReverseRequest request, boolean autoFlag) {
        log.info("保存红字信息,页面申请:{}",JSON.toJSON(request));
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
        if (filterData.size() > maxApply) {
            return Response.failed("单次申请最大支持:" + maxApply);
        }

        //自动申请没有上下文
        log.info("申请标识:{}", autoFlag);
        if (!autoFlag) {
            String loginName = UserUtil.getLoginName();
            String key = APPLY_REDNOTIFICATION_KEY + loginName;
            if (redisTemplate.opsForValue().get(key) != null) {
                return Response.failed("申请红字信息操作频率过高,请耐心等待申请结果后重试");
            } else {
                redisTemplate.opsForValue().set(key, GENERATE_PDF_KEY, 3, TimeUnit.SECONDS);
            }
        }

        if (!request.getForceApply()) {
            boolean isHanging = defaultSettingService.isHanging();
            if (isHanging) {
                filterData.forEach(it -> {
                    it.setApplyingStatus(RedNoApplyingStatus.HANG_APPLY.getValue());
                    it.setUpdateDate(new Date());
                });
                updateBatchById(filterData);
                return Response.ok("申请的红字信息已被挂起");
            }
        }

        List<List<TXfRedNotificationEntity>> partition = Lists.partition(filterData, 25);
        if (partition.size() > 1) {
            // new List 是因为后续业务存在remove
            List<TXfRedNotificationEntity> list01 = new ArrayList(partition.get(0));
            List<TXfRedNotificationEntity> list02 = new ArrayList(partition.get(1));
            //分批申请
            Response resultA = applyByBatch(list01, request);
            Response resultB = applyByBatch(list02, request);
            Response response = new Response();
            if (resultA.getCode() == 1 && resultB.getCode() == 1) {
                response.setCode(Response.OK);
                response.setMessage("请求成功");
            } else if (resultA.getCode() == 0 && resultB.getCode() == 0) {
                response.setCode(Response.Fail);
                response.setMessage("申请失败,失败原因：" + resultA.getMessage());
            } else {
                response.setCode(Response.Fail);
                response.setMessage("部分成功,失败原因：" + (resultA.getCode() == 0 ? resultA.getMessage() :
                        resultB.getMessage()));
            }
            return response;
        } else {
            return applyByBatch(new ArrayList(filterData), request);
        }

    }

    /**
     * .分批申请
     *
     * @param filterData
     * @param request
     * @return
     */
    Response<String> applyByBatch(List<TXfRedNotificationEntity> filterData,
                                  RedNotificationApplyReverseRequest request) {
        log.info("保存红字信息,分批申请,红字信息:{},参数:{}",JSON.toJSON(filterData),JSON.toJSON(request));
        // 校验是否重复操作（申请，撤销），如果存在重复直接remove
        redNotificationAssistService.checkRepOperator(filterData, ApplyType.APPLY);
        //构建税件请求
        if (!CollectionUtils.isEmpty(filterData)) {
            // 发送状态更新消息-红字申请
            filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.APPLY_NOTIFICATION, entity));

            ApplyRequest applyRequest = new ApplyRequest();
            applyRequest.setDeviceUn(request.getDeviceUn());
            applyRequest.setTerminalUn(request.getTerminalUn());
            applyRequest.setSerialNo(String.valueOf(iDSequence.nextId()));
            //这个方法会更新红字信息表申请状态， 并保存操作履历
            List<RedInfo> redInfoList = buildRedInfoList(filterData, applyRequest);
            applyRequest.setRedInfoList(redInfoList);
            try {
                TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
                if (Objects.equals(TaxWareCode.SUCCESS, taxWareResponse.getCode())) {
                    return Response.ok("请求成功", applyRequest.getSerialNo());
                } else {
                    //更新流水.全部失败
                    updateRequestFail(applyRequest.getSerialNo(), taxWareResponse.getMessage());
                    //更新失败原因到主表
                    List<Long> redIdList =
                            redInfoList.stream().map(item -> Long.parseLong(item.getPid())).collect(Collectors.toList());
                    TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                    record.setApplyRemark(taxWareResponse.getMessage());
                    record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                    LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.in(TXfRedNotificationEntity::getId, redIdList);
                    getBaseMapper().update(record, updateWrapper);

                    // 更新失败及时释放反正重复操作锁
                    redNotificationAssistService.clearLock(redIdList, ApplyType.APPLY);
                    return Response.failed(taxWareResponse.getMessage());
                }
            } catch (RRException e) {
                // 异常情况比较特殊
                //更新流水.全部失败
                updateRequestFail(applyRequest.getSerialNo(), e.getMessage());
                //更新失败原因到主表
                List<Long> redIdList =
                        redInfoList.stream().map(item -> Long.parseLong(item.getPid())).collect(Collectors.toList());
                TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                record.setApplyRemark(e.getMessage());
                record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(TXfRedNotificationEntity::getId, redIdList);
                getBaseMapper().update(record, updateWrapper);

                // 更新失败及时释放重复操作锁
                redNotificationAssistService.clearLock(redIdList, ApplyType.APPLY);

                return Response.failed(e.getMessage());
            }
        } else {
            return Response.ok("未找到申请数据");
        }
    }


    /**
     * .只支持单个撤销
     *
     * @param model
     * @return
     */
    public Response rollback(RedNotificationApplyModel model) {
        QueryModel queryModel = model.getQueryModel();
//        queryModel.setLockFlag(1);
//        queryModel.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
//        queryModel.setApproveStatus(ApproveStatus.APPROVE_PASS.getValue());
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        // && item.getApproveStatus() == ApproveStatus.APPROVE_PASS.getValue()
        List<TXfRedNotificationEntity> entityList = filterData.stream().filter(item ->
                item.getLockFlag() == 1
        ).collect(Collectors.toList());
        if (filterData.size() > 0 && entityList.size() != filterData.size()) {
            return Response.failed("锁定中或未审核通过 不允许撤销");
        }
        log.info("rollback======{}", JSON.toJSONString(entityList));

        // 发送状态更新消息-撤销申请
        filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_APPLY
                , entity));

        RevokeRequest revokeRequest = buildRevokeRequestAndLogs(entityList, model);

        TaxWareResponse rollbackResponse = null;
        try {
            rollbackResponse = taxWareService.rollback(revokeRequest);
        } catch (Exception e) {
            log.error("撤销失败", e);
            rollbackResponse = new TaxWareResponse();
            rollbackResponse.setCode("-1");
            rollbackResponse.setMessage(e.getMessage());
        }

        if (Objects.equals(TaxWareCode.SUCCESS, rollbackResponse.getCode())) {
            return Response.ok("请求成功", revokeRequest.getSerialNo());
        } else {
            // 更新失败 到撤销待审核
            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
            record.setApplyRemark(rollbackResponse.getMessage());
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            List<Long> collect = entityList.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
            updateWrapper.in(TXfRedNotificationEntity::getId, collect);
            getBaseMapper().update(record, updateWrapper);

            //更新流水.全部失败
            updateRequestFail(revokeRequest.getSerialNo(), rollbackResponse.getMessage());

            // 发送状态更新消息-撤销申请
            filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_FAILED, entity));

            return Response.failed("请求失败，" + rollbackResponse.getMessage());
        }

    }


    /**
     * todo
     * 红字信息申请弹窗
     *
     * @param queryModel
     * @return
     */
    public Response<GetTerminalResult> getTerminals(QueryModel queryModel) {
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        if (CollectionUtils.isEmpty(filterData)) {
            return Response.failed("未筛选到数据");
        }
        List<String> collect =
                filterData.stream().map(TXfRedNotificationEntity::getPurchaserTaxNo).distinct().collect(Collectors.toList());
        if (collect.size() > 1) {
            return Response.failed("所选购方税号不唯一,无法获取唯一终端");
        }
        GetTerminalResult getTerminalResult = new GetTerminalResult();
        // 补充弹窗信息
        TXfRedNotificationEntity record = filterData.get(0);
        getTerminalResult.setNewCompanyName(record.getPurchaserName());
        getTerminalResult.setInvoiceType(record.getInvoiceType());
        getTerminalResult.setInvoiceCount(filterData.size());
        //统计金额信息
        BigDecimal totalAmountWithTax = BigDecimal.ZERO;
        BigDecimal totalAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        for (TXfRedNotificationEntity item : filterData) {
            totalAmountWithTax = totalAmountWithTax.add(item.getAmountWithTax());
            totalAmountWithoutTax = totalAmountWithoutTax.add(item.getAmountWithoutTax());
            totalTaxAmount = totalTaxAmount.add(item.getTaxAmount());
        }

        getTerminalResult.setAmountWithTax(totalAmountWithTax.toPlainString());
        getTerminalResult.setAmountWithoutTax(totalAmountWithoutTax.toPlainString());
        getTerminalResult.setTaxAmount(totalTaxAmount.toPlainString());
        //WALMART-3053 固定指定税号下盘号
        getTerminalResult.setTerminalList(getAppointTerminalList(collect.get(0),getTerminalList(collect.get(0))));

        return Response.ok("成功", getTerminalResult);
    }


    /**
     * @Description 获取指定税号下终端数据
     * @param
     * @Date
     * @Author pengtao
     * @return
    **/
    public List<TerminalDTO> getAppointTerminalList(String  purchaserTaxNo,List<TerminalDTO> terminals){
        //WALMART-3053 固定指定税号下盘号
        List<TerminalDTO> terminalDTOList = new ArrayList<>();
        List<String> terminalList = taxWareService.getAppointTerminalList(purchaserTaxNo);
        if(!CollectionUtils.isEmpty(terminalList)){
            log.info("获取到指定税号下设备列表信息,terminalUn:{}",JSON.toJSON(terminalList));
            List<TerminalDTO> tempDTo = terminals.stream().filter(it -> terminalList.contains(it.getTerminalUn())).collect(Collectors.toList());
            terminalDTOList.addAll(tempDTo);
        }else{
            //不是指定的税号走原来逻辑
            terminalDTOList.addAll(terminals);
        }

        return terminalDTOList;
    }


    /**
     * @Description 请求集成平台获取终端号
     * @param  taxNo 税号
     * @Author pengtao
     * @return
    **/
    public List<TerminalDTO> getTerminalList(String taxNo) {
        GetTerminalResponse terminal = taxWareService.getTerminal(taxNo);
        List<TerminalDTO> terminalList = Lists.newLinkedList();
        if (Objects.equals("TXWR000000", terminal.getCode())) {
            terminal.getResult().getTerminalList().forEach(item -> {
                TerminalDTO terminalDTO = new TerminalDTO();
                terminalDTO.setTerminalType(String.valueOf(item.getTerminalType()));
                //不为空默认获取第一个终端号
                GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO =
                        !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0) : null;
                terminalDTO.setDeviceUn(deviceDTO != null ? deviceDTO.getDeviceUn() : null);
                terminalDTO.setTerminalUn(item.getTerminalUn());
                terminalDTO.setTerminalName(item.getTerminalName());
                terminalDTO.setDirectOnlineFlag(deviceDTO != null ? 1 : 0);
                terminalList.add(terminalDTO);
            });
        }
        return terminalList;
    }


    //获取红字信息数据(主数据)
    List<TXfRedNotificationEntity> getFilterData(QueryModel queryModel) {
        //判读如果 getIncludes 没有值，queryModel 全选标识没传 。默认true 逻辑
        if (CollectionUtils.isEmpty(queryModel.getIncludes()) && queryModel.getIsAllSelected() == null) {
            queryModel.setIsAllSelected(true);
        } else if (!CollectionUtils.isEmpty(queryModel.getIncludes()) && queryModel.getIsAllSelected() == null) {
            queryModel.setIsAllSelected(false);
        }

        //全选
        if (queryModel.getIsAllSelected()) {
            QueryWrapper<TXfRedNotificationEntity> queryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
            if (queryModel.getApplyingStatus() != null && queryModel.getApplyingStatus() == 3 && queryModel.getApproveStatus() == null) {
                queryWrapper.ne("approve_status", 4);
            }
            return getBaseMapper().selectList(queryWrapper);
        } else {
            // id 勾选
            return getBaseMapper().selectBatchIds(queryModel.getIncludes());
        }
    }

    private QueryWrapper<TXfRedNotificationEntity> getNotificationEntityLambdaQueryWrapper(QueryModel queryModel) {
        QueryWrapper<TXfRedNotificationEntity> queryWrapper = new QueryWrapper<>();
        if (queryModel.getInvoiceOrigin() != null) {
            queryWrapper.eq(TXfRedNotificationEntity.INVOICE_ORIGIN, queryModel.getInvoiceOrigin());
        }
        if (!StringUtils.isEmpty(queryModel.getCompanyCode())) {
            queryWrapper.eq(TXfRedNotificationEntity.COMPANY_CODE, queryModel.getCompanyCode());
        }
        if (!StringUtils.isEmpty(queryModel.getPurchaserName())) {
            queryWrapper.eq(TXfRedNotificationEntity.PURCHASER_NAME, queryModel.getPurchaserName());
        }

        if (!StringUtils.isEmpty(queryModel.getSellerName())) {
            queryWrapper.eq(TXfRedNotificationEntity.SELLER_NAME, queryModel.getSellerName());
        }

        if (!StringUtils.isEmpty(queryModel.getRedNotificationNo())) {
            List<String> nos =
                    Arrays.asList(queryModel.getRedNotificationNo().replaceAll(" ", "").split(","));
            queryWrapper.in(TXfRedNotificationEntity.RED_NOTIFICATION_NO,
                    nos.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (!StringUtils.isEmpty(queryModel.getBillNo())) {
            List<String> billNos = Arrays.asList(queryModel.getBillNo().replaceAll(" ", "").split(","));
            queryWrapper.in(TXfRedNotificationEntity.BILL_NO,
                    billNos.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(queryModel.getPaymentTime())) {
            // 1634860800000
            Date start = new Date(queryModel.getPaymentTime().get(0));
            Date end = new Date(queryModel.getPaymentTime().get(1));
            queryWrapper.between(TXfRedNotificationEntity.PAYMENT_TIME, start, end);
        }
        if (!CollectionUtils.isEmpty(queryModel.getPidList())) {
            queryWrapper.in(TXfRedNotificationEntity.PID, queryModel.getPidList());
        }
        if (queryModel.getApproveStatus() != null) {
            queryWrapper.eq(TXfRedNotificationEntity.APPROVE_STATUS, queryModel.getApproveStatus());
        }
        if (queryModel.getApplyingStatus() != null) {
            queryWrapper.eq(TXfRedNotificationEntity.APPLYING_STATUS, queryModel.getApplyingStatus());
        }
        if (queryModel.getLockFlag() != null) {
            queryWrapper.eq(TXfRedNotificationEntity.LOCK_FLAG, queryModel.getLockFlag());
        }
        if (!CollectionUtils.isEmpty(queryModel.getExcludes())) {
            queryWrapper.notIn(TXfRedNotificationEntity.ID, queryModel.getExcludes());
        }

        if (StringUtils.isNotBlank(queryModel.getCreateTimeBegin())) {
            queryWrapper.ge(TXfRedNotificationEntity.CREATE_DATE, queryModel.getCreateTimeBegin());
        }

        if (StringUtils.isNotBlank(queryModel.getCreateTimeEnd())) {
            final String format = DateUtils.addDayToYYYYMMDD(queryModel.getCreateTimeEnd(), 1);
            queryWrapper.lt(TXfRedNotificationEntity.CREATE_DATE, format);
        }

        if (StringUtils.isNotBlank(queryModel.getInvoiceDateBegin())) {
            String invoiceDateBegin = new DateTime(queryModel.getInvoiceDateBegin()).toString("yyyyMMdd");
            queryWrapper.ge(TXfRedNotificationEntity.INVOICE_DATE, invoiceDateBegin);
        }

        if (StringUtils.isNotBlank(queryModel.getInvoiceDateEnd())) {
            String invoiceDateEnd = new DateTime(queryModel.getInvoiceDateEnd()).toString("yyyyMMdd");
            queryWrapper.le(TXfRedNotificationEntity.INVOICE_DATE, invoiceDateEnd);
        }

        if (StringUtils.isNotBlank(queryModel.getCancelTimeBegin())) {
            queryWrapper.ge(TXfRedNotificationEntity.CANCEL_TIME, queryModel.getCancelTimeBegin());
        }

        if (StringUtils.isNotBlank(queryModel.getCancelTimeEnd())) {
            final String format = DateUtils.addDayToYYYYMMDD(queryModel.getCancelTimeEnd(), 1);
            queryWrapper.lt(TXfRedNotificationEntity.CANCEL_TIME, format);
        }


        if (StringUtils.isNotBlank(queryModel.getSellerTaxNo())) {
            queryWrapper.eq(TXfRedNotificationEntity.SELLER_TAX_NO, queryModel.getSellerTaxNo());
        }

        //默认带上 正常条件
        if (queryModel.getStatus() == null || Objects.equals(queryModel.getStatus(), 1)) {
            queryWrapper.eq(TXfRedNotificationEntity.STATUS, 1);
        } else {
            queryWrapper.eq(TXfRedNotificationEntity.STATUS, 0);
        }

        return queryWrapper;
    }


    /**
     * 更新流失失败
     *
     * @param serialNo
     * @param failMsg
     */
    private void updateRequestFail(String serialNo, String failMsg) {
        LambdaUpdateWrapper<TXfRedNotificationLogEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TXfRedNotificationLogEntity::getSerialNo, serialNo);
        TXfRedNotificationLogEntity record = new TXfRedNotificationLogEntity();
        record.setStatus(3);
        record.setProcessRemark(failMsg);
        redNotificationLogService.update(record, updateWrapper);
    }

    public static String mapInvoiceType(String type) {
        return ImmutableMap.<String, String>builder()
                .put("08", "se")
                .put("01", "s")
                .build().getOrDefault(type, "s");
    }

    private List<RedInfo> buildRedInfoList(List<TXfRedNotificationEntity> filterData, ApplyRequest applyRequest) {
        ArrayList<RedInfo> redInfoList = Lists.newArrayList();
        ArrayList<TXfRedNotificationLogEntity> logList = Lists.newArrayList();

        for (TXfRedNotificationEntity notificationEntity : filterData) {
            RedInfo redInfo = new RedInfo();
            redInfo.setPid(String.valueOf(notificationEntity.getId()));
            redInfo.setApplyType("0");
            redInfo.setDupTaxFlag((Objects.isNull(notificationEntity.getDeduction()) || notificationEntity.getDeduction().compareTo(ZERO) == 0) ? "0" : "2");
            // 成品油
            if (Objects.equals(notificationEntity.getSpecialInvoiceFlag(), 2)) {
                //https://jira.xforceplus.com/browse/WALMART-1157
                redInfo.setOilMemo(Objects.isNull(notificationEntity.getOilMemo()) ? "1" :
                        notificationEntity.getOilMemo() + "");
            }
//          redInfo.setOilMemo();
            redInfo.setPurchaserName(notificationEntity.getPurchaserName());
            redInfo.setPurchaserTaxCode(notificationEntity.getPurchaserTaxNo());
            redInfo.setSellerName(notificationEntity.getSellerName());
            redInfo.setSellerTaxCode(notificationEntity.getSellerTaxNo());
            //这个地方存在空需要提前赋值
            redInfo.setOriginalInvoiceType(mapInvoiceType(notificationEntity.getOriginInvoiceType()));
            redInfo.setOriginalInvoiceCode(notificationEntity.getOriginInvoiceCode());
            redInfo.setOriginalInvoiceNo(notificationEntity.getOriginInvoiceNo());
            redInfo.setOriginalInvoiceDate(notificationEntity.getOriginInvoiceDate());
            redInfo.setApplicationReason(String.valueOf(notificationEntity.getApplyType()));

            Amount amount = new Amount();
            amount.setTaxAmount(notificationEntity.getTaxAmount());
            amount.setAmountWithoutTax(notificationEntity.getAmountWithoutTax());
            amount.setAmountWithTax(notificationEntity.getAmountWithTax());
            redInfo.setAmount(amount);


            Tuple2<List<RedDetailInfo>, String> result = buildDetails(notificationEntity.getId(),
                    notificationEntity.getInvoiceOrigin(), Objects.equals(notificationEntity.getSpecialInvoiceFlag(),
                            2));
            List<RedDetailInfo> details = result._1;
            //明细字段
            redInfo.setTaxCodeVersion(result._2);
            redInfo.setDetails(details);
            redInfoList.add(redInfo);
            // ================ 插入申请流水=============
            TXfRedNotificationLogEntity logEntity = new TXfRedNotificationLogEntity();
            logEntity.setApplyId(notificationEntity.getId());
            logEntity.setStatus(1);
            logEntity.setProcessRemark("处理中");
            logEntity.setRedNotificationNo("");
            logEntity.setDeviceUn(applyRequest.getDeviceUn());
            logEntity.setTerminalUn(applyRequest.getTerminalUn());
            logEntity.setApplyType(ApplyType.APPLY.getValue());
            logEntity.setSerialNo(applyRequest.getSerialNo());
//            logEntity.setCreateUserId();
            logEntity.setCreateDate(new Date());
            logEntity.setUpdateDate(new Date());
            logEntity.setId(iDSequence.nextId());
            logList.add(logEntity);
        }
        //更新红字信息表 的申请流水号
        List<Long> ids = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ids)) {
            // 先更新状态为申请中
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId, ids);
            TXfRedNotificationEntity entity = new TXfRedNotificationEntity();
            //https://jira.xforceplus.com/browse/WALMART-309
            entity.setDeviceUn(applyRequest.getDeviceUn());
            entity.setTerminalUn(applyRequest.getTerminalUn());
            entity.setApplyingStatus(RedNoApplyingStatus.APPLYING.getValue());
            getBaseMapper().update(entity, updateWrapper);
            //如果是非导入,导入的申请流水号不变化
            ArrayList<Integer> invoiceOriginList = Lists.newArrayList(InvoiceOrigin.CLAIM.getValue(),
                    InvoiceOrigin.AGREE.getValue(), InvoiceOrigin.EPD.getValue());
            updateWrapper.in(TXfRedNotificationEntity::getInvoiceOrigin, invoiceOriginList);
            entity.setSerialNo(applyRequest.getSerialNo());
            getBaseMapper().update(entity, updateWrapper);
        }


        redNotificationLogService.saveBatch(logList);
        return redInfoList;
    }

    /**
     * 1、构建明细信息
     *
     * @param id            红字信息表ID
     * @param invoiceOrigin 红字信息来源1.索赔单，2协议单，3.EPD ,4 导入
     * @return
     */
    private Tuple2<List<RedDetailInfo>, String> buildDetails(Long id, int invoiceOrigin, boolean isOil) {
        ArrayList<RedDetailInfo> redItemInfoList = Lists.newArrayList();
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> detailMapper = new LambdaQueryWrapper<>();
        detailMapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities =
                redNotificationItemService.getBaseMapper().selectList(detailMapper);
        for (TXfRedNotificationDetailEntity detailEntity : tXfRedNotificationDetailEntities) {
            RedDetailInfo redDetailInfo = new RedDetailInfo();

            DetailAmount detailAmount = new DetailAmount();
            detailAmount.setTaxAmount(detailEntity.getTaxAmount());
            detailAmount.setAmountWithoutTax(detailEntity.getAmountWithoutTax());
            detailAmount.setUnitPrice(detailEntity.getUnitPrice());
            detailAmount.setQuantity(detailEntity.getNum());
            detailAmount.setTaxDeduction(detailEntity.getDeduction());
            redDetailInfo.setDetailAmount(detailAmount);

            // ===================== 成品油 =====================
            BigDecimal taxDeduction = detailAmount.getTaxDeduction();
            if (Objects.nonNull(taxDeduction) && taxDeduction.compareTo(BigDecimal.ZERO) == 0) {
                detailAmount.setTaxDeduction(null);
            }
            BigDecimal quantity = detailAmount.getQuantity();
            if (Objects.nonNull(quantity) && quantity.compareTo(BigDecimal.ZERO) == 0) {
                detailAmount.setQuantity(null);
            }
            BigDecimal unitPrice = detailAmount.getUnitPrice();
            if (Objects.nonNull(unitPrice) && unitPrice.compareTo(BigDecimal.ZERO) == 0) {
                detailAmount.setUnitPrice(null);
            }
            //=======================================================

            Production production = new Production();
            production.setProductionCode(detailEntity.getGoodsTaxNo());
            production.setProductionName(detailEntity.getGoodsName());
            //2022-08-08 新增，因为索赔没有规格型号，所以去掉
            if (invoiceOrigin != 1) {
                production.setSpecification(detailEntity.getModel());//规格
            }
            if (isOil) {
                production.setUnitName(null);//单位
            } else {
                production.setUnitName(detailEntity.getUnit());//单位
            }
            redDetailInfo.setProduction(production);

            Tax tax = new Tax();
            tax.setPreferentialTax(detailEntity.getTaxPre() == 1 ? true : false);
            tax.setTaxPolicy(detailEntity.getTaxPreCon());
            tax.setTaxRate(detailEntity.getTaxRate());
            tax.setZeroTax(detailEntity.getZeroTax() == null ? "" : String.valueOf(detailEntity.getZeroTax()));
            tax.setTaxCodeVersion(detailEntity.getGoodsNoVer());
            redDetailInfo.setTax(tax);

            redItemInfoList.add(redDetailInfo);
        }
        String goodsNoVer = tXfRedNotificationDetailEntities.get(0).getGoodsNoVer();
        return Tuple.of(redItemInfoList, goodsNoVer);
    }

    public Response<SummaryResult> summary(QueryModel queryModel) {
//        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
//        Map<Integer, List<TXfRedNotificationEntity>> listMap = filterData.stream().collect(Collectors.groupingBy
//        (TXfRedNotificationEntity::getApplyingStatus));

        QueryWrapper<TXfRedNotificationEntity> queryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
        //已撤销的红字信息表
        QueryWrapper<TXfRedNotificationEntity> queryWrapper_cancel =
                getNotificationEntityLambdaQueryWrapper(queryModel);
        queryWrapper_cancel.eq("applying_status", "3");//"applyingStatus":"3"  和"approveStatus":"4“ 为固定
        queryWrapper_cancel.eq("approve_status", "4");
        int count_cancel = getBaseMapper().selectCount(queryWrapper_cancel);

        queryWrapper.select("applying_status , count(1) as count").groupBy(TXfRedNotificationEntity.APPLYING_STATUS);
        List<Map<String, Object>> listMap = getBaseMapper().selectMaps(queryWrapper);

        //默认为0  1.未申请 2.申请中 3.已申请 4.撤销待审核 5.已撤销
        SummaryResult summaryResult = new SummaryResult(0, 0, 0, 0, 0, 0, 0);
        summaryResult.setAppliedCancel(count_cancel);

        queryModel.setStatus(0);
        int sellerDelCount = getBaseMapper().selectCount(getNotificationEntityLambdaQueryWrapper(queryModel));
        summaryResult.setSellerDel(sellerDelCount);


        listMap.forEach(itemMap -> {
            Short applying_status = (Short) itemMap.get("applying_status");
            Integer count = (Integer) itemMap.get("count");
            switch (applying_status) {
                case 1:
                    summaryResult.setApplyPending(count);
                    break;
                case 2:
                    summaryResult.setApplying(count);
                    break;
                case 3:
                    summaryResult.setApplied(count - count_cancel);
                    break;
                case 4:
                    summaryResult.setWaitApprove(count);
                    break;
            }
        });

        int total =
                summaryResult.getApplyPending() + summaryResult.getApplying() + summaryResult.getApplied() + summaryResult.getWaitApprove() + summaryResult.getSellerDel() + summaryResult.getAppliedCancel();
        summaryResult.setTotal(total);
        return Response.ok("成功", summaryResult);
    }

    public Response<PageResult<RedNotificationMain>> listData(QueryModel queryModel) {
        if (queryModel.getPageNo() == null) {
            queryModel.setPageNo(1);
            queryModel.setPageSize(20);
        }
        QueryWrapper<TXfRedNotificationEntity> notificationEntityLambdaQueryWrapper =
                getNotificationEntityLambdaQueryWrapper(queryModel);
        if (queryModel.getApplyingStatus() != null && queryModel.getApplyingStatus() == 3 && queryModel.getApproveStatus() == null) {
            notificationEntityLambdaQueryWrapper.ne("approve_status", 4);
        }
        Page<TXfRedNotificationEntity> page = new Page<>(queryModel.getPageNo(), queryModel.getPageSize());
        Page<TXfRedNotificationEntity> tXfRedNotificationEntityPage = getBaseMapper().selectPage(page,
                notificationEntityLambdaQueryWrapper);
        List<RedNotificationMain> redNotificationMains =
                redNotificationMainMapper.entityToMainInfoList(tXfRedNotificationEntityPage.getRecords());
        PageResult<RedNotificationMain> pageResult = PageResult.of(tXfRedNotificationEntityPage.getTotal(),
                redNotificationMains);
        return Response.ok("成功", pageResult);
    }

    public Response<RedNotificationInfo> detail(Long id) {
        TXfRedNotificationEntity tXfRedNotificationEntity = getBaseMapper().selectById(id);

        RedNotificationInfo redNotificationInfo = null;
        if (tXfRedNotificationEntity != null) {
            redNotificationInfo = new RedNotificationInfo();
            RedNotificationMain redNotificationMain =
                    redNotificationMainMapper.entityToMainInfo(tXfRedNotificationEntity);
            if (StringUtils.isEmpty(redNotificationMain.getInvoiceDate())) {
                redNotificationMain.setInvoiceDate(DateUtils.getCurentIssueDate());
            }

            LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities =
                    redNotificationItemService.getBaseMapper().selectList(queryWrapper);
            List<RedNotificationItem> redNotificationItems =
                    redNotificationMainMapper.entityToItemInfoList(tXfRedNotificationDetailEntities);
            redNotificationInfo.setRedNotificationItemList(redNotificationItems);
            redNotificationInfo.setRednotificationMain(redNotificationMain);
        }
        return Response.ok("成功", redNotificationInfo);
    }


    private RevokeRequest buildRevokeRequestAndLogs(List<TXfRedNotificationEntity> entityList,
                                                    RedNotificationApplyModel request) {

        Long serialNo = iDSequence.nextId();

        RevokeRequest revokeRequest = new RevokeRequest();
        revokeRequest.setSerialNo(String.valueOf(serialNo));
        revokeRequest.setApplyTaxCode(entityList.get(0).getPurchaserTaxNo());
        revokeRequest.setTerminalUn(request.getTerminalUn());
        revokeRequest.setDeviceUn(request.getDeviceUn());

        ArrayList<TXfRedNotificationLogEntity> logList = Lists.newArrayList();
        ArrayList<RevokeRedNotificationInfo> revokeRedNotificationInfos = Lists.newArrayList();
        entityList.stream().forEach(entity -> {
            RevokeRedNotificationInfo revokeRedNotificationInfo = new RevokeRedNotificationInfo();
            revokeRedNotificationInfo.setRedNotificationNo(entity.getRedNotificationNo());
            revokeRedNotificationInfos.add(revokeRedNotificationInfo);

            // ================ 插入撤销流水=============
            TXfRedNotificationLogEntity logEntity = new TXfRedNotificationLogEntity();
            logEntity.setApplyId(entity.getId());
            logEntity.setStatus(1);
            logEntity.setProcessRemark("处理中");
            logEntity.setRedNotificationNo(entity.getRedNotificationNo());
            logEntity.setDeviceUn(request.getDeviceUn());
            logEntity.setTerminalUn(request.getTerminalUn());
            logEntity.setEventType(request.getEventType());
            logEntity.setApplyType(ApplyType.ROLL_BACK.getValue());
            logEntity.setSerialNo(revokeRequest.getSerialNo());
//            logEntity.setCreateUserId();
            logEntity.setCreateDate(new Date());
            logEntity.setUpdateDate(new Date());
            logEntity.setId(iDSequence.nextId());
            logList.add(logEntity);
        });
        redNotificationLogService.saveBatch(logList);

        revokeRequest.setRedNotificationList(revokeRedNotificationInfos);
        return revokeRequest;
    }

    public Response importNotification(MultipartFile file) {
        Tuple3<Long, Long, String> longLongStringTuple3 = exportCommonService.insertRequest(file.getOriginalFilename());
        redNotificationThreadPool.execute(
                () -> {
                    InputStream inputStream = null;
                    try {
                        inputStream = new BufferedInputStream(file.getInputStream());
                    } catch (IOException e) {
                        log.error("获取导入文件失败", e);
                    }
                    //实例化实现了AnalysisEventListener接口的类
                    ExcelListener excelListener = new ExcelListener(this, redNotificationMainMapper, checkMainService
                            , longLongStringTuple3);
                    ExcelReader reader = new ExcelReader(inputStream, null, excelListener);
                    //读取信息
                    reader.read(new Sheet(1, 1, ImportInfo.class));
                }
        );

        return Response.ok("处理成功，请到右上角小铃铛查看导入结果");
    }

    public Response downloadPdf(RedNotificationExportPdfRequest request) {
        Tuple3<Long, Long, String> tuple3 = null;
        if (request.getAutoFlag() != null && request.getAutoFlag()) {
            //自动申请pdf 不需要插入日志 ，不校验频率
        } else {
            String loginName = UserUtil.getLoginName();
            String key = GENERATE_PDF_KEY + loginName;
            if (redisTemplate.opsForValue().get(key) != null) {
                return Response.failed("生成pdf操作频率过高,请耐心等待申请结果后重试");
            } else {
                redisTemplate.opsForValue().set(key, GENERATE_PDF_KEY, 3, TimeUnit.SECONDS);
            }
            //<logId,userId>
            tuple3 = exportCommonService.insertRequest(request);
        }

        Integer generateModel = request.getGenerateModel();
        QueryModel queryModel = request.getQueryModel();
        queryModel.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
        queryModel.setStatus(1);
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        if (filterData.size() > MAX_PDF_RED_NO_SIZE) {
            return Response.failed("单次生成pdf的红字信息数目不得超过" + MAX_PDF_RED_NO_SIZE);
        }
        List<Long> applyList = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        //获取明细
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationDetailEntity::getApplyId, applyList);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities =
                redNotificationItemService.getBaseMapper().selectList(queryWrapper);
        Map<Long, List<TXfRedNotificationDetailEntity>> listItemMap =
                tXfRedNotificationDetailEntities.stream().collect(Collectors.groupingBy(TXfRedNotificationDetailEntity::getApplyId));
        String downLoadUrl = exportRedNoPdf(filterData, listItemMap, generateModel, tuple3);
        return Response.ok("导出成功,请在消息中心查看", downLoadUrl);
    }

    private String exportRedNoPdf(List<TXfRedNotificationEntity> applies, Map<Long,
            List<TXfRedNotificationDetailEntity>> detailMap, Integer generateModel, Tuple3<Long, Long, String> tuple3) {

        List<ZipContentInfo> zipInfos = null;
        RedNoGeneratePdfModel pdfModel =
                ValueEnum.getEnumByValue(RedNoGeneratePdfModel.class, generateModel).orElse(RedNoGeneratePdfModel.Merge_All);
        switch (pdfModel) {
            case Merge_All:
                Map<String, List<TXfRedNotificationEntity>> mergeAllMap = new HashMap<>();
                mergeAllMap.put("全部合并", applies);
                zipInfos = generateRedNoPdf(mergeAllMap, detailMap, pdfModel);
                break;
            case Split_By_Seller:
                Map<String, List<TXfRedNotificationEntity>> groupBySeller = applies.stream()
                        .collect(Collectors.groupingBy((redNotificationEntity) -> {
                            if (StringUtils.isNotBlank(redNotificationEntity.getSellerName())) {
                                return redNotificationEntity.getSellerName();
                            } else {
                                return "销方名称为空";
                            }
                        }));
                zipInfos = generateRedNoPdf(groupBySeller, detailMap, pdfModel);
                break;
            case Split_By_Purchaser:
                Map<String, List<TXfRedNotificationEntity>> groupByPurchaser = applies.stream()
                        .collect(Collectors.groupingBy((redNotificationEntity) -> {
                            if (StringUtils.isNotBlank(redNotificationEntity.getPurchaserName())) {
                                return redNotificationEntity.getPurchaserName();
                            } else {
                                return "购方名称为空";
                            }
                        }));
                zipInfos = generateRedNoPdf(groupByPurchaser, detailMap, pdfModel);
                break;
        }
        return makeRedNoPdfZip(zipInfos, tuple3);
    }


    private List<ZipContentInfo> generateRedNoPdf(Map<String, List<TXfRedNotificationEntity>> redNoMap, Map<Long,
            List<TXfRedNotificationDetailEntity>> detailsMap, RedNoGeneratePdfModel model) {
        List<ZipContentInfo> zipContents = new CopyOnWriteArrayList<>();
        redNoMap.keySet().stream().forEach(head -> {
            redNoMap.get(head).parallelStream().forEach(redNoApply -> {
                if (StringUtils.isEmpty(redNoApply.getPdfUrl())) {
                    getPdf(redNoApply, detailsMap.get(redNoApply.getId())).ifPresent(pdfUrl -> {
                        String fileName = format("{}.pdf", redNoApply.getRedNotificationNo());
                        ZipContentInfo zipInfo = new ZipContentInfo();
                        zipInfo.setFile(false);
                        zipInfo.setRelativePath(fileName);
                        zipInfo.setSourceUrl(pdfUrl);
                        zipContents.add(zipInfo);
                        //更新数据库
                        if (StringUtils.isEmpty(redNoApply.getPdfUrl())) {
                            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                            record.setPdfUrl(pdfUrl);
                            record.setId(redNoApply.getId());
                            log.info("更新pdf路径链接");
                            updateById(record);
                        }
                    });
                } else {
                    //已经拿到pdf链接了，直接从数据库获取
                    ZipContentInfo zipInfo = new ZipContentInfo();
                    zipInfo.setFile(false);
                    String fileName = format("{}.pdf", redNoApply.getRedNotificationNo());
                    zipInfo.setRelativePath(fileName);
                    zipInfo.setSourceUrl(redNoApply.getPdfUrl());
                    zipContents.add(zipInfo);
                }
            });
        });
        return zipContents;
    }

    private Optional<String> getPdf(TXfRedNotificationEntity entity, List<TXfRedNotificationDetailEntity> item) {
        RedNotificationGeneratePdfRequest request = buildRedNotificationGeneratePdfRequest(entity, item);
        log.info("开始生成红字信息pdf:" + request.getSerialNo());
        long start = System.currentTimeMillis();
        TaxWareResponse response = taxWareService.generatePdf(request);
        log.info("红字信息生成pdf耗时:{}ms,流水号:{}", System.currentTimeMillis() - start, request.getSerialNo());
        if (response.getCode() != null && !Objects.equals(response.getCode(), TaxWareCode.SUCCESS)) {
            throw new RRException(response.getMessage());
        }
        TaxWareResponse.ResultDTO result = response.getResult();
        if (Objects.nonNull(result)) {
            return Optional.ofNullable(result.getPdfUrl());
        }
        return Optional.empty();
    }

    private String makeRedNoPdfZip(List<ZipContentInfo> zipContents, Tuple3<Long, Long, String> tuple3) {
        //自动申请pdf 无上下文 不处理zip
        if (tuple3 == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String zipFileNameWithOutSubfix = sdf.format(new Date());
        String zipFile = format("output/{}/{}/{}.zip", "invoice-service", "redNoZip", zipFileNameWithOutSubfix);
        if (zipContents.size() > 0) {
            downloadUrlUtils.commonZipFiles(zipContents, zipFile);
            //发送到消息中心
//            return DownloadUrlUtils.putFile(zipFile);
            // 插入日志记录
            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String zipFileName = zipFileNameWithOutSubfix + ".zip";
            String ftpFilePath = ftpPath + "/" + zipFileName;
            String s = exportCommonService.putFile(ftpPath, zipFile, zipFileName);

            if (s != null) {
                String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.FAIL, null);
                exportCommonService.sendMessage(tuple3._1, tuple3._3, "红字信息表下载pdf失败",
                        exportCommonService.getFailContent(s), false);
                return s;
            } else {
                String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.OK, ftpFilePath);
                exportCommonService.sendMessage(tuple3._1, tuple3._3, "红字信息表下载pdf成功",
                        exportCommonService.getSuccContent(), true);
                return "导出成功,请在消息中心查看";
            }
        }
        return null;
    }


    public static String format(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }

    private RedNotificationGeneratePdfRequest buildRedNotificationGeneratePdfRequest(TXfRedNotificationEntity apply,
                                                                                     List<TXfRedNotificationDetailEntity> applyDetails) {

        RedNotificationGeneratePdfRequest request = new RedNotificationGeneratePdfRequest();
        RequestHead head = new RequestHead();
        head.setDebug(null);
        head.setTenantId(taxWareService.tenantId);
        head.setTenantName(taxWareService.tenantName);
        request.setHead(head);

        RedGeneratePdfInfo redInfo = new RedGeneratePdfInfo();
        redInfo.setApplicant(apply.getApplyType());
        redInfo.setDate(apply.getInvoiceDate());

        List<RedGeneratePdfDetailInfo> detailInfos = applyDetails.stream().map(item -> {
            RedGeneratePdfDetailInfo detailInfo = new RedGeneratePdfDetailInfo();
            detailInfo.setAmountWithoutTax(item.getAmountWithoutTax().toPlainString());
            detailInfo.setCargoName(item.getGoodsName());
            detailInfo.setQuantity(item.getNum().toPlainString());
            detailInfo.setTaxAmount(item.getTaxAmount().toPlainString());
            detailInfo.setTaxRate(item.getTaxRate().toPlainString());
            detailInfo.setUnitPrice(item.getUnitPrice().toPlainString());
            return detailInfo;
        }).collect(Collectors.toList());

        if (applyDetails.size() > MAX_DETAIL_SIZE) {
            RedGeneratePdfDetailInfo merge = merge(applyDetails);
            redInfo.setDetails(Lists.newArrayList(merge));
        } else {
            redInfo.setDetails(detailInfos);
        }


        redInfo.setOriginInvoiceCode(apply.getOriginInvoiceCode());
        redInfo.setOriginInvoiceNo(apply.getOriginInvoiceNo());
        redInfo.setPurchaseTaxNo(apply.getPurchaserTaxNo());
        redInfo.setPurchaserName(apply.getPurchaserName());
        redInfo.setRedNotificationNo(apply.getRedNotificationNo());
        redInfo.setSellerName(apply.getSellerName());
        redInfo.setSellerTaxNo(apply.getSellerTaxNo());
        redInfo.setTotalAmountWithoutTax(apply.getAmountWithoutTax().toString());
        redInfo.setTotalTaxAmount(apply.getTaxAmount().toString());
        request.setRedInfo(redInfo);

        request.setSerialNo(String.valueOf(iDSequence.nextId()));
        return request;
    }

    /**
     * 导出excl
     *
     * @param request
     * @return
     */
    public Response export(RedNotificationExportPdfRequest request) {
        Tuple3<Long, Long, String> tuple3 = exportCommonService.insertRequest(request);

        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
//        List<Long> applyList = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        //获取明细
//        activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
//                Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.EXCEPTION_REPORT
//                .name())
//        );

        List<Long> redNoIds = new ArrayList<>();
        Map<Long, ExportInfo> exportInfoMap = Maps.newHashMap();

        List<ExportItemInfo> itemInfos = Lists.newArrayList();
        List<ExportInfo> exportInfos = filterData.stream().map(apply -> {
            ExportInfo dto = redNotificationMainMapper.mainEntityToExportInfo(apply);
            ApproveStatus applyStatus =
                    ValueEnum.getEnumByValue(ApproveStatus.class, apply.getApproveStatus()).orElse(ApproveStatus.OTHERS);
            dto.setApproveStatus(applyStatus != ApproveStatus.OTHERS ? applyStatus.getDesc() : "");

            if (StringUtils.isNotBlank(apply.getInvoiceType())) {
                dto.setInvoiceType(ValueEnum.getEnumByValue(InvoiceType.class, apply.getInvoiceType()).get().getDescription());
            }
            if (apply.getInvoiceOrigin() != null) {
                dto.setInvoiceOrigin(ValueEnum.getEnumByValue(InvoiceOrigin.class, apply.getInvoiceOrigin()).get().getDesc());
            }
            //去除导出无用字段
//            if (StringUtils.isNotBlank(apply.getOriginInvoiceType())) {
//                dto.setOriginInvoiceType(ValueEnum.getEnumByValue(InvoiceType.class, InvoiceTypeEnum.invoiceTypeMap
//                ().get(apply.getOriginInvoiceType())).get().getDescription());
//            }
            redNoIds.add(apply.getId());
            exportInfoMap.put(apply.getId(), dto);

            //封装1000一批次查询明细
            if (redNoIds.size() > 1000) {
                handleItemInfos(redNoIds, exportInfoMap, itemInfos);
            }

            return dto;
        }).collect(Collectors.toList());

        //最后一批次
        if (redNoIds.size() > 0) {
            handleItemInfos(redNoIds, exportInfoMap, itemInfos);
        }


        return writeExcel(exportInfos, itemInfos, new ExportInfo(), new ExportItemInfo(), tuple3);

    }


    private void handleItemInfos(List<Long> redNoIds, Map<Long, ExportInfo> exportInfoMap,
                                 List<ExportItemInfo> itemInfos) {
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationDetailEntity::getApplyId, redNoIds);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities =
                redNotificationItemService.getBaseMapper().selectList(queryWrapper);
        Map<Long, List<TXfRedNotificationDetailEntity>> listItemMap =
                tXfRedNotificationDetailEntities.stream().collect(Collectors.groupingBy(TXfRedNotificationDetailEntity::getApplyId));

        redNoIds.stream().forEach(data -> {
            // 获取明细
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities1 = listItemMap.get(data);
            if (CollectionUtils.isEmpty(tXfRedNotificationDetailEntities1)) {
                log.info("找不到明细:{}", data);
            } else {
                ExportInfo tmpDto = exportInfoMap.get(data);
                tXfRedNotificationDetailEntities1.stream().forEach(item -> {
                    ExportItemInfo exportItemInfo = redNotificationMainMapper.detailEntityToExportInfo(item, tmpDto);
                    itemInfos.add(exportItemInfo);
                });
            }


        });
        // 清空批次
        redNoIds.clear();
        exportInfoMap.clear();
        listItemMap.clear();
    }


    private Response writeExcel(List<? extends BaseRowModel> list, List<? extends BaseRowModel> list2,
                                BaseRowModel object, BaseRowModel object2, Tuple3<Long, Long, String> tuple3) {
        Long userId = tuple3._2;
        Long logId = tuple3._1;

        String fileName = "红字信息表";
        String sheetName = "红字信息主信息";
        String sheetName2 = "红字信息明细";

//        String businessId = String.valueOf(System.currentTimeMillis());
//        String filePath = "file/" + "walmart" + "/" + DateUtils.curDateMselStr17() + "/"  + fileName + "导出" + 
//        businessId + ".xlsx";

        //推送sftp
        final String excelFileName = ExcelExportUtil.getExcelFileName(userId, "红字信息表导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String ftpFilePath = ftpPath + "/" + excelFileName;
        log.info("文件ftp路径{}", ftpFilePath);

        String localFilePath = ftpFilePath.substring(1);
        File localFile = FileUtils.getFile(localFilePath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        String s;
        try (OutputStream out = new FileOutputStream(localFile);) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);

            Sheet sheet = new Sheet(1, 0, object.getClass());

            sheet.setSheetName(sheetName);

            writer.write(list, sheet);

            Sheet sheet2 = new Sheet(2, 0, object2.getClass());
            sheet2.setSheetName(sheetName2);

            writer.write(list2, sheet2);
            writer.finish();
            s = exportCommonService.putFile(ftpPath, localFilePath, excelFileName);

            if (Objects.isNull(s)) {
                String userName = exportCommonService.updatelogStatus(logId, ExcelExportLogService.OK, ftpFilePath);
                exportCommonService.sendMessage(tuple3._1, userName, "红字信息表导出成功", exportCommonService.getSuccContent());
                return Response.ok("导出成功,请在消息中心查看");
            }
        } catch (IOException fnfException) {
            fnfException.printStackTrace();
            log.error("new FileOutputStream(localFile) err!", fnfException);
            s = fnfException.getMessage();
        } finally {
            log.info("删除本地临时红字信息表导出文件");
            FileUtils.deleteQuietly(localFile);
        }
        String userName = exportCommonService.updatelogStatus(logId, ExcelExportLogService.FAIL, null);
        exportCommonService.sendMessageWithUrl(tuple3._1, userName, "红字信息表导出失败",
                exportCommonService.getFailContent(s), null);
        return Response.failed(s);

    }


    /**
     * 确认
     * 驳回  回到已申请 。从待审批页面消失
     *
     * @param model
     * @return
     */
    public Response<String> operation(RedNotificationConfirmRejectModel model) {
        List<TXfRedNotificationEntity> filterData = getFilterData(model.getQueryModel());
        //获取结算单号 获取弹窗  相同单号 一起审批
        List<String> billNos =
                filterData.stream().map(TXfRedNotificationEntity::getBillNo).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationEntity::getBillNo, billNos)
                .eq(TXfRedNotificationEntity::getApproveStatus, ApproveStatus.WAIT_TO_APPROVE.getValue())
                .eq(TXfRedNotificationEntity::getStatus, 1);
        List<TXfRedNotificationEntity> tXfRedNotificationEntities = getBaseMapper().selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tXfRedNotificationEntities)) {
            String msg = Objects.equals(OperationType.CONFIRM.getValue(), model.getOperationType()) ? "审批通过" : "驳回";
            log.info("该数据已被执行{}操作", msg);
            throw new EnhanceRuntimeException("该数据已被执行[" + msg + "]操作");
        }

        List<Long> list =
                tXfRedNotificationEntities.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());

        List<Long> pidList =
                tXfRedNotificationEntities.stream().map(item -> Long.parseLong(item.getPid())).collect(Collectors.toList());
        if (Objects.equals(OperationType.CONFIRM.getValue(), model.getOperationType())) {
            // 确认 //自动尝试一次 //撤销待审核
            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setRevertRemark(model.getOperationRemark());
            record.setApproveStatus(ApproveStatus.APPROVE_PASS.getValue());
            record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId, list);
            int update = getBaseMapper().update(record, updateWrapper);

            // 同意删除预制发票
            try {
                commSettlementService.agreeDestroySettlementPreInvoiceByPreInvoiceId(pidList);
            } catch (EnhanceRuntimeException e) {
                return Response.failed(e.getMessage());
            }

            RedNotificationApplyModel reverseRequest = new RedNotificationApplyModel();
            model.getQueryModel().setApproveStatus(null);
            model.getQueryModel().setIncludes(list);
            reverseRequest.setQueryModel(model.getQueryModel());
            rollback(reverseRequest);
        } else {
            // 驳回 ，修改状态到已申请
            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setApproveStatus(ApproveStatus.APPROVE_FAIL.getValue());
            record.setRejectRemark(model.getOperationRemark());
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId, list);
            int update = getBaseMapper().update(record, updateWrapper);

            // 驳回保留红字预制发票
            try {
                commSettlementService.rejectDestroySettlementPreInvoiceByPreInvoiceId(pidList,
                        model.getOperationRemark());
            } catch (EnhanceRuntimeException e) {
                return Response.failed(e.getMessage());
            }
            // 发送状态更新消息-审核驳回回到已申请状态
            tXfRedNotificationEntities.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.APPLY_SUCCEED, entity));
        }
        return Response.ok("操作成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<String> rollbackByIds(RedNotificationApplyModel model, String username, String loginname) {
        //判断用户信息是否有配置
        List<String> redAuthUserList = Arrays.asList(redAuthUser.split(","));
        if (!(redAuthUserList.contains(username) || redAuthUserList.contains(loginname))) {
            return Response.failed("无权限操作");
        }
        List<TXfRedNotificationEntity> filterData = getBaseMapper().selectBatchIds(Arrays.asList(model.getRedId()));
        if (filterData == null || filterData.size() == 0) {
            return Response.failed("数据为空，请重新选择");
        }
        //判断锁定
        for (TXfRedNotificationEntity tXfRedNotificationEntity : filterData) {
            if (tXfRedNotificationEntity.getInvoiceOrigin() != InvoiceOrigin.IMPORT.getValue()) {
                return Response.failed("只支持导入的红字信息表撤销");
            }
            if (tXfRedNotificationEntity.getLockFlag() != 1) {
                return Response.failed("锁定中或未审核通过不允许撤销");
            }
            if (tXfRedNotificationEntity.getApplyingStatus() == RedNoApplyingStatus.APPLYING.getValue()) {
                return Response.failed("列表中存在已申请中单据，请刷新界面重新勾选");
            }
            if (tXfRedNotificationEntity.getApproveStatus() == ApproveStatus.ALREADY_ROLL_BACK.getValue()) {
                return Response.failed("列表中存在已撤销的单据，请刷新界面重新勾选");
            }
        }
        List<TXfRedNotificationEntity> entityList =
                filterData.stream().filter(item -> item.getLockFlag() == 1).collect(Collectors.toList());

        // 校验是否重复操作（申请，撤销），如果存在重复直接remove
        redNotificationAssistService.checkRepOperator(entityList, ApplyType.ROLL_BACK);

        // 发送状态更新消息-撤销申请
        filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_APPLY
                , entity));

        RevokeRequest revokeRequest = buildRevokeRequestAndLogs(entityList, model);
        TaxWareResponse rollbackResponse = null;
        try {
            rollbackResponse = taxWareService.rollback(revokeRequest);
        } catch (Exception e) {
            log.error("撤销失败", e);
            rollbackResponse = new TaxWareResponse();
            rollbackResponse.setCode("-1");
            rollbackResponse.setMessage(e.getMessage());
        }
        //把红字信息表的状态设置为申请中或者申请失败
        TXfRedNotificationEntity record = new TXfRedNotificationEntity();
        record.setApplyingStatus(Objects.equals(TaxWareCode.SUCCESS, rollbackResponse.getCode()) ?
                RedNoApplyingStatus.APPLYING.getValue() : RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
        record.setApplyRemark(rollbackResponse.getMessage());
        LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        List<Long> collect = entityList.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        updateWrapper.in(TXfRedNotificationEntity::getId, collect);
        getBaseMapper().update(record, updateWrapper);
        //返回给业务系统
        if (Objects.equals(TaxWareCode.SUCCESS, rollbackResponse.getCode())) {
            return Response.ok("请求成功", revokeRequest.getSerialNo());
        } else {
            // 更新流水.全部失败
            updateRequestFail(revokeRequest.getSerialNo(), rollbackResponse.getMessage());
            // 更新失败及时释放反正重复操作锁
            redNotificationAssistService.clearLock(collect, ApplyType.ROLL_BACK);
            // 发送状态更新消息-撤销申请
            filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_FAILED, entity));

            return Response.failed("请求失败，" + rollbackResponse.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<String> rollbackByIdsV2(RedNotificationApplyModel model) {
        // 判断用户信息是否有配置 TODO 不清楚是否需要添加权限校验
        //List<String> redAuthUserList = Arrays.asList(redAuthUser.split(","));
        //Asserts.isFalse(redAuthUserList.contains(username) || redAuthUserList.contains(loginName), "无权限操作");

        List<TXfRedNotificationEntity> filterData = getBaseMapper().selectBatchIds(Arrays.asList(model.getRedId()));
        Asserts.isTrue(filterData == null || filterData.size() == 0, "数据为空，请重新选择");

        //判断锁定
        for (TXfRedNotificationEntity tXfRedNotificationEntity : filterData) {
            Asserts.isTrue(Objects.equals(RedNoApplyingStatus.APPLYING.getValue(),
                    tXfRedNotificationEntity.getApplyingStatus()), "列表中存在已申请中单据，请在申请成功后再进行操作");
            Asserts.isTrue(Objects.equals(ApproveStatus.ALREADY_ROLL_BACK.getValue(),
                    tXfRedNotificationEntity.getApproveStatus()), "列表中存在已撤销的单据，重新勾选后再进行操作");
        }

        // 发送状态更新消息-撤销申请
        filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_APPLY
                , entity));

        List<TXfRedNotificationEntity> entityList =
                filterData.stream().filter(item -> item.getLockFlag() == 1).collect(Collectors.toList());
        RevokeRequest revokeRequest = buildRevokeRequestAndLogs(entityList, model);
        TaxWareResponse rollbackResponse = null;
        try {
            rollbackResponse = taxWareService.rollback(revokeRequest);
        } catch (Exception e) {
            log.error("撤销V2失败:{}-", e.getMessage(), e);
            rollbackResponse = new TaxWareResponse();
            rollbackResponse.setCode("-1");
            rollbackResponse.setMessage(e.getMessage());
        }

        //把红字信息表的状态设置为申请中或者申请失败
        TXfRedNotificationEntity record = new TXfRedNotificationEntity();
        record.setApplyingStatus(Objects.equals(TaxWareCode.SUCCESS, rollbackResponse.getCode()) ?
                RedNoApplyingStatus.APPLYING.getValue() : RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
        record.setApplyRemark(rollbackResponse.getMessage());
        LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        List<Long> collect = entityList.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        updateWrapper.in(TXfRedNotificationEntity::getId, collect);
        getBaseMapper().update(record, updateWrapper);
        //返回给业务系统
        if (Objects.equals(TaxWareCode.SUCCESS, rollbackResponse.getCode())) {
            return Response.ok("请求成功", revokeRequest.getSerialNo());
        } else {
            // 更新流水.全部失败
            updateRequestFail(revokeRequest.getSerialNo(), rollbackResponse.getMessage());

            // 发送状态更新消息-撤销申请
            filterData.forEach(entity -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_FAILED, entity));

            return Response.failed("请求失败，" + rollbackResponse.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Response<String> deleteById(RedNotificationDeleteRequest request, String username, String loginname) {
        //判断用户信息是否有配置
        List<String> redAuthUserList = Arrays.asList(redAuthUser.split(","));
        if (!(redAuthUserList.contains(username) || redAuthUserList.contains(loginname))) {
            return Response.failed("无权限操作");
        }
        AtomicInteger totalDelete = new AtomicInteger(0);
        //根据ID循环删除，先删除主表，再删除明细表
        if (request != null && request.getRedId() != null) {
            for (String id : request.getRedId()) {
                TXfRedNotificationEntity tXfRedNotificationEntity = getBaseMapper().selectById(id);
                if (!Objects.equals(tXfRedNotificationEntity.getApplyingStatus(),
                        RedNoApplyingStatus.WAIT_TO_APPLY.getValue())) {
                    throw new EnhanceRuntimeException("结算单[" + tXfRedNotificationEntity.getBillNo() + "]已申请，不允许操作");
                }
                if (!Objects.equals(tXfRedNotificationEntity.getInvoiceOrigin(), InvoiceOrigin.IMPORT.getValue())) {
                    throw new EnhanceRuntimeException("结算单[" + tXfRedNotificationEntity.getBillNo() + "]类型错误，不允许操作");
                }
                if (tXfRedNotificationEntity != null && (getBaseMapper().deleteById(id) > 0)) {
                    LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
                    redNotificationItemService.getBaseMapper().delete(queryWrapper);
                    totalDelete.addAndGet(1);
                }
            }
        } else {//批量条件删除
            QueryWrapper<TXfRedNotificationEntity> notificationEntityLambdaQueryWrapper =
                    getNotificationEntityLambdaQueryWrapper(request.getQueryModel());
            List<TXfRedNotificationEntity> tXfRedNotificationEntityList =
                    getBaseMapper().selectList(notificationEntityLambdaQueryWrapper);
            Optional.ofNullable(tXfRedNotificationEntityList).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
                if (!Objects.equals(txfRedNotificationEntity.getApplyingStatus(),
                        RedNoApplyingStatus.WAIT_TO_APPLY.getValue())) {
                    throw new EnhanceRuntimeException("结算单[" + txfRedNotificationEntity.getBillNo() + "]已申请，不允许操作");
                }
                if (!Objects.equals(txfRedNotificationEntity.getInvoiceOrigin(), InvoiceOrigin.IMPORT.getValue())) {
                    throw new EnhanceRuntimeException("结算单[" + txfRedNotificationEntity.getBillNo() + "]类型错误，不允许操作");
                }
                if (txfRedNotificationEntity != null && (getBaseMapper().deleteById(txfRedNotificationEntity.getId()) > 0)) {
                    LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId, txfRedNotificationEntity.getId());
                    redNotificationItemService.getBaseMapper().delete(queryWrapper);
                    totalDelete.addAndGet(1);
                }
            }));
        }
        return Response.ok("删除成功数量：" + totalDelete.get());
    }

    private RedGeneratePdfDetailInfo merge(List<TXfRedNotificationDetailEntity> treatedRedNoDetails) {
        BigDecimal sumAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal sumTaxAmount = BigDecimal.ZERO;

        boolean isMixedRate = false;
        BigDecimal taxRate = null;

        for (TXfRedNotificationDetailEntity redNoDetailInfo : treatedRedNoDetails) {
            sumAmountWithoutTax = sumAmountWithoutTax.add(redNoDetailInfo.getAmountWithoutTax());
            sumTaxAmount = sumTaxAmount.add(redNoDetailInfo.getTaxAmount());
            if (Objects.isNull(taxRate)) {
                taxRate = redNoDetailInfo.getTaxRate();
            } else {
                if (!isMixedRate) {
                    if (!taxRate.equals(redNoDetailInfo.getTaxRate())) {
                        isMixedRate = true;
                    }
                }
            }
        }

        RedGeneratePdfDetailInfo combineEntity = new RedGeneratePdfDetailInfo();
        combineEntity.setAmountWithoutTax(sumAmountWithoutTax.toPlainString());
        combineEntity.setTaxAmount(sumTaxAmount.toPlainString());
        combineEntity.setCargoName("详见对应正数发票及清单");
//        combineEntity.setZeroTax("");
        if (!isMixedRate) {
            combineEntity.setTaxRate(taxRate.toPlainString());
        }
//        treatedRedNoDetails.clear();
        return combineEntity;
    }


    public Boolean updatePdfUrl(Long id) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .eq(TXfRedNotificationEntity::getId, id)
                .oneOpt()
                .map(entity -> {
                    List<TXfRedNotificationDetailEntity> item =
                            new LambdaQueryChainWrapper<>(redNotificationItemService.getBaseMapper())
                                    .eq(TXfRedNotificationDetailEntity::getApplyId, entity.getId())
                                    .list();
                    return getPdf(entity, item).map(pdfUrl -> {
                        TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                        record.setPdfUrl(pdfUrl);
                        record.setId(id);
                        log.info("更新pdf路径链接");
                        return updateById(record);
                    });
                }).isPresent();
    }

    /**
     * 根据预制发票ID查询对应红字信息表撤销结果
     *
     * @param pidList 预制发票ID集合
     */
    public R<List<RedNotificationRollbackFailResult>> getRollbackResult(List<String> pidList) {
        LambdaQueryWrapper<TXfRedNotificationEntity> redNotificationQueryWrapper =
                Wrappers.lambdaQuery(TXfRedNotificationEntity.class).in(TXfRedNotificationEntity::getPid, pidList);
        List<TXfRedNotificationEntity> redNotificationEntities = this.list(redNotificationQueryWrapper);
        if (CollectionUtil.isEmpty(redNotificationEntities)) {
            return R.ok(null, "红字信息表为空");
        }

        LambdaQueryWrapper<TXfRedNotificationLogEntity> logEntityLambdaQueryWrapper =
                Wrappers.lambdaQuery(TXfRedNotificationLogEntity.class)
                        .in(TXfRedNotificationLogEntity::getApplyId,
                                redNotificationEntities.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList()))
                        .eq(TXfRedNotificationLogEntity::getApplyType, ApplyType.ROLL_BACK.getValue());
        List<TXfRedNotificationLogEntity> redNotificationLogEntities =
                redNotificationLogService.list(logEntityLambdaQueryWrapper);
        if (CollectionUtil.isEmpty(redNotificationEntities)) {
            return R.ok(null, "红字信息表尚未发起撤销请求");
        }

        if (redNotificationLogEntities.stream().anyMatch(entity -> Integer.valueOf(1).equals(entity.getStatus()))) {
            return R.fail("红字信息表正在撤销中");
        }
        List<RedNotificationRollbackFailResult> rollbackFailResults =
                redNotificationLogEntities.stream().filter(entity -> Integer.valueOf(3).equals(entity.getStatus()))
                        .map(entity -> {
                            RedNotificationRollbackFailResult result = new RedNotificationRollbackFailResult();
                            result.setRedNotificationNo(entity.getRedNotificationNo());
                            result.setRemark(entity.getProcessRemark());
                            return result;
                        }).collect(Collectors.toList());
        return CollectionUtil.isEmpty(rollbackFailResults) ? R.fail("红字信息表正在撤销中") : R.ok(rollbackFailResults,
                "红字信息表撤销失败信息");
    }
}