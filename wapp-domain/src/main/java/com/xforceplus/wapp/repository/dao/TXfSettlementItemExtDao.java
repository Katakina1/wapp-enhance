package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.apache.ibatis.annotations.Mapper;
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
public interface TXfSettlementItemExtDao extends BaseMapper<TXfSettlementItemEntity> {
    @Select("select  * from t_xf_settlement_item  where   settlement_no =  #{settlementNo}   "  )
    List<TXfSettlementItemEntity> queryItemBySettlementNo(String settlementNo);
}
