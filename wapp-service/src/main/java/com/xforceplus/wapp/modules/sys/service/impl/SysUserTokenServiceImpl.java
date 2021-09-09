package com.xforceplus.wapp.modules.sys.service.impl;

import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.sys.dao.SysUserTokenDao;
import com.xforceplus.wapp.modules.sys.entity.SysUserTokenEntity;
import com.xforceplus.wapp.modules.sys.oauth2.TokenGenerator;
import com.xforceplus.wapp.modules.sys.service.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;


//@Service("sysUserTokenService")
public class SysUserTokenServiceImpl implements SysUserTokenService {

    @Value("${token.expire_time}")
    private int expire;

    private final SysUserTokenDao sysUserTokenDao;

    @Autowired
    public SysUserTokenServiceImpl(SysUserTokenDao sysUserTokenDao) {
        this.sysUserTokenDao = sysUserTokenDao;
    }

    @Override
    public SysUserTokenEntity queryByUserId(String schemaLabel, Long userId) {
        return sysUserTokenDao.queryByUserId(schemaLabel, userId);
    }

    @Override
    public SysUserTokenEntity queryByToken(String schemaLabel, String token) {
        return sysUserTokenDao.queryByToken(schemaLabel, token);
    }

    @Override
    public void save(String schemaLabel, SysUserTokenEntity token) {
        sysUserTokenDao.saveToken(schemaLabel, token);
    }

    @Override
    public void update(String schemaLabel, SysUserTokenEntity token) {
        sysUserTokenDao.updateToken(schemaLabel, token);
    }


    @Override
    public R createToken(String schemaLabel, long userId) {
        //生成一个token
        String token = TokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + expire * 1000);

        //判断是否生成过token
        SysUserTokenEntity tokenEntity = queryByUserId(schemaLabel, userId);
        if (tokenEntity == null) {
            tokenEntity = new SysUserTokenEntity();
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
