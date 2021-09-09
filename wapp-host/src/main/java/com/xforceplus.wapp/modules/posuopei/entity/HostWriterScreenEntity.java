package com.xforceplus.wapp.modules.posuopei.entity;

import com.aisinopdf.text.pdf.S;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;

/**
 * @author raymond.yan
 */
public class HostWriterScreenEntity   implements Serializable {
        private String id;//
        private String screen_name;//
        private  String system_name;
        private String login_store_id;
        private WriterScreenDataEntity data;//
        private  String matchNo;//

    public String getMatchNo() {
        return matchNo;
    }

    public void setMatchNo(String matchNo) {
        this.matchNo = matchNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getSystem_name() {
        return system_name;
    }

    public void setSystem_name(String system_name) {
        this.system_name = system_name;
    }

    public String getLogin_store_id() {
        return login_store_id;
    }

    public void setLogin_store_id(String login_store_id) {
        this.login_store_id = login_store_id;
    }

    public WriterScreenDataEntity getData() {
        return data;
    }

    public void setData(WriterScreenDataEntity data) {
        this.data = data;
    }
}
