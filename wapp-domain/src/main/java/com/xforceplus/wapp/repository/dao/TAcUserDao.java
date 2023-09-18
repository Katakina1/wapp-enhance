package com.xforceplus.wapp.repository.dao;

import com.xforceplus.wapp.repository.entity.TAcUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* <p>
* 用户表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2022-09-19
*/
public interface TAcUserDao extends BaseMapper<TAcUserEntity> {

    /**
     * 根据购方税号返回供应商id
     * 税号与供应商id为一对多
     * @param companyTaxNo
     * @return
     */
    TAcUserEntity getByTaxNo(@Param("companyTaxNo") String companyTaxNo);
}
