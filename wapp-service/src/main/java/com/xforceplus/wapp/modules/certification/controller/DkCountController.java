package com.xforceplus.wapp.modules.certification.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountDetail;
import com.xforceplus.wapp.modules.certification.entity.TDxDkCountEntity;
import com.xforceplus.wapp.modules.certification.service.DkCountService;
import com.xforceplus.wapp.modules.certification.service.ManualCheckService;

import com.xforceplus.wapp.modules.job.entity.TAcOrg;
import com.xforceplus.wapp.modules.job.entity.TDxTaxCurrent;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 抵扣统计
 * @author kevin.wang
 * @date 4/14/2018
 */
@RestController
@RequestMapping("certification/deductStatistics")
public class DkCountController extends AbstractController {

    private ManualCheckService manualCheckService;

    @Autowired
    private DkCountService dkCountService;
    private static final Logger LOGGER = getLogger(DkCountController.class);

    @Autowired
    public DkCountController(ManualCheckService manualCheckService) {

        this.manualCheckService = manualCheckService;
    }
    @RequestMapping("/checkDksh")
    @SysLog("判断税号是否抵扣是否可勾选认证")
    public R checkDksh(@RequestParam("gfsh") String gfsh){
        TAcOrg org = dkCountService.selectUpgrad(gfsh);
        if (org!=null && "1".equals(org.getIsUpdate())){
            List<TDxDkCountEntity> dkList = dkCountService.selectDksh(gfsh.split(","));
            if (dkList!=null && dkList.size()>0){
                TDxDkCountEntity cur = dkList.get(0);
                String skssq = cur.getSkssq();
                String year = skssq.substring(0,4);
                String mon = skssq.substring(4);
                LocalDate localDate =LocalDate.of(Integer.valueOf(year),Integer.valueOf(mon),01);
                LocalDate localDate1 = localDate.plusMonths(1);
                LocalDate localDate2 = LocalDate.now();
                if (!"0".equals(cur.getTjStatus())&&!"3".equals(cur.getTjStatus())&&!localDate2.isBefore(localDate1)){
                    return R.error(333,"该税号已申请抵扣统计");
                }else {
                    return R.ok();
                }
            }
            return R.error("该税号无属期相关信息");
        }
        return R.ok();
    }
    /**
     * 抵扣统计列表
     */
    @RequestMapping("/list")
    @SysLog("发票认证-抵扣统计")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        Query query = new Query(params);
        List<TDxDkCountEntity> list = dkCountService.queryList(schemaLabel,query);

