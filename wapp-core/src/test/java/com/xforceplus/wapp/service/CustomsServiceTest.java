package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.enums.customs.AccountStatusEnum;
import com.xforceplus.wapp.repository.dao.TDxCustomsDao;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/7/3 17:57
 */
@Slf4j
public class CustomsServiceTest extends BaseUnitTest {

    @Autowired
    public TDxCustomsDao tDxCustomsDao;

    /**
     * 获取需要比对的海关缴款书数据
     * @return
     */
    @Test
    public void getTaskCustoms() {
        LambdaQueryWrapper<TDxCustomsEntity> query = new LambdaQueryWrapper<>();
        query.eq(TDxCustomsEntity::getAccountStatus, AccountStatusEnum.ACCOUNT_00.getCode());
        query.and(wapper -> wapper.eq(TDxCustomsEntity::getPushBmsStatus, "0").or().isNull(TDxCustomsEntity::getPushBmsStatus));
        List<TDxCustomsEntity> tDxCustomsEntities = tDxCustomsDao.selectList(query);
        for (TDxCustomsEntity tDxCustomsEntity : tDxCustomsEntities) {
            System.out.println("tDxCustomsEntity = " + tDxCustomsEntity);
        }
    }



}
