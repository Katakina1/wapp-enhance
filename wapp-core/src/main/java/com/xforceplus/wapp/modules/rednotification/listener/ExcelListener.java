package com.xforceplus.wapp.modules.rednotification.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener extends AnalysisEventListener<TXfRedNotificationEntity> {
    private List<TXfRedNotificationEntity> datas = new ArrayList<>();
    private static final int BATCH_COUNT = 3000;
    private RedNotificationMainService redNotificationMainService;

    public ExcelListener(RedNotificationMainService redNotificationMainService){
        this.redNotificationMainService = redNotificationMainService;
    }

    @Override
    public void invoke(TXfRedNotificationEntity record, AnalysisContext analysisContext) {
        //数据存储到datas，供批量处理，或后续自己业务逻辑处理。
        datas.add(record);
        //达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if(datas.size() >= BATCH_COUNT){
            saveData();
            // 存储完成清理datas
            datas.clear();
        }
    }

    // 保存数据
    private void saveData() {
//        datas

    }

    public List<TXfRedNotificationEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<TXfRedNotificationEntity> datas) {
        this.datas = datas;
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();//确保所有数据都能入库
    }
}
