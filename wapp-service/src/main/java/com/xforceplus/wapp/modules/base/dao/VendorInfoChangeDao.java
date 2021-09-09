package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VendorInfoChangeDao {

    /**
     * 供应商信息变更提交
     * @return
     */
    void submit(UserEntity userEntity);

    /**
     * 修改供应商变更表信息
     * @param userEntity
     */
    void updateVendorInfoChange(UserEntity userEntity);

    /**
     * 修改审核状态为已审核
     * @param usercode
     */
    void updateAuditStatus(@Param("usercode") String usercode);

    /**
     * 修改供应商信息
     * @param userEntity
     */
    Integer updateVendorInfo(UserEntity userEntity);

    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<UserEntity> queryVendorInfoChangeList(Map<String, Object> map);

    /**
     * 查询供应商是否已提交过变更信息(未审核的)
     * @param usercode
     * @return
     */
    Integer queryVendorInfoChangeIsExist(@Param("usercode") String usercode);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryVendorInfoChangeCount(Map<String, Object> map);

    UserEntity queryChangeInfoById(@Param("id")Long id);
}
