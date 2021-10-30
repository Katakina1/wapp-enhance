package com.xforceplus.wapp.modules.backFill.service;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.daoExt.TDxRecordInvoiceExtDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by Kenny Wong on 2021/10/28.
 * 底账数据服务
 */
@Service
@Slf4j
public class RecordInvoiceExtService extends ServiceImpl<TDxRecordInvoiceExtDao, TDxRecordInvoiceEntity> {

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList) {
        return withdrawRemainingAmountById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TDxRecordInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "withdrawRemainingAmount";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TDxRecordInvoiceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
    }
}
