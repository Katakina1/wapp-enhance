package com.xforceplus.wapp.modules.blackwhitename.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.blackwhitename.convert.SpeacialCompanyConverter;
import com.xforceplus.wapp.modules.blackwhitename.dto.SpecialCompanyImportDto;
import com.xforceplus.wapp.modules.blackwhitename.listener.SpeclialCompanyImportListener;
import com.xforceplus.wapp.repository.dao.TXfBlackWhiteCompanyDao;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
@Slf4j
public class SpeacialCompanyService extends ServiceImpl<TXfBlackWhiteCompanyDao, TXfBlackWhiteCompanyEntity> {
    private final SpeacialCompanyConverter companyConverter;

    public SpeacialCompanyService(SpeacialCompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Tuple2<List<TXfBlackWhiteCompanyEntity>, Page<TXfBlackWhiteCompanyEntity>> page(Long current, Long size) {
//        LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity> wrapper = new LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity>(baseMapper);
//        Page<TXfBlackWhiteCompanyEntity> page = wrapper.page(new Page<>(current, size));
//        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
//        return Tuple.of(companyConverter.map(page.getRecords()), page);
        return null;
    }

    public TXfBlackWhiteCompanyEntity getBlackListBy6D(String supplier6d, String supplierType) {
        QueryWrapper<TXfBlackWhiteCompanyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_6D, supplier6d);
        wrapper.eq(TXfBlackWhiteCompanyEntity.SUPPLIER_TYPE, supplierType);
        return getOne(wrapper);

    }

    /**
     * 导入抬头信息
     *
     * @param is
     * @return
     */
    public Either<String, Integer> importData(InputStream is,String type) {
        QueryWrapper wrapper = new QueryWrapper<>();
        SpeclialCompanyImportListener listener = new SpeclialCompanyImportListener();
        EasyExcel.read(is, SpecialCompanyImportDto.class, listener).sheet().doRead();
        if (CollectionUtils.isEmpty(listener.getValidInvoices())) {
            return Either.left("未解析到数据");
        }
        log.info("导入数据解析条数:{}", listener.getRows());
        List<TXfBlackWhiteCompanyEntity> validList=companyConverter.reverse(listener.getValidInvoices(), 1L);
        validList.stream().forEach( e ->{
            e.setSupplierType(type);
        });
        boolean save = saveBatch(validList);
        return save ? Either.right(listener.getValidInvoices().size()) : Either.right(0);
    }


    /**
     * 判断供应商是否在黑白名单中
     *
     * @param supplierType {@link String} 0-黑名单 1-白名单
     * @param memo         供应商6D
     * @return
     */
    public boolean hitBlackOrWhiteList(String supplierType, String memo) {
        return 0 == count(
                new QueryWrapper<TXfBlackWhiteCompanyEntity>()
                        .lambda()
                        // 黑名单
                        .eq(TXfBlackWhiteCompanyEntity::getSupplierType, supplierType)
                        // 供应商6D
                        .eq(TXfBlackWhiteCompanyEntity::getSupplier6d, memo)
        );
    }
}
