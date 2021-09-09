package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 1 on 2018/11/10 14:20
 */
@Mapper
public interface CostPrintDao {

    List<OptionEntity> queryXL(@Param("sl") String sl);
}
