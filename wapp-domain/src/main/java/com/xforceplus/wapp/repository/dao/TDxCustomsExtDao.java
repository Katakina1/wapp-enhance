package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
* <p>
* 海关缴款书 Mapper 接口
* </p>
*/
public interface TDxCustomsExtDao extends BaseMapper<TDxCustomsEntity> {

//    @Select("<script>" +
//            "select COUNT(*)  from t_dx_customs " +
//            "WHERE id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no in (${customsNo})</if>"+
//            "<if test='companyTaxNo != null and companyTaxNo != &apos;&apos;'> and company_tax_no=#{companyTaxNo}</if>"+
//            "<if test='manageStatus != null and manageStatus != &apos;&apos; and manageStatus == 1'> " +
//            "and manage_status=#{manageStatus} and bill_status = '1' and customs_no is not null and voucher_account_time is not null " +
//            "</if>"+
//            "<if test='manageStatus != null and manageStatus != &apos;&apos; and manageStatus == 0'> " +
//            "and manage_status=#{manageStatus}" +
//            "</if>"+
//            "<if test='voucherNo != null and voucherNo != &apos;&apos;'> and voucher_no=#{voucherNo}</if>"+
//            "<if test='accountStatus != null and accountStatus != &apos;&apos;'> and account_status=#{accountStatus}</if>"+
//            "<if test='confirmStatus != null and confirmStatus != &apos;&apos;'> and confirm_status=#{confirmStatus}</if>"+
//            "<if test='isCheck != null and isCheck != &apos;&apos;'> and is_check in (${isCheck})</if>"+
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tax_period=#{taxPeriod}</if>"+
//            "<if test='billStatus != null and billStatus != &apos;&apos;'> and bill_status=#{billStatus}</if>"+
//            "<if test='checkTimeStart != null and checkTimeStart != &apos;&apos;'><![CDATA[ and (check_time BETWEEN #{checkTimeStart} and #{checkTimeEnd}) ]]></if>"+
//            "<if test='paperDrewDateStart != null and paperDrewDateStart != &apos;&apos;'><![CDATA[ and (paper_drew_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>"+
//            "<if test='companyName != null and companyName != &apos;&apos;'> and company_name like concat('%',#{companyName})</if>"+
//            "</script>")
    Integer countCustoms(@Param("customsNo") String customsNo, @Param("manageStatus") String manageStatus,@Param("companyTaxNo") String companyTaxNo,
                        @Param("companyName") String companyName, @Param("isCheck") String isCheck,
                        @Param("paperDrewDateStart")String paperDrewDateStart, @Param("paperDrewDateEnd")String paperDrewDateEnd,
                        @Param("checkTimeStart")String checkTimeStart,@Param("checkTimeEnd")String checkTimeEnd,
                        @Param("taxPeriod")String taxPeriod,@Param("voucherNo")String voucherNo,@Param("accountStatus")String accountStatus,
                        @Param("confirmStatus") String confirmStatus,@Param("billStatus") String billStatus);


//    @Select("<script>" +
//            "select *  from t_dx_customs " +
//            "WHERE id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no in (${customsNo})</if>"+
//            "<if test='companyTaxNo != null and companyTaxNo != &apos;&apos;'> and company_tax_no=#{companyTaxNo}</if>"+
//            "<if test='manageStatus != null and manageStatus != &apos;&apos; and manageStatus == 1'> " +
//            "and manage_status=#{manageStatus} and bill_status = '1' and customs_no is not null and voucher_account_time is not null " +
//            "</if>"+
//            "<if test='manageStatus != null and manageStatus != &apos;&apos; and manageStatus == 0'> " +
//            "and manage_status=#{manageStatus}" +
//            "</if>"+
//            "<if test='voucherNo != null and voucherNo != &apos;&apos;'> and voucher_no=#{voucherNo}</if>"+
//            "<if test='accountStatus != null and accountStatus != &apos;&apos;'> and account_status=#{accountStatus}</if>"+
//            "<if test='confirmStatus != null and confirmStatus != &apos;&apos;'> and confirm_status=#{confirmStatus}</if>"+
//            "<if test='isCheck != null and isCheck != &apos;&apos;'> and is_check in (${isCheck})</if>"+
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tax_period=#{taxPeriod}</if>"+
//            "<if test='billStatus != null and billStatus != &apos;&apos;'> and bill_status=#{billStatus}</if>"+
//            "<if test='checkTimeStart != null and checkTimeStart != &apos;&apos;'><![CDATA[ and (check_time BETWEEN #{checkTimeStart} and #{checkTimeEnd}) ]]></if>"+
//            "<if test='paperDrewDateStart != null and paperDrewDateStart != &apos;&apos;'><![CDATA[ and (paper_drew_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>"+
//            "<if test='companyName != null and companyName != &apos;&apos;'> and company_name like concat('%',#{companyName})</if>"+
//            "<if test='offset != null and next !=null'>"+
//            " ORDER by paper_drew_date desc offset #{offset} rows fetch next #{next} rows only" +
//            "</if>"+
//            "</script>")
    List<TDxCustomsEntity> queryPageCustoms(@Param("offset")Integer offset, @Param("next")Integer next,
                                            @Param("customsNo")String customsNo, @Param("manageStatus") String manageStatus, @Param("companyTaxNo")String companyTaxNo,
                                            @Param("companyName")String companyName, @Param("isCheck")String isCheck,
                                            @Param("paperDrewDateStart")String paperDrewDateStart, @Param("paperDrewDateEnd")String paperDrewDateEnd,
                                            @Param("checkTimeStart")String checkTimeStart,@Param("checkTimeEnd")String checkTimeEnd,
                                            @Param("taxPeriod")String taxPeriod,@Param("voucherNo")String voucherNo,@Param("accountStatus")String accountStatus,
                                            @Param("confirmStatus") String confirmStatus,@Param("billStatus") String billStatus);


//    @Update("<script>update t_dx_customs set update_time = #{updateTime},effective_tax_amount = ${effectiveTaxAmount}" +
//            " WHERE id = #{id} " +
//            "</script>")
    int updateCustoms(@Param("id")String id,@Param("effectiveTaxAmount") BigDecimal effectiveTaxAmount,@Param("taxPeriod") String taxPeriod,
    @Param("voucherNo") String voucherNo,@Param("updateTime") Date updateTime);


//    @Select("<script>" +
//            "SELECT is_check ,COUNT(*) as num from t_dx_customs group by is_check"+
//            "</script>")
    List<Map<String, Object>> queryAuthCount();

//    @Select("<script>" +
//            "SELECT account_status ,COUNT(*) as num from t_dx_customs group by account_status"+
//            "</script>")
    List<Map<String, Object>> queryEntryCount();

//    @Update("<script>update t_dx_customs set  update_time = #{updateTime},voucher_no = #{voucherNo}" +
//            " WHERE customs_no = #{customsNo} " +
//            "</script>")
    int updateByCustoms(@Param("customsNo")String customsNo,@Param("voucherNo") String voucherNo,
    @Param("updateTime") Date updateTime);



//    @Select("<script>" +
//            "select COUNT(*)  from t_dx_customs " +
//            "WHERE id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no in (${customsNo})</if>"+
//            "<if test='companyTaxNo != null and companyTaxNo != &apos;&apos;'> and company_tax_no=#{companyTaxNo}</if>"+
//            "<if test='voucherNo != null and voucherNo != &apos;&apos;'> and voucher_no=#{voucherNo}</if>"+
//            "<if test='accountStatus != null and accountStatus != &apos;&apos;'> and account_status=#{accountStatus}</if>"+
//            "<if test='confirmStatus != null and confirmStatus != &apos;&apos;'> and confirm_status=#{confirmStatus}</if>"+
//            "<if test='isCheck != null and isCheck != &apos;&apos;'> and is_check in (${isCheck})</if>"+
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tax_period=#{taxPeriod}</if>"+
//            "<if test='billStatus != null and billStatus != &apos;&apos;'> and bill_status=#{billStatus}</if>"+
//            "<if test='unCheckTimeStart != null and unCheckTimeStart != &apos;&apos;'><![CDATA[ and (un_check_time BETWEEN #{unCheckTimeStart} and #{unCheckTimeEnd}) ]]></if>"+
//            "<if test='contractNo != null and contractNo != &apos;&apos;'> and contract_no=#{contractNo}</if>"+
//            "<if test='customsDocNo != null and customsDocNo != &apos;&apos;'> and customs_doc_no=#{customsDocNo}</if>"+
//            "<if test='checkTimeStart != null and checkTimeStart != &apos;&apos;'><![CDATA[ and (check_time BETWEEN #{checkTimeStart} and #{checkTimeEnd}) ]]></if>"+
//            "<if test='paperDrewDateStart != null and paperDrewDateStart != &apos;&apos;'><![CDATA[ and (paper_drew_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>"+
//            "<if test='companyName != null and companyName != &apos;&apos;'> and company_name like concat('%',#{companyName})</if>"+
//            "</script>")
    int countEntryCustoms(@Param("customsNo") String customsNo, @Param("manageStatus") String manageStatus,@Param("companyTaxNo") String companyTaxNo,
                          @Param("companyName") String companyName, @Param("isCheck") String isCheck,
                          @Param("paperDrewDateStart")String paperDrewDateStart, @Param("paperDrewDateEnd")String paperDrewDateEnd,
                          @Param("checkTimeStart")String checkTimeStart,@Param("checkTimeEnd")String checkTimeEnd,
                          @Param("taxPeriod")String taxPeriod,@Param("voucherNo")String voucherNo,@Param("accountStatus")String accountStatus,
                          @Param("confirmStatus") String confirmStatus,
                          @Param("billStatus")String billStatus, @Param("unCheckTimeStart")String unCheckTimeStart,@Param("unCheckTimeEnd")String unCheckTimeEnd,
                          @Param("contractNo")String contractNo,@Param("customsDocNo")String customsDocNo,
                          @Param("voucherAccountTimeStart") String voucherAccountTimeStart, @Param("voucherAccountTimeEnd") String voucherAccountTimeEnd
    );

//    @Select("<script>" +
//            "select *  from t_dx_customs " +
//            "WHERE id is not null" +
//            "<if test='customsNo != null and customsNo != &apos;&apos;'> and customs_no in (${customsNo})</if>"+
//            "<if test='companyTaxNo != null and companyTaxNo != &apos;&apos;'> and company_tax_no=#{companyTaxNo}</if>"+
//            "<if test='voucherNo != null and voucherNo != &apos;&apos;'> and voucher_no=#{voucherNo}</if>"+
//            "<if test='accountStatus != null and accountStatus != &apos;&apos;'> and account_status=#{accountStatus}</if>"+
//            "<if test='confirmStatus != null and confirmStatus != &apos;&apos;'> and confirm_status=#{confirmStatus}</if>"+
//            "<if test='isCheck != null and isCheck != &apos;&apos;'> and is_check in (${isCheck})</if>"+
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tax_period=#{taxPeriod}</if>"+
//            "<if test='billStatus != null and billStatus != &apos;&apos;'> and bill_status=#{billStatus}</if>"+
//            "<if test='unCheckTimeStart != null and unCheckTimeStart != &apos;&apos;'><![CDATA[ and (un_check_time BETWEEN #{unCheckTimeStart} and #{unCheckTimeEnd}) ]]></if>"+
//            "<if test='contractNo != null and contractNo != &apos;&apos;'> and contract_no=#{contractNo}</if>"+
//            "<if test='customsDocNo != null and customsDocNo != &apos;&apos;'> and customs_doc_no=#{customsDocNo}</if>"+
//            "<if test='checkTimeStart != null and checkTimeStart != &apos;&apos;'><![CDATA[ and (check_time BETWEEN #{checkTimeStart} and #{checkTimeEnd}) ]]></if>"+
//            "<if test='paperDrewDateStart != null and paperDrewDateStart != &apos;&apos;'><![CDATA[ and (paper_drew_date BETWEEN #{paperDrewDateStart} and #{paperDrewDateEnd}) ]]></if>"+
//            "<if test='companyName != null and companyName != &apos;&apos;'> and company_name like concat('%',#{companyName})</if>"+
//            "<if test='offset != null and next !=null'>"+
//            " ORDER by paper_drew_date desc offset #{offset} rows fetch next #{next} rows only" +
//            "</if>"+
//            "</script>")
    List<TDxCustomsEntity> queryEntryPageCustoms(@Param("offset")Integer offset, @Param("next")Integer next,
                                                 @Param("customsNo")String customsNo, @Param("manageStatus") String manageStatus, @Param("companyTaxNo")String companyTaxNo,
                                                 @Param("companyName")String companyName, @Param("isCheck")String isCheck,
                                                 @Param("paperDrewDateStart")String paperDrewDateStart, @Param("paperDrewDateEnd")String paperDrewDateEnd,
                                                 @Param("checkTimeStart")String checkTimeStart,@Param("checkTimeEnd")String checkTimeEnd,
                                                 @Param("taxPeriod")String taxPeriod,@Param("voucherNo")String voucherNo,@Param("accountStatus")String accountStatus,
                                                 @Param("confirmStatus") String confirmStatus,
                                                 @Param("billStatus")String billStatus, @Param("unCheckTimeStart")String unCheckTimeStart,@Param("unCheckTimeEnd")String unCheckTimeEnd,
                                                 @Param("contractNo")String contractNo,@Param("customsDocNo")String customsDocNo,
                                                 @Param("voucherAccountTimeStart") String voucherAccountTimeStart, @Param("voucherAccountTimeEnd") String voucherAccountTimeEnd);
}
