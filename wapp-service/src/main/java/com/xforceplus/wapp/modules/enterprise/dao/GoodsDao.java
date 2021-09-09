package com.xforceplus.wapp.modules.enterprise.dao;

import com.xforceplus.wapp.modules.enterprise.entity.GoodsEntity;
import com.xforceplus.wapp.modules.enterprise.entity.TaxCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品黑名单Dao
 * Created by vito.xing on 2018/4/16
 */
@Mapper
public interface GoodsDao{

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
     * 获取商品数据总数
     * @param map 查询条件 goodsName 商品名称
     *                      goodsCode 商品编码
     *                      ssbmName  税收分类名称
     *                      ssbmCode  税收商品编码
     *                     schemaLabel 分库名
     * @return 商品数据总数
     */
    Integer queryGoodsTotal(Map<String, Object> map);

    /**
     * 获取税收分类编码信息数据集
     * @param map 查询条件  ssbmName  税收分类名称
     *                      ssbmCode  税收分类编码
     *                    schemaLabel 分库名
     * @return 税收分类编码信息数据集
     */
    List<TaxCodeEntity> queryTaxCodeList(Map<String, Object> map);

    /**
     * 获取税收分类编码数据总数
     * @param map 查询条件  ssbmName  税收分类名称
     *                      ssbmCode  税收分类编码
     *                    schemaLabel 分库名
     * @return 税收分类编码数据总数
     */
    Integer queryTaxCodeTotal(Map<String, Object> map);

    /**
     * 根据商品Id获取商品信息
     * @param goodsId 商品Id
     * @param schemaLabel 分库名
     * @return 商品信息
     */
    GoodsEntity queryGoodsById(@Param("schemaLabel") String schemaLabel,@Param("goodsId") Long goodsId);

    /**
     * 查询商品编码在商品表中是否存在
     * @param goodsEntity goodsCode 商品编码
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryGoodsByGoodsCode(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 查询商品编码在商品黑名单中是否存在
     * @param goodsEntity goodsCode 商品编码
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryBlackGoodsByGoodsCode(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 查询商品编码在商品中是否存在
     * @param goodsEntity goodsCode 商品编码
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryGoodsByCode(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);
    /**
     * 查询商品名称在商品黑名单中是否存在
     * @param goodsEntity goodsName 商品名称
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryBlackGoodsByGoodsName(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 根据税收分类编码获取税收分类编码信息
     * @param goodsEntity ssbmCode 税收分类编码
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    TaxCodeEntity queryTaxCodeBySsbmCode(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 查询商品名称在商品表中是否存在
     * @param goodsEntity goodsName 商品名称
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryGoodsByGoodsName(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 修改商品信息
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return 修改条数
     */
    Integer updateGoods(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 根据商品名称查询商品,修改商品信息黑名单状态为1 (将商品加入黑名单)
     * @param goodsEntity 商品名称
     * @param schemaLabel 分库名
     * @return 修改条数
     */
    Integer updateGoodsAsBlack(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 新增商品黑名单(商品存在修改黑名单状态。商品不存在，添加到商品表,黑名单状态为1)
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return 保存条数
     */
    Integer saveBlackGoods(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 新增商品信息
     * @param goodsEntity 商品信息
     * @param schemaLabel 分库名
     * @return 保存条数
     */
    Integer saveGoods(@Param("schemaLabel") String schemaLabel,@Param("goodsEntity") GoodsEntity goodsEntity);

    /**
     * 批量删除商品
     * @param goodsIds 勾选中的商品id
     * @param schemaLabel 分库名
     * @return 修改条数
     */
    Integer deleteBatchGoods(@Param("schemaLabel") String schemaLabel,@Param("goodsIds") Long[] goodsIds);

}
