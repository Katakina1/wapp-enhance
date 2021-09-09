package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InvoiceListService;
import com.xforceplus.wapp.modules.redInvoiceManager.service.UploadScarletLetterService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/redInvoiceManager/invoiceList")
public class InvoiceListController extends AbstractController {



    @Autowired
    private InvoiceListService invoiceListService;
    @Autowired
    private UploadScarletLetterService uploadScarletLetterService;
    private static final Logger LOGGER = getLogger(InvoiceListController.class);

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("红票资料查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        UploadScarletLetterEntity list1 = uploadScarletLetterService.getTypeById(schemaLabel,query);
        if(list1.getOrgType().equals("5")&&list1.getTaxNo().equals("0")){
            List<UploadScarletLetterEntity> list = uploadScarletLetterService.queryListByStore(schemaLabel,query);
            ReportStatisticsEntity result = uploadScarletLetterService.queryTotalResultByStore(schemaLabel,query);

            PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

            return R.ok().put("orgType",list1.getOrgType()).put("page", pageUtil).put("totalCount", result.getTotalCount());
        }else if(list1.getOrgType().equals("5")||list1.getOrgType().equals("2")){
            List<UploadScarletLetterEntity> list = invoiceListService.queryList(schemaLabel,query);
            ReportStatisticsEntity result = invoiceListService.queryTotalResult(schemaLabel,query);

            PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

            return R.ok().put("orgType",list1.getOrgType()).put("page", pageUtil).put("totalCount", result.getTotalCount());
        }
        return R.error();
    }

    /**
     * 查询机构类型
     * @param params
     * @return
     */
    @SysLog("查询机构类型")
    @RequestMapping("/Orgid")
    public R orgList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        UploadScarletLetterEntity list = uploadScarletLetterService.getTypeById(schemaLabel,query);
        return R.ok().put("orgType",list.getOrgType()).put("taxNo",list.getTaxNo());

    }

    @RequestMapping("/invoicelist")
    @SysLog("查询红票信息列表")
    public R returnInvoice(@RequestParam Map<String, Object> params) {

        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = invoiceListService.getRedInvoiceCount(query);
        if(resultReturn == 0){
            List<InvoiceListEntity> groupRefundEntity = invoiceListService.getRedInvoiceList(query);

            PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());
            return R.error(1,"未找到红票信息").put("totalCount",resultReturn).put("page1", pageUtil1);
        }
        List<InvoiceListEntity> groupRefundEntity = invoiceListService.getRedInvoiceList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());

        return R.ok().put("page1", pageUtil1).put("totalCount",resultReturn);

    }

    @RequestMapping("/invoicedetaillist")
    @SysLog("查询详细红票信息列表")
    public R returnInvoiceDetail(@RequestBody String value,@RequestBody String value1) {


//        LOGGER.info("查询条件为:{}", params);
//        Query query = new Query(params);

        //抵账查列表数据
//        Integer resultReturn = invoiceListService.getRedInvoiceCount(query);
        List<InvoiceListEntity> invoiceListEntity = invoiceListService.getRedInvoiceDetailList(value,value1);
        return R.ok();
//        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());

//        return R.ok().put("page1", pageUtil1);

    }
}
