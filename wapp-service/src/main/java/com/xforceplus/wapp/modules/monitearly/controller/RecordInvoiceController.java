package com.xforceplus.wapp.modules.monitearly.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.monitearly.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.monitearly.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.monitearly.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 逾期预警控制层
 * Created by alfred.zong on 2018/04/12.
 */
@RestController
@RequestMapping("monit/record/")
public class RecordInvoiceController extends AbstractController {

    private static final  Logger LOGGER = getLogger(RecordInvoiceController.class);

    @Autowired
    private RecordInvoiceService recordInvoiceservice;

    /**
     * 逾期预警
     *
     * @param params 为前台穿的条件集合
     * @return map集合；
     */
    @SysLog("逾期预警查询")
    @RequestMapping("/search")
    public R queryRecord(@RequestParam Map<String, Object> params) {
        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();
        //分库分表所需要的参数
        params.put("schemaLabel",schemaLabel);
        //getUserId()是为了获取当前用户的ID
        params.put("userId", getUserId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("查询条件为:{}", params);
        }

        //查询列表数据
        final Query query = new Query(params);
        final PagedQueryResult<RecordInvoiceEntity> RecordResultlist = recordInvoiceservice.queryInvoice(query);

        //分页
        final PageUtils pageUtil = new PageUtils(RecordResultlist.getResults(), RecordResultlist.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil).put("totalAmount", RecordResultlist.getTotalAmount()).put("totalTax", RecordResultlist.getTotalTax());
    }

    /**
     * 批量更新认证状态
     *
     * @param lists
     * @param gfNo
     * @return
     */
    @RequestMapping("/update")
    public R updateRecord(@RequestParam("lists[]") List<String> lists,@RequestParam("gfNo[]") List<String> gfNo) {
        //分库标识
        final String schemaLabel=getCurrentUserSchemaLabel();

        //获取登录用户的登录名及用户名
        UserEntity userEntity= getUser();

        //要传入更新的ID
        List<UserTaxnoEntity> invoicelist = Lists.newArrayList();
        UserTaxnoEntity userTaxnoEntity = new UserTaxnoEntity();

        userTaxnoEntity.setLoginname(userEntity.getLoginname());
        userTaxnoEntity.setUsername(userEntity.getUsername());
        Integer reslut =0;
        for (int i = 0; i < lists.size(); i++) {

            userTaxnoEntity.setId(lists.get(i));
            userTaxnoEntity.setSchemaLabel(schemaLabel);
            userTaxnoEntity.setUserid(getUserId());
            userTaxnoEntity.setOrgid(gfNo.get(i));
            String rzhBDate=recordInvoiceservice.getRzhBDate(schemaLabel,gfNo.get(i));

            userTaxnoEntity.setRzhDate(rzhBDate);
            invoicelist.add(userTaxnoEntity);
            reslut = recordInvoiceservice.updateInvoiceList(invoicelist);
        }
        if(0!=reslut){
            Map map=new HashMap<>();
            map.put("code",1);
            map.put("reslut",reslut);
            return R.ok(map);
        }
        return R.error();
    }
}
