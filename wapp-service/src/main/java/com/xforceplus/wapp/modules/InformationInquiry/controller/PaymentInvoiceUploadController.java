package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.PaymentInvoiceUploadExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentInvoiceUploadService;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InputRedTicketInformationEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.export.ComprehensiveInvoiceQueryExcel;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/InformationInquiry/paymentInvoiceUpload")
public class PaymentInvoiceUploadController extends AbstractController {



    @Autowired
    private PaymentInvoiceUploadService paymentInvoiceUploadService;
    @Autowired
    private PaymentInvoiceQueryService paymentInvoiceQueryService;
    private static final Logger LOGGER = getLogger(PaymentInvoiceUploadController.class);
    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扣款发票信息列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID
        query.put("userID", getUserId());
        List<PaymentInvoiceUploadEntity> list = paymentInvoiceUploadService.queryList(query);
            ReportStatisticsEntity result = paymentInvoiceUploadService.queryTotalResult(query);

            PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

            return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

    }

//    /**
//     * 保存扣款发票信息
//     */
//    @SysLog("保存扣款发票信息")
//    @RequestMapping("/savelist")
//    public R savelist(@RequestBody PaymentInvoiceUploadEntity menuEntity) {
//
//        paymentInvoiceUploadService.saveInvoice(menuEntity);
//        return R.ok();
//
//    }
//
//    /**
//     * 查询列表
//     * @param params
//     * @return
//     */
//    @SysLog("扣款发票明细信息列表")
//    @RequestMapping("/detaillist")
//    public R detaillist(@RequestParam Map<String, Object> params) {
//        LOGGER.info("查询条件为:{}", params);
//        final String schemaLabel = getCurrentUserSchemaLabel();
//        //查询列表数据
//        Query query = new Query(params);
//        //当前登录人ID
//        query.put("userID", getUserId());
//        List<PaymentInvoiceUploadEntity> list = paymentInvoiceUploadService.queryList1(query);
//        ReportStatisticsEntity result = paymentInvoiceUploadService.queryTotalResult1(query);
//
//        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());
//
//        return R.ok().put("page1", pageUtil).put("totalCount1", result.getTotalCount());
//
//    }
//
//    /**
//     * 保存扣款发票信息
//     */
//    @SysLog("保存扣款发票明细信息")
//    @RequestMapping("/savedetaillist")
//    public R savedetaillist(@RequestBody PaymentInvoiceUploadEntity menuEntity) {
//
//        paymentInvoiceUploadService.saveInvoice1(menuEntity);
//        return R.ok();
//
//    }

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扣款发票信息列表（销）")
    @RequestMapping("/querylist")
    public R querylist(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        query.put("userCode",getUser().getUsercode());
        List<PaymentInvoiceUploadEntity> list = paymentInvoiceQueryService.queryList(query);
        ReportStatisticsEntity result = paymentInvoiceQueryService.queryTotalResult(query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

    }

    /**
     * 查询列表
     * @param
     * @return
     */
    @SysLog("删除")
    @RequestMapping("/deletefail")
    public R deletefail(@RequestBody PaymentInvoiceUploadEntity paymentInvoiceUploadEntity) {
        LOGGER.info("查询条件为:{}", paymentInvoiceUploadEntity);
        //查询列表数据

        Long id = paymentInvoiceUploadEntity.getId();
        paymentInvoiceUploadService.deletefail(id);
        return R.ok();


    }

}
