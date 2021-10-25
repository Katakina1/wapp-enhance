package com.xforceplus.wapp.modules.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.repository.dao.TXfOperationLogDao;
import com.xforceplus.wapp.repository.entity.TXfOperationLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<TXfOperationLogEntity> query(Long businessId, Long userId){
        QueryWrapper<TXfOperationLogEntity> wrapper = new QueryWrapper<>();
        if(businessId !=null){
            wrapper.eq(TXfOperationLogEntity.BUSINESS_ID,businessId);
        }
        if(userId != null){
            wrapper.eq(TXfOperationLogEntity.USER_ID,userId);
        }
        List<TXfOperationLogEntity> tXfOperationLogEntities = tXfOperationLogDao.selectList(wrapper);
        return tXfOperationLogEntities;
    }


    public boolean add(Long businessId, OperateLogEnum operateLogEnum,String businessStatus, Long userId, String userName){
        if(businessId == null ||  operateLogEnum ==null || businessStatus == null){
            log.error("操作日志添加失败 businessId:{},operateLogEnum:{},businessStatus:{}",businessId,operateLogEnum,businessStatus);
            return false;
        }
        TXfOperationLogEntity operationLogEntity = new TXfOperationLogEntity();
        operationLogEntity.setBusinessId(businessId);
        operationLogEntity.setBusinessStatus(businessStatus);
        operationLogEntity.setOperateDesc(operateLogEnum.getOperateDesc());
        operationLogEntity.setOperateType(operateLogEnum.getOperateType());
        operationLogEntity.setCreateTime(new Date());
        operationLogEntity.setId(IdGenerator.generate());
        operationLogEntity.setUserId(userId);
        operationLogEntity.setUserName(userName);
        return tXfOperationLogDao.insert(operationLogEntity) >0;

    }

}
