package com.xforceplus.wapp.modules.taxcode.service;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.client.JanusClient;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.client.TaxCodeRsp;
import com.xforceplus.wapp.client.WappDb2Client;
import com.xforceplus.wapp.modules.taxcode.converters.TaxCodeConverter;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.repository.dao.TaxCodeDao;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class TaxCodeServiceImpl extends ServiceImpl<TaxCodeDao, TaxCodeEntity> {
    private final TaxCodeConverter taxCodeConverter;
    private final WappDb2Client wappDb2Client;
    private final JanusClient janusClient;

    public TaxCodeServiceImpl(TaxCodeConverter taxCodeConverter, WappDb2Client wappDb2Client, JanusClient janusClient) {
        this.taxCodeConverter = taxCodeConverter;
        this.wappDb2Client = wappDb2Client;
        this.janusClient = janusClient;
    }

    public Tuple2<List<TaxCode>, Page<TaxCodeEntity>> page(Long current, Long size,
                                                           String goodsTaxNo, String itemName, String itemNo, String medianCategoryCode) {
        log.debug("税编分页查询,入参,goodsTaxNo:{},itemName:{},itemNo:{},medianCategoryCode:{},分页数据,current:{},size:{}",
                goodsTaxNo, itemName, itemNo, medianCategoryCode, current, size);
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
        if (StringUtils.isNotBlank(medianCategoryCode)) {
            wrapper.eq(TaxCodeEntity::getMedianCategoryCode, medianCategoryCode);
        }
        Page<TaxCodeEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("税编分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(taxCodeConverter.map(page.getRecords()), page);
    }

    public Optional<TaxCode> getTaxCodeByItemNo(@NonNull String itemNo) {
        log.debug("通过itemNo[{}]查询税编", itemNo);
        Function<String, TaxCode> findTaxCode = (no) -> new LambdaQueryChainWrapper<>(getBaseMapper())
                .isNull(TaxCodeEntity::getDeleteFlag)
                .eq(TaxCodeEntity::getItemNo, no)
                .oneOpt().map(taxCodeConverter::map).orElse(null);
        TaxCode taxCode = findTaxCode.apply(itemNo);
        if (Objects.isNull(taxCode)) {
            taxCode = wappDb2Client.getItemNo(itemNo).map(findTaxCode).orElse(null);
        }
        log.debug("通过itemNo[{}]查询税编,结果:{}", itemNo, taxCode);
        return Optional.ofNullable(taxCode);
    }

    /**
     * 两个参数不能同时为空
     *
     * @param taxCode 税收编码
     * @param keyWord 搜搜关键字
     * @return 成功集合/失败原因
     */
    public Either<String, List<TaxCodeBean>> searchTaxCode(@Nullable String taxCode, @Nullable String keyWord) {
        return janusClient.searchTaxCode(taxCode, keyWord);
    }
}
