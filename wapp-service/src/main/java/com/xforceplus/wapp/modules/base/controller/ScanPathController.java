package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserScanPathEntity;
import com.xforceplus.wapp.modules.base.service.ScanPathService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jingsong.mao on 2018/08/10.
 * 扫描点管理控制层
 */
@RestController
public class ScanPathController extends AbstractController {

    @Autowired
    private ScanPathService Service;

    /**
     * 查询用户列表
     */
    @SysLog("扫描点列表查询")
    @RequestMapping("/base/scanpath/list")
    public R list(ScanPathEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();
        if(page!=null){
            //分页查询起始值
            Entity.setOffset((page - 1) * Entity.getLimit());
        }



        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<ScanPathEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);

        
        
        

        int total = Service.queryTotal(getCurrentUserSchemaLabel(), Entity);
        if(page!=null) {
            PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);
            return R.ok().put("page", pageUtil);
        }else{
            return  R.ok().put("optionList", EntityList);
        }
    }


    
    
    /**
     * 查询用户列表
     */
    @SysLog("扫描点列表查询")
    @RequestMapping("/base/scanpath/listU")
    public R listU(UserScanPathEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();

        //分页查询起始值
        Entity.setOffset((page - 1) * Entity.getLimit());

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<UserScanPathEntity> EntityList = Service.queryListU(getCurrentUserSchemaLabel(), Entity);

        int total = Service.queryTotalU(getCurrentUserSchemaLabel(), Entity);

        PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }
    
    
    /**
     * 查询用户列表
     */
    @SysLog("扫描点列表查询")
    @RequestMapping("/base/scanpath/listUNot")
    public R listUNot(UserScanPathEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();

        //分页查询起始值
        Entity.setOffset((page - 1) * Entity.getLimit());

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<UserScanPathEntity> EntityList = Service.queryListUNot(getCurrentUserSchemaLabel(), Entity);

        int total = Service.queryTotalUNot(getCurrentUserSchemaLabel(), Entity);

        PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }
    
    
    /**
     * 查询列表
     */
    @SysLog("扫描点列表查询")
    @RequestMapping("/base/scanpath/listQuery")
    public R listQuery(ScanPathEntity Entity) {

       
        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());
//        Entity.setProfit(getUser().getProfit());

        List<ScanPathEntity> EntityList = Service.queryListS(getCurrentUserSchemaLabel(), Entity);


        return R.ok().put("list", EntityList);
    }


    /**
     * 根据扫描点id获取扫描点信息
     */
    @SysLog("扫描点信息查询")
    @RequestMapping("/base/scanpath/getScanPathInfoById/{scanpathid}")
    public R selectSingle(@PathVariable Long scanpathid) {

        return R.ok().put("Info", Service.queryObject(getCurrentUserSchemaLabel(), scanpathid));
    }

    /**
     * 更新保存信息
     */
    @SysLog("扫描点信息保存")
    @RequestMapping("/base/scanpath/saveScanPath")
    public R save(@RequestBody ScanPathEntity Entity) {

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        Service.save(getCurrentUserSchemaLabel(), Entity);

        return R.ok();
    }

    /**
     * 更新扫描点信息
     */
    @SysLog("扫描点信息更新")
    @RequestMapping("/base/scanpath/updateScanPath")
    public R update(@RequestBody ScanPathEntity Entity) {

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());
        

        Service.update(getCurrentUserSchemaLabel(), Entity);

        return R.ok();
    }
    
    /**
     * 删除扫描点信息(批量)
     */
    @SysLog("扫描点信息删除")
    @RequestMapping("/base/scanpath/deleteScanPath")
    public R delete(@RequestBody ScanPathEntity entity) {

        //分库
       entity.setSchemaLabel(getCurrentUserSchemaLabel());

       Service.deleteBatch(getCurrentUserSchemaLabel(), entity.getIds());

        return R.ok();
    }
    
    /**
     * 添加用户的扫描点
     */
    @SysLog("用户的扫描点添加")
    @RequestMapping("/base/scanpath/scanPathAdd/{userId}")
    public R scanPathAdd(@RequestBody List<UserScanPathEntity> userBilltypeEntities, @PathVariable("userId") Integer userId) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        for (UserScanPathEntity entity :
                userBilltypeEntities) {
        	entity.setUserId(userId+"");
        	entity.setUuid(null);
        	entity.setScanId(entity.getId().toString());
            Service.saveUserScanPath(schemaLabel, entity);
        }
        return R.ok();

    }
    
    /**
     * 删除用户的扫描点
     */
    @SysLog("用户的扫描点删除")
    @RequestMapping("/base/scanpath/scanPathDel")
    public R delete(@RequestBody Long[] uuids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        Service.deleteUserScanPath(getCurrentUserSchemaLabel(), uuids);
        return R.ok();
    }

}
