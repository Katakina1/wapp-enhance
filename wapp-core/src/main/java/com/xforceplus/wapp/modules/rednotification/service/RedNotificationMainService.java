package com.xforceplus.wapp.modules.rednotification.service;

import com.alibaba.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.ApplyType;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.rednotification.listener.ExcelListener;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.sequence.IDSequence;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
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



    public String add(AddRedNotificationRequest request) {

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
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationMainMapper.itemInfoToEntityList(info.getRedNotificationItemList());
            tXfRedNotificationDetailEntities.stream().forEach(item->{
                item.setApplyId(id);
            });
            listMain.add(tXfRedNotificationEntity);
            listItem.addAll(tXfRedNotificationDetailEntities);
        });

        saveBatch(listMain);
        redNotificationItemService.saveBatch(listItem);

        return "" ;
    }

    public Response applyByPage(RedNotificationApplyReverseRequest request) {
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
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
                item.getLockFlag() == 0
                        && item.getApplyingStatus() == RedNoApplyingStatus.APPLIED.getValue()
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
        //全选
        if (queryModel.getIsAllSelected()){
            LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
            return getBaseMapper().selectList(queryWrapper);
        }else {
            // id 勾选
            return getBaseMapper().selectBatchIds(queryModel.getIncludes());
        }
    }

    private LambdaQueryWrapper<TXfRedNotificationEntity> getNotificationEntityLambdaQueryWrapper(QueryModel queryModel) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (queryModel.getInvoiceOrigin()!=null){
            queryWrapper.eq(TXfRedNotificationEntity::getInvoiceOrigin, queryModel.getInvoiceOrigin());
        }
        if (!StringUtils.isEmpty(queryModel.getCompanyCode())){
            queryWrapper.eq(TXfRedNotificationEntity::getCompanyCode, queryModel.getCompanyCode());
        }
        if (!StringUtils.isEmpty(queryModel.getPurchaserName())){
            queryWrapper.eq(TXfRedNotificationEntity::getPurchaserName, queryModel.getPurchaserName());
        }

        if (!StringUtils.isEmpty(queryModel.getRedNotificationNo())){
            queryWrapper.eq(TXfRedNotificationEntity::getRedNotificationNo, queryModel.getRedNotificationNo());
        }
        if (!StringUtils.isEmpty(queryModel.getBillNo())){
            queryWrapper.eq(TXfRedNotificationEntity::getBillNo, queryModel.getBillNo());
        }
        if (queryModel.getPaymentTime()!=null){
            queryWrapper.gt(TXfRedNotificationEntity::getPaymentTime, queryModel.getPaymentTime());
        }
        if (queryModel.getPid() !=null){
            queryWrapper.eq(TXfRedNotificationEntity::getPid,queryModel.getPid());
        }
        if (queryModel.getApproveStatus()!=null){
            queryWrapper.eq(TXfRedNotificationEntity::getApproveStatus,queryModel.getApproveStatus());
        }
        if (queryModel.getApplyingStatus()!=null){
            queryWrapper.eq(TXfRedNotificationEntity::getApplyingStatus,queryModel.getApplyingStatus());
        }
        if (queryModel.getLockFlag()!=null){
            queryWrapper.eq(TXfRedNotificationEntity::getLockFlag,queryModel.getLockFlag());
        }

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
            tax.setZeroTax(String.valueOf(detailEntity.getZeroTax()));
            tax.setTaxCodeVersion(detailEntity.getGoodsNoVer());
            redDetailInfo.setTax(tax);

            redItemInfoList.add(redDetailInfo);
        }
        String goodsNoVer = tXfRedNotificationDetailEntities.get(0).getGoodsNoVer();
        return Tuple.of(redItemInfoList,goodsNoVer);
    }

    public Response<SummaryResult> summary(QueryModel queryModel) {
        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
        Map<Integer, List<TXfRedNotificationEntity>> listMap = filterData.stream().collect(Collectors.groupingBy(TXfRedNotificationEntity::getApplyingStatus));
        //默认为0  1.未申请 2.申请中 3.已申请 4.撤销待审核
        SummaryResult summaryResult = new SummaryResult(0,0,0,0,0);
        listMap.entrySet().forEach(entry->{
            switch (entry.getKey()){
                case 1:
                    summaryResult.setApplyPending(entry.getValue().size());
                    break;
                case 2:
                    summaryResult.setApplying(entry.getValue().size());
                    break;
                case 3:
                    summaryResult.setApplied(entry.getValue().size());
                    break;
                case 4:
                    summaryResult.setWaitApprove(entry.getValue().size());
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
        LambdaQueryWrapper<TXfRedNotificationEntity> notificationEntityLambdaQueryWrapper = getNotificationEntityLambdaQueryWrapper(queryModel);
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

    public void importNotification(MultipartFile file) {
//        InputStream inputStream = new BufferedInputStream(file.getInputStream());
//        //实例化实现了AnalysisEventListener接口的类
//        ExcelListener excelListener = new ExcelListener(userDao);
//        ExcelReader reader = new ExcelReader(inputStream,null,excelListener);
//        //读取信息
//        reader.read(new Sheet(1,1,User.class));
    }
}
