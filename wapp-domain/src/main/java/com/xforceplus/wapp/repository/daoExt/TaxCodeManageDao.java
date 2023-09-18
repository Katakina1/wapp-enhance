package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.TaxCodeManageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxCodeManageDao {

    List<TaxCodeManageEntity> selectAll(@Param("taxNo")String taxNo,
                                        @Param("taxName")String taxName);

    int addTaxCode(@Param("taxCodeManage")TaxCodeManageEntity taxCodeManage);

    int editTaxCode(@Param("taxCodeManage")TaxCodeManageEntity taxCodeManage);

    boolean deleteTaxCode(@Param("taxCodeManage")TaxCodeManageEntity taxCodeManage);
}
