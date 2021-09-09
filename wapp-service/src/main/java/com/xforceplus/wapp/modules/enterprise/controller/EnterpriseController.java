package com.xforceplus.wapp.modules.enterprise.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.constantenum.BlackEnterpriseEnum;
import com.xforceplus.wapp.modules.enterprise.entity.EnterpriseEntity;
import com.xforceplus.wapp.modules.enterprise.export.EnterpriseBlackTemplate;
import com.xforceplus.wapp.modules.enterprise.service.EnterpriseService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.enterprise.WebUriMappingConstant.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 企业信息控制器
 * Created by vito.xing on 2018/4/12
 */
@RestController
public class EnterpriseController extends AbstractImportExcelController {

    private static final Logger LOGGER = getLogger(EnterpriseController.class);

    private static final String STATUS = "status";
    private static final String DATA = "data";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    private final EnterpriseService enterpriseService;

    @Autowired
    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    @SysLog("查询企业信息")
    @PostMapping(value = URI_ENTERPRISE_INFO_LIST)
    public R queryEnterpriseList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //根据当前登录账号的所属中心企业筛选结果
        params.put("userId", getUserId());
        params.put("schemaLabel", schemaLabel);
        Query query = new Query(params);

        final List<EnterpriseEntity> enterpriseList = enterpriseService.queryList(query);
        int total = enterpriseService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(enterpriseList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    @SysLog("查询黑名单企业信息")
    @PostMapping(value = URI_ENTERPRISE_BLACK_LIST)
    public R queryBlackEnterpriseList(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //根据当前登录账号的所属中心企业筛选结果
        params.put("userId", getUserId());
        params.put("schemaLabel", schemaLabel);

        //判断是否为总部根节点人员（总部id为1），不必限制
        if (getUser().getOrgid() != 1L) {
            params.put("company", getUser().getCompany());
        }

        Query query = new Query(params);

        final List<EnterpriseEntity> enterpriseList = enterpriseService.queryBlackEnterpriseList(query);
        int total = enterpriseService.queryBlackEnterpriseTotal(query);
        PageUtils pageUtil = new PageUtils(enterpriseList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据企业id获取企业信息
     */
    @SysLog("企业信息查询")
    @RequestMapping(URI_GET_ENTERPRISE_INFO_BY_ID)
    public R queryEnterpriseById(@PathVariable Long enterpriseId) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        EnterpriseEntity enterpriseEntity = enterpriseService.queryEnterpriseById(schemaLabel, enterpriseId);

        return R.ok().put("enterpriseInfo", enterpriseEntity);
    }

    /**
     * 根据黑名单企业id获取黑名单企业信息
     */
    @SysLog("黑名单企业信息查询")
    @RequestMapping(URI_GET_ENTERPRISE_BLACK_BY_ID)
    public R queryBlackEnterpriseById(@PathVariable Long blackEnterpriseId) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        EnterpriseEntity enterpriseEntity = enterpriseService.queryBlackEnterpriseById(schemaLabel, blackEnterpriseId);

        return R.ok().put("enterpriseInfo", enterpriseEntity);
    }

    @SysLog("删除黑名单企业")
    @PostMapping(value = URI_DELETE_BLACK_ENTERPRISE)
    public R deleteBlackEnterprise(@RequestBody Long[] orgIds) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String lastModifyBy = getUser().getUsername();
        enterpriseService.deleteBatchBlackEnterprise(schemaLabel, orgIds, lastModifyBy);

        return R.ok();
    }

    @SysLog("更新黑名单企业信息")
    @PostMapping(value = URI_UPDATE_BLACK_ENTERPRISE)
    public R updateBlackEnterprise(@RequestBody EnterpriseEntity enterpriseEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        String lastModifyBy = getUser().getUsername();
        enterpriseEntity.setLastModifyBy(lastModifyBy);
        enterpriseEntity.setUserId(getUser().getUserid().longValue());
        enterpriseService.updateBlackEnterprise(schemaLabel, enterpriseEntity);

        return R.ok();
    }

