package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;


/**
* <p>
* 业务单据信息 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-12
*/
public interface TXfBillDeductExtDao extends BaseMapper<TXfBillDeductEntity> {
    /**
     *查询折扣单列表
     * @param date
     * @param start
     * @param limit
     * @param billType
     * @param status
     * @return
     */
       @Select("select * from t_xf_bill_deduct " +
            "where create_date=>#{date} and business_type = #{billType} and status = #{status}  " +
            "order by id limit ${start} ,${limit} ")
    List<TXfBillDeductEntity> queryUnMatchBill(@Param("date") Date date,
                                               @Param("start") Integer start,
                                               @Param("limit") Integer limit,
                                               @Param("billType") Integer billType,
                                               @Param("status") Integer status);
}
