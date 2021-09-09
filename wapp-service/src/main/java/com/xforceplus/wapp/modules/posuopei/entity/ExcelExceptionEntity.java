package com.xforceplus.wapp.modules.posuopei.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author raymond.yan
 */
public class ExcelExceptionEntity implements Serializable {
    private static final long serialVersionUID = 4606091480479180608L;

    private Integer row;
    private List<ErrorDataEntity> errorDataLists;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public List<ErrorDataEntity> getErrorDataLists() {
        return errorDataLists;
    }

    public void setErrorDataLists(List<ErrorDataEntity> errorDataLists) {
        this.errorDataLists = errorDataLists;
    }

    public ExcelExceptionEntity(Integer row, List<ErrorDataEntity> errorDataLists) {
        this.row = row;
        this.errorDataLists = errorDataLists;
    }
}
