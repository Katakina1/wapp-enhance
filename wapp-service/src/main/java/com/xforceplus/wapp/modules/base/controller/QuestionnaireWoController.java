package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.QuestionnaireWoService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class QuestionnaireWoController extends AbstractController {

    private final static Logger LOGGER = getLogger(QuestionnaireWoController.class);

    private final QuestionnaireWoService questionnaireWoService;

    @Autowired
    public QuestionnaireWoController(QuestionnaireWoService questionnaireWoService) {
        this.questionnaireWoService = questionnaireWoService;
    }

    @SysLog("问卷列表查询")
    @RequestMapping("questionnaireWo/list")
    public R announcementInquiryList(@RequestParam Map<String, Object> params) {
        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<QuestionnaireEntity> infoPagedQueryResult = questionnaireWoService.questionnaireList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("供应商关联信息列表查询")
    @RequestMapping("questionnaireWo/venderList")
    public R venderList(@RequestParam Map<String, Object> params) {
        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<UserEntity> infoPagedQueryResult = questionnaireWoService.venderList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @RequestMapping("/base/questionnaireWo/queryQuestionnaire")
    public R queryQuestionnaire(QuestionnaireEntity entity){
        QuestionnaireEntity questionnaireEntity = questionnaireWoService.queryQuestionnaire(entity);
        return R.ok().put("entity",questionnaireEntity);
    }
}
