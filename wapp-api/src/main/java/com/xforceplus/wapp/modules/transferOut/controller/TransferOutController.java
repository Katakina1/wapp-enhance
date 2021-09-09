package com.xforceplus.wapp.modules.transferOut.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.Constant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.transferOut.export.transferOutExport;
import com.xforceplus.wapp.modules.transferOut.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/16
 * Time:16:43
*/
@RestController
@RequestMapping("/transferOut/transferOutQuery")
public class TransferOutController extends AbstractController {


    @Autowired
    private InvoiceService invoiceService;


    /**
     * 模糊查询销方名称
     * @param queryString
     * @return
     */
    @SysLog("模糊查询销方名称")
    @RequestMapping("/xfName")
    public R getXfName(@RequestParam String  queryString){
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<String> list = invoiceService.getXfName(schemaLabel,queryString);

        return R.ok().put("list", list);

    }

    /**
     * 查询的是已认证未转出的数据
     * @param params
     * @return
     */
    @SysLog("查询待转出表格数据")
    @RequestMapping("/transferOutQuery")
    public R transferOutList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //如果不是超级管理员，则只查询自己创建的角色列表
        if (getUserId() != Constant.SUPER_ADMIN) {
            params.put("createUserId", getUserId());
        }
        if (params.get("gfTaxNo")==null||params.get("gfTaxNo").equals("")){
            params.put("userId",getUserId());
        }
        //查询列表数据
        Query query = new Query(params);
        List<InvoiceEntity> list = invoiceService.transferOutQuery(schemaLabel,query);
        int total = invoiceService.transferOutQueryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询的是已转出的数据
     * @param params
     * @return
     */
    @SysLog("查询已转出数据")
    @RequestMapping("/TransferOutedQuery")
    public R cancelTransferOutList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //如果不是超级管理员，则只查询自己创建的角色列表
        if (getUserId() != Constant.SUPER_ADMIN) {
            params.put("createUserId", getUserId());
        }
        if (params.get("gfTaxNo")==null||params.get("gfTaxNo").equals("")){
            params.put("userId",getUserId());
        }
        //查询列表数据
        Query query = new Query(params);
        List<InvoiceEntity> list = invoiceService.transferOutedQuery(schemaLabel,query);
        int total = invoiceService.transferOutedQueryTotal(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查出的是点击转出后弹出窗口内的信息
     * @param ids
     * @return
     */
    @SysLog("查询待转出发票信息")
    @RequestMapping("/getToOutInformation")
    public R getToOutInformation(@RequestParam String  ids){
        final String schemaLabel = getCurrentUserSchemaLabel();

        InvoiceEntity  invoiceEntity=invoiceService.getToOutInformation(schemaLabel,ids);
        String outInvoiceAmout=invoiceEntity.getOutInvoiceAmout().toString();
        String outTaxAmount=invoiceEntity.getOutTaxAmount().toString();
        return  R.ok().put("outInvoiceAmout",outInvoiceAmout).put("outTaxAmount",outTaxAmount);

    }


    /**
     * 转出操作
     * @param ids
     * @param outRemark
     * @param outReason
     * @param outTaxAmount
     * @param outInvoiceAmout
     * @param outStatus
     * @return
     */
    @SysLog("转出操作")
    @RequestMapping("/setTransferOut")
    public R setTransferOut(@RequestParam String  ids,@RequestParam String outRemark,@RequestParam String outReason,
                            @RequestParam String outTaxAmount,@RequestParam String outInvoiceAmout,@RequestParam String outStatus){
        final String schemaLabel = getCurrentUserSchemaLabel();
        Boolean flag=false;
        final String  outBy=getUser().getUsername();
        flag = invoiceService.setTransferOut(schemaLabel,ids,outRemark,outReason,outTaxAmount,outInvoiceAmout,outStatus,outBy)>0;
        if (flag==true){
            return R.ok();
        }
        return R.error(1,"操作失败!");
    }

    /**
     * 取消转出操作
     * @param idss
     * @return
     */
    @SysLog("取消转出操作")
    @RequestMapping("cancelTransferOut")
    public R cancelTransferOut(@RequestParam String idss){
        final String schemaLabel = getCurrentUserSchemaLabel();

        final String[] ids = idss.split(",");
        final Boolean flag=invoiceService.cancelTransferOut(schemaLabel,ids);
        if (flag){
            return R.ok();
        }
        return R.error();

    }

    /**
     * 选择购方名称根据税号带出税款所属期
     * @param gfTaxNo
     * @return
     */
    @SysLog("选择购方带出税款所属期")
    @RequestMapping("/getDqskssq")
    public R getDqskssq(@RequestParam String gfTaxNo){
        final String schemaLabel = getCurrentUserSchemaLabel();

        String dqskssq=invoiceService.getDqskssq(schemaLabel,gfTaxNo);
        return R.ok().put("dqskssq", dqskssq);
    }

    /**
     * 导出数据-进项税转出查询
     * @param params
     * @return
     */
    @RequestMapping("/transferOutInvoiceQueryExport")
    public void transferOutInvoiceQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //当前登录人ID,用于查询对应税号
        params.put("userID", getUserId());
        //查询列表数据
        List<InvoiceEntity> list = invoiceService.transferOutedQuery(schemaLabel,params);

        final Map<String, List<InvoiceEntity>> map = newHashMapWithExpectedSize(1);
        map.put("transferOutInvoiceQueryList", list);
        //生成excel
        final transferOutExport excelView = new transferOutExport(map, "export/transferOut/transferOutExport.xlsx", "transferOutInvoiceQueryList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "transferOutInvoiceQueryList" + excelNameSuffix);
    }





}
