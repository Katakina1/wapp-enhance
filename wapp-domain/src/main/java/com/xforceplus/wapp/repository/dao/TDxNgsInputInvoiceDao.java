package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.evat.common.entity.TDxNgsInputInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TDxNgsInputInvoiceDao extends BaseMapper<TDxNgsInputInvoiceEntity> {
//    @Select("<script>" +
//            "select *  from t_dx_ngs_input_invoice " +
//            "WHERE id is not null" +
//            "<if test='taxPeriod != null and taxPeriod != &apos;&apos;'> and tax_period in (${taxPeriod})</if>"+
//            "<if test='gfTaxNo != null and gfTaxNo != &apos;&apos;'> and gf_tax_no=#{gfTaxNo}</if>"+
//            "<if test='offset != null and next !=null'>"+
//            " ORDER by paper_drew_date desc offset #{offset} rows fetch next #{next} rows only" +
//            "</if>"+
//            "</script>")
    List<TDxNgsInputInvoiceEntity> queryPageCustoms(@Param("offset")Integer offset, @Param("next")Integer next,
                                            @Param("taxPeriod")String taxPeriod,@Param("gfTaxNo")String gfTaxNo);


}
