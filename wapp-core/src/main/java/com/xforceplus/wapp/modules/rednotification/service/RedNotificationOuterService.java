package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.enums.LockFlag;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.enums.RedNoEventTypeEnum;
import com.xforceplus.wapp.modules.rednotification.log.LogOperate;
import com.xforceplus.wapp.modules.rednotification.log.LogOperateType;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RedNotificationOuterService {
    @Autowired
    RedNotificationMainService redNotificationService;
    @Autowired
    CommonMessageService commonMessageService;

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
    public Response<String> rollback(Long pid, RedNoEventTypeEnum eventTypeEnum) {
        RedNotificationApplyModel redNotificationApplyReverseRequest = new RedNotificationApplyModel();
        QueryModel queryModel = new QueryModel();
        ArrayList<Long> pidList = Lists.newArrayList(pid);
        queryModel.setPidList(pidList);
        redNotificationApplyReverseRequest.setQueryModel(queryModel);
        redNotificationApplyReverseRequest.setEventType(Optional.ofNullable(eventTypeEnum).map(RedNoEventTypeEnum::code).orElse(null));

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
     * 根据预制发票ID查询红字信息表信息
     * @param preInvoiceIdList
     * @return
     */
    public List<TXfRedNotificationEntity> queryRedNotiByPreInvoiceId(List<Long> preInvoiceIdList) {
    	 LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper.in(TXfRedNotificationEntity::getPid,preInvoiceIdList);
         return redNotificationService.getBaseMapper().selectList(queryWrapper);
    }

    /**
     * 修改已申请的红字信息为撤销待审核,进入审批页面
     * @param pid 预制发id
     * @return
     */
    @LogOperate(value = "对外接口修改已申请的红字信息为撤销待审核",type = LogOperateType.MODIFY)
    public Response<String> updateAppliedToWaitAppproveByPid(Long pid,String remark) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getPid,pid).eq(TXfRedNotificationEntity::getApplyingStatus,RedNoApplyingStatus.APPLIED.getValue());
        TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationService.getBaseMapper().selectOne(queryWrapper);
        if (tXfRedNotificationEntity ==null ){
          return   Response.failed(String.format("pid[%s]未找到已申请的记录",pid));
        }

        tXfRedNotificationEntity.setRevertRemark(remark);
        tXfRedNotificationEntity.setApproveStatus(ApproveStatus.WAIT_TO_APPROVE.getValue());
        tXfRedNotificationEntity.setUpdateDate(new Date());
        redNotificationService.getBaseMapper().updateById(tXfRedNotificationEntity);

        // 发送状态更新消息-撤销申请
        commonMessageService.sendMessage(DeductRedNotificationEventEnum.REVOCATION_APPLY, tXfRedNotificationEntity);
        return Response.ok("修改成功");
    }

    /**
     * 根据结算号
     * 查询待申请的红字信息预制发票id
     */
    @LogOperate(value = "对外接口查询待申请的红字信息预制发票id请求",type = LogOperateType.QUERY)
    public Tuple2<Boolean,List<Long>> getWaitApplyPreIds(List<String> preIds){
         LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
         queryWrapper.in(TXfRedNotificationEntity::getPid,preIds)
                 .eq(TXfRedNotificationEntity::getLockFlag, LockFlag.NORMAL.getValue())
                 .eq(TXfRedNotificationEntity::getStatus,1);
         List<TXfRedNotificationEntity> entityList = redNotificationService.getBaseMapper().selectList(queryWrapper);
         //判断是否有带申请的红字信息
        boolean b = entityList.stream().anyMatch(item ->
                Objects.equals(item.getApplyingStatus(),3) || Objects.equals(item.getApplyingStatus(),2));
        if (b){
           return Tuple.of(false,null);
        }
        List<Long> list = entityList.stream().map(item->Long.parseLong(item.getPid())).collect(Collectors.toList());
        return Tuple.of(true,list);
     }


    /**
     * 删除待申请的红字信息
     */
    @LogOperate(value = "对外接口删除待申请的红字信息请求",type = LogOperateType.MODIFY)
	public void deleteRednotification(List<Long> pidList, String remark) {
		LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.in(TXfRedNotificationEntity::getPid, pidList);
		updateWrapper.eq(TXfRedNotificationEntity::getApplyingStatus, RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
		TXfRedNotificationEntity record = new TXfRedNotificationEntity();
		record.setStatus(0);
		record.setRevertRemark(remark);
		redNotificationService.getBaseMapper().update(record, updateWrapper);
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

