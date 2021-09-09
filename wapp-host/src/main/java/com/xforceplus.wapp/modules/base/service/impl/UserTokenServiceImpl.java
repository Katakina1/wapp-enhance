package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.dao.UserTokenDao;
import com.xforceplus.wapp.modules.base.entity.UserTokenEntity;
import com.xforceplus.wapp.modules.base.service.UserTokenService;
import com.xforceplus.wapp.modules.sys.oauth2.TokenGenerator;
//import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service("userTokenService")
public class UserTokenServiceImpl implements UserTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTokenServiceImpl.class);

    @Value("${token.expire_time}")
    private int expire;

    private final UserTokenDao userTokenDao;

    @Autowired
    public UserTokenServiceImpl(UserTokenDao userTokenDao) {
        this.userTokenDao = userTokenDao;
    }

    @Override
    public UserTokenEntity queryByUserId(String schemaLabel, Long userId) {
        return userTokenDao.queryByUserId(schemaLabel, userId);
    }

    @Override
    public UserTokenEntity queryByToken(String schemaLabel, String token) {
        return userTokenDao.queryByToken(schemaLabel, token);
    }

    @Override
    public void save(String schemaLabel, UserTokenEntity token) {
        userTokenDao.saveToken(schemaLabel, token);
    }

    @Override
    public void update(String schemaLabel, UserTokenEntity token) {
        userTokenDao.updateToken(schemaLabel, token);
    }

    @Override
    public R createToken(String schemaLabel, long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);

        if (LOGGER.isDebugEnabled()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LOGGER.debug("用户:{}, 生成Token:{}, 过期时间:{}", userId, token, formatter.format(expireTime));
        }

        //判断是否生成过token
        UserTokenEntity tokenEntity = queryByUserId(schemaLabel, userId);
        if (tokenEntity == null) {
            tokenEntity = new UserTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //保存token
            save(schemaLabel, tokenEntity);
        } else {
            tokenEntity.setToken(token);
            tokenEntity.setUpdateTime(now);
            tokenEntity.setExpireTime(expireTime);

            //更新token
            update(schemaLabel, tokenEntity);
        }

        R r = R.ok().put("token", token).put("expire", expire);

        return r;
    }
}
