package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoiceStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 税率持久层
 * @author JLY
 *
 */
@Mapper
public interface RecordInvoiceStatisticsDao {

	void saveStatistics(@Param("rs") TDxRecordInvoiceStatistics rs,@Param("linkName")String  linkName);
}
