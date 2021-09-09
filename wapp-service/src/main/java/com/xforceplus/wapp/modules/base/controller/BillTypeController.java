package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.BillTypeEntity;

import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import com.xforceplus.wapp.modules.base.service.BillTypeService;

import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jingsong.mao on 2018/08/10.
 * 业务类型管理控制层
 */
@RestController
public class BillTypeController extends AbstractController {

    @Autowired
    private BillTypeService Service;
    @Autowired
    private UserBilltypeService userBilltypeService;


    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/billtype/list")
    public R list(BillTypeEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();

        //分页查询起始值
        Entity.setOffset((page - 1) * Entity.getLimit());

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<BillTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);

        int total = Service.queryTotal(getCurrentUserSchemaLabel(), Entity);

        PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/billtype/listNoPage")
    public R listNoPage(BillTypeEntity Entity) {



        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<BillTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);



        return R.ok().put("page", EntityList);
    }

    /**
     * 根据业务类型id获取业务类型信息
     */
    @SysLog("业务类型信息查询")
    @RequestMapping("/base/billtype/getBillTypeInfoById/{billtypeid}")
    public R selectSingle(@PathVariable Long billtypeid) {

        return R.ok().put("Info", Service.queryObject(getCurrentUserSchemaLabel(), billtypeid));
    }

    /**
     * 更新保存信息
     */
    @SysLog("业务类型信息保存")
    @RequestMapping("/base/billtype/saveBillType")
    public R save(@RequestBody BillTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getBilltypename(), entity.getBilltypecode(),null);
        if(num > 0)
        {
        	return R.error("保存失败：业务类型编码和业务类型名称唯一，不可重复");
        }

        Service.save(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }

    /**
     * 更新业务类型信息
     */
    @SysLog("业务类型信息更新")
    @RequestMapping("/base/billtype/updateBillType")
    public R update(@RequestBody BillTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getBilltypename(), entity.getBilltypecode(),entity.getBilltypeid());
        if(num > 0)
        {
        	return R.error("保存失败：业务类型编码和业务类型名称唯一，不可重复");
        }

        Service.update(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }
    
    /**
     * 删除业务类型信息(批量)
     */
    @SysLog("业务类型信息删除")
    @RequestMapping("/base/billtype/deleteBillType")
    public R delete(@RequestBody UserBilltypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

        int  total = userBilltypeService.queryListBillCount(getCurrentUserSchemaLabel(), entity);

        //如果统计结果大于0，表示这些票据已经授权给用户
        if (total > 0) {
            return R.error("删除失败,请先取消账户关联!");
        }

       Service.deleteBatch(getCurrentUserSchemaLabel(), entity.getIds());

        return R.ok();
    }
}
