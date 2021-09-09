package com.xforceplus.wapp.modules.base.entity;

import java.io.Serializable;
import java.util.Date;

public class DataAmendEntity implements Serializable {

    private static final long serialVersionUID = 1L;
   private int id;
   private String dictdetaNo;
   private Integer  onOff;
   private String userName;
   private Date amendDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDictdetaNo() {
        return dictdetaNo;
    }

    public void setDictdetaNo(String dictdetaNo) {
        this.dictdetaNo = dictdetaNo;
    }

    public Integer getOnOff() {
        return onOff;
    }

    public void setOnOff(Integer onOff) {
        this.onOff = onOff;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getAmendDate() {
        return amendDate;
    }

    public void setAmendDate(Date amendDate) {
        this.amendDate = amendDate;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
