package com.xforceplus.wapp.modules.rednotification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import com.xforceplus.wapp.modules.rednotification.model.QueryModel;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationApplyReverseRequest;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RedNotificationOuterService {
    @Autowired
    RedNotificationMainService redNotificationService;

    /**
     * 新增红字信息
     */
    public Response<String> add(AddRedNotificationRequest request) {
      String taskId = redNotificationService.add(request);
      return Response.ok("成功",taskId);
    }

    /**
     * 红字信息撤销
     * pid 预制发票id
     */
    public Response<String> rollback(Long pid) {
        RedNotificationApplyReverseRequest redNotificationApplyReverseRequest = new RedNotificationApplyReverseRequest();
        QueryModel queryModel = new QueryModel();
        queryModel.setPid(pid);
        redNotificationApplyReverseRequest.setQueryModel(queryModel);
        return redNotificationService.rollback(redNotificationApplyReverseRequest);
    }

    /**
     * 判断结算算单是否有待申请的红字信息
     * @param settlementNo
     * @return
     */
    public Boolean isWaitingApplyBySettlementNo(String settlementNo) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getBillNo,settlementNo).eq(TXfRedNotificationEntity::getApplyingStatus, RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
        Integer count = redNotificationService.getBaseMapper().selectCount(queryWrapper);
        return count > 0 ;
    }

    /**
     * 修改已申请的红字信息为撤销待审核
     * @param pid 预制发id
     * @return
     */
    public Response<String> updateAppliedToWaitAppproveByPid(Long pid) {
        LambdaQueryWrapper<TXfRedNotificationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TXfRedNotificationEntity::getPid,pid).eq(TXfRedNotificationEntity::getApplyingStatus,RedNoApplyingStatus.APPLIED.getValue());
        TXfRedNotificationEntity tXfRedNotificationEntity = redNotificationService.getBaseMapper().selectOne(queryWrapper);
        if (tXfRedNotificationEntity ==null ){
          return   Response.failed(String.format("pid[%s]未找到已申请的记录",pid));
        }

        tXfRedNotificationEntity.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPROVE.getValue());
        tXfRedNotificationEntity.setUpdateDate(new Date());
        redNotificationService.getBaseMapper().updateById(tXfRedNotificationEntity);
        return Response.ok("修改成功");
    }




}
