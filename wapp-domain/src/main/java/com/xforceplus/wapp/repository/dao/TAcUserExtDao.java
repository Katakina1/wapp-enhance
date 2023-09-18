package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
* <p>
* 用户表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2022-09-19
*/

public interface TAcUserExtDao extends BaseMapper<TAcUserEntity> {

    @Select("<script>" +
            "select COUNT(u.orgid)  from t_ac_user u,t_ac_org o " +
            "WHERE  o.orgtype =8 and u.orgid =o.orgid " +
            "<if test='usercode != null and usercode != &apos;&apos;'> and u.usercode in (${usercode})</if>"+
            "<if test='userId != null and userId != &apos;&apos;'> and u.userid in (${userId})</if>"+
            "<if test='username != null and username != &apos;&apos;'> and u.username like concat('%',#{username})</if>"+
            "<if test='taxno != null and taxno != &apos;&apos;'> and o.taxno=#{taxno}</if>"+
            "<if test='serviceType != null'> and u.serviceType=#{serviceType}</if>"+
            "<if test='assertDate != null and assertDate != &apos;&apos;'><![CDATA[ and (u.assertDate BETWEEN #{assertDate} and #{assertDateEnd}) ]]></if>"+
            "<if test='expireDate != null and expireDate != &apos;&apos;'><![CDATA[ and (u.expireDate BETWEEN #{expireDate} and #{expireDateEnd}) ]]></if>"+
            "<if test='updateDate != null and updateDate != &apos;&apos;'><![CDATA[ and (u.updateDate BETWEEN #{updateDate} and #{updateDateEnd}) ]]></if>"+
            "</script>")
    int countSupSerConf( @Param("usercode")String userCode, @Param("username")String userName,
                         @Param("taxno")String taxNo, @Param("serviceType")Integer serviceType,
                         @Param("assertDate")String assertDate, @Param("expireDate")String expireDate,
                         @Param("updateDate")String updateDate,@Param("assertDateEnd")String assertDateEnd, @Param("expireDateEnd")String expireDateEnd,
                         @Param("updateDateEnd")String updateDateEnd,@Param("userId")String userId);


    List<TAcUserEntity> queryPageSupSerConf(@Param("offset")Integer offset, @Param("next")Integer next,
                                            @Param("usercode")String userCode, @Param("username")String userName,
                                            @Param("taxno")String taxNo, @Param("serviceType")Integer serviceType,
                                            @Param("assertDate")String assertDate, @Param("expireDate")String expireDate,
                                            @Param("updateDate")String updateDate,@Param("assertDateEnd")String assertDateEnd, @Param("expireDateEnd")String expireDateEnd,
                                            @Param("updateDateEnd")String updateDateEnd,@Param("userId")String userId);


    @Update("<script>update t_ac_user set  serviceType =#{serviceType}," +
            "<if test='assertDate != null and assertDate != &apos;&apos;'><![CDATA[ assertDate = #{assertDate}, ]]></if>"+
            "<if test='expireDate != null and expireDate != &apos;&apos;'><![CDATA[ expireDate = #{expireDate}, ]]></if>"+
            "updateDate = #{updateDate} WHERE usercode = #{userCode} " +
            "<if test='userName != null and userName != &apos;&apos;'><![CDATA[ and username = #{userName} ]]></if>"+
            "and orgid  = (SELECT orgid FROM t_ac_org o WHERE o.orgtype =8 and o.orgid in " +
            "(select orgid  FROM t_ac_user u WHERE u.usercode =#{userCode} ))</script>")
    int updateSupSerConf(@Param("userCode")String userCode,@Param("userName")String userName, @Param("serviceType")Integer serviceType,
                         @Param("assertDate")String assertDate, @Param("expireDate")String expireDate,
                         @Param("updateDate")String updateDate);


}
