package com.xforceplus.wapp.modules.invoice.service;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
public class InvoiceServiceImpl extends ServiceImpl<TXfInvoiceDao, TXfInvoiceEntity> {

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList 实体对象集合
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(Collection<TXfInvoiceEntity> entityList) {
        return updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据id将入参实体的剩余金额加回到原发票上
     *
     * @param entityList
     * @param batchSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean withdrawRemainingAmountById(List<TXfInvoiceEntity> entityList, int batchSize) {
        String sqlStatement = "update t_xf_invoice set remaining_amount = remaining_amount + #{remainingAmount} where id = #{id}";
        return executeBatch(entityList, batchSize,
                (sqlSession, entity) -> {
                    MapperMethod.ParamMap<TXfInvoiceEntity> param = new MapperMethod.ParamMap<>();
                    param.put(Constants.ENTITY, entity);
                    sqlSession.update(sqlStatement, param);
                }
        );
    }

}
