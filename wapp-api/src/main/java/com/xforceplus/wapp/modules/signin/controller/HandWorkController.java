package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.ResponseVo;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.HandWorkService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/12.
 **/
@RestController
@RequestMapping("signIn/")
public class HandWorkController extends AbstractController {

    private HandWorkService handWorkService;

    @Autowired
    public HandWorkController(HandWorkService handWorkService) {
        this.handWorkService = handWorkService;
    }

    /**
     * 获取发票数据
     * @param params 查询条件
     * @return 手工签收页面数据集
     */
    @SysLog("发票签收-获取发票数据")
    @RequestMapping("PageList")
    public R getHandWorkList(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId=getUserId();
        params.put("userId",userId);

        //查询列表数据
        Query query = new Query(params);

        //执行业务层
        final List<RecordInvoiceEntity> invoiceList  = handWorkService.getRecordIncoiceList(schemaLabel,query);

        int total = handWorkService.queryTotal(schemaLabel,query);
        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 前台购方税号下拉框展示
     * @return
     */
    @RequestMapping("queryGf")
    public R getGfTaxNo(){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long userId=getUserId();
        List<OptionEntity> optionList = handWorkService.searchGf(schemaLabel,userId);
        return R.ok().put("optionList", optionList);
    }


    /**
     * 根据前台传过来的抵账表id的集合进行签收
     * @param ids
     * @return
     */
    @SysLog("发票签收-手工签收")
    @RequestMapping(value = "receiptInvoice",method = RequestMethod.POST)
    public R receiptInvoice(@RequestBody Long[]  ids){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        ResponseVo r=new ResponseVo(Boolean.FALSE);

        r.setSuccess(handWorkService.receiptInvoice(schemaLabel,ids,getUser()));
        return R.ok();
    }



}
