package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TDxCustomsSummonsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/7/4 21:06
 */
public interface TDxCustomsSummonsDao extends BaseMapper<TDxCustomsSummonsEntity> {

//    @Select("<script>" +
//            "SELECT count(tdcs.id) FROM t_dx_customs_summons tdcs left join t_dx_customs tdc on tdcs.invoice_no = tdc.customs_no " +
//            "WHERE tdcs.id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and tdcs.invoice_no = #{customsNo} </if>" +
//            "<if test='venderid != null and venderid != &apos;&apos;'> and tdcs.venderid = #{venderid} </if>" +
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tdc.tax_period = #{taxPeriod} </if>" +
//            "<if test='certificateNo != null and certificateNo != &apos;&apos;'> and tdcs.certificate_no = #{certificateNo} </if>" +
//            "<if test='contractNo != null and contractNo != &apos;&apos;'> and tdc.contract_no = #{contractNo} </if>" +
//            "<if test='paperDrewDateStart != null and paperDrewDateEnd != null'> <![CDATA[ and (tdcs.invoice_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>" +
//            "<if test='voucherAccountTimeStart != null and voucherAccountTimeEnd != null'> <![CDATA[ and (tdc.voucher_account_time BETWEEN #{voucherAccountTimeStart} and #{voucherAccountTimeEnd} ) ]]></if>" +
//            "<if test='isChecks != null '> " +
//                "and tdcs.is_check in " +
//                "<foreach collection='isChecks' item='isCkeck' open='(' close=')' separator=','>"+
//                " #{isCkeck}" +
//                "</foreach>"+
//            "</if>" +
//            "</script>")
    int queryCount(@Param("customsNo")String customsNo, @Param("venderid") String venderid,
                   @Param("taxPeriod") String taxPeriod, @Param("paperDrewDateStart") String paperDrewDateStart,
                   @Param("paperDrewDateEnd") String paperDrewDateEnd, @Param("isChecks") List<Integer> isChecks,
                   @Param("certificateNo") String certificateNo, @Param("contractNo") String contractNo,
                   @Param("voucherAccountTimeStart") String voucherAccountTimeStart, @Param("voucherAccountTimeEnd") String voucherAccountTimeEnd);

//    @Select("<script>" +
//            "SELECT tdcs.*,tdc.account_status,tdc.tax_period,tdc.voucher_account_time,tdc.contract_no FROM t_dx_customs_summons tdcs left join t_dx_customs tdc on tdcs.invoice_no = tdc.customs_no " +
//            "WHERE tdcs.id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and tdcs.invoice_no = #{customsNo} </if>" +
//            "<if test='venderid != null and venderid != &apos;&apos;'> and tdcs.venderid = #{venderid} </if>" +
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tdc.tax_period = #{taxPeriod} </if>" +
//            "<if test='certificateNo != null and certificateNo != &apos;&apos;'> and tdcs.certificate_no = #{certificateNo} </if>" +
//            "<if test='contractNo != null and contractNo != &apos;&apos;'> and tdc.contract_no = #{contractNo} </if>" +
//            "<if test='paperDrewDateStart != null and paperDrewDateEnd != null'> <![CDATA[ and (tdcs.invoice_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>" +
//            "<if test='voucherAccountTimeStart != null and voucherAccountTimeEnd != null'> <![CDATA[ and (tdc.voucher_account_time BETWEEN #{voucherAccountTimeStart} and #{voucherAccountTimeEnd} ) ]]></if>" +
//            "<if test='offset != null and next !=null'>"+
//            "<if test='isChecks != null '> " +
//                "and tdcs.is_check in " +
//                "<foreach collection='isChecks' item='isCkeck' open='(' close=')' separator=','>"+
//                " #{isCkeck}" +
//                "</foreach>"+
//            "</if>" +
//            " ORDER by tdcs.invoice_date desc offset #{offset} rows fetch next #{next} rows only" +
//            "</if>"+
//            "</script>")
    List<TDxCustomsSummonsEntity> queryByPage(@Param("offset") Integer offset, @Param("next") Integer next,
                                              @Param("customsNo") String customsNo, @Param("venderid") String venderid,
                                              @Param("taxPeriod") String taxPeriod, @Param("paperDrewDateStart") String paperDrewDateStart,
                                              @Param("paperDrewDateEnd") String paperDrewDateEnd, @Param("isChecks") List<Integer> isChecks,
                                              @Param("certificateNo") String certificateNo, @Param("contractNo") String contractNo,
                                              @Param("voucherAccountTimeStart") String voucherAccountTimeStart, @Param("voucherAccountTimeEnd") String voucherAccountTimeEnd);

    /**
     * 根据id查询数据
     * @param ids
     * @return
     */
//    @Select("<script>" +
//            "SELECT tdcs.*,tdc.account_status,tdc.tax_period,tdc.voucher_account_time,tdc.contract_no FROM t_dx_customs_summons tdcs left join t_dx_customs tdc on tdcs.invoice_no = tdc.customs_no " +
//            "WHERE tdcs.id in " +
//            "<foreach collection='ids' item='id' open='(' close=')' separator=','>"+
//            " #{id}" +
//            "</foreach>"+
//            "</script>")
    List<TDxCustomsSummonsEntity> queryByIds(@Param("ids") List<Long> ids);


}
