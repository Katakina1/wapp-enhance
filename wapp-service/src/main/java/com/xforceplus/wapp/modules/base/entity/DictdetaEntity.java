package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Daily.zhang on 2018/04/18.
 */
@Getter
@Setter
public class DictdetaEntity extends BaseEntity  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 字典明细表id
     */
    private Integer dictid;

    /**
     * 字典类型关联id
     */
    private Integer dicttype;

    /**
     * 明细业务字典名称
     */
    private String dictname;

    /**
     * 排序
     */
    private String sortno;

    /**
     * 状态
     */
    private String status;

    /**
     * 明细业务字典编码
     */
    private String dictcode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    /**
     * 修改人
     */
    private String lastModifyBy;

    public Date getCreateTime() {
        return DateUtils.obtainValidDate(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = DateUtils.obtainValidDate(createTime);
    }

    public Date getLastModifyTime() {
        return DateUtils.obtainValidDate(this.lastModifyTime);
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = DateUtils.obtainValidDate(lastModifyTime);
    }
}
