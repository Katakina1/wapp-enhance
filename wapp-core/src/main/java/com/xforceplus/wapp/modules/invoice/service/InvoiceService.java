package com.xforceplus.wapp.modules.invoice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceDto;
import com.xforceplus.wapp.modules.invoice.dto.InvoiceItemDto;
import com.xforceplus.wapp.modules.invoice.mapstruct.InvoiceMapper;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.repository.dao.TXfInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfInvoiceItemDao;
import com.xforceplus.wapp.repository.entity.TXfInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfInvoiceItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    TXfInvoiceDao tXfInvoiceDao;
    @Autowired
    TXfInvoiceItemDao tXfInvoiceItemDao;
    @Autowired
    InvoiceMapper invoiceMapper;

    public Response<InvoiceDto> detail(Long id) {
        TXfInvoiceEntity tXfInvoiceEntity = tXfInvoiceDao.selectById(id);
        if (tXfInvoiceEntity != null){
            LambdaQueryWrapper<TXfInvoiceItemEntity> queryWrapper = new LambdaQueryWrapper<>();
            List<TXfInvoiceItemEntity> tXfInvoiceItemEntities = tXfInvoiceItemDao.selectList(queryWrapper);

            InvoiceDto invoiceDto = invoiceMapper.entityToInvoiceDto(tXfInvoiceEntity);
            List<InvoiceItemDto> invoiceItemDtos = invoiceMapper.entityToInvoiceItemDtoList(tXfInvoiceItemEntities);
            invoiceDto.setDetails(invoiceItemDtos);
            return Response.ok("查询成功",invoiceDto);
        }

        return Response.ok("查询成功",null);
    }
}
