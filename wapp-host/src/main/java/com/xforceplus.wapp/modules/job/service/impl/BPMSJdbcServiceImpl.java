package com.xforceplus.wapp.modules.job.service.impl;


import com.xforceplus.wapp.common.utils.JDBCUtils;
import com.xforceplus.wapp.common.utils.MD5Utils;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.job.dao.VendorMasterDao;

import com.xforceplus.wapp.modules.job.pojo.vendorMaster.Table1;
import com.xforceplus.wapp.modules.job.pojo.vendorMaster.VendorLog;

import com.xforceplus.wapp.modules.job.service.BPMSJdbcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.ResultSet;

import java.util.*;

@PropertySource(value = {"classpath:config.properties"})
@Service
public class BPMSJdbcServiceImpl implements BPMSJdbcService {
    private static Logger logger = LoggerFactory.getLogger(BPMSJdbcServiceImpl.class);
    @Autowired
    private VendorMasterDao vendorMasterDao;
    /**
     * 执行VenderMaster任务
     */
    @Override
    public void executeVenderJDBC() {
        logger.info("--------获取VendorMaster数据jdbc开始------");
        // 保存数据库记录

        String sql = "SELECT COUNT(1) FROM VendorInfo";
        try {
            ResultSet rs = JDBCUtils.select(sql);
            while (rs.next()) {
              int num =  rs.getInt(1);
              for (int i=0;i<=num;){
                  int start= i;
                  i=1000+i;
                  int over = i;
                  logger.info(new Date()+"开始"+start);
                List<Table1> VendorList = parseJdbc(start, over);
                System.out.println(VendorList.size());
                saveOrgAndUser(VendorList);
                logger.info(new Date()+"结束"+over);
              }

            }
            //插入机构用户信息
            logger.info("--------获取VendorMaster数据jdbc结束------");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存vendorMaster信息到数据库中
     * @param vendorList
     */
    private void saveOrgAndUser(List<Table1> vendorList) {

         if(vendorList.size()>0) {
             Integer shangpinroleid = vendorMasterDao.findRoleIdByCode("WRMSP");
             Integer feiyongroleid = vendorMasterDao.findRoleIdByCode("WRMFY");
             for (Table1 data : vendorList) {
                 // 遍历集合，先拿供应商号查询用户表，再去查机构表是否有
                 UserEntity usercount = vendorMasterDao.findUserById(data.getVendorNumber());
                 if("TRUE".equals(data.getDeletionStatus())){
                     data.setUserStatus("4");
                 }else{
                     data.setUserStatus("1");
                 }
                 if (usercount != null) {
                     //更新用户信息

                     //int count = vendorMasterDao.findOrgByTaxnoAndId(data.getTaxNumber(), usercount.getOrgid()+"",data.getBankAccount());
                     //if (count > 0) {
                            //更新原来用户信息
                           //vendorMasterDao.updateUser(data,"interface");
                     //}else{
                         //更新机构信息 以及 用户信息
                         data.setTaxNumber(data.getTaxNumber().replace("k","K"));
                         vendorMasterDao.updateOrg(data, usercount.getOrgid());
                         vendorMasterDao.updateUser(data,"interface");
                     //}
                 }else{
                     // 没有用户信息， 查询机构信息是否存在
                        OrganizationEntity count = vendorMasterDao.findOrgByTaxno(data.getTaxNumber(),data.getVendorName());
                        if(count!=null) {
                            // 有 -- 插入用户信息
                            data.setOrgid(Integer.valueOf(count.getOrgid()+""));
                            String encodePW = MD5Utils.encode(data.getVendorNumber());

                            vendorMasterDao.insertUser(data,encodePW,"interface");
                            Integer userid = data.getUserid();
                            Integer roid = 0;
                            //插入角色表
                            if(data.getTypeCode().startsWith("P")){
                                 roid = shangpinroleid;
                            }
                            if(data.getTypeCode().startsWith("E")){
                                 roid = feiyongroleid;
                            }
                            vendorMasterDao.insertRole(userid,roid);
                        }else {
                            // 无 -- 插入机构信息  再插入用户信息
                            int length = data.getTaxNumber().length();
                            String orgcode="";
                            if(length>6) {
                                orgcode = data.getTaxNumber().substring(length - 6, length);
                            }
                            String company="walmart";
                            data.setTaxNumber(data.getTaxNumber().replace("k","K"));
                            int status = vendorMasterDao.insertOrgInfo(data,"interface",orgcode,company);
                            data.setOrgid(data.getOrgid());
                            String encodePW = MD5Utils.encode(data.getVendorNumber());
                            vendorMasterDao.insertUser(data,encodePW,"interface");
                            Integer userid = data.getUserid();
                            Integer roid = 0;
                            //插入角色表
                            if(data.getTypeCode().startsWith("P")){ //商品
                                roid = shangpinroleid;
                            }
                            if(data.getTypeCode().startsWith("E")){ //费用
                                roid = feiyongroleid;
                            }
                            vendorMasterDao.insertRole(userid,roid);
                        }
                 }
             }
         }


    }

    /**
     * 解析vendorMaster数据
     * @return
     */
    private List<Table1> parseJdbc(int start,int over) {
        List<Table1> list = new LinkedList<Table1>();
        String sql = "SELECT * FROM(SELECT VendorNumber,VendorName,TypeCode,TaxNumber,TelephoneNumber1,FinanceFaxNumber1,ContactPerson1,EmailAddress1,Address1,BankName,BankAccount,HoldStatus,DeletionStatus,City,ROW_NUMBER () OVER ( ORDER BY id ) AS rownum from VendorInfo WHERE 1 = 1 ) a WHERE rownum > "+start+" AND rownum <= "+over;
        try {
            ResultSet rs = JDBCUtils.select(sql);
            while (rs.next()) {
                Table1 table = new Table1();
                String no = rs.getString("VendorNumber");
                String zero = "";
                if (no.length() < 6) {
                    int e = 6 - no.length();
                    for (int i = 0; i < e; i++) {
                        zero = zero + "0";
                    }
                    no = zero + no;
                }
                table.setVendorNumber(no); //供应商号
                table.setVendorName(rs.getString("VendorName")); //供应商名称
                table.setTypeCode(rs.getString("TypeCode"));  //供应商类型
                table.setTaxNumber(rs.getString("TaxNumber")); //供应商税号
                table.setFinanceTelephoneNumber1(rs.getString("TelephoneNumber1")); //财务电话号码
                table.setFinanceFaxNumber1(rs.getString("FinanceFaxNumber1")); //财务传真号码
                table.setFinanceContactPerson1(rs.getString("ContactPerson1")); //财务联系人
                table.setEmailAddress1(rs.getString("EmailAddress1")); //财务人邮箱
                table.setAddress1(rs.getString("Address1")); //财务人邮箱
                table.setBankName(rs.getString("BankName"));
                table.setBankAccount(rs.getString("BankAccount"));
                table.setHoldStatus(rs.getString("HoldStatus"));
                table.setDeletionStatus(rs.getString("DeletionStatus"));
                table.setCity(fromCity(rs.getString("City")));
                list.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    private  String fromCity(String city){
        String [] zw={"北京市",
                "上海市",
                "天津市",
                "重庆市",
                "河北省",
                "山西省",
                "内蒙古自治区 ",
                "辽宁省",
                "吉林省",
                "黑龙江省 ",
                "江苏省",
                "浙江省",
                "安徽省",
                "福建省",
                "江西省",
                "山东省",
                "河南省",
                "湖北省",
                "湖南省",
                "广东省",
                "广西壮族自治区",
                "海南省",
                "四川省",
                "贵州省",
                "云南省",
                "西藏自治区",
                "陕西省",
                "甘肃省",
                "青海省",
                "宁夏回族自治区",
                "新疆维吾尔自治区",
                "台湾省",
                "香港特别行政区",
                "澳门特别行政区"};
        String [] py={"BJ",
                "SH",
                "TJ",
                "CQ",
                "HJ",
                "SJ",
                "NM",
                "LN",
                "JL",
                "HL",
                "JS",
                "ZJ",
                "AH",
                "FJ",
                "JX",
                "SD",
                "HY",
                "HB",
                "HX",
                "GD",
                "GX",
                "HI",
                "SC",
                "GZ",
                "YN",
                "XZ",
                "SW",
                "GS",
                "QH",
                "NX",
                "XJ",
                "TW",
                "HK",
                "MO"};

        for(int i=0;i<zw.length;i++){
            if(city!=null) {
                if (city.indexOf(py[i]) != -1) {
                    city = city.replace(py[i], zw[i]);
                }
            }
        }
        return city;
    }
}