    @SysLog("新增黑名单企业")
    @PostMapping(value = URI_SAVE_BLACK_ENTERPRISE)
    public R saveBlackEnterprise(@RequestBody EnterpriseEntity enterpriseEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        enterpriseEntity.setLastModifyBy(getUser().getUsername());
        enterpriseEntity.setUserId(getUserId());
        enterpriseService.saveBlackEnterprise(schemaLabel, enterpriseEntity);
        return R.ok();
    }

    @SysLog("下载企业黑名单模板")
    @AuthIgnore
    @GetMapping(value = URI_DOWNLOAD_TEMPLATE_BLACK_ENTERPRISE)
    public void downloadEnterpriseBlackTemplate(HttpServletResponse response) {
        LOGGER.info("导出勾选模板");

        //生成excel
        final EnterpriseBlackTemplate excelView = new EnterpriseBlackTemplate();
        excelView.write(response, "enterpriseblack");
    }

    @SysLog("导入黑名单企业")
    @PostMapping(value = URI_IMPORT_BLACK_ENTERPRISE, produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String importBlackEnterprise(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }

        //获取导入excel人的名称和id
        String createBy = getUser().getUsername();
        Long userId = getUserId();

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
            result = insertBlackEnterprise(sheet, rowCount, createBy, userId);
        } else if (rowCount > 500) {
            return new Gson().toJson(R.error("导入数据超过500条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        //获取导入成功和失败的数量
        Integer total = result.get("total");
        Integer success = result.get("success");
        Integer failure = result.get("failure");

        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条";

        return new Gson().toJson(R.ok(message));
    }


    /**
     * 读取excel文件获取数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     * @param createBy 当前登录账号人名称
     * @param userId   当前登录账号人Id
     */
    @SuppressWarnings("unchecked")
    private Map insertBlackEnterprise(Sheet sheet, int rowCount, String createBy, long userId) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        final List<EnterpriseEntity> enterpriseList = newArrayList();
        final Map<String, Integer> result = newHashMap();
        //所属中心企业
        final String company = getUser().getCompany();

        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapBlackEnterprise(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                EnterpriseEntity enterpriseEntity = (EnterpriseEntity) wrapResult.get(DATA);
                //为每条黑名单企业设置创建人和创建人Id
                enterpriseEntity.setUserId(userId);
                enterpriseEntity.setCreateBy(createBy);
                enterpriseEntity.setLastModifyBy(createBy);
                enterpriseEntity.setCompany(company);

                enterpriseList.add(enterpriseEntity);
            }
        }

        //批量保存企业黑名单(企业存在修改企业黑名单状态为1。不存在则添加到企业黑名单表，黑名单状态为1)
        Integer success = enterpriseService.saveBatchBlackEnterprise(schemaLabel, enterpriseList);
        result.put("failure", enterpriseList.size() - success);
        result.put("success", success);
        result.put("total", enterpriseList.size());

        return result;
    }

    /**
     * 将Excel数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 企业黑名单数据
     */
    private Map<String, Object> wrapBlackEnterprise(Row row) {
        final Map<String, Object> result = newHashMap();
        final EnterpriseEntity enterpriseEntity = new EnterpriseEntity();

        //如果纳税人税号或纳税人名称为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row, BlackEnterpriseEnum.TAXNO.getValue())) &&
                Strings.isNullOrEmpty(getCellData(row, BlackEnterpriseEnum.ORGNAME.getValue()))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //将Excel数据包装进实体类
        enterpriseEntity.setTaxNo(getCellData(row, BlackEnterpriseEnum.TAXNO.getValue()));
        enterpriseEntity.setOrgName(getCellData(row, BlackEnterpriseEnum.ORGNAME.getValue()));
        enterpriseEntity.setComType(getCellData(row, BlackEnterpriseEnum.COMPANYTYPE.getValue()));

        result.put(STATUS, STATUS_NORMAL);
        result.put(DATA, enterpriseEntity);

        return result;
    }

}
