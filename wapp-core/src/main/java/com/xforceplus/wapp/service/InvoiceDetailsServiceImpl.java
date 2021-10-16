package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.repository.dao.InvoiceDao;
import com.xforceplus.wapp.repository.dao.InvoiceDetailsDao;
import com.xforceplus.wapp.repository.entity.InvoiceDetailsEntity;
import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import org.springframework.stereotype.Service;

/**
 * @author mashaopeng@xforceplus.com
 */
@Service
public class InvoiceDetailsServiceImpl extends ServiceImpl<InvoiceDetailsDao, InvoiceDetailsEntity> {
}
