package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.AccountCentRequest;
import com.xforceplus.wapp.modules.base.entity.AccountCentResponse;

/**
 * Created by Daily.zhang on 2018/04/23.
 */
public interface AccountCentService {

    /**
     * 保存用户信息
     * "username":"用户账号",
     * "pwd":"登录密码",
     * "phoneNum":"手机号",
     * "userMail":"用户邮箱"
     */
    AccountCentResponse saveUser(AccountCentRequest request);

    /**
     * 修改用户信息
     * "username":"用户账号",
     * "oldPwd":"原密码",  updatePwdType 为 1 时必填
     * "newPwd":"新密码",  updatePwdType 为 1 时必填
     * "phoneNum":"手机号",
     * "userMail":"用户邮箱",
     * "updatePwdType":"是否修改密码 0 否 1是"
     */
    AccountCentResponse updateUser(AccountCentRequest request);

    /**
     * 单用户查询    登录需要调用
     * "username":"用户账号",
     * "pwd":"登录密码"
     */
    AccountCentResponse queryUser(AccountCentRequest request);

    /**
     * 删除用户
     * "username":"用户账号"
     */
    AccountCentResponse deleteUser(AccountCentRequest request);

    /**
     * 重置用户密码
     * "username":"用户账号",
     * "pwd":"新密码"
     */
    AccountCentResponse resetPwd(AccountCentRequest request);
}
