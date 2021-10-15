package com.xforceplus.wapp.modules.blackwhitename.convert;

import com.xforceplus.wapp.converters.BaseConverter;
import com.xforceplus.wapp.converters.GlobalConfig;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Mapper(config = GlobalConfig.class, uses = BaseConverter.class)
public interface SpeacialCompanyConverter {
    List<TXfBlackWhiteCompanyEntity> map(List<TXfBlackWhiteCompanyEntity> entity);

    TXfBlackWhiteCompanyEntity map(TXfBlackWhiteCompanyEntity entity);
}
