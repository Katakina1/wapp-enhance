package com.xforceplus.wapp.modules.base.entity;

import java.io.Serializable;

public class AnnouncementUserMiddleEntity implements Serializable {
    private static final long serialVersionUID = -6653838336421959373L;
    private Long id;
    private Long announcementid;//公告表关联id
    private Long bigint;//供应商表关联id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnnouncementid() {
        return announcementid;
    }

    public void setAnnouncementid(Long announcementid) {
        this.announcementid = announcementid;
    }

    public Long getBigint() {
        return bigint;
    }

    public void setBigint(Long bigint) {
        this.bigint = bigint;
    }
}
