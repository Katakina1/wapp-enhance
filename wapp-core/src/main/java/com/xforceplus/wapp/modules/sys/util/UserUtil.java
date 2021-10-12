package com.xforceplus.wapp.modules.sys.util;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import org.apache.shiro.SecurityUtils;

/**
 * 获取当前登录用户信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月9日 下午9:42:26
 */
public abstract class UserUtil {

    public static UserEntity getUser() {
        return (UserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    public static Long getUserId() {
        return getUser().getUserid().longValue();
    }


    public static String getUserName() {
        return getUser().getUsername();
    }

    public static String getLoginName() {
        return getUser().getLoginname();
    }

    public static Long getDeptId() {
        return Long.valueOf(getUser().getDepno());
    }

}
