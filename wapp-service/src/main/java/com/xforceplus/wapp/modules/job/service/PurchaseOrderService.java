package com.xforceplus.wapp.modules.job.service;

import com.xforceplus.wapp.modules.job.pojo.question.QuestionDetail;
import com.xforceplus.wapp.modules.posuopei.entity.QuestionPaperEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

public interface PurchaseOrderService {

    public void executePush();

    public int sendSingle(int error, QuestionPaperEntity entity);

}
