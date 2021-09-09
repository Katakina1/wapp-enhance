package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.base.dao.BaseDao;
import com.xforceplus.wapp.modules.job.entity.TAcOrg;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TAcOrgDao extends BaseDao<TAcOrg> {

     List<TAcOrg> getTaxnoByType(@Param("type") String type,@Param("linkName")String linkName);
}