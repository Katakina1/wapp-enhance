package com.xforceplus.wapp.modules.invoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.modules.invoice.mapstruct.InvoiceMapper;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.dao.TXfInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
public class InvoiceServiceImpl extends ServiceImpl<TXfInvoiceDao, TXfInvoiceEntity> {

    @Autowired
    TXfInvoiceItemDao tXfInvoiceItemDao;
    @Autowired
    InvoiceMapper invoiceMapper;

    public Response<InvoiceDto> detail(Long id) {
        TXfInvoiceEntity tXfInvoiceEntity = getBaseMapper().selectById(id);
        if (tXfInvoiceEntity != null) {
            LambdaQueryWrapper<TXfInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
            List<TXfInvoiceItemEntity> tXfInvoiceItemEntities = tXfInvoiceItemDao.selectList(queryWrapper);

            InvoiceDto invoiceDto = invoiceMapper.entityToInvoiceDto(tXfInvoiceEntity);
            List<InvoiceItemDto> invoiceItemDtos = invoiceMapper.entityToInvoiceItemDtoList(tXfInvoiceItemEntities);
            invoiceDto.setDetails(invoiceItemDtos);
            return Response.ok("查询成功", invoiceDto);
        }

        return Response.ok("查询成功", null);
    }

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
