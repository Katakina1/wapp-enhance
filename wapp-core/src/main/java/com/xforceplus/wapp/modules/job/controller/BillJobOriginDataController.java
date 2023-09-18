package com.xforceplus.wapp.modules.job.controller;

import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.enums.exceptionreport.BillJobOriginDataTypeEnum;
import com.xforceplus.wapp.export.ExportHandlerEnum;
import com.xforceplus.wapp.export.IExportHandler;
import com.xforceplus.wapp.export.dto.BillJobOriginDataExportDto;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.job.service.BillJobOriginDataService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.mq.ActiveMqProducer;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.repository.vo.OriginClaimBillVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * @author Xforce
 */
@Slf4j
@RestController
@Api(tags = "原始数据管理")
@RequestMapping(EnhanceApi.BASE_PATH + "/bill/job/origin/data")
public class BillJobOriginDataController {

    @Autowired
    private BillJobOriginDataService billJobOriginDataService;
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private ActiveMqProducer activeMqProducer;
    @Value("${activemq.queue-name.export-request}")
    private String exportQueue;
    @Autowired
    private LockClient lockClient;
    @Autowired
    private CacheClient cacheClient;
    @Value("${wapp.bill.export.limit:99999}")
    Integer billExportLimit;

    @ApiOperation("索赔主信息")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/info")
    public R<PageResult<OriginClaimBillVo>> claimInfo(@RequestParam(value = "exchangeNo",required = false) String exchangeNo,
                                                      @RequestParam("startDate") String startDate,
                                                      @RequestParam("endDate") String endDate,
                                                      @RequestParam(value = "jobName", required = false) String jobName,
                                                      @RequestParam("page") Integer page,
                                                      @RequestParam("size") Integer size) throws Exception {
    	log.info("claimInfo exchangeNo:{},startDate:{},endDate:{},batchNo:{},page:{},size:{}", exchangeNo, startDate, endDate, jobName, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.claimInfo(exchangeNo, start, end, jobName, page, size));
    }

