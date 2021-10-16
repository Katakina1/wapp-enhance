package com.xforceplus.wapp.repository.dao.ext;

import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface DeductViewDao {

    @Select("select tax_rate,count(1) from t_xf_bill_deduct where business_type=#{w.businessType} group by tax_rate")
    Map<String,Integer> summaryByTaxRate(@Param("w") TXfBillDeductEntity entity);
}
