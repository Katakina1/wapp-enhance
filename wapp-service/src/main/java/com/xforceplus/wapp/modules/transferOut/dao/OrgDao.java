package com.xforceplus.wapp.modules.transferOut.dao;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/18
 * Time:10:17
*/

import com.xforceplus.wapp.modules.sys.dao.BaseDao;
import com.xforceplus.wapp.modules.transferOut.entity.OrgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface OrgDao extends BaseDao<OrgEntity> {

    List<OrgEntity> getGfNameAndTaxNo(@Param("schemaLabel") String schemaLabel,@Param("userId")Long  userId);

}
