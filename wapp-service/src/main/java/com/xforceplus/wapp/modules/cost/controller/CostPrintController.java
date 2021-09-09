package com.xforceplus.wapp.modules.cost.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.utils.*;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.InvoiceRateEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import com.xforceplus.wapp.modules.cost.export.CostMatchTemplate;
import com.xforceplus.wapp.modules.cost.export.CostTemplate;
import com.xforceplus.wapp.modules.cost.importTemplate.CostTypeTemplate;
import com.xforceplus.wapp.modules.cost.service.CostMatchService;
import com.xforceplus.wapp.modules.cost.service.CostPrintService;
import com.xforceplus.wapp.modules.cost.service.CostPushService;
import com.xforceplus.wapp.modules.cost.service.CostTypeService;
import com.xforceplus.wapp.modules.redTicket.service.QueryOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.getResource;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class CostPrintController extends AbstractController {
    @Value("${filePathConstan.erweimaPath}")
    private  String erPath;
    private static final Logger LOGGER = getLogger(CostPrintController.class);
    @Autowired
    private QueryOpenRedTicketDataService queryOpenRedTicketDataService;

    @Autowired
    private CostPrintService costPrintService;
    @Autowired
    private CostPushService costPushService;
    @Autowired
    private CostMatchService costMatchService;
    

    @SysLog("费用供应商付款生成pdf")
    @RequestMapping("/export/cost/costProviderExport")
    public void exportPDF(@RequestParam("costNo")String costNo , HttpServletResponse response, HttpServletRequest request) {
        LOGGER.info("查询条件为:{}", costNo);
        Map <String,Object> params =new HashMap<>();
        List<SettlementEntity> pushData = costPushService.getPushData(costNo);
        List<InvoiceRateEntity> invoiceRateList=pushData.get(0).getInvoiceRateList();

        List<OptionEntity> optionEntities = costPrintService.queryXL("WALMART_RATE");
        for (int i = 0; i<invoiceRateList.size();i++){
            String taxRate = invoiceRateList.get(i).getTaxRate();
            for (int j = 0; j<optionEntities.size();j++){
                if(taxRate.equals(optionEntities.get(j).getValue())){
                    invoiceRateList.get(i).setTaxRate(optionEntities.get(j).getLabel());
                }
            }
            List<CostEntity> costList =invoiceRateList.get(i).getCostTableData();
            for (CostEntity ce:costList){
                ce.setCostDept(ce.getCostDept().replace("&",""));
            }
        }

        pushData.get(0).setInvoiceRateList(invoiceRateList);
        params.put("settlementEntity",pushData.get(0));
        //0-非预付款  1-预付款 2-BPMS推送的非预付款
        if(pushData.get(0).getPayModel().equals("0")){
            params.put("payOne","（带票付款）");

        }else if(pushData.get(0).getPayModel().equals("1")) {
            params.put("payOne","（预付款补录发票）");
        }else if(pushData.get(0).getPayModel().equals("2")) {
            params.put("payOne","（BPMS推送的非预付款）");
        }
        //by costNo  查询主表信息
        //获取税率信息
       // SettlementEntity settlementEntity = costPrintService.selectSettlementByCostNo(costNo);
        //SettlementEntity settlementEntity = costPrintService.selectSettlementRateByCostNo(costNo);


      /*  params.put("invoiceEntity",invoiceEntity);
        params.put("resultList", resultList);
        params.put("totalData", totalData);
        params.put("currentDate", now().toString("yyyy-MM-dd"));
        params.put("user",getUser());*/
        //String path2 = request.getServletContext().getRealPath("/");
        try {
            //生成条形码
            String msg = "lsh:"+pushData.get(0).getPayModel()+"-"+costNo;

//            URL url = this.getClass().getClassLoader().getResource("static/barCode/1212.png");
           // URL url = getResource("static/barCode/1212.png");
            String path = erPath+costNo+"QRCode.png";


            int width = 100; // 二维码图片的宽
            int height = 100; // 二维码图片的高
            String format = "png"; // 二维码图片的格式

            try {
                // 生成二维码图片，并返回图片路径
                String pathName = QRCodeUtil.generateQRCode(msg, width, height, format,path);


                //String content = QRCodeUtil.parseQRCode(pathName);
                //System.out.println("解析出二维码的图片的内容为： " + content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //BarcodeUtil.generateFile(msg,path); 生成条形码
            //生成二维码

            costPrintService.costProviderExport(params, response,costNo);
        } catch (Exception e){
            LOGGER.error("导出PDF出错:"+e);
            e.printStackTrace();
        }
    }



    /**
     * 费用导入模板下载
     */
    @SysLog("费用导入模板下载")
    @AuthIgnore
    @GetMapping("/export/cost/application/template")
    public void getTemplate(HttpServletResponse response) {
        //生成excel
        final CostTemplate excelView = new CostTemplate();
        excelView.write(response, "costTemplate");
    }

    /**
     * 费用匹配导入模板下载
     */
    @SysLog("费用匹配导入模板下载")
    @AuthIgnore
    @GetMapping("/export/cost/match/template")
    public void getMatchTemplate(@RequestParam("costNo") String costNo, HttpServletResponse response) {
        List<CostEntity> detailList = costMatchService.queryDetail(costNo);
        //生成excel
        final CostMatchTemplate excelView = new CostMatchTemplate(detailList, "costMatchTemplate");
        excelView.write(response, "costMatchTemplate");
    }

}
