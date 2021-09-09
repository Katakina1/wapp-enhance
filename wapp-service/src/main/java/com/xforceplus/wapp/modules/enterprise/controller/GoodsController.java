package com.xforceplus.wapp.modules.enterprise.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.constantenum.BlackGoodsEnum;
import com.xforceplus.wapp.modules.constantenum.GoodsTaxCodeEnum;
import com.xforceplus.wapp.modules.enterprise.entity.GoodsEntity;
import com.xforceplus.wapp.modules.enterprise.entity.TaxCodeEntity;
import com.xforceplus.wapp.modules.enterprise.export.GoodsBlackTemplate;
import com.xforceplus.wapp.modules.enterprise.export.TaxCodeTemplate;
import com.xforceplus.wapp.modules.enterprise.service.GoodsService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_DELETE_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_DOWNLOAD_TEMPLATE_BLACK_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_DOWNLOAD_TEMPLATE_GOODS_TAX_CODE;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_GET_GOODS_BY_ID;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_GOODS_LIST;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_IMPORT_BLACK_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_IMPORT_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_SAVE_BLACK_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_SAVE_GOODS;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_TAX_CODE_LIST;
import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.URI_UPDATE_GOODS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 商品控制器
 * Created by vito.xing on 2018/4/12
 */
@RestController
public class GoodsController extends AbstractImportExcelController {

    private static final Logger LOGGER = getLogger(GoodsController.class);

    private static final String STATUS = "status";
    private static final String DATA = "data";
    //status 为1cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @SysLog("查询商品列表")
    @PostMapping(value = URI_GOODS_LIST)
    public R queryGoodsList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //判断是否为总部根节点人员（总部id为1），不必限制
        if (getUser().getOrgid() != 1L) {
            params.put("company", getUser().getCompany());
        }

        Query query = new Query(params);

        final List<GoodsEntity> goodsList = goodsService.queryGoodsList(query);
        int total = goodsService.queryGoodsTotal(query);
        PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    @SysLog("获取税收分类编码列表")
    @PostMapping(value = URI_TAX_CODE_LIST)
    public R queryTaxCodeList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        Query query = new Query(params);

        final List<TaxCodeEntity> goodsList = goodsService.queryTaxCodeList(query);
        int total = goodsService.queryTaxCodeTotal(query);
        PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据商品id获取商品信息
     */
    @SysLog("商品信息查询")
    @RequestMapping(URI_GET_GOODS_BY_ID)
    public R queryBlackGoodsById(@PathVariable Long goodsId) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        GoodsEntity goodsEntity = goodsService.queryGoodsById(schemaLabel, goodsId);

