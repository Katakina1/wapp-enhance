package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.common.vo.InvoiceSummonsVo;
import com.xforceplus.wapp.repository.entity.TDxSummonsRMSEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/7/17 15:56
 */
public interface TDxSummonsRMSDao extends BaseMapper<TDxSummonsRMSEntity> {

    /**
     * 查询符合条件的总量
     * @param vo
     * @return
     */
    int queryCount(InvoiceSummonsVo vo);

    /**
     * 条件查询数据
     * @param vo
     * @return
     */
    List<TDxSummonsRMSEntity> invoiceSummonsList(InvoiceSummonsVo vo);
}
