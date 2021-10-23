package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
* <p>
*  Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-14
 */
@Mapper
public interface TXfSettlementExtDao extends BaseMapper<TXfSettlementEntity> {
    /**
     * 查询可拆票的结算
     * @param status
     * @return
     */
    @Select("select top ${limit} * from t_xf_settlement " +
            "where id > #{id}    and settlement_status = #{status}  " +
            "order by id  ")
    List<TXfSettlementEntity> querySettlementByStatus( @Param("status") Integer status , @Param("id")  Long id, @Param("limit") Integer limit);

    /**
     * 查询可拆票的结算列表
     * @param settlementNo
     * @param status

     * @return
     */
    @Select("<script> select  * from t_xf_settlement  where id > #{id} and  settlement_no =  #{settlementNo}  " +
            "<if test='status!=null'>"+
            "and    settlement_status = #{status}  "+
            "</if>"+
            " </script> "  )
     TXfSettlementEntity  querySettlementByNo(@Param("id") Long id, @Param("settlementNo") String settlementNo, @Param("status") Integer status);

}
