package com.xforceplus.wapp.modules.system.controller;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller公共组件
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月9日 下午9:42:26
 */
public abstract class AbstractController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected UserEntity getUser() {
        return (UserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getUserId() {
        return getUser().getUserid().longValue();
    }


    protected String getUserName() {
        return getUser().getUsername();
    }
    
    protected String getLoginName() {
        return getUser().getLoginname();
    }

    protected Long getDeptId() {
        return Long.valueOf(getUser().getDepno());
    }

    /**
     * 获得当前用户所在的分库名
     *
     * @return 当前用户所在的分库名
     */
    protected String getCurrentUserSchemaLabel() {
        try{
            return getUser().getSchemaLabel();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
