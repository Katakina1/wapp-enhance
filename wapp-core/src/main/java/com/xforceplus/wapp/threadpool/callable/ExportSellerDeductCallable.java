package com.xforceplus.wapp.threadpool.callable;//package com.xforceplus.wapp.threadpool.callable;
//
//import com.alibaba.fastjson.JSONObject;
//import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
//import com.xforceplus.wapp.export.dto.DeductViewBillExportDto;
//import com.xforceplus.wapp.export.dto.SellerDeductBillExportDto;
//import com.xforceplus.wapp.modules.deduct.service.DeductViewService;
//import com.xforceplus.wapp.modules.deduct.service.SellerBillExportService;
//import com.xforceplus.wapp.modules.deduct.service.SellerBillQueryService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.Callable;
//
///**
// * Describe: 供应商业务单导出
// *
// * @Author xiezhongyong
// * @Date 2022/9/18
// */
//public class ExportSellerDeductCallable implements Callable<Boolean> {
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private SellerBillExportService sellerBillExportService;
//
//    private SellerDeductBillExportDto request;
//
//    public ExportSellerDeductCallable(SellerBillExportService sellerBillQueryService, SellerDeductBillExportDto dto) {
//        this.sellerBillExportService = sellerBillQueryService;
//        this.request = dto;
//    }
//
//    @Override
//    public Boolean call(){
//        Boolean isSuccess = false;
//        long startTime = System.currentTimeMillis();
//        try {
//            logger.info("***********通过线程池发起供应商侧-业务单导出执行开始,request:{}", JSONObject.toJSONString(request));
//			isSuccess = sellerBillExportService.doExport(request);
//        } catch (Exception e) {
//            logger.error("导出失败", e);
//            isSuccess = false;
//        }
//        logger.info("***********通过线程池发起供应商侧-业务单导出执行完毕，执行结果：isSuccess[{}],costTime[{}]", isSuccess, (System.currentTimeMillis() - startTime));
//        return isSuccess;
//    }
//}