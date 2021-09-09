package com.xforceplus.wapp.modules.enterprise.service;

import com.xforceplus.wapp.modules.enterprise.entity.GoodsEntity;
import com.xforceplus.wapp.modules.enterprise.entity.TaxCodeEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品黑名单Service接口
 * Created by vito.xing on 2018/4/16
 */
public interface GoodsService {

    /**
     * 获取商品信息数据集
     * @param map 查询条件 goodsName 商品名称
     *                      goodsCode 商品编码
     *                      ssbmName  税收分类名称
     *                      ssbmCode  税收商品编码
     *                      schemaLabel 分库名
     * @return 商品数据集
     */
    List<GoodsEntity> queryGoodsList(Map<String, Object> map);

    /**
     * 获取黑名单商品数据总数
     * @param map 查询条件 goodsName 商品名称
     *                      goodsCode 商品编码
     *                      ssbmName  税收分类名称
     *                      ssbmCode  税收商品编码
     *                      schemaLabel 分库名
     * @return 商品数据总数
     */
    Integer queryGoodsTotal(Map<String, Object> map);

    /**
     * 获取税收分类编码信息数据集
     * @param map 查询条件  ssbmName  税收分类名称
     *                      ssbmCode  税收分类编码
     *                      schemaLabel 分库名
     * @return 税收分类编码信息数据集
     */
    List<TaxCodeEntity> queryTaxCodeList(Map<String, Object> map);

    /**
     * 获取税收分类编码数据总数
     * @param map 查询条件  ssbmName  税收分类名称
     *                      ssbmCode  税收分类编码
     *                      schemaLabel 分库名
     * @return 税收分类编码数据总数
     */
    Integer queryTaxCodeTotal(Map<String, Object> map);

    /**
     * 根据商品Id获取商品信息
     * @param goodsId 商品Id
     * @param schemaLabel 分库名
     * @return 商品信息
     */
    GoodsEntity queryGoodsById(String schemaLabel,Long goodsId);

    /**
     * 批量删除商品
     * @param goodsIds 勾选中的商品id
     * @param schemaLabel 分库名
     * @return true-成功 false-失败
     */
    Boolean deleteBatchGoods(String schemaLabel,Long[] goodsIds);

    /**
     * 新增商品黑名单(商品信息如果存在直接修改黑名单状态为1，不存在则保存入库)
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return true-成功 false-失败
     */
    Boolean saveBlackGoods(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 新增商品信息
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return true-成功 false-失败
     */
    Boolean saveGoods(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 批量从excel文件导入黑名单商品 (修改商品黑名单状态为1)
     * @param goodsList 商品数据集
     * @param schemaLabel 分库名
     * @return 成功导入数量
     */
    Integer saveBatchBlackGoods(String schemaLabel,List<GoodsEntity> goodsList);

    /**
     * 批量从excel文件导入商品信息
     * @param goodsList 商品数据集
     * @param schemaLabel 分库名
     * @return 成功导入数量
     */
    Integer saveBatchGoods(String schemaLabel,List<GoodsEntity> goodsList);

    /**
     * 修改商品信息
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return true-成功 false-失败
     */
    Boolean updateGoods(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 查询商品编码在商品表中是否存在
     * @param goodsEntity goodsCode 商品编码
     * @param schemaLabel 分库名
     * @return @return true-成功 false-失败
     */
    Boolean queryGoodsByGoodsCode(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 查询商品名称在商品表中是否存在
     * @param goodsEntity goodsName 商品名称
     * @param schemaLabel 分库名
     * @return @return true-成功 false-失败
     */
    Boolean queryGoodsByGoodsName(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 查询商品编码在商品黑名单中是否存在
     * @param goodsEntity goodsCode 商品编码
     * @param schemaLabel 分库名
     * @return @return true-成功 false-失败
     */
    Boolean queryBlackGoodsByGoodsCode(String schemaLabel,GoodsEntity goodsEntity);

    /**
     * 查询商品名称在商品黑名单中是否存在
     * @param goodsEntity goodsName 商品名称
     * @param schemaLabel 分库名
     * @return @return true-成功 false-失败
     */
    Boolean queryBlackGoodsByGoodsName(String schemaLabel,GoodsEntity goodsEntity);

}
