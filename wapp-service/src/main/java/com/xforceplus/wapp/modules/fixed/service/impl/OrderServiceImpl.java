package com.xforceplus.wapp.modules.fixed.service.impl;

import com.xforceplus.wapp.modules.fixed.dao.OrderEntityDao;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderEntityDao orderEntityDao;

    @Override
    public List<OrderEntity> findOrderList(Map<String, Object> map) {
        return orderEntityDao.findOrderList(map);
    }

    @Override
    public Integer countOrders(Map<String, Object> map) {
        return orderEntityDao.countOrders(map);
    }
}
