package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.utils.MD5Utils;
import com.xforceplus.wapp.modules.base.dao.BaseUserDao;
import com.xforceplus.wapp.modules.base.entity.AccountCentRequest;
import com.xforceplus.wapp.modules.base.entity.AccountCentResponse;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.AccountCentService;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Service("baseUserService")
public class BaseUserServiceImpl implements BaseUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUserServiceImpl.class);

    private final BaseUserDao baseUserDao;

    private final AccountCentService accountCentService;

    @Value("${AccountCent.isService}")
    private Boolean isAccountCent;

    @Autowired
    public BaseUserServiceImpl(BaseUserDao baseUserDao, AccountCentService accountCentService) {
        this.baseUserDao = baseUserDao;
        this.accountCentService = accountCentService;
    }

    @Override
    public List<Long> queryAllMenuId(String schemaLabel, Long userId) {
        return baseUserDao.queryAllMenuId(schemaLabel, userId);
    }

    @Override
    public UserEntity queryByUserName(String schemaLabel, String username) {
        return baseUserDao.queryByUserName(schemaLabel, username);
    }

    @Override
    public UserEntity queryObject(String schemaLabel, Long userId) {
        return baseUserDao.queryObject(schemaLabel, userId);
    }

    @Override
    public List<UserEntity> queryList(String schemaLabel, UserEntity entity) {
        return baseUserDao.queryList(schemaLabel, entity);
    }

    @Override
    public int queryTotal(String schemaLabel, UserEntity entity) {
        return baseUserDao.queryTotal(schemaLabel, entity);
    }

    @Override
    public int userTotal(String schemaLabel, Long[] orgIds) {

        return baseUserDao.userTotal(schemaLabel, orgIds);
    }

    @Override
    public List<UserEntity> queryDataAccessList(String schemaLabel, UserEntity entity) {
        return baseUserDao.queryDataAccessList(schemaLabel, entity);
    }

    @Override
    public int queryDataAccessTotal(String schemaLabel, UserEntity entity) {
        return baseUserDao.queryDataAccessTotal(schemaLabel, entity);
    }

    @Override
    public String getOrgtype(Long userid) {
        return baseUserDao.getOrgtype(userid);
    }

    @Override
    public String getOrgtypes(Long userid) {
        return baseUserDao.getOrgtypes(userid);
    }

    @Override
    public Map<String, Object> save(String schemaLabel, UserEntity user) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", 0);

        if (user == null) {
            LOGGER.info("保存的用户信息对象为null!");
            return map;
        }
        if (isAccountCent) {
            AccountCentRequest request = new AccountCentRequest();
            request.setUsername(user.getLoginname());
            request.setPwd(user.getPlainpassword());
            request.setPhoneNum(user.getCellphone());
            request.setUserMail(user.getEmail());
            AccountCentResponse response = accountCentService.saveUser(request);
            if (!"0000".equals(response.getCode())) {
                LOGGER.warn("调用账户中心服务保存用户信息失败。 message:{} userinfo:{}", response.getMessage(), user.toString());

                map.put("code", 1);
                map.put("msg", response.getMessage());

                return map;
            }
        } else {
            LOGGER.info("账号服务中心开关为：{}", isAccountCent);
        }
        if (StringUtils.isNotBlank(user.getPlainpassword())) {
            String encodePW = MD5Utils.encode(user.getPlainpassword());
            user.setPassword(encodePW);
        }
        try {
            if (StringUtils.isNotBlank(user.getStatus())) {
                user.setStatus("1");
            }
           final int r = baseUserDao.save(schemaLabel, user);
            if (r == 1) {
                LOGGER.info("保存用户信息 {} 成功!", user.toString());
            } else {
                LOGGER.info("保存用户信息 {} 失败!", user.toString());
            }
        } catch (DuplicateKeyException de) {
            map.put("code", 1);
            map.put("msg",  "用户数据已存在!");
            LOGGER.info("用户数据已存在!", de);
        } catch (Exception e) {
            LOGGER.error("保存用户信息异常！cause:{} userInfo:{}", e.getCause(), user.toString(), e);
            map.put("code", 2);
            map.put("msg",  "更新用户信息异常");
        }
        return map;
    }

    @Override
    public Map<String, Object> update(String schemaLabel, UserEntity user) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", 0);

        //如果手机号或者邮箱有一个字段有值就需要调用账号中心接口
        if (isAccountCent) {
            if (StringUtils.isNotBlank(user.getCellphone()) || StringUtils.isNotBlank(user.getEmail())) {
                //查询拿到用户信息(登录名称)  以及可能需要的邮箱或者手机号
                try {
                    UserEntity entity = queryObject(schemaLabel, Long.valueOf(user.getUserid()));
                    AccountCentRequest request = new AccountCentRequest();
                    request.setUsername(entity.getLoginname());
                    request.setUpdatePwdType("0");
                    if (StringUtils.isBlank(user.getCellphone())) {
                        request.setPhoneNum(entity.getCellphone());
                    } else {
                        request.setPhoneNum(user.getCellphone());
                    }
                    if (StringUtils.isBlank(user.getEmail())) {
                        request.setUserMail(entity.getEmail());
                    } else {
                        request.setUserMail(user.getEmail());
                    }
                    AccountCentResponse response = accountCentService.updateUser(request);
                    if (!"0000".equals(response.getCode())) {
                        LOGGER.warn("调用账户中心服务更新用户信息失败。 message:{} userinfo:{}", response.getMessage(), request.toString());
                        map.put("code", 1);
                        map.put("msg", response.getMessage());

                        return map;
                    }
                } catch (Exception e) {
                    LOGGER.error("更新账号中心信息前查询用户信息异常！userid:{} cause:{}", user.getUserid(), e.getCause(), e);

                    map.put("code", 2);
                    map.put("msg", "更新账号中心信息前查询用户信息异常");

                    return map;
                }
            }
        } else {
            LOGGER.info("账号服务中心开关为：{}", isAccountCent);
        }

        try {
            if (StringUtils.isNotBlank(user.getPlainpassword())) {
                String encodePW = MD5Utils.encode(user.getPlainpassword());
                user.setPassword(encodePW);
            }
            baseUserDao.update(schemaLabel, user);
        } catch (Exception e) {
            LOGGER.error("更新用户信息异常。cause:{} user:{}", e.getCause(), user, e);
            map.put("code", 3);
            map.put("msg",  "更新用户信息异常");
        }
        return map;
    }

    @Override
    public int delete(String schemaLabel, Long userid) {
        int r = 0;
        if (isAccountCent) {
            AccountCentResponse response = deleteAccoCentUser(schemaLabel, userid);
            if (!"0000".equals(response.getCode())) {
                LOGGER.warn("账号中心删除用户信息失败！userid:{} cause:{}", userid, response.getMessage());
                return r;
            }
        } else {
            LOGGER.info("账号服务中心开关为：{}", isAccountCent);
        }
        try {
            r = baseUserDao.delete(schemaLabel, userid);
        } catch (Exception e) {
            LOGGER.error("删除用户信息异常! userid:{} cause:{}", userid, e.getCause(), e);
        }
        return r;
    }

    private AccountCentResponse deleteAccoCentUser(String schemaLabel, Long userid) {
        AccountCentResponse response = new AccountCentResponse();
        try {
            UserEntity entity = queryObject(schemaLabel, userid);
            AccountCentRequest request = new AccountCentRequest();
            request.setUsername(entity.getLoginname());
            response = accountCentService.deleteUser(request);
        } catch (Exception e) {
            LOGGER.error("删除用户信息异常！userid:{} cause:{}", userid, e.getCause(), e);
            response.setMessage(e.getCause().toString());
            response.setCode("9999");
        }
        return response;
    }

    @Override
    public int modifyPassword(String schemaLabel, Long userid, String oldPW, String newPW) {
        int r = 0;
        if (isAccountCent) {
            UserEntity entity = queryObject(schemaLabel, userid);
            AccountCentRequest request = new AccountCentRequest();
            request.setUsername(entity.getLoginname());
            request.setUserMail(entity.getEmail());
            request.setPhoneNum(entity.getCellphone());
            request.setUpdatePwdType("1");
            request.setOldPwd(oldPW);
            request.setNewPwd(newPW);
            AccountCentResponse response = accountCentService.updateUser(request);
            if (!"0000".equals(response.getCode())) {
                LOGGER.warn("账号中心修改用户密码失败！request:{} cause:{}", request.toString(), response.getMessage());
                return r;
            }
        } else {
            LOGGER.info("账号服务中心开关为：{}", isAccountCent);
        }
        try {
            String enodePW = MD5Utils.encode(newPW);
            r = baseUserDao.updatePassword(schemaLabel, userid, oldPW, newPW, enodePW);
        } catch (Exception e) {
            LOGGER.error("修改用户密码异常。causet:{} userid:{} oldPW:{} newPW:{}。", e.getCause(), userid, oldPW, newPW, e);
        }
        return r;
    }
}
