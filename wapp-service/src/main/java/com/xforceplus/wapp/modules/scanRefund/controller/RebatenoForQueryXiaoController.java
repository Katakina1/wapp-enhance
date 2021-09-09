package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryXiaoEntity;
import com.xforceplus.wapp.modules.scanRefund.export.RebatenoForQueryInquiryExcel;
import com.xforceplus.wapp.modules.scanRefund.export.RebatenoForQueryInquiryXiaoExcel;
import com.xforceplus.wapp.modules.scanRefund.service.RebatenoForQueryXiaoService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
public class RebatenoForQueryXiaoController extends AbstractController {
    private static final Logger LOGGER = getLogger(RebatenoForQueryXiaoController.class);
    @Autowired
    private RebatenoForQueryXiaoService rebatenoForQueryXiaoService;

    /**
     * 发票签收查询
     * @param param
     * @return
     */
    @SysLog("发票退票查询")
    @RequestMapping("modules/scanRefund/groupRefund/xiaolist")
    public R matchQuerys(@RequestParam Map<String,Object> param){
        LOGGER.info("发票退票查询,param {}",param);

        Query query=new Query(param);
        query.put("usercode",getUser().getUsercode());
        Integer result = rebatenoForQueryXiaoService.invoiceMatchCount(query);
        List<RebatenoForQueryXiaoEntity> list=rebatenoForQueryXiaoService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    /**
     * 导出数据查询
     * @param params
     * @return
     */
    @SysLog("查询导出")
    @RequestMapping("export/scanRefund/rebateForQueryXiaoExport")
    public void rebateForQueryExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {

        LOGGER.info("查询条件为:{}", params);
        //查询列表数据
        params.put("usercode",getUser().getUsercode());
        List<RebatenoForQueryXiaoEntity> list = rebatenoForQueryXiaoService.queryListAll(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("rebateForQueryXiao", list);
        //生成excel
        final RebatenoForQueryInquiryXiaoExcel excelView = new RebatenoForQueryInquiryXiaoExcel(map, "export/scanRefund/rebateForQueryXiao.xlsx", "rebateForQueryXiao");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "rebateForQueryXiao" + excelNameSuffix);
    }

}
