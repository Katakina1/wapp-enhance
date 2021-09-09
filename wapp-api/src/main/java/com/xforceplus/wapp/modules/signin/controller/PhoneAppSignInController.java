package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.PhoneAppSignInService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
@RestController
@RequestMapping("phoneApp/")
public class PhoneAppSignInController extends AbstractController {

    private PhoneAppSignInService phoneAppSignInService;

    @Autowired
    public PhoneAppSignInController(PhoneAppSignInService phoneAppSignInService) {
        this.phoneAppSignInService = phoneAppSignInService;
    }

    /**
     * 手机app签收的分页数据查询
     * @param params
     * @return
     */
    @RequestMapping("phoneAppList")
    public R getPhoneAppList(@RequestBody Map<String, Object> params){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId=getUserId();
        params.put("userId",userId);
        //查询列表数据
        Query query = new Query(params);

        //执行业务层
        final List<RecordInvoiceEntity> invoiceList  = phoneAppSignInService.getRecordIncoiceList(schemaLabel,query);

        int total = phoneAppSignInService.queryTotal(schemaLabel,query);
        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 购方税号下拉列表查询--可优化
     * @return
     */
    @RequestMapping("queryGf")
    public R getGfData(){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId=getUserId();
        List<OptionEntity> optionList = phoneAppSignInService.searchGf(schemaLabel,userId);
        return R.ok().put("optionList", optionList);
    }

    /**
     * 获取图片路径
     * @param uuid
     * @return
     */
    @RequestMapping("getPicture")
    public R getDetailsPicture(@RequestParam String uuid){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        String url=phoneAppSignInService.getUrlById(schemaLabel,uuid);
        if(url==null||url==""){
            return R.error(1,"未查询到发票影像信息！");
        }
        return R.ok().put("url",url);
    }
}
