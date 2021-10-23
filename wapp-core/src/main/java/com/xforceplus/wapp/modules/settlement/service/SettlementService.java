package com.xforceplus.wapp.modules.settlement.service;

import com.xforceplus.wapp.enums.TXfAmountSplitRuleEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类描述：
 *
 * @ClassName SettlementService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 13:59
 */
@Service
public class SettlementService {
    @Autowired
    private TXfSettlementExtDao settlementDao;


    public List<TXfSettlementEntity> queryWaitSplitSettlement(Long id, Integer status, Integer limit ) {
        return settlementDao.querySettlementByStatus(status, id, limit);
    }



    public TXfSettlementEntity getById(Long id){
        return settlementDao.selectById(id);
    }




}
