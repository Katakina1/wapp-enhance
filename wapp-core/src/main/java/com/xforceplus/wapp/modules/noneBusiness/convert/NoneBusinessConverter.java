package com.xforceplus.wapp.modules.noneBusiness.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailDto;
import com.xforceplus.wapp.repository.entity.TXfNoneBusinessUploadDetailEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface NoneBusinessConverter {
    List<TXfNoneBusinessUploadDetailEntity> map(List<TXfNoneBusinessUploadDetailDto> list);

}
