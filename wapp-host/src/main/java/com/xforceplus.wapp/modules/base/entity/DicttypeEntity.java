package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务字典主表
 *
 * Created by Daily.zhang on 2018/04/18.
 */
@Getter
@Setter
public class DicttypeEntity extends BaseEntity  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer dicttypeid;

    public Integer getDicttypeid() {
		return dicttypeid;
	}

	public void setDicttypeid(Integer dicttypeid) {
		this.dicttypeid = dicttypeid;
	}

	public String getDicttypename() {
		return dicttypename;
	}

	public void setDicttypename(String dicttypename) {
		this.dicttypename = dicttypename;
	}

	public String getDicttypedesc() {
		return dicttypedesc;
	}

	public void setDicttypedesc(String dicttypedesc) {
		this.dicttypedesc = dicttypedesc;
	}

	public String getDicttypecode() {
		return dicttypecode;
	}

	public void setDicttypecode(String dicttypecode) {
		this.dicttypecode = dicttypecode;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getLastModifyBy() {
		return lastModifyBy;
	}

	public void setLastModifyBy(String lastModifyBy) {
		this.lastModifyBy = lastModifyBy;
	}

	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 字典名称
     */
    private String dicttypename;

    /**
     * 字典描述
     */
    private String dicttypedesc;

    /**
     * 字典编码
     */
    private String dicttypecode;

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

    /**
     * 系统编码
     */
    private String sysCode;

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