        return R.ok().put("goodsInfo", goodsEntity);
    }

    @SysLog("更新商品信息")
    @PostMapping(value = URI_UPDATE_GOODS)
    public R updateGoods(@RequestBody GoodsEntity goodsEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        Boolean isExist;

        isExist = goodsService.queryGoodsByGoodsCode(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品编号已存在，请重新输入!");
        }

        isExist = goodsService.queryGoodsByGoodsName(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品名称已经存在，请重新输入!");
        }

        goodsService.updateGoods(schemaLabel, goodsEntity);

        return R.ok();
    }

    @SysLog("删除商品")
    @PostMapping(value = URI_DELETE_GOODS)
    public R deleteGoods(@RequestBody Long[] goodsIds) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        goodsService.deleteBatchGoods(schemaLabel, goodsIds);
        return R.ok();
    }

    @SysLog("增加黑名单商品")
    @PostMapping(value = URI_SAVE_BLACK_GOODS)
    public R saveBlackGoods(@RequestBody GoodsEntity goodsEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        goodsEntity.setIsBlack("1");
        Boolean isExist;

        //若商品编码已存在，提示商品编码重复
        isExist = goodsService.queryBlackGoodsByGoodsCode(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品编码重复!");
        }

        //若商品名称已存在，提示商品编码重复
        isExist = goodsService.queryBlackGoodsByGoodsName(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品名称重复!");
        }

        //所属中心企业
        goodsEntity.setCompany(getUser().getCompany());

        goodsService.saveBlackGoods(schemaLabel, goodsEntity);
        return R.ok();
    }

    @SysLog("增加商品信息")
    @PostMapping(value = URI_SAVE_GOODS)
    public R saveGoods(@RequestBody GoodsEntity goodsEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        Boolean isExist;

        //若商品编码在黑名单中已存在，提示商品编码重复
        isExist = goodsService.queryBlackGoodsByGoodsCode(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品编码在黑名单中,请重新输入!");
        }

        //若商品名称在黑名单中已存在，提示商品编码重复
        isExist = goodsService.queryBlackGoodsByGoodsName(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品名称在黑名单中,请重新输入!");
        }

        //若商品编码已存在，提示商品编码重复
        isExist = goodsService.queryGoodsByGoodsCode(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品编码重复,请重新输入!");
        }

        //若商品名称已存在，提示商品编码重复
        isExist = goodsService.queryGoodsByGoodsName(schemaLabel, goodsEntity);
        if (isExist) {
            return R.error("商品名称重复,请重新输入!");
        }

        //所属中心企业
        goodsEntity.setCompany(getUser().getCompany());

        //保存商品信息
        goodsService.saveGoods(schemaLabel, goodsEntity);
        return R.ok();
    }

    @SysLog("下载商品黑名单模板")
    @AuthIgnore
    @GetMapping(value = URI_DOWNLOAD_TEMPLATE_BLACK_GOODS)
    public void downloadGoodsBlackTemplate(HttpServletResponse response) {
        LOGGER.info("导出勾选模板");

        //生成excel
        final GoodsBlackTemplate excelView = new GoodsBlackTemplate();
        excelView.write(response, "goodsblack");
    }

    @SysLog("下载商品税收分类模板")
    @AuthIgnore
    @GetMapping(value = URI_DOWNLOAD_TEMPLATE_GOODS_TAX_CODE)
    public void downloadGoodsTaxCodeTemplate(HttpServletResponse response) {
        LOGGER.info("导出勾选模板");

        //生成excel
        final TaxCodeTemplate excelView = new TaxCodeTemplate();
        excelView.write(response, "goodstaxcode");
    }

    @SysLog("导入黑名单商品")
    @PostMapping(value = URI_IMPORT_BLACK_GOODS, produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String importBlackGoods(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }
        //创建工作簿对象
        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Integer> result;
        if (1 < rowCount && rowCount <= 500) {
            result = insertBlackGoods(sheet, rowCount);
        } else if (rowCount > 500) {
            return new Gson().toJson(R.error("导入数据超过500条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        //获取成功和失败数量
        Integer total = result.get("total");
        Integer success = result.get("success");
        Integer failure = result.get("failure");

        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条";
        return new Gson().toJson(R.ok(message));
    }

    @SysLog("导入商品税收分类")
    @PostMapping(value = URI_IMPORT_GOODS, produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String importGoods(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }

        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        Map<String, Integer> result;
        if (1 < rowCount && rowCount <= 500) {
            result = insertGoods(sheet, rowCount);
        } else if (rowCount > 500) {
            return new Gson().toJson(R.error("导入数据超过500条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        Integer total = result.get("total");
        Integer success = result.get("success");
        Integer failure = result.get("failure");

        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条";
        return new Gson().toJson(R.ok(message));
    }


    /**
     * 读取excel文件获取黑名单商品数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertBlackGoods(Sheet sheet, int rowCount) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        final List<GoodsEntity> goodsList = newArrayList();
        final Map<String, Integer> result = newHashMap();
        //所属中心企业
        final String company = getUser().getCompany();

        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapBlackGoods(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            if (STATUS_NORMAL.equals(status)) {
                GoodsEntity goodsEntity = (GoodsEntity) wrapResult.get(DATA);
                goodsEntity.setCompany(company);

                goodsList.add(goodsEntity);
            }
        }

        //批量保存商品黑名单(商品存在修改商品黑名单状态为1。不存在添加商品信息到商品表，黑名单状态为1)
        Integer success = goodsService.saveBatchBlackGoods(schemaLabel, goodsList);
        result.put("failure", goodsList.size() - success);
        result.put("success", success);
        result.put("total", goodsList.size());

        return result;
    }

    /**
     * 将Excel数据包装进商品黑名单实体类
     *
     * @param row Excel 行数据
     * @return 企业黑名单数据
     */
    private Map<String, Object> wrapBlackGoods(Row row) {
        final Map<String, Object> result = newHashMap();
        final GoodsEntity goodsEntity = new GoodsEntity();

        //如果商品编码或商品名称为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row, BlackGoodsEnum.GOODSCODE.getValue())) &&
                Strings.isNullOrEmpty(getCellData(row, BlackGoodsEnum.GOODSNAME.getValue()))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //将Excel数据包装进商品黑名单实体类
        goodsEntity.setGoodsCode(getCellData(row, BlackGoodsEnum.GOODSCODE.getValue()));
        goodsEntity.setGoodsName(getCellData(row, BlackGoodsEnum.GOODSNAME.getValue()));
        goodsEntity.setGoodsRemark(getCellData(row, BlackGoodsEnum.REMARK.getValue()));

        result.put(STATUS, STATUS_NORMAL);
        result.put(DATA, goodsEntity);

        return result;
    }

    /**
     * 读取excel文件获取商品税收分类数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertGoods(Sheet sheet, int rowCount) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        final List<GoodsEntity> goodsList = newArrayList();
        final Map<String, Integer> result = newHashMap();
        //所属中心企业
        final String company = getUser().getCompany();

        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapGoods(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            if (STATUS_NORMAL.equals(status)) {
                GoodsEntity goodsEntity = (GoodsEntity) wrapResult.get(DATA);
                goodsEntity.setCompany(company);

                goodsList.add(goodsEntity);
            }
        }

        //批量保存商品税收分类信息
        Integer success = goodsService.saveBatchGoods(schemaLabel, goodsList);
        result.put("failure", goodsList.size() - success);
        result.put("success", success);
        result.put("total", goodsList.size());

        return result;
    }

    /**
     * 将Excel数据包装进商品税收分类实体类
     *
     * @param row Excel 行数据
     * @return 商品信息数据
     */
    private Map<String, Object> wrapGoods(Row row) {
        final Map<String, Object> result = newHashMap();
        final GoodsEntity goodsEntity = new GoodsEntity();

        //如果商品编码或商品名称或税收分类编码或税收分类名称为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row, GoodsTaxCodeEnum.GOODSCODE.getValue())) &&
                Strings.isNullOrEmpty(getCellData(row, GoodsTaxCodeEnum.GOODSNAME.getValue())) &&
                Strings.isNullOrEmpty(getCellData(row, GoodsTaxCodeEnum.TAXCODE.getValue())) &&
                Strings.isNullOrEmpty(getCellData(row, GoodsTaxCodeEnum.TAXCODENAME.getValue()))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //将Excel数据包装进商品税收分类实体类
        goodsEntity.setGoodsCode(getCellData(row, GoodsTaxCodeEnum.GOODSCODE.getValue()));
        goodsEntity.setGoodsName(getCellData(row, GoodsTaxCodeEnum.GOODSNAME.getValue()));
        goodsEntity.setSsbmCode(getCellData(row, GoodsTaxCodeEnum.TAXCODE.getValue()));
        goodsEntity.setSsbmName(getCellData(row, GoodsTaxCodeEnum.TAXCODENAME.getValue()));
        //给从excel导入的数据设置默认ssbmId
        goodsEntity.setSsbmId("-1");

        result.put(STATUS, STATUS_NORMAL);
        result.put(DATA, goodsEntity);

        return result;
    }

}
