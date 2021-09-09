package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.Table1;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.VendorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VendorMasterDao {


    List<DictPo> findDict();

    void inserVendorLog(@Param("vlog") VendorLog vlog);

    int findOrgByTaxnoAndId(@Param("taxno") String taxNumber, @Param("orgid") String orgid,@Param("bankAccount") String bankAccount);

    UserEntity findUserById(@Param("username") String vendorNumber);

    void updateUser(@Param("data") Table1 data, @Param("modifyBy") String modifyBy);

    void updateOrg(@Param("data") Table1 data, @Param("orgid") Integer orgid);

    OrganizationEntity findOrgByTaxno(@Param("taxno") String taxNumber, @Param("name") String vendorname);

    void insertUser(@Param("data") Table1 data, @Param("password") String password, @Param("createBy") String createBy);

    void insertRole(@Param("userid") Integer userid, @Param("roleid") Integer roid);

    int insertOrgInfo(@Param("data") Table1 data, @Param("createBy") String createBy, @Param("orgcode") String orgcode, @Param("company") String company);

    Integer findRoleIdByCode(@Param("code") String wrmsp);
}
