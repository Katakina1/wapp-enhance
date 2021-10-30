package com.xforceplus.wapp.modules.rednotification.service;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.*;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
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
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommSettlementService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    TaxWareService taxWareService;
    @Autowired
    IDSequence iDSequence;
    @Autowired
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
    RedisTemplate redisTemplate;

    private static final Integer MAX_PDF_RED_NO_SIZE = 100;

    private static final String GENERATE_PDF_KEY = "generate_pdf_key";
    private static final String APPLY_REDNOTIFICATION_KEY = "apply_rednotification_key";
    private static final String EXPORT_REDNOTIFICATION_KEY = "export_rednotification_key";

    @Value("${wapp.rednotification.maxApply}")
    private Integer maxApply;



    public Response add(AddRedNotificationRequest request) {

       // 保存红字信息 进入待审核
        List<TXfRedNotificationEntity> listMain = Lists.newLinkedList();
        List<TXfRedNotificationDetailEntity> listItem = Lists.newLinkedList();
        List<RedNotificationInfo> redNotificationInfoList = request.getRedNotificationInfoList();
        redNotificationInfoList.stream().forEach(info->{
            TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationMainMapper.mainInfoToEntity(info.getRednotificationMain());
            Long id = iDSequence.nextId();
            tXfRedNotificationEntity.setId(id);
            tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
            tXfRedNotificationEntity.setStatus(1);
            tXfRedNotificationEntity.setLockFlag(LockFlag.NORMAL.getValue());
            tXfRedNotificationEntity.setApproveStatus(ApproveStatus.OTHERS.getValue());
            tXfRedNotificationEntity.setCreateDate(new Date());
            tXfRedNotificationEntity.setUpdateDate(new Date());
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationMainMapper.itemInfoToEntityList(info.getRedNotificationItemList());
            tXfRedNotificationDetailEntities.stream().forEach(item->{
                item.setApplyId(id);
            });
            listMain.add(tXfRedNotificationEntity);
            listItem.addAll(tXfRedNotificationDetailEntities);
        });

        saveBatch(listMain);
        redNotificationItemService.saveBatch(listItem);
        //判断是否自动申请
        if(request.getAutoApplyFlag() ==1){
            // 申请请求
            RedNotificationApplyReverseRequest applyRequest = new RedNotificationApplyReverseRequest();

            RedNotificationMain rednotificationMain = request.getRedNotificationInfoList().get(0).getRednotificationMain();
            // 获取在线终端
            GetTerminalResponse terminalResponse = taxWareService.getTerminal(rednotificationMain.getPurchaserTaxNo());
            if (Objects.equals(TaxWareCode.SUCCESS,terminalResponse.getCode())) {

                for (GetTerminalResponse.ResultDTO.TerminalListDTO item : terminalResponse.getResult().getTerminalList()) {
                    GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO = !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0) : null;
                    if (deviceDTO != null) {
                        applyRequest.setDeviceUn(deviceDTO.getDeviceUn());
                        applyRequest.setTerminalUn(item.getTerminalUn());
                        break;
                    }
                }
            }
            if (!StringUtils.isEmpty(applyRequest.getDeviceUn())){
                QueryModel queryModel = new QueryModel();
                List<Long> pidList = request.getRedNotificationInfoList().stream().map(item -> Long.parseLong(item.getRednotificationMain().getPid())).collect(Collectors.toList());
                queryModel.setPidList(pidList);
                applyRequest.setQueryModel(queryModel);
            }else {
                // 终端不在线更新到红字信息表
                List<Long> redIdList = listMain.stream().map(item -> item.getId()).collect(Collectors.toList());
                TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                record.setApplyRemark("税盘不在线");
                LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(TXfRedNotificationEntity::getId,redIdList);
                getBaseMapper().update(record,updateWrapper);
                throw new RRException(String.format("未获取税号[%s]的在线终端",rednotificationMain.getPurchaserTaxNo()));
            }
            //申请
            return applyByPage(applyRequest,true);
        }
        return Response.ok("新增成功");
    }

    public Response applyByPage(RedNotificationApplyReverseRequest request,boolean autoFlag) {
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
        if (filterData.size() > maxApply){
            return  Response.failed("单次申请最大支持:"+maxApply);
        }

        //自动申请没有上下文
        log.info("申请标识:{}",autoFlag);
        if( !autoFlag){
            String loginName = UserUtil.getLoginName();
            String key = APPLY_REDNOTIFICATION_KEY+loginName;
            if (redisTemplate.opsForValue().get(key) != null){
                return Response.failed("申请红字信息操作频率过高,请耐心等待申请结果后重试");
            }else {
                redisTemplate.opsForValue().set(key,GENERATE_PDF_KEY,3, TimeUnit.SECONDS);
            }
        }

        List<List<TXfRedNotificationEntity>> partition = Lists.partition(filterData, 50);
        if (partition.size()>1){
            CompletableFuture<Response> cfA = CompletableFuture.supplyAsync(() -> applyByBatch(partition.get(0),request));
            CompletableFuture<Response> cfB = CompletableFuture.supplyAsync(() -> applyByBatch(partition.get(1),request));

            Response response =  new Response();
            try {
                    cfA.thenAcceptBoth(cfB, (resultA, resultB) -> {
                    if (resultA.getCode() == 1 && resultB.getCode() == 1) {
                        response.setCode(Response.OK);
                        response.setMessage("请求成功");
                    } else if (resultA.getCode() == 0 && resultB.getCode() == 0) {
                        response.setCode(Response.Fail);
                        response.setMessage("申请失败");
                    } else {
                        response.setCode(Response.Fail);
                        response.setMessage("部分成功,失败原因：" + (resultA.getCode() == 0 ? resultA.getMessage() : resultB.getMessage()));
                    }
                }).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return response;
        }else {
            return  applyByBatch(filterData,request);
        }

    }

    /**
     *  分批申请
     * @param filterData
     * @param request
     * @return
     */
    Response applyByBatch(List<TXfRedNotificationEntity> filterData , RedNotificationApplyReverseRequest request){
        //构建税件请求
        if (!CollectionUtils.isEmpty(filterData)){
            ApplyRequest applyRequest = new ApplyRequest();
            applyRequest.setDeviceUn(request.getDeviceUn());
            applyRequest.setTerminalUn(request.getTerminalUn());
            applyRequest.setSerialNo(String.valueOf(iDSequence.nextId()));
            List<RedInfo> redInfoList = buildRedInfoList(filterData,applyRequest);
            applyRequest.setRedInfoList(redInfoList);
            TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
            if (Objects.equals(TaxWareCode.SUCCESS,taxWareResponse.getCode())){
                return  Response.ok("请求成功" , applyRequest.getSerialNo());
            }else {
                //更新流水.全部失败
                updateRequestFail(applyRequest.getSerialNo(), taxWareResponse);
                //更新失败原因到主表
                List<Long> redIdList = redInfoList.stream().map(item -> Long.parseLong(item.getPid())).collect(Collectors.toList());
                TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                record.setApplyRemark(taxWareResponse.getMessage());
                LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(TXfRedNotificationEntity::getId,redIdList);
                getBaseMapper().update(record,updateWrapper);

                return  Response.failed(taxWareResponse.getMessage());
            }
        }else {
            return  Response.ok("未找到申请数据");
        }
    }






    public Response rollback(RedNotificationApplyReverseRequest request) {
        QueryModel queryModel = request.getQueryModel();
//        queryModel.setLockFlag(1);
//        queryModel.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
//        queryModel.setApproveStatus(ApproveStatus.APPROVE_PASS.getValue());
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        List<TXfRedNotificationEntity> entityList = filterData.stream().filter(item ->
                item.getLockFlag() == 1
                        && item.getApproveStatus() == ApproveStatus.APPROVE_PASS.getValue()
        ).collect(Collectors.toList());
        if (filterData.size()>0 && entityList.size() != filterData.size()){
            return Response.failed("锁定中或未审核通过 不允许撤销");
        }
        RevokeRequest revokeRequest = buildRevokeRequestAndLogs(entityList,request);
        TaxWareResponse rollbackResponse = taxWareService.rollback(revokeRequest);

        if (Objects.equals(TaxWareCode.SUCCESS,rollbackResponse.getCode())){
            return  Response.ok("请求成功" , revokeRequest.getSerialNo());
        }else {
            //更新流水.全部失败
            updateRequestFail(revokeRequest.getSerialNo(), rollbackResponse);
            return  Response.failed(rollbackResponse.getMessage());
        }

    }



    /**
     * todo
     * 红字信息申请弹窗
     * @param queryModel
     * @return
     */
    public Response<GetTerminalResult> getTerminals(QueryModel queryModel) {
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        if (CollectionUtils.isEmpty(filterData)){
            return Response.failed("未筛选到数据");
        }
        List<String> collect = filterData.stream().map(TXfRedNotificationEntity::getPurchaserTaxNo).distinct().collect(Collectors.toList());
        if (collect.size()>1){
            return Response.failed("所选购方税号不唯一,无法获取唯一终端");
        }
        GetTerminalResult getTerminalResult = new GetTerminalResult();
        // 补充弹窗信息
        TXfRedNotificationEntity record = filterData.get(0);
        getTerminalResult.setNewCompanyName(record.getPurchaserName());
        getTerminalResult.setInvoiceType(record.getInvoiceType());
        getTerminalResult.setInvoiceCount(filterData.size());
        //统计金额信息
        BigDecimal totalAmountWithTax = BigDecimal.ZERO ;
        BigDecimal totalAmountWithoutTax = BigDecimal.ZERO ;
        BigDecimal totalTaxAmount = BigDecimal.ZERO ;
        for (TXfRedNotificationEntity item : filterData) {
            totalAmountWithTax = totalAmountWithTax.add(item.getAmountWithTax());
            totalAmountWithoutTax =totalAmountWithoutTax.add(item.getAmountWithoutTax());
            totalTaxAmount = totalTaxAmount.add(item.getTaxAmount());
        }

        getTerminalResult.setAmountWithTax(totalAmountWithTax.toPlainString());
        getTerminalResult.setAmountWithoutTax(totalAmountWithoutTax.toPlainString());
        getTerminalResult.setTaxAmount(totalTaxAmount.toPlainString());


        GetTerminalResponse terminal = taxWareService.getTerminal(collect.get(0));
        List<TerminalDTO> terminalList = Lists.newLinkedList();
        if (Objects.equals("TXWR000000",terminal.getCode())){
            terminal.getResult().getTerminalList().forEach(item->{
                TerminalDTO terminalDTO = new TerminalDTO();
                terminalDTO.setTerminalType(String.valueOf(item.getTerminalType()));

                GetTerminalResponse.ResultDTO.DeviceDTO deviceDTO = !CollectionUtils.isEmpty(item.getOnlineDeviceList()) ? item.getOnlineDeviceList().get(0):null;
                terminalDTO.setDeviceUn(deviceDTO!=null?deviceDTO.getDeviceUn():null);
                terminalDTO.setTerminalUn(item.getTerminalUn());
                terminalDTO.setTerminalName(item.getTerminalName());
                terminalDTO.setDirectOnlineFlag(deviceDTO!=null?1:0);
                terminalList.add(terminalDTO);
            });
        }
        getTerminalResult.setTerminalList(terminalList);

        return Response.ok("成功",getTerminalResult);
    }


    //获取红字信息数据(主数据)
    List<TXfRedNotificationEntity> getFilterData(QueryModel queryModel){
        //判读如果 getIncludes 没有值，queryModel 全选标识没传 。默认true 逻辑
        if (CollectionUtils.isEmpty(queryModel.getIncludes()) && queryModel.getIsAllSelected()==null){
            queryModel.setIsAllSelected(true);
        }else if (!CollectionUtils.isEmpty(queryModel.getIncludes()) && queryModel.getIsAllSelected()==null){
            queryModel.setIsAllSelected(false);
        }

        //全选
        if (queryModel.getIsAllSelected()){
            QueryWrapper<TXfRedNotificationEntity> queryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
            return getBaseMapper().selectList(queryWrapper);
        }else {
            // id 勾选
            return getBaseMapper().selectBatchIds(queryModel.getIncludes());
        }
    }

    private QueryWrapper<TXfRedNotificationEntity> getNotificationEntityLambdaQueryWrapper(QueryModel queryModel) {
        QueryWrapper<TXfRedNotificationEntity> queryWrapper = new QueryWrapper<>();
        if (queryModel.getInvoiceOrigin()!=null){
            queryWrapper.eq(TXfRedNotificationEntity.INVOICE_ORIGIN, queryModel.getInvoiceOrigin());
        }
        if (!StringUtils.isEmpty(queryModel.getCompanyCode())){
            queryWrapper.eq(TXfRedNotificationEntity.COMPANY_CODE, queryModel.getCompanyCode());
        }
        if (!StringUtils.isEmpty(queryModel.getPurchaserName())){
            queryWrapper.eq(TXfRedNotificationEntity.PURCHASER_NAME, queryModel.getPurchaserName());
        }

        if (!StringUtils.isEmpty(queryModel.getSellerName())){
            queryWrapper.eq(TXfRedNotificationEntity.SELLER_NAME, queryModel.getSellerName());
        }

        if (!StringUtils.isEmpty(queryModel.getRedNotificationNo())){
            queryWrapper.eq(TXfRedNotificationEntity.RED_NOTIFICATION_NO, queryModel.getRedNotificationNo());
        }
        if (!StringUtils.isEmpty(queryModel.getBillNo())){
            queryWrapper.eq(TXfRedNotificationEntity.BILL_NO, queryModel.getBillNo());
        }
        if (queryModel.getPaymentTime()!=null){
            // 1634860800000
            queryWrapper.eq(TXfRedNotificationEntity.PAYMENT_TIME,  new Date(queryModel.getPaymentTime()));
        }
        if (!CollectionUtils.isEmpty(queryModel.getPidList())){
            queryWrapper.in(TXfRedNotificationEntity.PID,queryModel.getPidList());
        }
        if (queryModel.getApproveStatus()!=null){
            queryWrapper.eq(TXfRedNotificationEntity.APPROVE_STATUS,queryModel.getApproveStatus());
        }
        if (queryModel.getApplyingStatus()!=null){
            queryWrapper.eq(TXfRedNotificationEntity.APPLYING_STATUS,queryModel.getApplyingStatus());
        }
        if (queryModel.getLockFlag()!=null){
            queryWrapper.eq(TXfRedNotificationEntity.LOCK_FLAG,queryModel.getLockFlag());
        }
        if (!CollectionUtils.isEmpty(queryModel.getExcludes())){
            queryWrapper.notIn(TXfRedNotificationEntity.ID,queryModel.getExcludes());
        }
        //默认带上 正常条件
        queryWrapper.eq(TXfRedNotificationEntity.STATUS,1);

        return queryWrapper;
    }



    /**
     * 更新流失失败
     * @param  serialNo
     * @param taxWareResponse
     */
    private void updateRequestFail(String serialNo, TaxWareResponse taxWareResponse) {
        LambdaUpdateWrapper<TXfRedNotificationLogEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TXfRedNotificationLogEntity::getSerialNo, serialNo);
        TXfRedNotificationLogEntity record = new TXfRedNotificationLogEntity();
        record.setStatus(3);
        record.setProcessRemark(taxWareResponse.getMessage());
        redNotificationLogService.update(record,updateWrapper);
    }

    private List<RedInfo> buildRedInfoList(List<TXfRedNotificationEntity> filterData,ApplyRequest applyRequest) {
        ArrayList<RedInfo> redInfoList = Lists.newArrayList();
        ArrayList<TXfRedNotificationLogEntity> logList = Lists.newArrayList();

        for (TXfRedNotificationEntity notificationEntity : filterData) {
            RedInfo redInfo = new RedInfo();
            redInfo.setPid(String.valueOf(notificationEntity.getId()));
            redInfo.setApplyType("0");
            redInfo.setDupTaxFlag("0");
          // 成品油
//          redInfo.setOilMemo();
            redInfo.setPurchaserName(notificationEntity.getPurchaserName());
            redInfo.setPurchaserTaxCode(notificationEntity.getPurchaserTaxNo());
            redInfo.setSellerName(notificationEntity.getSellerName());
            redInfo.setSellerTaxCode(notificationEntity.getSellerTaxNo());

            redInfo.setOriginalInvoiceType(notificationEntity.getOriginInvoiceType());
            redInfo.setOriginalInvoiceCode(notificationEntity.getOriginInvoiceCode());
            redInfo.setOriginalInvoiceNo(notificationEntity.getOriginInvoiceNo());
            redInfo.setOriginalInvoiceDate(notificationEntity.getInvoiceDate());
            redInfo.setApplicationReason(String.valueOf(notificationEntity.getApplyType()));

            Amount amount = new Amount();
            amount.setTaxAmount(notificationEntity.getTaxAmount());
            amount.setAmountWithoutTax(notificationEntity.getAmountWithoutTax());
            amount.setAmountWithTax(notificationEntity.getAmountWithTax());
            redInfo.setAmount(amount);


            Tuple2<List<RedDetailInfo>,String> result= buildDetails(notificationEntity.getId());
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
        if (!CollectionUtils.isEmpty(ids)){
            // 先更新状态为申请中
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId,ids);
            TXfRedNotificationEntity entity = new TXfRedNotificationEntity();
            entity.setApplyingStatus(RedNoApplyingStatus.APPLYING.getValue());
            getBaseMapper().update(entity,updateWrapper);
            //如果是非导入,导入的申请流水号不变化
            ArrayList<Integer> invoiceOriginList = Lists.newArrayList(InvoiceOrigin.CLAIM.getValue(), InvoiceOrigin.AGREE.getValue(), InvoiceOrigin.EPD.getValue());
            updateWrapper.in(TXfRedNotificationEntity::getInvoiceOrigin,invoiceOriginList);
            entity.setSerialNo(applyRequest.getSerialNo());
            getBaseMapper().update(entity,updateWrapper);
        }


        redNotificationLogService.saveBatch(logList);
        return redInfoList;
    }

    private Tuple2<List<RedDetailInfo>,String> buildDetails(Long id) {
        ArrayList<RedDetailInfo> redItemInfoList = Lists.newArrayList();
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> detailMapper = new LambdaQueryWrapper<>();
        detailMapper.eq(TXfRedNotificationDetailEntity::getApplyId, id);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationItemService.getBaseMapper().selectList(detailMapper);
        for (TXfRedNotificationDetailEntity detailEntity : tXfRedNotificationDetailEntities) {
            RedDetailInfo redDetailInfo = new RedDetailInfo();

            DetailAmount detailAmount = new DetailAmount();
            detailAmount.setTaxAmount(detailEntity.getTaxAmount());
            detailAmount.setAmountWithoutTax(detailEntity.getAmountWithoutTax());
            detailAmount.setUnitPrice(detailEntity.getUnitPrice());
            detailAmount.setQuantity(detailEntity.getNum());
            detailAmount.setTaxDeduction(detailEntity.getDeduction());
            redDetailInfo.setDetailAmount(detailAmount);

            Production production = new Production();
            production.setProductionCode(detailEntity.getGoodsTaxNo());
            production.setProductionName(detailEntity.getGoodsName());
            redDetailInfo.setProduction(production);

            Tax tax = new Tax();
            tax.setPreferentialTax(detailEntity.getTaxPre()==1?true:false);
            tax.setTaxPolicy(detailEntity.getTaxPreCon());
            tax.setTaxRate(detailEntity.getTaxRate());
            tax.setZeroTax(detailEntity.getZeroTax()==null?"":String.valueOf(detailEntity.getZeroTax()));
            tax.setTaxCodeVersion(detailEntity.getGoodsNoVer());
            redDetailInfo.setTax(tax);

            redItemInfoList.add(redDetailInfo);
        }
        String goodsNoVer = tXfRedNotificationDetailEntities.get(0).getGoodsNoVer();
        return Tuple.of(redItemInfoList,goodsNoVer);
    }

    public Response<SummaryResult> summary(QueryModel queryModel) {
//        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
//        Map<Integer, List<TXfRedNotificationEntity>> listMap = filterData.stream().collect(Collectors.groupingBy(TXfRedNotificationEntity::getApplyingStatus));

        QueryWrapper<TXfRedNotificationEntity> queryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
        queryWrapper.select("applying_status , count(1) as count").groupBy(TXfRedNotificationEntity.APPLYING_STATUS);
        List<Map<String, Object>> listMap = getBaseMapper().selectMaps(queryWrapper);

        //默认为0  1.未申请 2.申请中 3.已申请 4.撤销待审核
        SummaryResult summaryResult = new SummaryResult(0,0,0,0,0);


        listMap.forEach(itemMap->{
            Short applying_status =(Short) itemMap.get("applying_status");
            Integer count = (Integer)itemMap.get("count");
            switch (applying_status){
                case 1:
                    summaryResult.setApplyPending(count);
                    break;
                case 2:
                    summaryResult.setApplying(count);
                    break;
                case 3:
                    summaryResult.setApplied(count);
                    break;
                case 4:
                    summaryResult.setWaitApprove(count);
                    break;
            }

        });
       int total = summaryResult.getApplyPending()+summaryResult.getApplying()+summaryResult.getApplied()+summaryResult.getWaitApprove();
       summaryResult.setTotal(total);
       return Response.ok("成功",summaryResult);
    }

    public Response<PageResult<RedNotificationMain>> listData(QueryModel queryModel) {
        if (queryModel.getPageNo()==null){
            queryModel.setPageNo(1);
            queryModel.setPageSize(20);
        }
        QueryWrapper<TXfRedNotificationEntity> notificationEntityLambdaQueryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
        Page<TXfRedNotificationEntity> page = new Page<>(queryModel.getPageNo(), queryModel.getPageSize());
        Page<TXfRedNotificationEntity> tXfRedNotificationEntityPage = getBaseMapper().selectPage(page, notificationEntityLambdaQueryWrapper);
        List<RedNotificationMain> redNotificationMains = redNotificationMainMapper.entityToMainInfoList(tXfRedNotificationEntityPage.getRecords());
        PageResult<RedNotificationMain> pageResult = PageResult.of(tXfRedNotificationEntityPage.getTotal(),redNotificationMains);
        return Response.ok("成功",pageResult);
    }

    public Response<RedNotificationInfo> detail(Long id) {
        TXfRedNotificationEntity tXfRedNotificationEntity = getBaseMapper().selectById(id);

        RedNotificationInfo redNotificationInfo = null;
        if (tXfRedNotificationEntity!=null){
            redNotificationInfo = new RedNotificationInfo();
            RedNotificationMain redNotificationMain = redNotificationMainMapper.entityToMainInfo(tXfRedNotificationEntity);
            if (StringUtils.isEmpty(redNotificationMain.getInvoiceDate())){
                redNotificationMain.setInvoiceDate(DateUtils.getCurentIssueDate());
            }

            LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TXfRedNotificationDetailEntity::getApplyId,id);
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationItemService.getBaseMapper().selectList(queryWrapper);
            List<RedNotificationItem> redNotificationItems = redNotificationMainMapper.entityToItemInfoList(tXfRedNotificationDetailEntities);
            redNotificationInfo.setRedNotificationItemList(redNotificationItems);
            redNotificationInfo.setRednotificationMain(redNotificationMain);
        }
        return Response.ok("成功",redNotificationInfo);
    }



    private RevokeRequest buildRevokeRequestAndLogs(List<TXfRedNotificationEntity> entityList, RedNotificationApplyReverseRequest request) {

        Long serialNo = iDSequence.nextId();

        RevokeRequest revokeRequest = new RevokeRequest();
        revokeRequest.setSerialNo(String.valueOf(serialNo));
        revokeRequest.setApplyTaxCode(entityList.get(0).getPurchaserTaxNo());
        revokeRequest.setTerminalUn(request.getTerminalUn());
        revokeRequest.setDeviceUn(request.getDeviceUn());

        ArrayList<TXfRedNotificationLogEntity> logList = Lists.newArrayList();
        ArrayList<RevokeRedNotificationInfo> revokeRedNotificationInfos = Lists.newArrayList();
        entityList.stream().forEach(entity->{
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
                ()->{
                    InputStream inputStream = null;
                    try {
                        inputStream = new BufferedInputStream(file.getInputStream());
                    } catch (IOException e) {
                        log.error("获取导入文件失败",e);
                    }
                    //实例化实现了AnalysisEventListener接口的类
                    ExcelListener excelListener = new ExcelListener(this,redNotificationMainMapper,checkMainService,longLongStringTuple3);
                    ExcelReader reader = new ExcelReader(inputStream,null,excelListener);
                    //读取信息
                    reader.read(new Sheet(1,1, ImportInfo.class));
                }
        );

        return Response.ok("导入成功,请到消息中心查看结果");
    }

    public Response downloadPdf(RedNotificationExportPdfRequest request) {
        Tuple3<Long, Long,String> tuple3 = null ;
        if (request.getAutoFlag()!=null && request.getAutoFlag()){
            //自动申请pdf 不需要插入日志 ，不校验频率
        }else {
            String loginName = UserUtil.getLoginName();
            String key = GENERATE_PDF_KEY+loginName;
            if (redisTemplate.opsForValue().get(key) != null){
                return Response.failed("生成pdf操作频率过高,请耐心等待申请结果后重试");
            }else {
                redisTemplate.opsForValue().set(key,GENERATE_PDF_KEY,3, TimeUnit.SECONDS);
            }
            //<logId,userId>
            tuple3 = exportCommonService.insertRequest(request);
        }

        Integer generateModel = request.getGenerateModel();
        QueryModel queryModel = request.getQueryModel();
        queryModel.setApplyingStatus(RedNoApplyingStatus.APPLIED.getValue());
        queryModel.setStatus(1);
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        if(filterData.size()>MAX_PDF_RED_NO_SIZE){
            return Response.failed("单次生成pdf的红字信息数目不得超过"+MAX_PDF_RED_NO_SIZE);
        }
        List<Long> applyList = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        //获取明细
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationDetailEntity::getApplyId, applyList);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationItemService.getBaseMapper().selectList(queryWrapper);
        Map<Long, List<TXfRedNotificationDetailEntity>> listItemMap = tXfRedNotificationDetailEntities.stream().collect(Collectors.groupingBy(TXfRedNotificationDetailEntity::getApplyId));
        String downLoadUrl = exportRedNoPdf(filterData, listItemMap, generateModel,tuple3);
        return Response.ok("导出成功,请在消息中心查看",downLoadUrl);
    }

    private String exportRedNoPdf(List<TXfRedNotificationEntity> applies, Map<Long, List<TXfRedNotificationDetailEntity>> detailMap, Integer generateModel,Tuple3<Long, Long,String> tuple3){

        List<ZipContentInfo> zipInfos = null;
        RedNoGeneratePdfModel pdfModel = ValueEnum.getEnumByValue(RedNoGeneratePdfModel.class, generateModel).orElse(RedNoGeneratePdfModel.Merge_All);
        switch (pdfModel){
            case Merge_All:
                Map<String, List<TXfRedNotificationEntity>> mergeAllMap = new HashMap<>();
                mergeAllMap.put("全部合并",applies);
                zipInfos = generateRedNoPdf(mergeAllMap, detailMap,pdfModel);
                break;
            case Split_By_Seller:
                Map<String, List<TXfRedNotificationEntity>> groupBySeller = applies.stream()
                        .collect(Collectors.groupingBy((redNotificationEntity)->{
                            if(StringUtils.isNotBlank(redNotificationEntity.getSellerName())){
                                return redNotificationEntity.getSellerName();
                            }else{
                                return "销方名称为空";
                            }
                        }));
                zipInfos = generateRedNoPdf(groupBySeller, detailMap,pdfModel);
                break;
            case Split_By_Purchaser:
                Map<String, List<TXfRedNotificationEntity>> groupByPurchaser = applies.stream()
                        .collect(Collectors.groupingBy((redNotificationEntity)->{
                            if(StringUtils.isNotBlank(redNotificationEntity.getPurchaserName())){
                                return redNotificationEntity.getPurchaserName();
                            }else{
                                return "购方名称为空";
                            }
                        }));
                zipInfos = generateRedNoPdf(groupByPurchaser, detailMap,pdfModel);
                break;
        }
        return makeRedNoPdfZip(zipInfos,tuple3);
    }


    private List<ZipContentInfo> generateRedNoPdf(Map<String, List<TXfRedNotificationEntity>> redNoMap, Map<Long, List<TXfRedNotificationDetailEntity>> detailsMap, RedNoGeneratePdfModel model){
        List<ZipContentInfo> zipContents = new CopyOnWriteArrayList<>();
        redNoMap.keySet().stream().forEach(head->{
            redNoMap.get(head).parallelStream().forEach(redNoApply->{
                if (StringUtils.isEmpty(redNoApply.getPdfUrl())){
                    RedNotificationGeneratePdfRequest request = buildRedNotificationGeneratePdfRequest(redNoApply,detailsMap.get(redNoApply.getId()));
                    log.info("开始生成红字信息pdf:"+request.getSerialNo());
                    long start = System.currentTimeMillis();
                    TaxWareResponse response = taxWareService.generatePdf(request);
                    log.info("红字信息生成pdf耗时:{}ms,流水号:{}",System.currentTimeMillis()-start, request.getSerialNo());
                    if( response.getCode()!=null && !Objects.equals(response.getCode(),TaxWareCode.SUCCESS)){
                        throw new RRException(response.getMessage());
                    }
                    TaxWareResponse.ResultDTO result;
                    String pdfUrl;
                    if(Objects.nonNull(result = response.getResult()) && StringUtils.isNotBlank(pdfUrl = result.getPdfUrl())){
                        String fileName;
//                    if(model == RedNoGeneratePdfModel.Merge_All){
//                        fileName = redNoApply.getPurchaserTaxNo() + ".pdf";
//                    }else{
                        fileName = format("{}.pdf",  redNoApply.getRedNotificationNo());
//                    }
                        ZipContentInfo zipInfo = new ZipContentInfo();
                        zipInfo.setFile(false);
                        zipInfo.setRelativePath(fileName);
                        zipInfo.setSourceUrl(pdfUrl);
                        zipContents.add(zipInfo);
                        //更新数据库
                        if (StringUtils.isEmpty(redNoApply.getPdfUrl())){
                            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                            record.setPdfUrl(pdfUrl);
                            record.setId(redNoApply.getId());
                            log.info("更新pdf路径链接");
                            updateById(record);
                        }

                    }
                }else {
                    //已经拿到pdf链接了，直接从数据库获取
                    ZipContentInfo zipInfo = new ZipContentInfo();
                    zipInfo.setFile(false);
                    String  fileName = format("{}.pdf",  redNoApply.getRedNotificationNo());
                    zipInfo.setRelativePath(fileName);
                    zipInfo.setSourceUrl(redNoApply.getPdfUrl());
                    zipContents.add(zipInfo);
                }
            });
        });
        return zipContents;
    }

    private String makeRedNoPdfZip(List<ZipContentInfo> zipContents,Tuple3<Long, Long,String> tuple3){
        //自动申请pdf 无上下文 不处理zip
        if (tuple3 == null){
            return "" ;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String zipFileNameWithOutSubfix = sdf.format(new Date());
        String zipFile = format("output/{}/{}/{}.zip",  "invoice-service", "redNoZip",zipFileNameWithOutSubfix);
        if(zipContents.size()>0){
            DownloadUrlUtils.commonZipFiles(zipContents,zipFile);
            //发送到消息中心
//            return DownloadUrlUtils.putFile(zipFile);
            // 插入日志记录
            String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String zipFileName = zipFileNameWithOutSubfix+".zip";
            String ftpFilePath = ftpPath+"/"+zipFileName;
            String s = exportCommonService.putFile(ftpPath,zipFile, zipFileName);

            if(s != null){
                String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.FAIL, null);
                exportCommonService.sendMessage(tuple3._1,tuple3._3,"红字信息表下载pdf失败",exportCommonService.getFailContent(s));
                return s;
            }else {
                String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.OK,ftpFilePath);
                exportCommonService.sendMessage(tuple3._1,tuple3._3,"红字信息表下载pdf成功", exportCommonService.getSuccContent());
                return "导出成功,请在消息中心查看";
            }
        }
        return null;
    }


    public static String format(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }

    private RedNotificationGeneratePdfRequest buildRedNotificationGeneratePdfRequest(TXfRedNotificationEntity apply, List<TXfRedNotificationDetailEntity> applyDetails){

        RedNotificationGeneratePdfRequest request = new RedNotificationGeneratePdfRequest();
        RequestHead head = new RequestHead();
        head.setDebug(null);
        head.setTenantId(taxWareService.tenantId);
        head.setTenantName(taxWareService.tenantName);
        request.setHead(head);

        RedGeneratePdfInfo redInfo = new RedGeneratePdfInfo();
        redInfo.setApplicant(apply.getApplyType());
        redInfo.setDate(apply.getInvoiceDate());

        List<RedGeneratePdfDetailInfo> detailInfos = applyDetails.stream().map(item->{
            RedGeneratePdfDetailInfo detailInfo = new RedGeneratePdfDetailInfo();
            detailInfo.setAmountWithoutTax(item.getAmountWithoutTax().toPlainString());
            detailInfo.setCargoName(item.getGoodsName());
            detailInfo.setQuantity(item.getNum().toPlainString());
            detailInfo.setTaxAmount(item.getTaxAmount().toPlainString());
            detailInfo.setTaxRate(item.getTaxRate().toPlainString());
            detailInfo.setUnitPrice(item.getUnitPrice().toPlainString());
            return detailInfo;
        }).collect(Collectors.toList());

        redInfo.setDetails(detailInfos);

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
     * @param request
     * @return
     */
    public Response export(RedNotificationExportPdfRequest request) {
        Tuple3<Long,Long,String> tuple3 = exportCommonService.insertRequest(request);

        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
//        List<Long> applyList = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
        //获取明细


        List<Long> redNoIds = new ArrayList<>();
        Map<Long,ExportInfo> exportInfoMap = Maps.newHashMap();

        List<ExportItemInfo> itemInfos = Lists.newArrayList();
        List<ExportInfo> exportInfos = filterData.stream().map(apply -> {
            ExportInfo dto = redNotificationMainMapper.mainEntityToExportInfo(apply);
            ApproveStatus applyStatus = ValueEnum.getEnumByValue(ApproveStatus.class, apply.getApproveStatus()).orElse(ApproveStatus.OTHERS);
            dto.setApproveStatus(applyStatus!=ApproveStatus.OTHERS?applyStatus.getDesc():"");

            if (StringUtils.isNotBlank(apply.getInvoiceType())){
                dto.setInvoiceType(ValueEnum.getEnumByValue(InvoiceType.class, apply.getInvoiceType()).get().getDescription());
            }
            if (StringUtils.isNotBlank(apply.getOriginInvoiceType())){
                dto.setOriginInvoiceType(ValueEnum.getEnumByValue(InvoiceType.class, apply.getOriginInvoiceType()).get().getDescription());
            }
            redNoIds.add(apply.getId());
            exportInfoMap.put(apply.getId(),dto);

            //封装1000一批次查询明细
            if (redNoIds.size()>1000){
                handleItemInfos(redNoIds, exportInfoMap, itemInfos);
            }

            return dto;
        }).collect(Collectors.toList());

        //最后一批次
        if (redNoIds.size()>0){
            handleItemInfos(redNoIds, exportInfoMap, itemInfos);
        }



        return writeExcel(exportInfos, itemInfos, new ExportInfo(), new ExportItemInfo(),tuple3);

    }

    private void handleItemInfos(List<Long> redNoIds, Map<Long, ExportInfo> exportInfoMap, List<ExportItemInfo> itemInfos) {
        LambdaQueryWrapper<TXfRedNotificationDetailEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationDetailEntity::getApplyId, redNoIds);
        List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationItemService.getBaseMapper().selectList(queryWrapper);
        Map<Long, List<TXfRedNotificationDetailEntity>> listItemMap = tXfRedNotificationDetailEntities.stream().collect(Collectors.groupingBy(TXfRedNotificationDetailEntity::getApplyId));

        redNoIds.stream().forEach(data->{
            // 获取明细
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities1 = listItemMap.get(data);
            ExportInfo tmpDto = exportInfoMap.get(data);
            tXfRedNotificationDetailEntities1.stream().forEach(item->{
                ExportItemInfo exportItemInfo = redNotificationMainMapper.detailEntityToExportInfo(item, tmpDto);
                itemInfos.add(exportItemInfo);
            });
        });
        // 清空批次
        redNoIds.clear();
        exportInfoMap.clear();
        listItemMap.clear();
    }


    private Response writeExcel(List<? extends BaseRowModel> list, List<? extends BaseRowModel> list2, BaseRowModel object, BaseRowModel object2,Tuple3<Long,Long,String> tuple3) {
        Long userId =tuple3._2;
        Long logId =tuple3._1;

        String fileName = "红字信息表";
        String sheetName = "红字信息主信息";
        String sheetName2 = "红字信息明细";

//        String businessId = String.valueOf(System.currentTimeMillis());
//        String filePath = "file/" + "walmart" + "/" + DateUtils.curDateMselStr17() + "/"  + fileName + "导出" + businessId + ".xlsx";

        //推送sftp
        final String excelFileName = ExcelExportUtil.getExcelFileName(userId, "红字信息表导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        String ftpFilePath = ftpPath + "/" + excelFileName;
        log.info("文件ftp路径{}",ftpFilePath);

        String localFilePath = ftpFilePath.substring(1);
        File localFile = new File(localFilePath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream(localFile);
        } catch (FileNotFoundException fnfException) {
            fnfException.printStackTrace();
            log.error("new FileOutputStream(localFile) err!");
        }
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);

        Sheet sheet = new Sheet(1, 0, object.getClass());

        sheet.setSheetName(sheetName);

        writer.write(list, sheet);

        Sheet sheet2 = new Sheet(2, 0, object2.getClass());
        sheet2.setSheetName(sheetName2);

        writer.write(list2, sheet2);
        writer.finish();
        String s = exportCommonService.putFile(ftpPath,localFilePath, excelFileName);

        try {
            out.close();
        } catch (IOException ioException) {
            log.info(" out.close() err!");
        }
        if(s != null){
            String userName = exportCommonService.updatelogStatus(logId, ExcelExportLogService.FAIL, null);
            exportCommonService.sendMessageWithUrl(tuple3._1,userName,"红字信息表导出失败",exportCommonService.getFailContent(s),null);
            return Response.failed(s);
        }else {
            String userName = exportCommonService.updatelogStatus(logId, ExcelExportLogService.OK,ftpFilePath);
            exportCommonService.sendMessage(tuple3._1,userName,"红字信息表导出成功", exportCommonService.getSuccContent());
            return Response.ok("导出成功,请在消息中心查看");
        }

    }

