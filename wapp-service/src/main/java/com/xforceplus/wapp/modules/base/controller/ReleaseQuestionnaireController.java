package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.base.service.ReleaseQuestionnaireService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class ReleaseQuestionnaireController extends AbstractController {
    private final static Logger LOGGER = getLogger(ReleaseQuestionnaireController.class);

    @Autowired
    private ReleaseQuestionnaireService releaseQuestionnaireService;

    @SysLog("发布问卷调查")
    @RequestMapping("base/releaseQuestionnaire/{title}")
    public R list(@RequestBody List<QuestionnaireEntity> list, @PathVariable("title") String title) {
        releaseQuestionnaireService.releaseQuestionnaire(list,title);
        return R.ok();
    }

}
