package com.xforceplus.wapp.modules.settlement.service;

import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
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


    public List<TXfSettlementEntity> querySettlementByStatus(Long id, Integer status, Integer limit ) {
        return settlementDao.querySettlementByStatus(status, id, limit);
    }



    public TXfSettlementEntity getById(Long id){
        return settlementDao.selectById(id);
    }




}
