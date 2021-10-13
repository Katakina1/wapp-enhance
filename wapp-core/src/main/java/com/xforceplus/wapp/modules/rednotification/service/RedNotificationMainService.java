package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;
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


    public String add(AddRedNotificationRequest request) {

       // 保存红字信息 进入待审核
        List<TXfRedNotificationEntity> listMain = Lists.newLinkedList();
        List<TXfRedNotificationDetailEntity> listItem = Lists.newLinkedList();
        List<RedNotificationInfo> redNotificationInfoList = request.getRedNotificationInfoList();
        redNotificationInfoList.stream().forEach(info->{
            TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationMainMapper.mainInfoToEntity(info.getRednotificationMain());
            List<TXfRedNotificationDetailEntity> tXfRedNotificationDetailEntities = redNotificationMainMapper.itemInfoToEntityList(info.getRedNotificationItemList());

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
//        List<TXfRedNotificationEntity> filterData = getFilterData(queryModel);
//        List<String> collect = filterData.stream().map(TXfRedNotificationEntity::getPurchaserTaxNo).distinct().collect(Collectors.toList());
//        if (collect.size()>1){
//            return Response.failed("所选购方税号不唯一,无法获取唯一终端");
//        }
        GetTerminalResult getTerminalResult = new GetTerminalResult();

        GetTerminalResponse terminal = taxWareService.getTerminal("91420111271850146W");
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
            LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
            if (queryModel.getInvoiceOrigin()!=null){
                queryWrapper.eq(TXfRedNotificationEntity::getInvoiceOrigin,queryModel.getInvoiceOrigin());
            }
            if (StringUtils.isEmpty(queryModel.getCompanyCode())){
                queryWrapper.eq(TXfRedNotificationEntity::getCompanyCode,queryModel.getCompanyCode());
            }
            //todo 其他条件

            return getBaseMapper().selectList(queryWrapper);
        }else {
            // id 勾选
            return getBaseMapper().selectBatchIds(queryModel.getIncludes());
        }
    }

    public Response applyByPage(RedNotificationApplyReverseRequest request) {
        List<TXfRedNotificationEntity> filterData = getFilterData(request.getQueryModel());
        return  Response.ok("");
    }
}
