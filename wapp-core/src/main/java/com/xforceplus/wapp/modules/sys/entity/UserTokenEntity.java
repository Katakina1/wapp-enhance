package com.xforceplus.wapp.modules.sys.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统用户Token
 */
@Getter
@Setter
public class UserTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //用户ID
    private Long userId;
    //token
    private String token;
    //过期时间
    private Date expireTime;
    //更新时间
    private Date updateTime;

    public Date getExpireTime() {
        return DateUtils.obtainValidDate(this.expireTime);
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = DateUtils.obtainValidDate(expireTime);
    }

    public Date getUpdateTime() {
        return DateUtils.obtainValidDate(this.updateTime);
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = DateUtils.obtainValidDate(updateTime);
    }
}
