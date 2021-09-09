package com.xforceplus.wapp.modules.posuopei.entity;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/23
 * Time:18:17
*/

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import com.xforceplus.wapp.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DetailVehicleEntity extends AbstractBaseDomain {


    private static final long serialVersionUID = -2781384226123262406L;

    @Override
    public Boolean isNullObject() {
        return Boolean.FALSE;
    }

    private Long id;
    private String uuid;
    private String buyerIdNum;
    private String vehicleType;
    private String factoryModel;
    private String productPlace;
    private String certificate;
    private String certificateImport;
    private String inspectionNum;
    private String engineNo;
    private String vehicleNo;
    private String phone;
    private String buyerBank;
    private String taxRate;
    private String taxBureauName;
    private String taxBureauBode;
    private String taxRecords;
    private String limitPeople;
    private String checkStatus;
    private String tonnage;
    private Date createDate;

    public Date getCreateDate() {
        return DateUtils.obtainValidDate(this.createDate);
    }

    public void setCreateDate(Date createDate) {
        this.createDate = DateUtils.obtainValidDate(createDate);
    }
}
