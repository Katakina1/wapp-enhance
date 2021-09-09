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
