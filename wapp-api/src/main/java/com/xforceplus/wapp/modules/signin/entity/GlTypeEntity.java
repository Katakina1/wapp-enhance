package com.xforceplus.wapp.modules.signin.entity;

import java.util.Arrays;

public class GlTypeEntity {
    private static final long serialVersionUID = 1L;


    private String isdefault;

    private String rownumber;

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault;
    }

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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    private Integer id;

    private String glType;

    private String matchName1;

    private String matchName2;

    private Long[] ids;
    private String remark;
    private String glName;

    public String getGlName() {
        return glName;
    }

    public void setGlName(String glName) {
        this.glName = glName;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    //sheet行
    private Integer row;


    public Long[] getIds() {
        return (ids == null) ? null : Arrays.copyOf(ids, ids.length);
    }

    public void setIds(Long[] ids) {
        this.ids = ids == null ? null : Arrays.copyOf(ids, ids.length);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getGlType() {
        return glType;
    }

    public void setGlType(String glType) {
        this.glType = glType;
    }

    public String getMatchName1() {
        return matchName1;
    }

    public void setMatchName1(String matchName1) {
        this.matchName1 = matchName1;
    }

    public String getMatchName2() {
        return matchName2;
    }

    public void setMatchName2(String matchName2) {
        this.matchName2 = matchName2;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