        ReportStatisticsEntity result = dkCountService.queryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 申请统计
     */
    @RequestMapping("/applyCount")
    @SysLog("发票认证-申请统计")
    public R applyCount(@RequestParam(value = "gfshs") String gfshs,@RequestParam("skssqs") String skssqs) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String loginName = getLoginName();
        String[] arrGfsh = gfshs.split(",");
        String[] arrSkssq = skssqs.split(",");
        StringBuffer tsxx = new StringBuffer();
        tsxx.append("以下税号不能申请统计,请先处理完认证发票再进行申请抵扣统计: \n ");
        boolean flag = false;
        for (int i = 0; i < arrGfsh.length; i++) {
            Map<String, Object> pramMap = new HashMap<String, Object>(16);
            pramMap.put("gfsh", arrGfsh[i]);
            pramMap.put("skssq", arrSkssq[i]);
            Integer num = dkCountService.selectBeforeTj(pramMap);
            if (num > 0) {
                tsxx.append(arrGfsh[i] +" \n ");
                flag =true;
            }
        }
        if (flag){
            return R.error(333,tsxx.toString());
        }
        for (int i = 0; i < arrGfsh.length; i++){
            String gfsh = arrGfsh[i];
            String skssq = arrSkssq[i];
            dkCountService.insertDk(gfsh,skssq);
        }
        dkCountService.insertDkLog(arrGfsh,arrSkssq,loginName,"0");
        return R.ok("已申请报表统计，请稍后查询统计报表");
    }
    @RequestMapping("/confirmCount")
    @SysLog("发票认证-确认统计")
    public R confirmCount(@RequestParam(value = "gfshs") String gfshs,@RequestParam("skssqs") String skssqs,@RequestParam("dkPassword") String dkPassword) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String loginName = getLoginName();
        String[] arrGfsh = gfshs.split(",");
        String[] arrSkssq = skssqs.split(",");
        List<TDxDkCountEntity> curList = dkCountService.selectDksh(arrGfsh);
        StringBuffer tsxx = new StringBuffer();
        StringBuffer tsxx1 = new StringBuffer();
        tsxx.append("以下税号请次月1号之后再进行确认统计： \n ");
        tsxx1.append("以下税号不能确认统计,请撤销统计后重新申请统计再执行确认统计: \n ");
        boolean tsflag = false;
        boolean ts1flag = false;
        List<String> taxList = new ArrayList<>();
        List<String> taxList1 = new ArrayList<>();
        boolean flag = false;
        for (TDxDkCountEntity cur : curList){
            String tjDate = cur.getTjDate();
            String skssq = cur.getSkssq();
            String year = skssq.substring(0,4);
            String mon = skssq.substring(4);
            LocalDate localDate =LocalDate.of(Integer.valueOf(year),Integer.valueOf(mon),01);
            LocalDate localDate1 = localDate.plusMonths(1);
            LocalDate localDate2 = LocalDate.now();
            if (localDate2.isBefore(localDate1)){
                if (!taxList.contains(cur.getTaxno())){
                    tsxx.append(cur.getTaxno()+" \n ");
                    taxList.add(cur.getTaxno());
                }
                flag = true;
                tsflag = true;
                continue;
            }else{
                LocalDate tjsj =LocalDate.of(Integer.valueOf(tjDate.substring(0,4)),Integer.valueOf(tjDate.substring(4,6)),Integer.valueOf(tjDate.substring(6,8)));
                if (tjsj.isBefore(localDate1)){
                    if (!taxList1.contains(cur.getTaxno())){
                        tsxx1.append(cur.getTaxno()+" \n ");
                        taxList1.add(cur.getTaxno());
                    }
                    flag = true;
                    ts1flag = true;
                    continue;
                }
            }
        }
        String message = "";
        if (tsflag){
            message = tsxx.toString();
        }
        if (ts1flag){
            message = message + tsxx1.toString();
        }
        if (flag){
            return R.error(333,message.toString());
        }

        for (int i = 0; i < arrGfsh.length; i++){
            String gfsh = arrGfsh[i];
            String skssq = arrSkssq[i];
            dkCountService.insertConfirm(gfsh,skssq,dkPassword);
        }
        dkCountService.insertDkLog(arrGfsh,arrSkssq,loginName,"1");
        return R.ok("已申请确认统计，请稍后查询确认结果");
    }

    /**
     *撤销统计
     * @param gfshs
     * @param skssqs
     * @return
     */
    @RequestMapping("/revokeCount")
    @SysLog("发票认证-撤销统计")
    public R revokeCount(@RequestParam(value = "gfshs") String gfshs,@RequestParam("skssqs") String skssqs) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String loginName = getLoginName();
        String[] arrGfsh = gfshs.split(",");
        String[] arrSkssq = skssqs.split(",");

        for (int i=0;i<arrGfsh.length;i++){
            String gfsh = arrGfsh[i];
            String skssq = arrSkssq[i];
            dkCountService.insertCxtj(gfsh,skssq);
        }
        return R.ok("已申请撤销统计，请稍后查询撤销结果");
    }
    /**
     *撤销确认
     * @param gfshs
     * @param skssqs
     * @return
     */
    @RequestMapping("/revokeConfirm")
    @SysLog("发票认证-撤销确认")
    public R revokeConfirm(@RequestParam(value = "gfshs") String gfshs,@RequestParam("skssqs") String skssqs) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String loginName = getLoginName();
        String[] arrGfsh = gfshs.split(",");
        String[] arrSkssq = skssqs.split(",");

        for (int i=0;i<arrGfsh.length;i++){
            String gfsh = arrGfsh[i];
            String skssq = arrSkssq[i];
            dkCountService.insertCxqs(gfsh,skssq);
        }
        dkCountService.insertDkLog(arrGfsh,arrSkssq,loginName,"3");
        return R.ok("已申请撤销确认，请稍后查询撤销结果");
    }

    @RequestMapping("/dkCountDetail")
    @SysLog("发票认证-抵扣统计明细")
    public R dkCountDetail(@RequestParam("taxno") String taxno,@RequestParam("skssq") String skssq) {
        String gfsh = taxno;
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<TDxDkCountDetail> detailList = new ArrayList<>();
        String tjdate = "";
        try{
            List<TDxDkCountDetail> dkList = dkCountService.selectDkDetail(gfsh,skssq);
            skssq = skssq.substring(0,4) + "年" + skssq.substring(4,6) + "月";
            // 页面展示对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
            StringBuffer sb = new StringBuffer();
            String tjmonth = "";
            String taxName = "";
            for (int i = 0; i < dkList.size(); i++) {
                sb.append(dkList.get(i).getInvoiceType());
                String tjDate = dkList.get(i).getTjDate();
                Date date = sdf.parse(tjDate);
                tjdate = sdf1.format(date);
                tjmonth = dkList.get(i).getTjMonth();
            }
            if (sb.indexOf("01")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("01");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("01".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());

                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("03")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("03");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("03".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("14")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("14");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("14".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("17")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("17");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("17".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("24")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("24");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("24".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("30")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("30");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("30".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("08")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("08");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("08".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            if (sb.indexOf("80")<0){
                TDxDkCountDetail td = new TDxDkCountDetail();
                td.setBdkAmountCount("0.00");
                td.setBdkInvoiceCount("0");
                td.setBdkTaxAmountCount("0.00");
                td.setDkAmountCount("0.00");
                td.setDkInvoiceCount("0");
                td.setDkTaxAmountCount("0.00");
                td.setTaxno(gfsh);
                td.setTjDate(tjdate);
                td.setTjMonth(tjmonth);
                td.setInvoiceType("80");
                detailList.add(td);
            }else {
                for (int i=0;i<dkList.size();i++){
                    if ("80".equals(dkList.get(i).getInvoiceType())){
                        TDxDkCountDetail td = new TDxDkCountDetail();
                        sb.append(dkList.get(i).getInvoiceType());
                        td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                        td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                        td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                        td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                        td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                        td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                        td.setTaxno(dkList.get(i).getTaxno());
                        String tjDate = dkList.get(i).getTjDate();
                        Date date = sdf.parse(tjDate);
                        td.setTjDate(sdf1.format(date));
                        tjdate = sdf1.format(date);
                        td.setTjMonth(dkList.get(i).getTjMonth());
                        tjmonth = dkList.get(i).getTjMonth();
                        td.setInvoiceType(dkList.get(i).getInvoiceType());
                        detailList.add(td);
                    }
                }
            }
            for (int i=0;i<dkList.size();i++){
                if ("99".equals(dkList.get(i).getInvoiceType())){
                    TDxDkCountDetail td = new TDxDkCountDetail();
                    sb.append(dkList.get(i).getInvoiceType());
                    td.setBdkAmountCount(dkList.get(i).getBdkAmountCount());
                    td.setBdkInvoiceCount(dkList.get(i).getBdkInvoiceCount());
                    td.setBdkTaxAmountCount(dkList.get(i).getBdkTaxAmountCount());
                    td.setDkAmountCount(dkList.get(i).getDkAmountCount());
                    td.setDkInvoiceCount(dkList.get(i).getDkInvoiceCount());
                    td.setDkTaxAmountCount(dkList.get(i).getDkTaxAmountCount());
                    td.setTaxno(dkList.get(i).getTaxno());
                    String tjDate = dkList.get(i).getTjDate();
                    Date date = sdf.parse(tjDate);
                    td.setTjDate(sdf1.format(date));
                    tjdate = sdf1.format(date);
                    td.setTjMonth(dkList.get(i).getTjMonth());
                    tjmonth = dkList.get(i).getTjMonth();
                    td.setInvoiceType(dkList.get(i).getInvoiceType());
                    detailList.add(td);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return R.ok().put("skssq", skssq).put("detailList",detailList).put("tjDate",tjdate);
    }

}
