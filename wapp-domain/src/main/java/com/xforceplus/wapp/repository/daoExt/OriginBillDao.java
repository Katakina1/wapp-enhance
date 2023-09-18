package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.vo.OriginClaimBillVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 原始单据查询
 * @date : 2022/09/08 9:33
 **/
@Mapper
public interface OriginBillDao {

    long countOriginClaim(Map<String, Object> map);

    List<OriginClaimBillVo> selectOriginClaimPage(Map<String, Object> map);
}
