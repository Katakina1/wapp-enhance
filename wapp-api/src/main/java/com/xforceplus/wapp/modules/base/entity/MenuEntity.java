package com.xforceplus.wapp.modules.base.entity;

import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 功能菜单
 * <p>
 * Created by Daily.zhang on 2018/04/13.
 */
@Getter
@Setter
public class MenuEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户所在分库名
     */
    private String schemaLabel;

    public String getSchemaLabel() {
		return schemaLabel;
	}

	public void setSchemaLabel(String schemaLabel) {
		this.schemaLabel = schemaLabel;
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public String getMenucode() {
		return menucode;
	}

	public void setMenucode(String menucode) {
		this.menucode = menucode;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getOrgid() {
		return orgid;
	}

	public void setOrgid(Integer orgid) {
		this.orgid = orgid;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public String getMenulabel() {
		return menulabel;
	}

	public void setMenulabel(String menulabel) {
		this.menulabel = menulabel;
	}

	public String getMenuaction() {
		return menuaction;
	}

	public void setMenuaction(String menuaction) {
		this.menuaction = menuaction;
	}

	public Integer getMenulevel() {
		return menulevel;
	}

	public void setMenulevel(Integer menulevel) {
		this.menulevel = menulevel;
	}

	public String getMenudesc() {
		return menudesc;
	}

	public void setMenudesc(String menudesc) {
		this.menudesc = menudesc;
	}

	public String getIsfunc() {
		return isfunc;
	}

	public void setIsfunc(String isfunc) {
		this.isfunc = isfunc;
	}

	public String getIsbottom() {
		return isbottom;
	}

	public void setIsbottom(String isbottom) {
		this.isbottom = isbottom;
	}

	public Integer getSubsysid() {
		return subsysid;
	}

	public void setSubsysid(Integer subsysid) {
		this.subsysid = subsysid;
	}

	public String getSortno() {
		return sortno;
	}

	public void setSortno(String sortno) {
		this.sortno = sortno;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public String getMenulayer() {
		return menulayer;
	}

	public void setMenulayer(String menulayer) {
		this.menulayer = menulayer;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getMenuldStr() {
		return menuldStr;
	}

	public void setMenuldStr(String menuldStr) {
		this.menuldStr = menuldStr;
	}

	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

	public List<MenuEntity> getChildren() {
		return children;
	}

	public void setChildren(List<MenuEntity> children) {
		this.children = children;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 菜单id
     */
    private Integer menuid;

    /**
     * 功能菜单编码
     */
    private String menucode;

    /**
     * 上级菜单id
     */
    private Integer parentid;

    /**
     * 所属机构id
     */
    private Integer orgid;

    /**
     * 功能菜单名称
     */
    private String menuname;

    /**
     * 菜单显示名称
     */
    private String menulabel;

    /**
     * 功能调用入口
     */
    private String menuaction;

    /**
     * 菜单层级
     */
    private Integer menulevel;

    /**
     * 功能菜单描述
     */
    private String menudesc;

    /**
     * 是否功能 0 - 是 1 - 否
     */
    private String isfunc;

    /**
     * 是否有下级 0 - 无 1 -有
     */
    private String isbottom;

    public List<?> getSubList() {
        return subList;
    }

    public void setSubList(List<?> subList) {
        this.subList = subList;
    }

    /**
     * 子系统id
     */
    private Integer subsysid;

    /**
     * 排序字段
     */
    private String sortno;

    /**
     * 显示图片
     */
    private String image;

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
     * 菜单层级代码
     */
    private String menulayer;

    /**
     * 子菜单集合(仅前端展示用)
     */
    private List<?> subList;

    private Long[] menuIds;

    //是否为叶子节点
    private Boolean isLeaf = Boolean.FALSE;

    private String menuldStr;

    //是否打开节点,默认关闭
    private Boolean open = Boolean.FALSE;

    //子节点
    private List<MenuEntity> children;

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

    public Long[] getMenuIds() {
        return (menuIds == null) ? null : Arrays.copyOf(menuIds, menuIds.length);
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds == null ? null : Arrays.copyOf(menuIds, menuIds.length);
    }
}
