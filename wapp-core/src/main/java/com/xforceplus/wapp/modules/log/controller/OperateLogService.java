package com.xforceplus.wapp.modules.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.modules.log.model.QueryOperationLogResponse;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.repository.dao.TXfOperationLogDao;
import com.xforceplus.wapp.repository.entity.TXfOperationLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SunShiyong on 2021/10/25.
 */

@Service
@Slf4j
public class OperateLogService {

    @Autowired
    private TXfOperationLogDao tXfOperationLogDao;

    public List<QueryOperationLogResponse> query(Long businessId, Long userId){
        QueryWrapper<TXfOperationLogEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(TXfOperationLogEntity.ID);
        if(businessId !=null){
            wrapper.eq(TXfOperationLogEntity.BUSINESS_ID,businessId);
        }
        if(userId != null){
            wrapper.eq(TXfOperationLogEntity.USER_ID,userId);
        }
        List<TXfOperationLogEntity> tXfOperationLogEntities = tXfOperationLogDao.selectList(wrapper);
        List<QueryOperationLogResponse> response = new ArrayList<>();
        BeanUtil.copyList(tXfOperationLogEntities,response,QueryOperationLogResponse.class);
        return response;
    }

    @Async
    public void add(Long businessId, OperateLogEnum operateLogEnum,String businessStatus, Long userId, String userName){
        try {
            if(businessId == null ||  operateLogEnum ==null || businessStatus == null){
                log.info("操作日志添加失败 businessId:{},operateLogEnum:{},businessStatus:{}",businessId,operateLogEnum,businessStatus);
                return;
            }
            TXfOperationLogEntity operationLogEntity = new TXfOperationLogEntity();
            operationLogEntity.setBusinessId(businessId);
            operationLogEntity.setBusinessStatus(businessStatus);
            operationLogEntity.setOperateDesc(operateLogEnum.getOperateDesc());
            operationLogEntity.setOperateType(operateLogEnum.getOperateType());
            operationLogEntity.setOperateCode(operateLogEnum.getOperateCode());
            operationLogEntity.setCreateTime(new Date());
            operationLogEntity.setId(IdGenerator.generate());
            operationLogEntity.setUserId(userId);
            operationLogEntity.setUserName(userName);
            tXfOperationLogDao.insert(operationLogEntity);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

}
