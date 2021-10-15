package com.xforceplus.wapp.modules.rednotification.service;

import com.alibaba.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.rednotification.listener.ExcelListener;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.sequence.IDSequence;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.sl.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RedNotificationMainService extends ServiceImpl<TXfRedNotificationDao, TXfRedNotificationEntity> {

    @Autowired
    RedNotificationMainMapper redNotificationMainMapper;
    @Autowired
    RedNotificationItemService redNotificationItemService;
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
            queryWrapper.eq(TXfRedNotificationEntity::getApplyingStatus,queryModel.getApproveStatus());
        }
        return queryWrapper;
    }

    public Response applyByPage(RedNotificationApplyReverseRequest request) {
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
        //构建税件请求
        if (!CollectionUtils.isEmpty(filterData)){
            ApplyRequest applyRequest = new ApplyRequest();
            applyRequest.setDeviceUn(request.getDeviceUn());
            applyRequest.setTerminalUn(request.getTerminalUn());
            applyRequest.setSerialNo(String.valueOf(iDSequence.nextId()));
            List<RedInfo> redInfoList = buildRedInfoList(filterData);
            applyRequest.setRedInfoList(redInfoList);
            TaxWareResponse taxWareResponse = taxWareService.applyRedInfo(applyRequest);
            if (Objects.equals(TaxWareCode.SUCCESS,taxWareResponse.getCode())){
                return  Response.ok("请求成功" , applyRequest.getSerialNo());
            }else {
                return  Response.failed(taxWareResponse.getMessage());
            }
        }else {
            return  Response.ok("未找到申请数据");
        }

    }

    private List<RedInfo> buildRedInfoList(List<TXfRedNotificationEntity> filterData) {
        ArrayList<RedInfo> redInfoList = Lists.newArrayList();
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
            //明细字段
//           redInfo.setTaxCodeVersion();
            redInfo.setOriginalInvoiceType(notificationEntity.getOriginInvoiceType());
            redInfo.setOriginalInvoiceCode(notificationEntity.getOriginInvoiceCode());
            redInfo.setOriginalInvoiceNo(notificationEntity.getOriginInvoiceNo());
            redInfo.setOriginalInvoiceDate(notificationEntity.getInvoiceDate());
            redInfo.setApplicationReason(String.valueOf(notificationEntity.getApplyType()));

            Amount amount = new Amount();
//            amount.setTaxAmount(notificationEntity.getTaxAmount());
//            redInfo.setAmount();
//            redInfo.setDetails();

            redInfoList.add(redInfo);
        }
        return redInfoList;
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

    public Response rollback(RedNotificationApplyReverseRequest request) {

        return null;
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
