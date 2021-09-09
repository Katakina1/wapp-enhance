package com.xforceplus.wapp.modules.index.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.index.service.IndexMainWalmartService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@RestController
@RequestMapping("/index/walmart")
public class IndexMainWalmartController extends AbstractController {

    @Autowired
    private IndexMainWalmartService service;

    @SysLog("购方首页")
    @PostMapping("/mainGf")
    public R mainGf(){
        Map<String, Object> params = newHashMap();
        params.put("userId", getUserId());
        return R.ok().put("count1", service.queryCount1(params))
                .put("count2", service.queryCount2(params))
                .put("count3", service.queryCount3(params))
                .put("count4", service.queryCount4(params))
                .put("count5", service.queryCount5(params))
                .put("count6", service.queryCount6(params))
                .put("count7", service.queryCount7(params))
                .put("count8", service.queryCount8(params))
                .put("count9", service.queryCount9(params))
                .put("count10", service.queryCount10(params))
                .put("count11", service.queryCount11(params))
                .put("count12", service.queryCount12(params))
                .put("count13", service.queryCount13(params));
    }

    @SysLog("今日发票采集情况")
    @PostMapping("/list1")
    public R list1(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList1(query);
        int resultCount = service.queryCount1(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票查验情况")
    @PostMapping("/list2")
    public R list2(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList2(query);
        int resultCount = service.queryCount2(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票录入情况")
    @PostMapping("/list3")
    public R list3(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList3(query);
        int resultCount = service.queryCount3(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票签收成功")
    @PostMapping("/list4")
    public R list4(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList4(query);
        int resultCount = service.queryCount4(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票签收失败")
    @PostMapping("/list5")
    public R list5(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList5(query);
        int resultCount = service.queryCount5(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票认证成功")
    @PostMapping("/list6")
    public R list6(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList6(query);
        int resultCount = service.queryCount6(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票认证失败")
    @PostMapping("/list7")
    public R list7(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList7(query);
        int resultCount = service.queryCount7(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票未认证")
    @PostMapping("/list8")
    public R list8(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList8(query);
        int resultCount = service.queryCount8(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票扫描匹配成功")
    @PostMapping("/list9")
    public R list9(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList9(query);
        int resultCount = service.queryCount9(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票扫描匹配失败")
    @PostMapping("/list10")
    public R list10(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList10(query);
        int resultCount = service.queryCount10(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日发票未扫描匹配")
    @PostMapping("/list11")
    public R list11(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList11(query);
        int resultCount = service.queryCount11(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日申请开红票")
    @PostMapping("/list12")
    public R list12(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList12(query);
        int resultCount = service.queryCount12(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("今日已开红票")
    @PostMapping("/list13")
    public R list13(@RequestParam Map<String, Object> params) {
        params.put("userId", getUserId());
        final Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> resultList = service.queryList13(query);
        int resultCount = service.queryCount13(query);
        final PageUtils pageUtil = new PageUtils(resultList, resultCount, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
