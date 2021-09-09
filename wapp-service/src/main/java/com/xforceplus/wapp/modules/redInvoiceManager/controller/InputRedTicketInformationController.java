package com.xforceplus.wapp.modules.redInvoiceManager.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InputRedTicketInformationEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.InvoiceListEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.UploadScarletLetterEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.export.BatchRedTicketExcel;
import com.xforceplus.wapp.modules.redInvoiceManager.service.InputRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/redInvoiceManager/inputRedTicketInformation")
public class InputRedTicketInformationController extends AbstractController {



    @Autowired
    private InputRedTicketInformationService inputRedTicketInformationService;
//    private InvoiceListService invoiceListService;
    @Autowired
//    private UploadScarletLetterService uploadScarletLetterService;
    private static final Logger LOGGER = getLogger(InputRedTicketInformationController.class);

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
        List<UploadScarletLetterEntity> list = inputRedTicketInformationService.queryList(schemaLabel,query);
            ReportStatisticsEntity result = inputRedTicketInformationService.queryTotalResult(schemaLabel,query);

            PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

            return R.ok().put("page", pageUtil).put("totalCount", result.getTotalCount());

    }


    @RequestMapping("/invoicelist")
    @SysLog("查询红票信息列表")
    public R returnInvoice(@RequestParam Map<String, Object> params) {

        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = inputRedTicketInformationService.getRedInvoiceCount(query);
        if(resultReturn == 0){
            List<InvoiceListEntity> groupRefundEntity = inputRedTicketInformationService.getRedInvoiceList(query);

            PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());
            return R.error(1,"未找到红票信息").put("totalCount",resultReturn).put("page1", pageUtil1);
        }
        List<InvoiceListEntity> groupRefundEntity = inputRedTicketInformationService.getRedInvoiceList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());

        return R.ok().put("page1", pageUtil1).put("totalCount",resultReturn);

    }

    @RequestMapping("/invoicelist1")
    @SysLog("查询红票信息列表")
    public R returnInvoice1(@RequestParam Map<String, Object> params) {

        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = inputRedTicketInformationService.getRedInvoiceCount(query);

        List<InvoiceListEntity> groupRefundEntity = inputRedTicketInformationService.getRedInvoiceList1(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());

        return R.ok().put("page1", pageUtil1);

    }

    /**
     * 发票带出
     * @param params
     * @return
     */
    @SysLog("发票带出")
