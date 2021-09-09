package com.xforceplus.wapp.modules.transferOut.dao;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/12
 * Time:16:55
*/

import com.xforceplus.wapp.modules.sys.dao.SysBaseDao;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailVehicleEntity;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface DetailDao extends SysBaseDao<DetailEntity> {

    List<DetailEntity> getInvoiceDetail(@Param("schemaLabel") String schemaLabel,@Param("id")Long  id);

    DetailVehicleEntity getVehicleDetail(@Param("schemaLabel") String schemaLabel,@Param("id")Long id);

    int getInvoiceDetailTotal(@Param("schemaLabel") String schemaLabel,@Param("id")Long id);

    List<InvoiceEntity> getOutInfo(@Param("schemaLabel") String schemaLabel,@Param("uuid")String  uuid);
}