//
//    public static void main(String[] args) {
//        String ftpFilePath = "home/wappftp/wapp/excel/20211021040817/1_红字信息表导出_2021-10-21_1634803697146.xlsx";
//        File localFile = new File(ftpFilePath);
//        if (!localFile.getParentFile().exists()) {
//            boolean mkdirs = localFile.getParentFile().mkdirs();
//            System.out.println(mkdirs);
//        }
//    }


    /**
     * 确认
     * 驳回  回到已申请 。从待审批页面消失
     * @param request
     * @return
     */
    public Response<String> operation(RedNotificationConfirmRejectRequest request) {
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
        List<Long> list = filterData.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());

        //
        List<Long> pidList = filterData.stream().map(item->Long.parseLong(item.getPid())).collect(Collectors.toList());
        if (Objects.equals(OperationType.CONFIRM.getValue(),request.getOperationType())){
           // 确认 //自动尝试一次 //撤销待审核
            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setApproveStatus(ApproveStatus.APPROVE_PASS.getValue());
            record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId,list);
            int update = getBaseMapper().update(record, updateWrapper);

            // 同意删除预制发票
            try{
                commSettlementService.agreeDestroySettlementPreInvoiceByPreInvoiceId(pidList);
            }catch (EnhanceRuntimeException e){
                return Response.failed(e.getMessage());
            }

            RedNotificationApplyReverseRequest reverseRequest = new RedNotificationApplyReverseRequest();
            request.getQueryModel().setApproveStatus(null);
            reverseRequest.setQueryModel(request.getQueryModel());
            rollback(reverseRequest);
        }else {
            // 驳回 ，修改状态到已申请
            TXfRedNotificationEntity record = new TXfRedNotificationEntity();
            record.setApproveStatus(ApproveStatus.APPROVE_FAIL.getValue());
            LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(TXfRedNotificationEntity::getId,list);
            int update = getBaseMapper().update(record, updateWrapper);

            // 驳回保留红字预制发票
            try {
                commSettlementService.rejectDestroySettlementPreInvoiceByPreInvoiceId(pidList);
            }catch (EnhanceRuntimeException e){
                return Response.failed(e.getMessage());
            }

        }
        return Response.ok("操作成功");


    }
}