    @ApiOperation("索赔主信息导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/info/export")
    public R claimInfoExport(@RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate,
                             @RequestParam(value = "jobName", required = false) String jobName) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        final Long userId = UserUtil.getUserId();
        // 导出数量上限判断
        final PageResult<OriginClaimBillVo> pageResult = billJobOriginDataService.claimInfo("", start, end, jobName, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        String key = "claimInfoExport:" + userId + ":" + BillJobOriginDataTypeEnum.CLAIM_INFO.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.CLAIM_INFO);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setJobName(jobName);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("索赔Hyper")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/hyper")
    public R<PageResult<TXfOriginClaimItemHyperEntity>> claimHyper(@RequestParam("startDate") String startDate,
                                                                   @RequestParam("endDate") String endDate,
                                                                   @RequestParam("page") Integer page,
                                                                   @RequestParam("size") Integer size) throws Exception {
    	log.info("claimHyper exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.claimHyper(start, end, page, size));
    }

    @ApiOperation("索赔Hyper导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/hyper/export")
    public R claimHyperExport(@RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        final Long userId = UserUtil.getUserId();
        // 导出数量上限判断
        final PageResult<TXfOriginClaimItemHyperEntity> pageResult = billJobOriginDataService.claimHyper(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        String key = "claimHyperExport:" + userId + ":" + BillJobOriginDataTypeEnum.CLAIM_HYPER.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.CLAIM_HYPER);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("索赔Sams")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/sams")
    public R<PageResult<TXfOriginClaimItemSamsEntity>> claimSams(@RequestParam("startDate") String startDate,
                                                                 @RequestParam("endDate") String endDate,
                                                                 @RequestParam("page") Integer page,
                                                                 @RequestParam("size") Integer size) throws Exception {
    	log.info("claimSams exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.claimSams(start, end, page, size));
    }

    @ApiOperation("索赔Sams导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/claim/sams/export")
    public R claimSamsExport(@RequestParam("startDate") String startDate,
                             @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        final Long userId = UserUtil.getUserId();
        // 导出数量上限判断
        final PageResult<TXfOriginClaimItemSamsEntity> pageResult = billJobOriginDataService.claimSams(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        String key = "claimSamsExport:" + userId + ":" + BillJobOriginDataTypeEnum.CLAIM_SAMS.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.CLAIM_SAMS);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("协议zarr")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/agreement/zarr")
    public R<PageResult<TXfOriginSapZarrEntity>> agreementZarr(@RequestParam("startDate") String startDate,
                                                               @RequestParam("endDate") String endDate,
                                                               @RequestParam("page") Integer page,
                                                               @RequestParam("size") Integer size) throws Exception {
    	log.info("agreementZarr exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.agreementZarr(start, end, page, size));
    }

    @ApiOperation("协议zarr导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/agreement/zarr/export")
    public R agreementZarrExport(@RequestParam("startDate") String startDate,
                                 @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        final Long userId = UserUtil.getUserId();
        // 导出数量上限判断
        final PageResult<TXfOriginSapZarrEntity> pageResult = billJobOriginDataService.agreementZarr(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        String key = "agreementZarrExport:" + userId + ":" + BillJobOriginDataTypeEnum.AGREEMENT_ZARR.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.AGREEMENT_ZARR);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("协议fbl5n")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/agreement/fbl5n")
    public R<PageResult<TXfOriginSapFbl5nEntity>> agreementFbl5n(@RequestParam("startDate") String startDate,
                                                                 @RequestParam("endDate") String endDate,
                                                                 @RequestParam("page") Integer page,
                                                                 @RequestParam("size") Integer size) throws Exception {
    	log.info("agreementFbl5n exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.agreementFbl5n(start, end, page, size));
    }

    @ApiOperation("协议fbl5n导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/agreement/fbl5n/export")
    public R agreementFbl5nExport(@RequestParam("startDate") String startDate,
                                  @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        // 导出数量上限判断
        final PageResult<TXfOriginSapFbl5nEntity> pageResult = billJobOriginDataService.agreementFbl5n(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        final Long userId = UserUtil.getUserId();
        String key = "agreementFbl5nExport:" + userId + ":" + BillJobOriginDataTypeEnum.AGREEMENT_FBL5N.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.AGREEMENT_FBL5N);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("epd主信息")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/epd/info")
    public R<PageResult<TXfOriginEpdBillEntity>> epdInfo(@RequestParam("startDate") String startDate,
                                                         @RequestParam("endDate") String endDate,
                                                         @RequestParam("page") Integer page,
                                                         @RequestParam("size") Integer size) throws Exception {
    	log.info("epdInfo exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.epdInfo(start, end, page, size));
    }

    @ApiOperation("epd主信息导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/epd/info/export")
    public R epdInfoExport(@RequestParam("startDate") String startDate,
                           @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        // 导出数量上限判断
        final PageResult<TXfOriginEpdBillEntity> pageResult = billJobOriginDataService.epdInfo(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        final Long userId = UserUtil.getUserId();
        String key = "apdInfoExport:" + userId + ":" + BillJobOriginDataTypeEnum.EPD_INFO.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.EPD_INFO);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

    @ApiOperation("epdLog")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/epd/log")
    public R<PageResult<TXfOriginEpdLogItemEntity>> epdLog(@RequestParam("startDate") String startDate,
                                                           @RequestParam("endDate") String endDate,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam("size") Integer size) throws Exception {
    	log.info("epdLog exchangeNo:{},startDate:{},endDate:{},page:{},size:{}", startDate, endDate, page, size);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        return R.ok(billJobOriginDataService.epdLog(start, end, page, size));
    }

    @ApiOperation("epdLog导出")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "response", response = R.class)})
    @GetMapping("/epd/log/export")
    public R epdLogExport(@RequestParam("startDate") String startDate,
                          @RequestParam("endDate") String endDate) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = fmt.parse(startDate);
        Date end = fmt.parse(endDate);
        // 导出数量上限判断
        final PageResult<TXfOriginEpdLogItemEntity> pageResult = billJobOriginDataService.epdLog(start, end, 1, 1);
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }
        final Long userId = UserUtil.getUserId();
        String key = "apdLogExport:" + userId + ":" + BillJobOriginDataTypeEnum.EPD_LOG.getType();
        lockClient.tryLock(key, () -> {
            cacheClient.get(key, () -> {
                BillJobOriginDataExportDto dto = new BillJobOriginDataExportDto();
                dto.setType(BillJobOriginDataTypeEnum.EPD_LOG);
                dto.setUserId(userId);
                dto.setStartDate(start);
                dto.setEndDate(end);
                dto.setLoginName(UserUtil.getLoginName());
                TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
                excelExportlogEntity.setCreateDate(new Date());
                //这里的userAccount是userid
                excelExportlogEntity.setUserAccount(dto.getUserId().toString());
                excelExportlogEntity.setUserName(dto.getLoginName());
                excelExportlogEntity.setConditions(JSON.toJSONString(dto));
                excelExportlogEntity.setStartDate(new Date());
                excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
                excelExportlogEntity.setServiceType(SERVICE_TYPE);
                this.excelExportLogService.save(excelExportlogEntity);
                dto.setLogId(excelExportlogEntity.getId());
                activeMqProducer.send(exportQueue, JSON.toJSONString(dto),
                        Collections.singletonMap(IExportHandler.KEY_OF_HANDLER_NAME, ExportHandlerEnum.BILL_JOB_REPORT.name())
                );
                return 1;
            }, 300);
        }, -1, 1);
        return R.ok("单据导出正在处理，请在消息中心");
    }

}
