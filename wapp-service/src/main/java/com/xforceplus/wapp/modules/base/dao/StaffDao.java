package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Mapper
public interface StaffDao {

    List<Staff> queryList(Map<String, Object> params);

    int queryCount(Map<String, Object> params);

    Integer updateStaff(Staff staff);

    Integer deleteStaff(int id);

    Integer insertStaff(Staff staff);

    void insertStaffAndVender(@Param("staffNo") String staffNo, @Param("vendor") String[] vender);

    int findStaff(@Param("staffNo") String staffNo, @Param("email") String email);

    int findVendor(@Param("vendors") String[] vendors);

    int findVendorBySingle(@Param("vendors") String vendors);


    List<UserEntity> queryList1(Map<String, Object> params);

    int queryCount1(Map<String, Object> params);

    List<UserEntity> queryList2(Map<String, Object> params);

    int queryCount2(Map<String, Object> params);

    Integer deleteVendor(String[] ids);

    Integer addVendor(@Param("staff") String staff, @Param("vendors") String[] vendors);

    void deleteStaffByNo(String staffNo);

    void deleteStaffAndVendor(String staffNo);

    void insertJv(@Param("staff") Staff staff);

    List<Integer> findOrgIdsByJv(@Param("jv") String[] jv);
    //成本中心去重
    Integer jvRepetition(@Param("orgid") int orgid, @Param("arr") String arr);

    Integer findOrgIdSingle(String jvs);

    void deleteJvByNo(String staffNo);

    List<JvEntity> queryJv(Query query);

    Integer queryJvCount(Query query);

    List<JvEntity> queryNotAddJv(Query query);

    Integer queryNotAddJvCount(Query query);

    Integer addJvs(@Param("staff") String staff, @Param("jvs") String[] jvs);

    Integer deleteJv(@Param("ids") String[] ids);

    List<CostEntity> queryCost(Query query);

    Integer queryCostCount(Query query);

    void insertCost(@Param("cost") CostEntity cost);

    void deleteCost(@Param("ids") String[] ids);

    void insertOrgIdAndCosts(@Param("orgid") int orgid, @Param("arr") String[] arr);
}
