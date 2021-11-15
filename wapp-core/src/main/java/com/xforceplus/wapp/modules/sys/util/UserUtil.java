package com.xforceplus.wapp.modules.sys.util;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import org.apache.shiro.SecurityUtils;

import java.util.Optional;

/**
 * 获取当前登录用户信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月9日 下午9:42:26
 */
public abstract class UserUtil {
    private static final Long DEFAULT_USER_ID = 0L;
    private static final String SYSTEM_USER_NAME="系统";

    private UserUtil(){}

    public static UserEntity getUser() {
        return (UserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    public static Long getUserId() {
        return Optional.ofNullable(getUser()).map(x->Long.valueOf(x.getUserid())).orElse(DEFAULT_USER_ID);
    }


    public static String getUserName() {
        return Optional.ofNullable(getUser()).map(UserEntity::getUsername).orElse(SYSTEM_USER_NAME);
    }

    public static String getLoginName() {
        return Optional.ofNullable(getUser()).map(UserEntity::getLoginname).orElse(SYSTEM_USER_NAME);
    }

    public static Long getDeptId() {
        return Optional.ofNullable(getUser()).map(x->Long.valueOf(x.getDepno())).orElse(DEFAULT_USER_ID);
    }

}
