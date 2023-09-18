package com.xforceplus.wapp.repository.daoExt;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.repository.vo.SettlementRedVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-05 17:55
 **/
@Mapper
public interface SettlementExtDao {

    Page<SettlementRedVo> redList(Page<SettlementRedVo> page,
                                  @Param("sellerNo") String sellerNo, @Param("qsStatus") String qsStatus,
                                  @Param("settlementNo") String settlementNo, @Param("redNotification") String redNotification);

}
