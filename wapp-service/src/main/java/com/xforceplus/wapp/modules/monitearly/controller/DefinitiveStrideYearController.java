package com.xforceplus.wapp.modules.monitearly.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.service.DefinitiveStrideYearCopService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 普票跨跨年度预警
 * Created by alfred.zong on 2018/04/16.
 */
@RestController
@RequestMapping("monit/definitve")
public class DefinitiveStrideYearController extends AbstractController {

    //日志
    private static final  Logger LOGGER = getLogger(DefinitiveStrideYearController.class);

    private final DefinitiveStrideYearCopService definitiveStrideYearCopService;

    @Autowired
    public DefinitiveStrideYearController(DefinitiveStrideYearCopService definitiveStrideYearCopService){
        this.definitiveStrideYearCopService=definitiveStrideYearCopService;
    }

    @SysLog("普票跨年度预警查询")
    @RequestMapping("/search")
    public R  queryDefinitiveStrideList(@RequestParam Map<String,Object> params){
        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();
        //schemaLabel：分库分表所需要的参数
        params.put("schemaLabel",schemaLabel);
        //getUserId()是为了获取当前用户的ID
        params.put("userId", getUserId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("查询条件为:{}", params);
        }

        //查询列表数据
        final Query query = new Query(params);
        final PagedQueryResult<RecordInvoiceEntity> definitiveResultList=definitiveStrideYearCopService.queryDefinitiveList(query);

        //分页
        final PageUtils pageUtil=new PageUtils(definitiveResultList.getResults(),definitiveResultList.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page", pageUtil).put("totalAmount", definitiveResultList.getTotalAmount()).put("totalTax", definitiveResultList.getTotalTax());
    }

}
