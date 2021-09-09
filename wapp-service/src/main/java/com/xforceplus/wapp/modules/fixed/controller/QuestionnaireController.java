package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.fixed.entity.FileEntity;
import com.xforceplus.wapp.modules.fixed.entity.MatchQueryEntity;
import com.xforceplus.wapp.modules.fixed.entity.OrderEntity;
import com.xforceplus.wapp.modules.fixed.service.QuestionnaireService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController("FixedQuestionnaireController")
@RequestMapping("/fixed/questionnaire")
public class QuestionnaireController extends AbstractController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @SysLog("匹配失败列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        query.put("venderid", getUser().getUsercode());
        List<MatchQueryEntity> list = questionnaireService.queryList(query);
        int count = questionnaireService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("匹配明细")
    @RequestMapping("/detail")
    public R detail(@RequestParam("matchId") Long matchId) {
        List<RecordInvoiceEntity> invoiceList = questionnaireService.getInvoiceDetail(matchId);
        List<OrderEntity> orderList = questionnaireService.getOrderDetail(matchId);
        return R.ok().put("invoiceList", invoiceList).put("orderList", orderList);
    }

    @SysLog("问题单文件明细")
    @RequestMapping("/fileDetail")
    public R fielDetail(@RequestParam("matchId") Long matchId) {
        List<FileEntity> fileList = questionnaireService.getFileDetail(matchId);
        return R.ok().put("fileList", fileList);
    }

    @SysLog("查看问题单文件")
    @RequestMapping("/viewFile")
    public void viewFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //查看
        questionnaireService.viewImg(id, response);
    }

    @SysLog("下载问题文件")
    @RequestMapping("/downloadFile")
    public void downloadFile(@RequestParam("id")Long id, HttpServletResponse response) {
        //下载
        questionnaireService.downloadFile(id, response);
    }

    @SysLog("保存上传的问题单")
    @RequestMapping("/fileConfirm")
    public R fileConfirm(@RequestBody MatchQueryEntity match) {
        try{
            questionnaireService.fileConfirm(match);
        } catch (Exception e){
            e.printStackTrace();
            return R.error();
        }
        return R.ok();
    }
}
