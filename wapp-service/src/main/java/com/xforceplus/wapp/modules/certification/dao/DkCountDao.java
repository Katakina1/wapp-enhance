package com.xforceplus.wapp.modules.certification.dao;

import com.aisinopdf.text.pdf.S;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountDetail;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountLogEntity;

import com.xforceplus.wapp.modules.job.entity.TAcOrg;
import com.xforceplus.wapp.modules.job.entity.TDxTaxCurrent;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 抵扣统计
 * 
 */
@Mapper
public interface DkCountDao {

   /**
    * 手工勾选
    * @param id
    * @param schemaLabel mycat分库参数
    * @return Integer
    */
   Integer manualCheck(@Param("schemaLabel") String schemaLabel, @Param("id") String id, @Param("loginName") String loginName, @Param("userName") String userName);

   /**
    * 抵扣统计列表
    * @param map 查询条件
    * @param schemaLabel mycat分库参数
    * @return 可勾选操作的数据集
    */
   List<TDxDkCountEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);

   /**
    * 抵扣统计
    * @param map 查询条件
    * @param schemaLabel mycat分库参数
    * @return 可勾选操作的数据数
    */
   ReportStatisticsEntity queryTotal(@Param("schemaLabel") String schemaLabel, @Param("map") Map<String, Object> map);
   Integer selectBeforeTj(@Param("map") Map<String, Object> map);
   void insertDk(@Param("gfsh") String gfsh, @Param("skssq") String skssq);
   void insertConfirm(@Param("gfsh") String gfsh, @Param("skssq") String skssq,@Param("ywmm") String ywmm);
   List<TDxDkCountEntity> selectDksh(@Param("gfsh") String[] gfsh);
   void insertDkLog(@Param("list") List<TDxDkCountLogEntity> list);
   void insertCxtj(@Param("gfsh") String gfsh, @Param("skssq") String skssq);
   void insertCxqs(@Param("gfsh") String gfsh,@Param("skssq") String skssq);
   List<TDxDkCountDetail> selectDkDetail(@Param("gfsh") String gfsh, @Param("skssq") String skssq);
   List<TDxDkCountEntity> checkDksh(@Param("map") Map<String, Object> map);
   TAcOrg selectUpgrad(@Param("gfsh") String gfsh);
   /**
    * 查税号所属期
    * @param schemaLabel
    * @param taxNos
    * @return
    */
   List<TDxTaxCurrent> getTaxPeriod(@Param("schemaLabel") String schemaLabel, @Param("taxNos") String[] taxNos);
}
