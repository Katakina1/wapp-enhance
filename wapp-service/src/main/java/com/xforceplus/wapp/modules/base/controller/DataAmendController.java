package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;

import com.xforceplus.wapp.modules.base.entity.DataAmendEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.DataAmendService;

import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * 修改查询
 */
@RestController
public class DataAmendController extends AbstractController {
    private static final Logger LOGGER = getLogger(DataAmendController.class);
    @Autowired
    private DataAmendService dataAmendService;

    /**
     * 点击查询
     * @param param
     * @return
     */
    @SysLog("变量信息查询")
    @RequestMapping("base/dataAmend/dataQuery/list")
    public R dataQuery(@RequestParam Map<String,Object> param){
        LOGGER.info("数据查询修改,param {}",param);
        Query query=new Query(param);
        Integer result = dataAmendService.queryListCount(query);
        List<DataAmendEntity> list=dataAmendService.queryList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("变量信息维护更新")
    @RequestMapping("base/dataAmend/update/updateAmend")
    public R updateAmend(@RequestBody DataAmendEntity dataAmendEntity) {
        LOGGER.info("变量信息开始更新");
        dataAmendService.update(dataAmendEntity);
        return R.ok();
    }
//    public void update(@RequestBody Map<String,Object> param ) {
//        LOGGER.info("变量信息开始更新,param {}",param);
//        dataAmendService.update(param);
//    }

}
