package com.xforceplus.wapp.repository.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfBillSettlementEntity;

/**
 * <p>
 * 1、业务单和结算单关联关系
 * </p>
 *
 * @author malong@xforceplus.com
 * @since 2021-10-14
 */
@Mapper
public interface TXfBillSettlementDao extends BaseMapper<TXfBillSettlementEntity> {

	/**
	 * 1、修改status
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @param status
	 * @return
	 */
	@Update("update t_xf_bill_settlement set status =#{status},update_time=GETDATE()  "
			+ "where business_no = #{businessNo} and settlement_no = #{settlementNo} and  business_type = #{businessType} ")
	public int updateByBusinessNoAndSettlementNo(@Param("businessNo") String businessNo, @Param("settlementNo") String settlementNo, @Param("businessType") Integer businessType, @Param("status") int status);

	/**
	 * 1、根据业务单类型和结算单号查询
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	@Select("select * from t_xf_bill_settlement where business_no =  #{businessNo} and business_type = #{businessType} ")
	public List<TXfBillSettlementEntity> queryByBusinessNo(@Param("businessNo") String businessNo, @Param("businessType") Integer businessType);

	/**
	 * 1、根据业务单类型和结算单号查询
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	@Select("select * from t_xf_bill_settlement where settlement_no = #{settlementNo} and business_type = #{businessType} ")
	public List<TXfBillSettlementEntity> queryBySettlementNo(@Param("settlementNo") String settlementNo, @Param("businessType") Integer businessType);

	/**
	 * 1、根据业务单类型和结算单号查询
	 * @param businessNo
	 * @param settlementNo
	 * @param businessType
	 * @return
	 */
	@Select("select * from t_xf_bill_settlement where business_no = #{businessNo} and settlement_no = #{settlementNo} and business_type = #{businessType} ")
	public List<TXfBillSettlementEntity> queryByBusinessNoAndSettlementNo(@Param("businessNo") String businessNo, @Param("settlementNo") String settlementNo, @Param("businessType") Integer businessType);
}
