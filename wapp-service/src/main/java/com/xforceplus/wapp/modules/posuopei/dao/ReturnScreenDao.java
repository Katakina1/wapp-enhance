package com.xforceplus.wapp.modules.posuopei.dao;

import com.xforceplus.wapp.interfaceBPMS.Table;
import com.xforceplus.wapp.modules.cost.entity.SettlementFileEntity;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReturnScreenDao {
    Integer insertReturnScreen(@Param("entity") HostReturnScreenEntity hostReturnScreenEntity);
    List<HostReturnScreenEntity> getReturnScreenList(Map<String,Object> params);
    Integer getReturnScreenCount(Map<String,Object> params);
}
