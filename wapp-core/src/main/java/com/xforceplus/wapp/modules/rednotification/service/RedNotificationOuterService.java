package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.LockFlag;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.rednotification.log.LogOperate;
import com.xforceplus.wapp.modules.rednotification.log.LogOperateType;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import com.xforceplus.wapp.modules.rednotification.model.QueryModel;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationApplyReverseRequest;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedNotificationOuterService {
    @Autowired
    RedNotificationMainService redNotificationService;

    /**
     * 新增红字信息
     */
    @LogOperate(value = "对外接口新增红字信息",type = LogOperateType.ADD)
    public Response<String> add(AddRedNotificationRequest request) {
        Response add = redNotificationService.add(request);
        log.info("对外接口新增红字信息返回:{}",JsonUtil.toJsonStr(add));
        return add ;
    }

    /**
     * 红字信息撤销
     * pid 预制发票id
     */
    @LogOperate(value = "对外接口红字信息撤销",type = LogOperateType.MODIFY)
    public Response<String> rollback(Long pid) {
        RedNotificationApplyReverseRequest redNotificationApplyReverseRequest = new RedNotificationApplyReverseRequest();
        QueryModel queryModel = new QueryModel();
        ArrayList<Long> pidList = Lists.newArrayList(pid);
        queryModel.setPidList(pidList);
        redNotificationApplyReverseRequest.setQueryModel(queryModel);

        Response rollback = null;
        try {
            rollback = redNotificationService.rollback(redNotificationApplyReverseRequest);
        } catch (Exception e) {
            log.error("撤销失败",e);
            return Response.failed(e.getMessage());
        }
        log.info("对外接口红字信息撤销返回:{}", rollback);
        return rollback ;
    }

    /**
     * 判断结算算单是否有待申请的红字信息
     * @param settlementNo
     * @return
     */
    @LogOperate(value = "对外接口判断结算算单是否有待申请的红字信息",type = LogOperateType.QUERY)
    public Boolean isWaitingApplyBySettlementNo(String settlementNo) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getBillNo,settlementNo)
                .in(TXfRedNotificationEntity::getApplyingStatus, Arrays.asList(RedNoApplyingStatus.APPLIED.getValue(),RedNoApplyingStatus.APPLYING.getValue()));
        Integer count = redNotificationService.getBaseMapper().selectCount(queryWrapper);
        return count > 0 ;
    }

    @LogOperate(value = "对外接口判断结算算单是否有待申请的红字信息",type = LogOperateType.QUERY)
    public Boolean isWaitingApplyByPreInvoiceId(List<Long> preInvoiceIdList) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TXfRedNotificationEntity::getPid,preInvoiceIdList)
                .in(TXfRedNotificationEntity::getApplyingStatus, Arrays.asList(RedNoApplyingStatus.APPLIED.getValue(),RedNoApplyingStatus.APPLYING.getValue()));
        Integer count = redNotificationService.getBaseMapper().selectCount(queryWrapper);
        return count > 0 ;
    }

    /**
     * 修改已申请的红字信息为撤销待审核,进入审批页面
     * @param pid 预制发id
     * @return
     */
    @LogOperate(value = "对外接口修改已申请的红字信息为撤销待审核",type = LogOperateType.MODIFY)
    public Response<String> updateAppliedToWaitAppproveByPid(Long pid) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getPid,pid).eq(TXfRedNotificationEntity::getApplyingStatus,RedNoApplyingStatus.APPLIED.getValue());
        TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationService.getBaseMapper().selectOne(queryWrapper);
        if (tXfRedNotificationEntity ==null ){
          return   Response.failed(String.format("pid[%s]未找到已申请的记录",pid));
        }

        tXfRedNotificationEntity.setApproveStatus(ApproveStatus.WAIT_TO_APPROVE.getValue());
        tXfRedNotificationEntity.setUpdateDate(new Date());
        redNotificationService.getBaseMapper().updateById(tXfRedNotificationEntity);
        return Response.ok("修改成功");
    }

    /**
     * 根据结算号
     * 查询待申请的红字信息预制发票id
     */
    @LogOperate(value = "对外接口查询待申请的红字信息预制发票id请求",type = LogOperateType.QUERY)
    public List<Long> getWaitApplyPreIds(String settlementNo){
         LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper.eq(TXfRedNotificationEntity::getBillNo,settlementNo).eq(TXfRedNotificationEntity::getLockFlag, LockFlag.NORMAL.getValue()).eq(TXfRedNotificationEntity::getStatus,1);
         List<TXfRedNotificationEntity> entityList = redNotificationService.getBaseMapper().selectList(queryWrapper);
         List<Long> list = entityList.stream().map(item->Long.parseLong(item.getPid())).collect(Collectors.toList());
         return list ;
     }

    /**
     * 删除待申请的红字信息
     */
    @LogOperate(value = "对外接口删除待申请的红字信息请求",type = LogOperateType.MODIFY)
    public void deleteRednotification(List<Long> pidList){
         LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
         updateWrapper.in(TXfRedNotificationEntity::getPid , pidList);
         TXfRedNotificationEntity record = new TXfRedNotificationEntity();
         record.setStatus(0);
         redNotificationService.getBaseMapper().update(record,updateWrapper);
    }


    /**
     * 修改已申请的红字信息表编号
     * 对应的状态改为已核销 标明红字信息已使用      ALREADY_USE(3,"已核销"),
     * 或者该红票作废，释放对应的红字信息表编号      APPROVE_PASS(1,"审核通过"),
     * @param redNotification 红字信息表编号
     * @return
     */
    @LogOperate(value = "对外接口修改已申请的红字信息表编号请求",type = LogOperateType.MODIFY)
    public Response<String> update(String redNotification,ApproveStatus approveStatus) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getRedNotificationNo,redNotification).eq(TXfRedNotificationEntity::getApplyingStatus,RedNoApplyingStatus.APPLIED.getValue());
        TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationService.getBaseMapper().selectOne(queryWrapper);
        if (tXfRedNotificationEntity ==null ){
            return   Response.failed(String.format("红字信息表编号[%s]未找到已申请的记录",redNotification));
        }

        tXfRedNotificationEntity.setApproveStatus(approveStatus.getValue());
        tXfRedNotificationEntity.setUpdateDate(new Date());
        redNotificationService.getBaseMapper().updateById(tXfRedNotificationEntity);
        return Response.ok("修改成功");
    }




}
