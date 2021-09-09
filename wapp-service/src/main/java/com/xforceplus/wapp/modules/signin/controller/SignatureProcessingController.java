package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.SignatureProcessingService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/19.
 **/
@RestController
@RequestMapping("SignatureProcessing/")
public class SignatureProcessingController extends AbstractController {
    private final static Logger LOGGER = LoggerFactory.getLogger(SignatureProcessingController.class);

    private SignatureProcessingService signatureProcessingService;

    private final String RESULT_CODE = "0001";

    @Autowired
    public SignatureProcessingController(SignatureProcessingService signatureProcessingService) {
        this.signatureProcessingService = signatureProcessingService;
    }

    @RequestMapping("PageList")
    public R getHandWorkList(@RequestBody Map<String, Object> params) {

        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long userId = getUserId();
        params.put("userId", userId);
        //查询列表数据
        Query query = new Query(params);

        //执行业务层
        final List<RecordInvoiceEntity> invoiceList = signatureProcessingService.getRecordIncoiceList(schemaLabel, query);

        int total = signatureProcessingService.queryTotal(schemaLabel, query);
        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("queryGf")
    public R getGfData() {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId = getUserId();
        List<OptionEntity> optionList = signatureProcessingService.searchGf(schemaLabel, userId);
        return R.ok().put("optionList", optionList);
    }


    @RequestMapping("scanDeleteIevoice")
    public R scanDeleteIevoice(@RequestBody Map<String, Object> params) {
        UserEntity user = getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            String qsStatus=String.valueOf(params.get("qsStatus"));

            String scanMatchStatus=String.valueOf(params.get("scanMatchStatus"));
            if("1".equals(qsStatus)){
                if(!("0".equals(scanMatchStatus)|| StringUtils.isEmpty(scanMatchStatus))&&!"2".equals(scanMatchStatus)){
                    return R.ok().put("msg", "匹配成功的发票不允许刪除");
                }
            }
            Boolean a = signatureProcessingService.scanDeleteIevoice(schemaLabel, String.valueOf(params.get("id")));
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return R.ok().put("msg", "刪除成功！");
    }

    /**
     * 数据删除--根据uuid去更新签收状态
     *
     * @param params
     * @return
     */
    @RequestMapping("deleteRevoice")
    public R deleteIncoice(@RequestBody Map<String, Object> params) {
        UserEntity user = getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            /*String uuid = String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo"));*/
            //退票原因判断
            // 若签收失败取签收失败原因
            //若签收成功 取匹配状态 0（平台无匹配关系）  2匹配失败原因
            String errMsg="";
            String qsStatus=String.valueOf(params.get("qsStatus"));
            String notes=String.valueOf(params.get("notes"));
            String scanMatchStatus=String.valueOf(params.get("scanMatchStatus"));
            String scanFailReason=String.valueOf(params.get("scanFailReason"));
            if("1".equals(qsStatus)){
                if("0".equals(scanMatchStatus)){
                    errMsg="平台无匹配关系";
                }else if("2".equals(scanMatchStatus)){
                    errMsg=scanFailReason;
                }else if(StringUtils.isEmpty(scanMatchStatus)||scanMatchStatus.equals("null")){
                    errMsg="平台无匹配关系";
                } else{
                    return R.ok().put("msg", "匹配成功的发票不允许退票");
                }
            }else{
                errMsg=notes;
            }
            Boolean a = signatureProcessingService.deleteMsgById(schemaLabel, String.valueOf(params.get("id")),errMsg);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return R.ok().put("msg", "退票成功！");
    }


    @RequestMapping("returnRevoice")
    public R returnIncoice(@RequestBody Map<String, Object> params) {
        UserEntity user = getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            String uuid = String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo"));
            Boolean a = signatureProcessingService.returnById(schemaLabel, String.valueOf(params.get("id")));
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return R.ok().put("msg", "撤回成功！");
    }

    /**
     * 专票、通行费、机动车销售统一发票 (查询抵账表)
     *
     * @param params
     * @return
     */
    @RequestMapping("checkInvoice")
    public R checkInvoice(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        UserEntity user = getUser();
        params.put("user", user);
        Boolean a = signatureProcessingService.checkedInvoice(schemaLabel, params);
        if (a) {
            try {
                Boolean b = signatureProcessingService.updateInvoice(schemaLabel, String.valueOf(params.get("uuid")), user, params);
                if (b) {
                    return R.ok("签收成功！");
                } else {
                    return R.error(1, "没有税号权限，签收失败！");
                }
            } catch (RRException re) {
                LOGGER.error("数据验证失败:{}", re);
                return R.error(1, re.getMsg());
            } catch (Exception e) {
                LOGGER.error("系统异常：{}", e);
                return R.error(1, "系统异常,请联系管理员！");
            }
        }
        return R.error(1, "发票不存在，签收失败！");
    }


    /**
     * 普票，电票，卷票（调查验接口）
     *
     * @param params
     * @return
     */
    @RequestMapping("checkPlainInvoice")
    public R checkPlainInvoice(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        UserEntity user = getUser();
        params.put("user", user);
        try {
            ResponseInvoice r = signatureProcessingService.checkPlainInvoice(schemaLabel, params);
            return R.error(1, r.getResultTip());
        } catch (RRException re) {
            LOGGER.error("数据验证失败:{}", re);
            return R.error(1, re.getMsg());
        } catch (Exception e) {
            LOGGER.error("系统异常：{}", e);
            return R.error(1, "系统异常,请联系管理员！");
        }
    }

    /**
     * 根据uuid查询抵账表数据（暂无调用）
     *
     * @param params
     * @return
     */
    @RequestMapping("getData")
    public R selectData(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        RecordInvoiceEntity recordInvoiceEntity = signatureProcessingService.getDataByuuid(schemaLabel, params);
        return R.ok().put("entity", recordInvoiceEntity);
    }

    @PostMapping("deleteInvoice")
    public R deleteInvoice(@RequestParam Map<String, Object> params) {
        UserEntity user = getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            String uuid = String.valueOf(params.get("invoiceCode")) + String.valueOf(params.get("invoiceNo"));
            signatureProcessingService.deleteById(schemaLabel,  String.valueOf(params.get("id")));
            return R.ok();
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return R.error();
        }
    }
}
