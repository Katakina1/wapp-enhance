package com.xforceplus.wapp.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.xforceplus.wapp.entity.XfBillDeductDO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface XfBillDeductMapper extends BaseMapper<XfBillDeductDO> {

    List<XfBillDeductDO> selectList();

}
