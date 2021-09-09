package com.xforceplus.wapp.modules.sys.oauth2;

import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;
import com.xforceplus.wapp.modules.sys.service.ShiroService;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * 认证
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-05-20 14:00
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Realm.class);

    private final ShiroService shiroService;

    @Value("${token.expire_time}")
    private int expire;

    @Value("${mycat.default_schema_label}")
    private String mycatDefaultSchemaLabel;

    @Autowired
    public OAuth2Realm(ShiroService shiroService) {
        this.shiroService = shiroService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        
		try {
			
			 LOGGER.debug("token: "+ accessToken);
			//查询用户信息
			byte[] decode = Base64.decode(URLDecoder.decode(accessToken));
			String string = new String(decode,"UTF-8");
			UserEntity user = JsonUtil.fromJson(string, UserEntity.class);
			
			 //token失效
	        if (Long.parseLong(user.getExpireTime()) < System.currentTimeMillis()) {

	            if (LOGGER.isDebugEnabled()) {
	                LOGGER.debug("token失效，请重新登录...");
	            }

	            throw new IncorrectCredentialsException("token失效，请重新登录");
	        }

			if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("当前登陆人所属分库名:{}", user.getSchemaLabel());
	        }
	        //如果当前账号没有指定的分库名，则设为默认分库名
	        user.setSchemaLabel(firstNonNull(user.getSchemaLabel(), mycatDefaultSchemaLabel));

	        //账号锁定
	        if (!"1".equals(user.getStatus())) {
	            throw new LockedAccountException("账号已被锁定或不可用,请联系管理员");
	        }
	        //验证通过后，更新token失效时间
	        //当前时间
	        Date now = new Date();
	        //过期时间
	        Date expireTime = new Date(now.getTime() + expire * 1000);
	        long time = expireTime.getTime();
	        user.setExpireTime(String.valueOf(time));
	        return new SimpleAuthenticationInfo(user, accessToken, getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    /**
     * 授权(登录时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }
}
