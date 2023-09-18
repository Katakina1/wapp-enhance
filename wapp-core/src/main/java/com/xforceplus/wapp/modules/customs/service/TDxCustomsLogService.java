package com.xforceplus.wapp.modules.customs.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.customs.CustomsLogRequest;
import com.xforceplus.wapp.repository.dao.TDxCustomsLogDao;
import com.xforceplus.wapp.repository.entity.TDxCustomsLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class TDxCustomsLogService extends ServiceImpl<TDxCustomsLogDao, TDxCustomsLogEntity> {

    @Autowired
    private TDxCustomsLogDao tDxCustomsLogDao;

    /**
     * @Description 按照条件查询数据
     * @Author pengtao
     * @return
    **/
    public PageResult<TDxCustomsLogEntity> paged(CustomsLogRequest request){
        Integer pageNo = request.getPageNo();
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        request.setPageNo(offset);

//        List<TDxCustomsLogEntity> entities = tDxCustomsLogDao.selectListCustoms(request.getPageNo(),request.getPageSize(),
//                request.getCustomsId(),request.getCustomsNo(),request.getType(),request.getCheckTime(),request.getUserId());
        List<TDxCustomsLogEntity> entities = tDxCustomsLogDao.selectListCustoms(request);
        return PageResult.of(entities,queryCount(request),pageNo, request.getPageSize());
    }

    /**
     * @Description 查询数量
     * @Author pengtao
     * @return
     **/
    public Integer queryCount(CustomsLogRequest request){
//        return  tDxCustomsLogDao.countCustoms(request.getCustomsId(),request.getCustomsNo(),request.getType(),
//                request.getCheckTime(),request.getUserId());
        return  tDxCustomsLogDao.countCustoms(request);
    }
}
