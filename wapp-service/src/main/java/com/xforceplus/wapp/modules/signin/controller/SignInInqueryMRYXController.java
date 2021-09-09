package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.export.InqueryInvoiceMRYXExcel;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryMRXYService;
import com.xforceplus.wapp.modules.signin.toexcel.InqueryInvoiceExcel;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@RestController
@RequestMapping("/inqueryMRXY")
public class SignInInqueryMRYXController extends AbstractController {

    private SignInInqueryMRXYService signInInqueryService;

    @Autowired
    public SignInInqueryMRYXController(SignInInqueryMRXYService signInInqueryService) {
        this.signInInqueryService = signInInqueryService;
    }



    /**
     * 获取发票数据
     * @param params 查询条件
     * @return 签收查询数据集
     */
    @RequestMapping("/PageList")
    public R getHandWorkList(@RequestBody Map<String, Object> params) {

        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long userId=getUserId();
        params.put("userId",userId);

        //查询列表数据
        Query query = new Query(params);

        //执行业务层
        final List<RecordInvoiceEntity> invoiceList  = signInInqueryService.getRecordIncoiceList(schemaLabel,query);

        int total = signInInqueryService.queryTotal(schemaLabel,query);
        final Map<String, BigDecimal> totalMap = signInInqueryService.getSumAmount(schemaLabel,query);
        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage(),
                totalMap == null ? new BigDecimal(0) : totalMap.get("sumTotalAmount") ,
                totalMap == null ? new BigDecimal(0) : totalMap.get("sumTaxAmount") );


        return R.ok().put("page", pageUtil);
    }
    /**
     * 购方税号下拉列表数据
     * @return
     */
    @RequestMapping("/queryGf")
    public R getGfData(){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId=getUserId();
        List<OptionEntity> optionList = signInInqueryService.searchGf(schemaLabel,userId);
        return R.ok().put("optionList", optionList);
    }

    /**
     * 专票、通行费、机动车销售统一发票 (查询抵账表)
     *
     * @param params
     * @return
     */
    @RequestMapping("/checkInvoice")
    public R checkInvoice(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        UserEntity user = getUser();
        params.put("user", user);
        Boolean a = signInInqueryService.checkedInvoice(schemaLabel, params);
        if (a) {
            try {
                Boolean b = signInInqueryService.updateInvoice(schemaLabel, String.valueOf(params.get("uuid")), user, params);
                if (b) {
                    return R.ok("签收成功！");
                } else {
                    return R.error(1, "没有税号权限，签收失败！");
                }
            } catch (RRException re) {
                System.err.println("数据验证失败:{"+ re+"}");
                return R.error(1, re.getMsg());
            } catch (Exception e) {
                System.err.println("系统异常：{"+ e+"}");
                return R.error(1, "系统异常,请联系管理员！");
            }
        }
        return R.error(1, "发票不存在，签收失败！");
    }

    @RequestMapping(value = "/oneKey", method = RequestMethod.POST)
    public Map<String, Object> oneKey(@RequestBody Set<String> idSet, Model model){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        UserEntity user = getUser();
        int size = idSet.size();
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuffer buffer = new StringBuffer("<br>总共勾选数量："+size);
        try {
            int tbNum = signInInqueryService.batchUpdate(schemaLabel,user,idSet);
            buffer.append("<br>同步成功数量："+tbNum);
            map.put("flg",true);
            map.put("result", buffer.toString());
        } catch (Exception e) {
            System.err.println(("一键同步错误:idSet = " + idSet.toString()+e));
            map.put("flg",false);
            map.put("result","操作失败");
        }
        return map;
    }
}
