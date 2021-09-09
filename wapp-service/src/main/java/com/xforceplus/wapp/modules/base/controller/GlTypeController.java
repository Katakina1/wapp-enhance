package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.AribaBillTypeEntity;
import com.xforceplus.wapp.modules.base.entity.GlTypeEntity;
import com.xforceplus.wapp.modules.base.export.JVStoreTemplateExport;
import com.xforceplus.wapp.modules.base.service.AribaBillTypeService;
import com.xforceplus.wapp.modules.base.service.GlTypeService;
import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

/**
 * Created by jingsong.mao on 2018/08/10.
 * 业务类型管理控制层
 */
@RestController
public class GlTypeController extends AbstractController {

    @Autowired
    private GlTypeService Service;



    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/gltype/list")
    public R list(GlTypeEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();

        //分页查询起始值
        Entity.setOffset((page - 1) * Entity.getLimit());

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<GlTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);

        int total = Service.queryTotal(getCurrentUserSchemaLabel(), Entity);

        PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/gltype/listNoPage")
    public R listNoPage(GlTypeEntity Entity) {



        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<GlTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);



        return R.ok().put("page", EntityList);
    }

    /**
     * 根据业务类型id获取业务类型信息
     */
    @SysLog("业务类型信息查询")
    @RequestMapping("/base/gltype/getBillTypeInfoById/{id}")
    public R selectSingle(@PathVariable Long id) {

        return R.ok().put("Info", Service.queryObject(getCurrentUserSchemaLabel(), id));
    }

    /**
     * 更新保存信息
     */
    @SysLog("业务类型信息保存")
    @RequestMapping("/base/gltype/saveBillType")
    public R save(@RequestBody GlTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getRemark(), entity.getMatchName1(),entity.getMatchName2(),entity.getGlType(),entity.getGlName(),null);
        if(num > 0)
        {
        	return R.error("保存失败：不可重复");
        }

        Service.save(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }

    /**
     * 更新业务类型信息
     */
    @SysLog("业务类型信息更新")
    @RequestMapping("/base/gltype/updateBillType")
    public R update(@RequestBody GlTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getRemark(), entity.getMatchName1(),entity.getMatchName2(),entity.getGlType(),entity.getGlName(),entity.getId());
        if(num > 0)
        {
        	return R.error("保存失败：不可重复");
        }

        Service.update(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }
    
    /**
     * 删除业务类型信息(批量)
     */
    @SysLog("业务类型信息删除")
    @RequestMapping("/base/gltype/deleteBillType")
    public R delete(@RequestBody GlTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

       Service.deleteBatch(getCurrentUserSchemaLabel(), entity.getIds());

        return R.ok();
    }
}
