package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.OrderService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器(Order)
 */
@RestController
public class OrderController extends AbstractController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    private final OrganizationService organizationService;

    @Autowired
    public OrderController(OrderService orderService, OrganizationService organizationService) {
        this.orderService = orderService;
        this.organizationService = organizationService;
    }

    @SysLog("订单查询")
    @PostMapping("/modules/fixed/orderList/query")
    public R queryOrders(@RequestParam Map<String,Object> params){

        Query query = new Query(params);

        List<OrderEntity> orderEntities = orderService.findOrderList(query);
        Integer countOrder = orderService.countOrders(query);
        PageUtils pageUtil = new PageUtils(orderEntities, countOrder, query.getLimit(), query.getPage());

        return R.ok().put("page",pageUtil);

    }

    @SysLog("获取机构类型orgtype判断购方和销方")
    @PostMapping("/modules/fixed/orgtypeQuery")
    public R orgtypeQuery(){
        UserEntity userEntity = getUser();
        OrganizationEntity organizationEntity = organizationService.queryObject(getCurrentUserSchemaLabel(), (long) userEntity.getOrgid());

        logger.info("用户orgid = " + userEntity.getOrgid() + "和organizationEntity = " + organizationEntity);

        return R.ok().put("orgtype",organizationEntity.getOrgtype());
    }


}
