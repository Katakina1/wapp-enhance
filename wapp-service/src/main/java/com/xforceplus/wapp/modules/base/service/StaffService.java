package com.xforceplus.wapp.modules.base.service;


import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StaffService {

    List<Staff> queryList(Map<String, Object> params);

    int queryCount(Map<String, Object> params);

    Integer updateStaff(Staff staff);

    Integer deleteStaff(int id);

    Integer insertStaff(Staff staff);

    Map<String,Object> parseExcel(MultipartFile multipartFile);


    List<UserEntity> queryList1(Map<String, Object> params);

    int queryCount1(Map<String, Object> params);

    List<UserEntity> queryList2(Map<String, Object> params);

    int queryCount2(Map<String, Object> params);

    Integer deleteVendor(String[] ids);

    Integer addVendor(String staff, String[] vendors);

    void deleteStaff(String staffNo);

    List<JvEntity> queryJv(Query query);

    Integer queryJvCount(Query query);

    List<JvEntity> queryNotAddJv(Query query);

    Integer queryNotAddJvCount(Query query);

    Integer addJvs(String staff, String[] vendors);

    Integer deleteJv(String[] split);

    List<CostEntity> queryCost(Query query);

    Integer queryCostCount(Query query);

    void insertCost(CostEntity cost);

    void deleteCost(String[] split);

    Map<String,Object> parseCbzxExcel(MultipartFile multipartFile);

	int isExists(Integer orgid, String costcode);
}
