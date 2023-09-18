package com.xforceplus.wapp.modules.blue.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.modules.backfill.model.BackFillVerifyBean;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfBlueRelationDao;
import com.xforceplus.wapp.repository.entity.TXfBlueRelationEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-20 19:53
 **/
@Service
public class BlueInvoiceRelationServiceImpl extends ServiceImpl<TXfBlueRelationDao, TXfBlueRelationEntity> implements BlueInvoiceRelationService {
   @Autowired
   private IDSequence idSequence;

    /**
     * 插入（批量）
     *
     */
    @Override
    public boolean saveBatch(String originInvoiceNo, String originInvoiceCode, List<BackFillVerifyBean> blueInvoices) {
        final List<TXfBlueRelationEntity> collect = blueInvoices.stream().map(x -> {
            TXfBlueRelationEntity entity = new TXfBlueRelationEntity();
            entity.setBlueInvoiceCode(x.getInvoiceCode());
            entity.setBlueInvoiceNo(x.getInvoiceNo());
            entity.setRedInvoiceNo(originInvoiceNo);
            entity.setRedInvoiceCode(originInvoiceCode);
            entity.setCreateTime(new Date());
            entity.setId(idSequence.nextId());
            entity.setCreateUser(UserUtil.getLoginName());
            return entity;
        }).collect(Collectors.toList());
        return super.saveBatch(collect);
    }

    /**
     * 查询对应蓝字发票是否存在
     *
     * @param blueInvoiceNo 蓝字发票号码
     * @param blueInvoiceCode 蓝字发票代码
     * @return
     */
    @Override
    public boolean existsByBlueInvoice(String blueInvoiceNo, String blueInvoiceCode) {
        LambdaQueryWrapper<TXfBlueRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity::getBlueInvoiceNo, blueInvoiceNo);
        wrapper.eq(TXfBlueRelationEntity::getBlueInvoiceCode, blueInvoiceCode);
        return super.count(wrapper) > 0;
    }
    
    /**
     * 查询红字发票对应的蓝字发票
     *
     * @param blueInvoiceNo 蓝字发票号码
     * @param blueInvoiceCode 蓝字发票代码
     * @return
     */
    @Override
    public boolean existsByRedInvoice(String redInvoiceNo, String redInvoiceCode) {
        LambdaQueryWrapper<TXfBlueRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceNo, redInvoiceNo);
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceCode, redInvoiceCode);
        return super.count(wrapper) > 0;
    }

    @Override
    public boolean deleteByRedInvoice(String redInvoiceNo, String redInvoiceCode) {
        Asserts.isNull(redInvoiceNo, "红字发票号码不能为空");
        Asserts.isNull(redInvoiceCode, "红字发票代码不能为空");

        LambdaQueryWrapper<TXfBlueRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceNo, redInvoiceNo);
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceCode, redInvoiceCode);
        return super.remove(wrapper);
    }

    @Override
    public List<TXfBlueRelationEntity> getByRedInfo(String redInvoiceNo, String redInvoiceCode) {
        Asserts.isNull(redInvoiceNo, "红字发票号码不能为空");
        Asserts.isNull(redInvoiceCode, "红字发票代码不能为空");

        LambdaQueryWrapper<TXfBlueRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceNo, redInvoiceNo);
        wrapper.eq(TXfBlueRelationEntity::getRedInvoiceCode, redInvoiceCode);
        return super.list(wrapper);
    }

    @Override
    public TXfBlueRelationEntity getByBlueInfo(String blueInvoiceNo, String blueInvoiceCode) {
        Asserts.isNull(blueInvoiceNo, "蓝字发票号码不能为空");
        Asserts.isNull(blueInvoiceCode, "蓝字发票代码不能为空");

        LambdaQueryWrapper<TXfBlueRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TXfBlueRelationEntity::getBlueInvoiceNo, blueInvoiceNo);
        wrapper.eq(TXfBlueRelationEntity::getBlueInvoiceCode, blueInvoiceCode);
        List<TXfBlueRelationEntity> list = super.list(wrapper);
        return CollectionUtil.isEmpty(list) ? null : list.get(0);
    }
}
