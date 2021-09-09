package com.xforceplus.wapp.modules.signin.controller;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.service.SweepCodeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/17.
 **/
@RestController
@RequestMapping("sweepcode/")
public class SweepCodeController extends AbstractController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SweepCodeController.class);

    private SweepCodeService sweepCodeService;

    @Autowired
    public SweepCodeController(SweepCodeService sweepCodeService) {
        this.sweepCodeService = sweepCodeService;
    }


    /**
     * 普票、汽车等签收 --查验接口签收
     * @param params
     * @return
     */
    @RequestMapping("ReceiptInvoice")
    public R receiptInvoice(@RequestBody Map<String, Object> params) {
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            UserEntity user=getUser();
            params.put("user",user);
            RecordInvoiceEntity r=sweepCodeService.ReceiptInvoice(schemaLabel,params);
            return R.ok().put("entity",r);
        }catch (Exception e){
            System.out.println("-----------------");
            LOGGER.error(e.toString());
            System.out.println("-----------------");
            return R.error(1,"查验错误！");
        }




    }

    /**
     * 专票、机动车销售统一发票、通行费发票 查询抵账表签收

     * @param params
     * @return
     */
    @RequestMapping("receiptInvoiceTwo")
    public R receiptInvoiceTwo(@RequestBody Map<String, Object> params){
        UserEntity user=getUser();
        params.put("user",user);
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        try {
            RecordInvoiceEntity r=sweepCodeService.ReceiptInvoiceTwo(schemaLabel,params);
            return R.ok().put("entity",r);
        }catch (Exception e){
            return R.error(1,"查验错误！");
        }
    }


    @RequestMapping("deleteInvoiceData")
    public R deleteInvoiceData(@RequestParam("uuid") String uuid){
        UserEntity user=getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();
            Boolean a=sweepCodeService.deleteInvoiceData(schemaLabel,uuid,user);
        }catch (Exception e){
            LOGGER.error(e.toString());
        }

        return R.ok().put("msg", "删除成功！");
    }

    @RequestMapping("getInvoiceId")
    public R getInvoiceId(@RequestParam  String uuid){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long id= sweepCodeService.getInvoiceId(schemaLabel,uuid);
        if(id==null){
          return R.error(1,"无明细数据！");
        }
        return R.ok().put("id",id);
    }

    @RequestMapping("getInvoiceData")
    public R getInvoiceData(@RequestParam  String uuid){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();
        Long id=sweepCodeService.getInvoiceData(schemaLabel,uuid);
        return R.ok().put("invoiceId",id);
    }
}
