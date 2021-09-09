package com.xforceplus.wapp.modules.enterprise.service.impl;

import com.xforceplus.wapp.common.validator.ValidatorUtils;
import com.xforceplus.wapp.modules.enterprise.dao.GoodsDao;
import com.xforceplus.wapp.modules.enterprise.entity.GoodsEntity;
import com.xforceplus.wapp.modules.enterprise.entity.TaxCodeEntity;
import com.xforceplus.wapp.modules.enterprise.service.GoodsService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 商品信息Service实现类
 * Created by vito.xing on 2018/4/16
 */
@Service("goodsServiceImpl")
public class GoodsServiceImpl implements GoodsService {

    private static final Logger LOGGER = getLogger(GoodsServiceImpl.class);

    private GoodsDao goodsDao;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public GoodsServiceImpl(GoodsDao goodsDao){
        this.goodsDao = goodsDao;
    }

    @Override
    public List<GoodsEntity> queryGoodsList(Map<String, Object> map) {
        return goodsDao.queryGoodsList(map);
    }

    @Override
    public Integer queryGoodsTotal(Map<String, Object> map) {
        return goodsDao.queryGoodsTotal(map);
    }

    @Override
    public List<TaxCodeEntity> queryTaxCodeList(Map<String, Object> map) {
        return goodsDao.queryTaxCodeList(map);
    }

    @Override
    public Integer queryTaxCodeTotal(Map<String, Object> map) {
        return goodsDao.queryTaxCodeTotal(map);
    }

    @Override
    public GoodsEntity queryGoodsById(String schemaLabel,Long goodsId) {
        return goodsDao.queryGoodsById(schemaLabel,goodsId);
    }

    @Override
    @Transactional
    public Boolean deleteBatchGoods(String schemaLabel,Long[] orgIds) {
        return goodsDao.deleteBatchGoods(schemaLabel,orgIds) > 0;
    }

    @Override
    @Transactional
    public Boolean saveBlackGoods(String schemaLabel,GoodsEntity goodsEntity) {
        return goodsDao.saveBlackGoods(schemaLabel,goodsEntity) > 0;
    }

    @Override
    @Transactional
    public Boolean saveGoods(String schemaLabel,GoodsEntity goodsEntity) {
        ValidatorUtils.validateEntity(goodsEntity);
        return goodsDao.saveGoods(schemaLabel,goodsEntity) > 0;
    }

    @Override
    @Transactional
    public Integer saveBatchBlackGoods(String schemaLabel,List<GoodsEntity> goodsList) {
        Integer successCount = 0;
        for(int i=0;i<goodsList.size();i++) {
            if (i <500) {
                String goodsCode = goodsList.get(i).getGoodsCode();
                String goodsName = goodsList.get(i).getGoodsName();
                //行中商品编码或商品名称不为空，读取此条数据。否则跳过此条，继续下一个循环
                if ( !(Strings.isNullOrEmpty(goodsCode)) && !(Strings.isNullOrEmpty(goodsName))) {
                    //excel行中数据不超过长度限制，读取此条数据。否则跳过此条,继续下一个循环
                    if(goodsCode.length() < 100 && goodsName.length() < 100) {
                        //根据商品编码判断是否商品存在，存在则直接修改状态,不存在则保存此条商品到商品表，黑名单状态为1
                        Boolean isExist = goodsDao.queryGoodsByCode(schemaLabel,goodsList.get(i)) > 0;

                        if (isExist) {
                            //根据商品编码查询商品,修改商品信息黑名单状态为1 (将商品加入黑名单)
                            goodsDao.updateGoodsAsBlack(schemaLabel,goodsList.get(i));
                            ++successCount;
                        } else {
                            //保存商品信息到商品表，并且黑名单状态为1
                            goodsDao.saveBlackGoods(schemaLabel,goodsList.get(i));
                            ++successCount;
                        }
                    }
                }
            }

        }
        return successCount;
    }

    @Override
    @Transactional
    public Integer saveBatchGoods(String schemaLabel,List<GoodsEntity> goodsList) {
        //成功从Excel读取的数量
        Integer successCount = 0;

        Boolean result;
        for(int i = 0;i<goodsList.size(); i++) {
            if (i < 500) {
                String goodsCode = goodsList.get(i).getGoodsCode();
                String goodsName = goodsList.get(i).getGoodsName();
                String ssbmCode = goodsList.get(i).getSsbmCode();
                String ssbmName = goodsList.get(i).getSsbmName();
                //行中商品编码或商品名称或税收分类编码、名称为空，则跳过此条，继续下一个循环
                if ( !(Strings.isNullOrEmpty(goodsCode)) && !(Strings.isNullOrEmpty(goodsName)) &&
                        !(Strings.isNullOrEmpty(ssbmCode)) && !(Strings.isNullOrEmpty(ssbmName))) {
                    //excel行中数据超过长度限制，则跳过此条，继续下一个循环
                    if( !(goodsCode.length() > 100) && !(goodsName.length() > 100) &&
                            !(ssbmCode.length() > 100) && !(ssbmName.length() > 100)) {

                        Boolean codeExist;
                        Boolean nameExist;
                        codeExist = goodsDao.queryGoodsByGoodsCode(schemaLabel,goodsList.get(i)) > 0;
                        nameExist = goodsDao.queryGoodsByGoodsName(schemaLabel,goodsList.get(i)) > 0;

                        //商品编码和商品名称都不存在，导入此条数据。存在则跳过此条，继续读取下一条
                        if(!codeExist && !nameExist) {
                            //根据税收分类编码获取税收分类信息
                            TaxCodeEntity taxCodeEntity = goodsDao.queryTaxCodeBySsbmCode(schemaLabel,goodsList.get(i));
                            //如果税收分类编码在数据库中存在，导入此条数据。不存在则跳过此条，继续读取下一条
                            if(null != taxCodeEntity) {
                                //为商品设置税收分类编码和税收分类名称
                                goodsList.get(i).setSsbmId(taxCodeEntity.getId().toString());
                                goodsList.get(i).setSsbmName(taxCodeEntity.getSsbmName());
                                //保存商品信息到商品表
                                result = goodsDao.saveGoods(schemaLabel,goodsList.get(i)) > 0;
                                //成功保存商品信息到数据库，则计数器加1
                                if(result) {
                                    ++successCount;
                                }
                            }
                        }

                    }
                }

            }
        }
        return successCount;
    }

    @Override
    @Transactional
    public Boolean updateGoods(String schemaLabel,GoodsEntity goodsEntity) {
        ValidatorUtils.validateEntity(goodsEntity);
        return goodsDao.updateGoods(schemaLabel,goodsEntity) > 0;
    }

    @Override
    public Boolean queryGoodsByGoodsCode(String schemaLabel,GoodsEntity goodsEntity) {
        return goodsDao.queryGoodsByGoodsCode(schemaLabel,goodsEntity) > 0;
    }

    @Override
    public Boolean queryGoodsByGoodsName(String schemaLabel,GoodsEntity goodsEntity) {
        return goodsDao.queryGoodsByGoodsName(schemaLabel,goodsEntity) > 0;
    }

    @Override
    public Boolean queryBlackGoodsByGoodsCode(String schemaLabel,GoodsEntity goodsEntity) {
        return goodsDao.queryBlackGoodsByGoodsCode(schemaLabel,goodsEntity) > 0;
    }

    @Override
    public Boolean queryBlackGoodsByGoodsName(String schemaLabel,GoodsEntity goodsEntity) {
        return goodsDao.queryBlackGoodsByGoodsName(schemaLabel,goodsEntity) > 0;
    }
}
