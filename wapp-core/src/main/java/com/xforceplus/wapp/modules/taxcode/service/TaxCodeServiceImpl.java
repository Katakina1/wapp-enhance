package com.xforceplus.wapp.modules.taxcode.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.taxcode.converters.TaxCodeConverter;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.repository.dao.TaxCodeDao;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class TaxCodeServiceImpl extends ServiceImpl<TaxCodeDao, TaxCodeEntity> {
    private final TaxCodeConverter taxCodeConverter;

    public TaxCodeServiceImpl(TaxCodeConverter taxCodeConverter) {
        this.taxCodeConverter = taxCodeConverter;
    }

    public Tuple2<List<TaxCode>, Page<TaxCodeEntity>> page(Long current, Long size,
                                                           String goodsTaxNo, String itemName, String itemNo) {
        LambdaQueryChainWrapper<TaxCodeEntity> wrapper = new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag);
        if (StringUtils.isNotBlank(goodsTaxNo)) {
            wrapper.eq(TaxCodeEntity::getGoodsTaxNo, goodsTaxNo);
        }
        if (StringUtils.isNotBlank(itemName)) {
            wrapper.eq(TaxCodeEntity::getItemName, itemName);
        }
        if (StringUtils.isNotBlank(itemNo)) {
            wrapper.eq(TaxCodeEntity::getItemNo, itemNo);
        }
        Page<TaxCodeEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("超期配置分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(taxCodeConverter.map(page.getRecords()), page);
    }

    public TaxCode getTaxCodeByItemNo(@NonNull String itemNo) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag)
                .eq(TaxCodeEntity::getItemNo, itemNo)
                .oneOpt().map(taxCodeConverter::map).orElse(null);
    }
}
