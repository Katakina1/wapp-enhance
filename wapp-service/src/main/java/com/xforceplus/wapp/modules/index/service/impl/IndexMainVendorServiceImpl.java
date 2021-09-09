package com.xforceplus.wapp.modules.index.service.impl;

import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.index.dao.IndexMainVendorDao;
import com.xforceplus.wapp.modules.index.entity.ReceiptInfoEntity;
import com.xforceplus.wapp.modules.index.service.IndexMainVendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

@Service
public class IndexMainVendorServiceImpl implements IndexMainVendorService {

    @Autowired
    IndexMainVendorDao indexMainVendorDao;

    @Override
    public Map<String, Object> getIndexMainInfo(Long userid, String usercode) {
        Map<String, Object> map = newHashMap();
        map.put("noticeAllCount", indexMainVendorDao.getNoticeAllCount(userid));
        map.put("noticeReadCount", indexMainVendorDao.getNoticeReadCount(userid));
        map.put("matchSuccessCount", indexMainVendorDao.getMatchSuccessCount(usercode));
        map.put("matchFailedCount", indexMainVendorDao.getMatchFailedCount(usercode));
        map.put("refundCount", indexMainVendorDao.getRefundCount(usercode));
        map.put("costCount", indexMainVendorDao.getCostCount(usercode));
        map.put("redCount", indexMainVendorDao.getRedCount(usercode));
        map.put("agreeRedCount", indexMainVendorDao.getAgreeRedCount(usercode));
        map.put("disagreeRedCount", indexMainVendorDao.getDisagreeRedCount(usercode));
        map.put("abnormalCount", indexMainVendorDao.getAbnormalCount(usercode));
        map.put("abnormalList", indexMainVendorDao.getAbnormal(usercode));
        return map;
    }

    @Override
    public Map<String, Object> getReceiptInfo() {
        Map<String, Object> map = newHashMap();
        List<SelectionOptionEntity> list = indexMainVendorDao.getReceiptInfo();
        ReceiptInfoEntity receiptInfoSp = new ReceiptInfoEntity();
        ReceiptInfoEntity receiptInfoFy = new ReceiptInfoEntity();
        for(SelectionOptionEntity entity : list){
            if("ADDRESS1".equals(entity.getOptionKey())){
                receiptInfoSp.setAddress(entity.getOptionName());
                continue;
            }
            if("CODE1".equals(entity.getOptionKey())){
                receiptInfoSp.setZipCode(entity.getOptionName());
                continue;
            }
            if("TEL1".equals(entity.getOptionKey())){
                receiptInfoSp.setTel(entity.getOptionName());
                continue;
            }
            if("ADDRESS2".equals(entity.getOptionKey())){
                receiptInfoFy.setAddress(entity.getOptionName());
                continue;
            }
            if("CODE2".equals(entity.getOptionKey())){
                receiptInfoFy.setZipCode(entity.getOptionName());
                continue;
            }
            if("TEL2".equals(entity.getOptionKey())){
                receiptInfoFy.setTel(entity.getOptionName());
                continue;
            }
            if("RECIPIENTS1".equals(entity.getOptionKey())){
                receiptInfoSp.setRecipients(entity.getOptionName());
                continue;
            }
            if("RECIPIENTS2".equals(entity.getOptionKey())){
                receiptInfoFy.setRecipients(entity.getOptionName());
                continue;
            }
        }
        map.put("SPXX", receiptInfoSp);
        map.put("FYXX", receiptInfoFy);
        return map;
    }
}