//    @PostMapping(value = URI_RDE_RED_TICKET_INVOICE)
    @RequestMapping("/query")
    public R invoiceQuery(@RequestBody Map<String,Object> params){
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = inputRedTicketInformationService.getGfNameAndTaxNo(userId);
        gfNameAndTaxNoList.forEach(org->{
            //if(params.get("gfName").equals(org.getOrgname())){
            params.put("gfTaxno",org.getTaxno());
            //}
        });
//        params.put("gfTaxno",gfNameAndTaxNoList.get(0).getTaxno());
        LOGGER.info("发票带出,param{}",params);
        Boolean flag=true;
        if(!("04".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))||"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode"))))){
            return R.error(488, "发票代码格式错误");
        }else{
                params.put("invoiceType","01");
        }
        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=inputRedTicketInformationService.invoiceQueryOut(params);
        //List<RedTicketMatch> list1=poEntityPagedQueryResult.getResults();

        if(!(poEntityPagedQueryResult.getMsg()==null)){
            return R.error(488,poEntityPagedQueryResult.getMsg());
        }
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),1,0,0);
        return R.ok().put("page",pageUtils);
    }

    /**
     * 发票录入
     * @param params
     * @return
     */
    @SysLog("发票录入")
    @RequestMapping("/saveRedTicket")
    public R invoiceIn(@RequestParam Map<String,Object> params){
        LOGGER.info("发票录入,param{}",params);
        Boolean flag=true;
        Long userId=getUserId();
        List<OrgEntity> gfNameAndTaxNoList = inputRedTicketInformationService.getGfNameAndTaxNo(userId);
        //获取发票总金额
//        RedTicketMatch redTicketMatch = inputRedTicketInformationService.selectNoticeById(params);
//        BigDecimal mo = redTicketMatch.getRedTotalAmount();
        Integer resultReturn = inputRedTicketInformationService.getRedInvoiceCount1(params);

        gfNameAndTaxNoList.forEach(org->{
            /*if(params.get("gfName").equals(org.getOrgname())){*/
            params.put("xfTaxno",org.getTaxno());
           /* }*/
        });
        if(!CommonUtil.isValidNum((String)params.get("invoiceCode"),"^(\\d{10}|\\d{12})$")) {
            return R.error(488, "发票代码格式错误");
        }else if(!CommonUtil.isValidNum((String)params.get("invoiceNo"),"^[\\d]{8}$")){
            return R.error(488, "发票号码格式错误");
        }else if(!"01".equals(CommonUtil.getFplx((String)params.get("invoiceCode")))){
            return R.error(488, "不是专票代码");
        }

        if(resultReturn == 0){

                if( (new BigDecimal(((String)params.get("taxAmount"))).setScale(2, BigDecimal.ROUND_HALF_UP)).compareTo((new BigDecimal(((String)params.get("invoiceAmount"))).setScale(2, BigDecimal.ROUND_HALF_UP)).multiply((new BigDecimal(((String)params.get("taxRate"))).divide(new BigDecimal(100)))).setScale(2, BigDecimal.ROUND_HALF_UP))!=0){
                        return R.error(488, "金额、税率、税额 输入有误");
                }
                if( (new BigDecimal(((String)params.get("totalAmount"))).setScale(2, BigDecimal.ROUND_HALF_UP)).compareTo((new BigDecimal(((String)params.get("invoiceAmount")))).add((new BigDecimal(((String)params.get("taxAmount"))))).setScale(2, BigDecimal.ROUND_HALF_UP))!=0){
                    return R.error(488, "金额、价税合计、税额 输入有误");
                }

        }
        if( (new BigDecimal(((String)params.get("taxAmount"))).setScale(2, BigDecimal.ROUND_HALF_UP)).compareTo((new BigDecimal(((String)params.get("invoiceAmount")))).multiply((new BigDecimal(((String)params.get("taxRate"))).divide(new BigDecimal(100)))).setScale(2, BigDecimal.ROUND_HALF_UP))!=0){
            return R.error(488, "金额、税率、税额 输入有误");
        }
        if( (new BigDecimal(((String)params.get("totalAmount"))).setScale(2, BigDecimal.ROUND_HALF_UP)).compareTo((new BigDecimal(((String)params.get("invoiceAmount")))).add((new BigDecimal(((String)params.get("taxAmount"))))).setScale(2, BigDecimal.ROUND_HALF_UP))!=0){
            return R.error(488, "金额、价税合计、税额 输入有误");
        }

        BigDecimal amount=new BigDecimal((String)params.get("invoiceAmount"));
        BigDecimal rate=new BigDecimal((String) params.get("taxRate")).divide(new BigDecimal(100));;
        BigDecimal taxAmount=new BigDecimal((String)params.get("taxAmount"));
        PagedQueryResult<InvoiceEntity> poEntityPagedQueryResult=inputRedTicketInformationService.invoiceQueryList(params);
        if(poEntityPagedQueryResult.getMsg()!=null){
            return R.error(488,poEntityPagedQueryResult.getMsg());
        }
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),1,0,0);
        return R.ok().put("page",pageUtils);
    }

    @RequestMapping("/selectRedTicketById")
    @SysLog("红票查询列表")
    public R selectRedTicketById(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        params.put("userId",getUserId());
        RedTicketMatch invoiceEntity = inputRedTicketInformationService.selectRedTicketById(params);
        return R.ok().put("invoiceEntity", invoiceEntity);

    }

    /**
     * 发票录入
     * @param inputRedTicketInformationEntity
     * @return
     */
    @SysLog("发票清空")
    @RequestMapping("/emptyRedTicket")
    public R empty(@RequestBody InputRedTicketInformationEntity inputRedTicketInformationEntity){

        InputRedTicketInformationEntity entity = inputRedTicketInformationService.queryUuid(inputRedTicketInformationEntity.getId());
       String uuid = entity.getInvoiceCode()+entity.getInvoiceNo();
        inputRedTicketInformationService.emptyRecord(uuid);
        inputRedTicketInformationService.emptyRedInvoice(inputRedTicketInformationEntity.getId());
        return  R.ok();

    }


}
