package com.xforceplus.wapp.modules.sys.util;

import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.UnknownSessionException;

import java.util.Optional;

/**
 * 获取当前登录用户信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月9日 下午9:42:26
 */
@Slf4j
public abstract class UserUtil {
    private static final Long DEFAULT_USER_ID = 0L;
    private static final String SYSTEM_USER_NAME="系统";
    private static final UserEntity defaultUser=new UserEntity();
    static {
        defaultUser.setUserid(DEFAULT_USER_ID.intValue());
        defaultUser.setUsername(SYSTEM_USER_NAME);
        defaultUser.setLoginname(SYSTEM_USER_NAME);
        defaultUser.setDepno(String.valueOf(DEFAULT_USER_ID));
    }

    private UserUtil(){}

    public static UserEntity getUser() {
        try {
            return (UserEntity) SecurityUtils.getSubject().getPrincipal();
        }catch (UnavailableSecurityManagerException e){
            log.error("未获取到用户信息或未登录");
        } catch (UnknownSessionException use) {
            log.error("sessionId 已过期:{}", use.getMessage());
        } catch (Exception ee) {
            log.error("获取用户信息异常:", ee);
        }
        return null;
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
