package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.enums.XFDeductionEnum;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
public class DeductService {

    /**
     * 接收清洗数据
     * @param deductBillBaseData
     * @return
     */
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseData, String batchNo, XFDeductionEnum deductionEnum) {
        return true;
    }

    /**
     * 某批次完成通知，索赔单保证，明细信息完成后，再进行主信息保存
     * @param batchNo
     * @return
     */
    public boolean receiveDone(String batchNo) {
        return true;
    }

    public boolean unlockAndCancel(String businessNo,XFDeductionEnum deductionEnum) {
        return false;
    }
}
