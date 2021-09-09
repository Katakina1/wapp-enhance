package com.xforceplus.wapp.modules.signin.controller;


import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.common.utils.ScanEditHttpClient;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.ExportEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntityApi;
import com.xforceplus.wapp.modules.signin.service.SignInInqueryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@RestController
@RequestMapping("inquery/")
public class SignInInqueryController extends AbstractController {

    private SignInInqueryService signInInqueryService;
    @Value("${scanEditUrl}")
    private String scanEditUrl;
    @Autowired
    public SignInInqueryController(SignInInqueryService signInInqueryService) {
        this.signInInqueryService = signInInqueryService;
    }

    /**
     * 获取发票数据
     * @param params 查询条件
     * @return 签收查询数据集
     */
    @RequestMapping("PageList")
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
//        final Map<String, BigDecimal> totalMap = signInInqueryService.getSumAmount(schemaLabel,query);
        Map<String, BigDecimal> totalMap=null;
        PageUtils pageUtil = new PageUtils(invoiceList, total, query.getLimit(), query.getPage(),
                totalMap == null ? new BigDecimal(0) : totalMap.get("sumTotalAmount") ,
                totalMap == null ? new BigDecimal(0) : totalMap.get("sumTaxAmount") );

        return R.ok().put("page", pageUtil);
    }

    /**
     * 购方税号下拉列表数据
     * @return
     */
    @RequestMapping("queryGf")
    public R getGfData(){
        //获取分库分表的入口
        final String schemaLabel = getCurrentUserSchemaLabel();

        Long userId=getUserId();
        List<OptionEntity> optionList = signInInqueryService.searchGf(schemaLabel,userId);
        return R.ok().put("optionList", optionList);
    }


    @RequestMapping("checkInvoice")
    public String checkInvoice(@RequestBody Map<String, Object> params) {
        //获取分库分表的入口,与登录用户
        ExportEntity exportEntity= buildExportEntity();
        params.put("exportEntitySchemaLabel",exportEntity.getSchemaLabel());
        params.put("exportEntityUserId",exportEntity.getUserId());
        params.put("exportEntityUserAccount",exportEntity.getUserAccount());
        params.put("exportEntityUserName",exportEntity.getUserName());

//        Map<String, Object> params2=new HashMap<>();
//        params2.put("invoices",params);
        //

            JSONObject jsonObject = JSONObject.fromObject(params);
            //调用修改接口 scanEditUrl
            JSONObject returnJson = null;
        //判断修改后内容是否有重复数据
        Integer count=signInInqueryService.selectByuuid(buildExportEntity().getSchemaLabel(),(String)params.get("dyInvoiceCode")+(String)params.get("dyInvoiceNo"),(Integer)params.get("id")+"",(String)params.get("invoiceDate"),(String)params.get("invoiceAmount"));
        if(count==0) {
            try {
                returnJson = ScanEditHttpClient.httpPost(scanEditUrl, jsonObject, false);
            } catch (Exception e) {
                return "500";
            }
        }else{
            return "5001";
        }
        Map<String,Object> m=returnJson;
        //判断匹配关系，并修改匹配关系
        if("0".equals(m.get("code").toString())){
            JSONArray recordInvoiceEntityApisJson= (JSONArray) m.get("page");

            if(recordInvoiceEntityApisJson.size()>0){
                for(int i=0;i<recordInvoiceEntityApisJson.size();i++){
                    JSONObject recordInvoiceEntity = (JSONObject)recordInvoiceEntityApisJson.get(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                    recordInvoiceEntity.remove("venderidEdit");
                    RecordInvoiceEntityApi recordInvoiceEntityApi=(RecordInvoiceEntityApi) JSONObject.toBean(recordInvoiceEntity, RecordInvoiceEntityApi.class);
                    if(StringUtils.isNotBlank(recordInvoiceEntityApi.getQsStatus())
                            &&recordInvoiceEntityApi.getQsStatus().equals("1")
                            &&StringUtils.isNotBlank(recordInvoiceEntityApi.getFileType())
                            &&recordInvoiceEntityApi.getFileType().equals("1")){
                        //根据不同的匹配类型，修改不同的匹配关系
                        signInInqueryService.ScanMatch(exportEntity.getSchemaLabel(),recordInvoiceEntityApi);
                    }
                }
            }


        }

        return  m.get("code").toString();
    }

    @RequestMapping("updateGl")
    public R returnIncoice(@RequestBody Map<String, Object> params) {
        UserEntity user = getUser();
        try {
            //获取分库分表的入口
            final String schemaLabel = getCurrentUserSchemaLabel();

             signInInqueryService.updateGl(schemaLabel, params);
        } catch (Exception e) {
            return R.error().put("msg", "确认失败！");
        }
        return R.ok().put("msg", "确认成功！");
    }

    /**
     * 构建实体
     * @return 实体
     */
    private ExportEntity buildExportEntity() {
        final ExportEntity exportEntity = new ExportEntity();
        exportEntity.setSchemaLabel(getCurrentUserSchemaLabel());
        //人员id
        exportEntity.setUserId(getUserId());
        //帐号
        exportEntity.setUserAccount(getUser().getLoginname());
        //人名
        exportEntity.setUserName(getUserName());
        return exportEntity;
    }
}
