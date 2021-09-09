package com.xforceplus.wapp.modules.redTicket.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
import com.xforceplus.wapp.modules.base.export.DebtExcel;
import com.xforceplus.wapp.modules.redTicket.entity.*;
import com.xforceplus.wapp.modules.redTicket.export.OpenRedTicketExcel;
import com.xforceplus.wapp.modules.redTicket.service.AgreementRedTicketInformationService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.redTicket.WebUriMappingConstant.*;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class QueryOpenRedTicketDataController extends AbstractController {
    private QueryOpenRedTicketDataService queryOpenRedTicketDataService;
    private static final Logger LOGGER = getLogger(QueryOpenRedTicketDataController.class);

    @Autowired
    public QueryOpenRedTicketDataController(QueryOpenRedTicketDataService queryOpenRedTicketDataService) {
        this.queryOpenRedTicketDataService = queryOpenRedTicketDataService;
    }
    @Autowired
    private AgreementRedTicketInformationService agreementRedTicketInformationService;
    @RequestMapping(URI_OPEN_RED_TICKET_LIST)
    @SysLog("开红票查询列表")
    public R list(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        Integer result = queryOpenRedTicketDataService.getRedTicketMatchListCount(query);
        List<RedTicketMatch> list = queryOpenRedTicketDataService.queryOpenRedTicket(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);

    }

    @SysLog("开红票资料导出")
    @AuthIgnore
    @RequestMapping("export/RedTicket")
    public void exportRedTicket(@RequestParam Map<String, Object> params,HttpServletResponse response) {


        List<RedTicketMatch> list = queryOpenRedTicketDataService.queryOpenRedTicket(params);
        final Map<String, List<RedTicketMatch>> map = newHashMapWithExpectedSize(1);
        map.put("redTicketMatchList",list);
        //生成excel
        final OpenRedTicketExcel excelView = new OpenRedTicketExcel(map, "export/redTicket/openRedTicket.xlsx", "redTicketMatchList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "openRedTicket" + excelName);
    }

    @RequestMapping(URI_OPEN_RED_TICKET_RETURN_DETAIL)
    @SysLog("开红票查询明细列表")
    public R allList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //退货查列表数据
        Integer resultReturn = queryOpenRedTicketDataService.getReturnGoodsListCount(query);
        List<ReturnGoodsEntity> returnGoodsList = queryOpenRedTicketDataService.getReturnGoodsList(query);

        //蓝票列表数据
        Integer resultRecord = queryOpenRedTicketDataService.getRecordInvoiceListCount(query);
        List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(query);
        //蓝票明细列表数据
        Integer resultRecordDetail= queryOpenRedTicketDataService.getRecordInvoiceDetailListCount(query);
        List<InvoiceDetail> recordInvoiceDetailList = queryOpenRedTicketDataService.getRecordInvoiceDetailList(query);
        for (InvoiceDetail rd:recordInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        //红票明细列表数据
        Integer resultMerge = queryOpenRedTicketDataService.getMergeInvoiceDetailListCount(query);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        for (RedTicketMatchDetail rd:mergeInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        PageUtils pageUtil1 = new PageUtils(returnGoodsList, resultReturn, query.getLimit(), query.getPage());
        PageUtils pageUtil2 = new PageUtils(recordInvoiceList, resultRecord, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, resultRecordDetail, query.getLimit(),query.getPage());
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, resultMerge, query.getLimit(),query.getPage());

        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2).put("page3", pageUtil3).put("page4", pageUtil4);

    }

    @RequestMapping(URI_OPEN_RED_TICKET_RETURN_LIST_BY_SERIAL_NUMBER)
    @SysLog("通过id查询退货列表")
    public R returnGoodsListByNumber(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //退货查列表数据
        Integer resultReturn = queryOpenRedTicketDataService.getReturnGoodsListCount(query);
        List<ReturnGoodsEntity> returnGoodsList = queryOpenRedTicketDataService.getReturnGoodsList(query);

        PageUtils pageUtil1 = new PageUtils(returnGoodsList, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }

    @RequestMapping(URI_OPEN_RED_TICKET_RETURN_LIST_BY_INVOICECODE_INVOICENO)
    @SysLog("通过代号查询蓝票列表")
    public R invoiceListByCodeNo(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //蓝票查列表数据
        Integer resultRecord = queryOpenRedTicketDataService.getRecordInvoiceListCount(query);
        List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(query);

        //蓝票明细列表数据
        Integer resultRecordDetail= queryOpenRedTicketDataService.getRecordInvoiceDetailListCount(query);
        List<InvoiceDetail> recordInvoiceDetailList = queryOpenRedTicketDataService.getRecordInvoiceDetailList(query);
        for (InvoiceDetail rd:recordInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        //红票明细列表数据
        Integer resultMerge = queryOpenRedTicketDataService.getMergeInvoiceDetailListCount(query);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        for (RedTicketMatchDetail rd:mergeInvoiceDetailList){
            rd.setRedRushTaxAmount(rd.getRedRushAmount().multiply(new BigDecimal(rd.getTaxRate()).divide(new BigDecimal(100))));
        }
        PageUtils pageUtil2 = new PageUtils(recordInvoiceList, resultRecord, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, resultRecordDetail, query.getLimit(),query.getPage());
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, resultMerge, query.getLimit(),query.getPage());

        return R.ok().put("page2", pageUtil2).put("page3", pageUtil3).put("page4", pageUtil4);

    }

    @RequestMapping(URI_OPEN_RED_TICKET_RETURN_INVOICE_DETAIL_LIST_BY_SERIAL_NUMBER)
    @SysLog("通过序列号查询蓝票明细列表")
    public R invoiceListByNumber(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //蓝票明细列表数据
        Integer resultRecordDetail= queryOpenRedTicketDataService.getRecordInvoiceDetailListCount(query);
        List<InvoiceDetail> recordInvoiceDetailList = queryOpenRedTicketDataService.getRecordInvoiceDetailList(query);
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, resultRecordDetail, query.getLimit(),query.getPage());
        return R.ok().put("page3", pageUtil3);

    }

    @RequestMapping(URI_OPEN_RED_TICKET_RETURN_MERGE_LIST_BY_SERIAL_NUMBER)
    @SysLog("查询蓝票明细列表")
    public R megerListByNumber(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //合并明细列表数据
        Integer resultMerge = queryOpenRedTicketDataService.getMergeInvoiceDetailListCount(query);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, resultMerge, query.getLimit(),query.getPage());
        return R.ok().put("page4", pageUtil4);

    }




    @RequestMapping(URI_OPEN_RED_TICKET_AGREEMENT_DETAIL)
    @SysLog("开红票查询明细列表")
    public R allListAgreement(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //协议查列表数据
        Integer resultReturn = queryOpenRedTicketDataService.getAgreementListCount(query);
        List<AgreementEntity> returnGoodsList = queryOpenRedTicketDataService.getAgreementList(query);

        //蓝票列表数据
        Integer resultRecord = queryOpenRedTicketDataService.getRecordInvoiceListCount(query);
        List<InvoiceEntity> recordInvoiceList = queryOpenRedTicketDataService.getRecordInvoiceList(query);
        //蓝票明细列表数据
        Integer resultRecordDetail= queryOpenRedTicketDataService.getRecordInvoiceDetailListCount(query);
        List<InvoiceDetail> recordInvoiceDetailList = queryOpenRedTicketDataService.getRecordInvoiceDetailList(query);
        //合并明细列表数据
        Integer resultMerge = queryOpenRedTicketDataService.getMergeInvoiceDetailListCount(query);
        List<RedTicketMatchDetail> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        PageUtils pageUtil1 = new PageUtils(returnGoodsList, resultReturn, query.getLimit(), query.getPage());
        PageUtils pageUtil2 = new PageUtils(recordInvoiceList, resultRecord, query.getLimit(),query.getPage());
        PageUtils pageUtil3 = new PageUtils(recordInvoiceDetailList, resultRecordDetail, query.getLimit(),query.getPage());
        PageUtils pageUtil4 = new PageUtils(mergeInvoiceDetailList, resultMerge, query.getLimit(),query.getPage());

        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2).put("page3", pageUtil3).put("page4", pageUtil4);

    }



    @RequestMapping(URI_OPEN_RED_TICKET_AGREEMENT_QUERY_BY_BUMBER)
    @SysLog("开红票查询明细列表")
    public R queryAgreementList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //协议查列表数据
        Integer resultReturn = queryOpenRedTicketDataService.getAgreementListCount(query);
        List<AgreementEntity> returnGoodsList = queryOpenRedTicketDataService.getAgreementList(query);


       // List<InvoiceRedFlush> mergeInvoiceDetailList = queryOpenRedTicketDataService.getMergeInvoiceDetailList(query);
        PageUtils pageUtil1 = new PageUtils(returnGoodsList, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }


    @RequestMapping(URI_OPEN_RED_TICKET_QUERY_IMG)
    @SysLog("开红票查看红票资料")
    public R queryImg(@RequestParam Map<String, Object> para) {

        LOGGER.info("查询条件为:{}", para);
        //列表数据
        List<FileEntity> fileList = queryOpenRedTicketDataService.getQueryImg(para);

        return R.ok().put("fileList", fileList);
    }

  /*  @RequestMapping(URI_OPEN_RED_TICKET_QUERY_RED_ROTICE_IMG)
    @SysLog("开红票查看红字通知单")
    public R getQueryRedNoticeImg(@RequestParam Map<String, Object> para) {

        LOGGER.info("查询条件为:{}", para);
        //协议查列表数据
        List<FileEntity> fileList = queryOpenRedTicketDataService.getQueryRedNoticeImg(para);
        System.out.println(fileList);
        return R.ok().put("fileList", fileList);
    }*/


    @SysLog("开红票资料上传")
    @RequestMapping(value = URI_OPEN_RED_TICKET_DATA_UPLOAD, method = {RequestMethod.POST})
    public R uploadRedTicketData(@RequestParam("file") MultipartFile file,@RequestParam("fileNumber") String fileNumber ,@RequestParam("id") String id ) {

        LOGGER.info("开红票资料上传参数：" +file +"fileNumber"+fileNumber+"id"+id);

        return R.ok().put("msg", queryOpenRedTicketDataService.uploadRedTicketData( file, getUser(), fileNumber,Integer.valueOf(id)));
    }

    @SysLog("TOKEN CHECK EXPIRE")
    @RequestMapping(value = URI_OPEN_RED_TICKET_FOR_IMAGE_CHECK, method = {RequestMethod.POST})
    public R checkTokenForGetImage() {
        return R.ok();
    }


    @SysLog("获取图片--资料")
    @RequestMapping(value = URI_OPEN_RED_TICKET_GET_IMAGE_ALL, method = {RequestMethod.GET})
    public void getImageForAll(@RequestParam("id") Long id, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片资料--------------------");
        //final String schemaLabel = getCurrentUserSchemaLabel();
         queryOpenRedTicketDataService.getInvoiceImageForAll( id, getUser(), response);
    }
    @SysLog("获取图片--红字通知单")
    @RequestMapping(value = URI_OPEN_RED_TICKET_GET_IMAGE_NOTICE, method = {RequestMethod.GET})
    public void getImageForNotice(@RequestParam("redNoticeAssociation") Long redNoticeAssociation, HttpServletResponse response) {
        LOGGER.debug("----------------获取图片红字通知单--------------------");
        //final String schemaLabel = getCurrentUserSchemaLabel();
        queryOpenRedTicketDataService.getInvoiceImageForNotice( redNoticeAssociation, getUser(), response);
    }

    @SysLog("TOKEN CHECK EXPIRE")
    @RequestMapping(value = URI_OPEN_RED_TICKET_FOR_FILE_CHECK, method = {RequestMethod.POST})
    public R checkTokenForGetImag() {
        return R.ok();
    }


    @SysLog("下载文件==资料")
    @RequestMapping(value = URI_OPEN_RED_TICKET_DOWN_LOAD_FILE)
    public void downLoadFile(@RequestParam("id") long id, HttpServletResponse response) {
        LOGGER.debug("---------------下载文件==资料--------------------");
        //final String schemaLabel = getCurrentUserSchemaLabel();
        queryOpenRedTicketDataService.getDownLoadFile(id, getUser(), response);
    }



    @SysLog("查询税率")
    @RequestMapping(value = URI_INVOICE_OPEN_RED_QUERY_XL)
    public  R queryXL() {
        LOGGER.debug("---------------查询税率--------------------");
        List<OptionEntity> optionList = queryOpenRedTicketDataService.queryXL();

        return R.ok().put("optionList", optionList);
    }

    @SysLog("查询开红票资料类型")
    @RequestMapping(value = URI_INVOICE_OPEN_RED_QUERY_RED_TICKET_TYPE)
    public  R queryRedTicketType() {
        LOGGER.debug("---------------查询开红票资料类型--------------------");
        List<OptionEntity> optionList = queryOpenRedTicketDataService.queryRedTicketType();
        Long userId=getUserId();
        List<RoleEntity> code=agreementRedTicketInformationService.selectRoleCode(userId);
       //判断是否是协议角色
        Boolean yesOrNo=false;
        for (RoleEntity re:code){
            if(re.getRoleCode().equals("redGoods")){
                yesOrNo=true;
            }
        }
        //不是协议角色去掉协议类型
        for (OptionEntity oe:optionList) {
            if(!yesOrNo){
                if(oe.getLabel().equals("协议类型")){
                    optionList.remove(oe);
                    return R.ok().put("optionList", optionList);
                }
            }
        }
        return R.ok().put("optionList", optionList);
    }





}
