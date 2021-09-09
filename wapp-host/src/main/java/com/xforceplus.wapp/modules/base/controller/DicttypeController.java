package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import com.xforceplus.wapp.modules.base.entity.DicttypeEntity;
import com.xforceplus.wapp.modules.base.service.DictdetaService;
import com.xforceplus.wapp.modules.base.service.DicttypeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Mark.chen on 2018/04/18.
 */
@RestController
@RequestMapping("base/dictype")
public class DicttypeController extends AbstractController {

    private static final  Logger LOGGER = getLogger(DicttypeController.class);

    private final DicttypeService dicttypeService;

    private final DictdetaService dictdetaService;

    @Autowired
    public DicttypeController(DicttypeService dicttypeService, DictdetaService dictdetaService) {
        this.dicttypeService = dicttypeService;
        this.dictdetaService = dictdetaService;
    }

    /**
     * 业务字典类型信息
     */
    @SysLog("业务字典类型列表查询")
    @RequestMapping("/list")
    public R list(DicttypeEntity dicttypeEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        //获取当前页面
        final Integer page = dicttypeEntity.getPage();

        //分页查询起始值
        dicttypeEntity.setOffset((page - 1) * dicttypeEntity.getLimit());

        List<DicttypeEntity> list = dicttypeService.queryList(schemaLabel, dicttypeEntity);
        int total = dicttypeService.queryTotal(schemaLabel, dicttypeEntity);

        PageUtils pageUtil = new PageUtils(list, total, dicttypeEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 业务字典类型保存
     */
    @SysLog("业务字典类型保存")
    @RequestMapping("/save")
    public R save(@RequestBody DicttypeEntity dicttypeEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        try {
            dicttypeService.save(schemaLabel, dicttypeEntity);
        } catch (UncategorizedSQLException e) {
            return R.error("字典类型编码不能重复！");
        }


        return R.ok();
    }

    /**
     * 业务字典类型，修改回显
     */
    @SysLog("业务字典类型，修改回显")
    @RequestMapping("/info/{dicttypeid}")
    public R info(@PathVariable("dicttypeid") Long dicttypeid) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        final DicttypeEntity dicttypeEntity = dicttypeService.queryObject(schemaLabel, dicttypeid);
        return R.ok().put("dicttypeEntity", dicttypeEntity);
    }

    /**
     * 业务字典类型更新
     */
    @SysLog("业务字典类型更新")
    @RequestMapping("/update")
    public R update(@RequestBody DicttypeEntity dicttypeEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        dicttypeService.update(schemaLabel, dicttypeEntity);

        return R.ok();
    }

    /**
     * 业务字典类型删除
     */
    @SysLog("业务字典类型删除")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] dicTypeIds) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        for (Long dicTypeId :
                dicTypeIds) {
            dicttypeService.delete(schemaLabel, dicTypeId);
        }
        return R.ok();
    }

    /**
     * 业务字典明细信息
     */
    @SysLog("业务字典明细查询")
    @RequestMapping("/detailList")
    public R detailList(DictdetaEntity dictdetaEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        //获取当前页面
        final Integer page = dictdetaEntity.getPage();

        //分页查询起始值
        dictdetaEntity.setOffset((page - 1) * dictdetaEntity.getLimit());

        List<DictdetaEntity> list = dictdetaService.queryList(schemaLabel, dictdetaEntity);

        int total = dictdetaService.queryTotal(schemaLabel, dictdetaEntity);

        PageUtils pageUtil = new PageUtils(list, total, dictdetaEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 业务字典明细信息
     */
    @SysLog("业务字典明细信息查询")
    @RequestMapping("/detailListQuery")
    public R detailListQuery(DictdetaEntity dictdetaEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        List<DictdetaEntity> list = dictdetaService.queryList(schemaLabel, dictdetaEntity);

        return R.ok().put("list", list);
    }

    /**
     * 业务字典明细保存
     */
    @SysLog("业务字典明细保存")
    @RequestMapping("/detailSave")
    public R detailSave(@RequestBody DictdetaEntity dictdetaEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        dictdetaService.save(schemaLabel, dictdetaEntity);

        return R.ok();
    }

    /**
     * 业务字典明细删除
     */
    @SysLog("业务字典明细删除")
    @RequestMapping("/detailDelete")
    public R detailDelete(@RequestBody Long[] dicDetIds) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        for (Long dicDetId :
                dicDetIds) {
            dictdetaService.delete(schemaLabel, dicDetId);
        }
        return R.ok();
    }

    /**
     * 业务字典明细，修改回显
     */
    @SysLog("业务字典明细，修改回显")
    @RequestMapping("/detailInfo/{dictid}")
    public R detailInfo(@PathVariable("dictid") Long dictid) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        final DictdetaEntity dictdetaEntity = dictdetaService.queryObject(schemaLabel, dictid);
        return R.ok().put("dictdetaEntity", dictdetaEntity);
    }

    /**
     * 业务字典明细更新
     */
    @SysLog("业务字典明细更新")
    @RequestMapping("/detailUpdate")
    public R detailUpdate(@RequestBody DictdetaEntity dictdetaEntity) {

        final String schemaLabel = getCurrentUserSchemaLabel();

        dictdetaService.update(schemaLabel, dictdetaEntity);
        return R.ok();
    }
}
