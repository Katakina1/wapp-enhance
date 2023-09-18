package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-11-05 17:55
 **/
@Mapper
public interface RecordInvoiceDetailExtDao {

    @Select("select top (${size}) goods_name,uuid from t_dx_record_invoice_detail where uuid = #{uuid}")
    List<TDxRecordInvoiceDetailEntity> selectTopGoodsName(@Param("size") int size, @Param("uuid")String  uuid);

    @Select("select goods_name,uuid from t_dx_record_invoice_detail where uuid in (#{uuid})")
    List<TDxRecordInvoiceDetailEntity> selectGoodsName( @Param("uuid")List<String> uuid);
}
