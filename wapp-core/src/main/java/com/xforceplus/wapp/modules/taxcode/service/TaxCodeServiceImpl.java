package com.xforceplus.wapp.modules.taxcode.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.repository.dao.TaxCodeDao;
import com.xforceplus.wapp.repository.entity.TaxCodeEntity;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Service
public class TaxCodeServiceImpl extends ServiceImpl<TaxCodeDao, TaxCodeEntity> {
    public Tuple2<List<TaxCode>, Long> page(Long current, Long size, String goodsTaxNo, String itemName, String itemNo) {
        return null;
    }

    public boolean delete(List<Long> ids) {
        return false;
    }

    public TaxCode getTaxCodeByItemNo(String itemNo) {
        return null;
    }
}
