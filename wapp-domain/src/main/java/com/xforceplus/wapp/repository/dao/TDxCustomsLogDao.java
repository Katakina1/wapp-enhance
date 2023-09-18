package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TDxCustomsLogEntity;
import com.xforceplus.wapp.common.dto.customs.CustomsLogRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface TDxCustomsLogDao extends BaseMapper<TDxCustomsLogEntity> {

//    @Select("<script>" +
//            "select COUNT(*)  from t_dx_customs_log " +
//            "WHERE id is not null" +
//            "<if test='customsId != null and customsId != &apos;&apos;'> and customs_id = #{customsId}</if>"+
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no = #{customsNo}</if>"+
//            "<if test='type != null and type != &apos;&apos;'> and type = #{type}</if>"+
//            "<if test='checkTime != null and checkTime != &apos;&apos;'> and check_time = #{checkTime}</if>"+
//            "<if test='userId != null and userId != &apos;&apos;'> and user_id = #{userId}</if>"+
//            "</script>")
//    Integer countCustoms(String customsId, String customsNo, String type, String checkTime, String userId);
    Integer countCustoms(CustomsLogRequest request);

//    @Select("<script>" +
//            "select *  from t_dx_customs_log " +
//            "WHERE id is not null" +
//            "<if test='customsId != null and customsId != &apos;&apos;'> and customs_id = #{customsId}</if>"+
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no = #{customsNo}</if>"+
//            "<if test='type != null and type != &apos;&apos;'> and type = #{type}</if>"+
//            "<if test='checkTime != null and checkTime != &apos;&apos;'> and check_time = #{checkTime}</if>"+
//            "<if test='userId != null and userId != &apos;&apos;'> and user_id = #{userId}</if>"+
//            "<if test='offset != null and next !=null'>"+
//            " ORDER by create_time desc offset #{offset} rows fetch next #{next} rows only" +
//            "</if>"+
//            "</script>")
//    List<TDxCustomsLogEntity> selectListCustoms(@Param("offset") Integer offset,@Param("next") Integer next,@Param("customsId") String customsId,
//                                                @Param("customsNo") String customsNo,@Param("type") String type,@Param("checkTime") String checkTime,
//                                                @Param("userId") String userId);
    List<TDxCustomsLogEntity> selectListCustoms(CustomsLogRequest request);
}