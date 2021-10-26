package com.xforceplus.wapp.repository.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
* <p>
* 非商电票上传记录明细表 Mapper 接口
* </p>
*
* @author malong@xforceplus.com
* @since 2021-10-21
*/
public interface TXfNoneBusinessUploadDetailDao extends BaseMapper<TXfNoneBusinessUploadDetailEntity> {

    public Page<TXfNoneBusinessUploadDetailDto> list(Page<TXfNoneBusinessUploadDetailDto> page,@Param("entity") TXfNoneBusinessUploadQueryDto dto);


    public List<TXfNoneBusinessUploadDetailDto> list(@Param("entity") TXfNoneBusinessUploadQueryDto dto);

}
