package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.dao.StaffDao;
import com.xforceplus.wapp.modules.base.dao.UserRoleDao;
import com.xforceplus.wapp.modules.base.entity.*;
import com.xforceplus.wapp.modules.base.export.CostCenterImport;
import com.xforceplus.wapp.modules.base.export.StaffImport;
import com.xforceplus.wapp.modules.base.service.StaffService;
import com.xforceplus.wapp.modules.base.service.UserRoleService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Daily.zhang on 2018/04/17.
 */
@Service("staffService")
public class StaffServiceImpl implements StaffService {
    private static final Logger LOGGER= getLogger(StaffServiceImpl.class);
    @Autowired
    StaffDao staffDao;

    @Override
    public List<Staff> queryList(Map<String, Object> params) {
        return staffDao.queryList(params);
    }

    @Override
    public int queryCount(Map<String, Object> params) {
        return staffDao.queryCount(params);
    }

    @Override
    public Integer updateStaff(Staff staff) {
        return staffDao.updateStaff(staff);
    }

    @Override
    public Integer deleteStaff(int id) {
        return staffDao.deleteStaff(id);
    }

    @Override
    public Integer insertStaff(Staff staff) {
        return staffDao.insertStaff(staff);
    }

    @Override
    public List<UserEntity> queryList1(Map<String, Object> params) {
        return staffDao.queryList1(params);
    }

    @Override
    public int queryCount1(Map<String, Object> params) {
        return staffDao.queryCount1(params);
    }

    @Override
    public List<UserEntity> queryList2(Map<String, Object> params) {
        return staffDao.queryList2(params);
    }

    @Override
    public int queryCount2(Map<String, Object> params) {
        return staffDao.queryCount2(params);
    }

    @Override
    public Integer deleteVendor(String[] ids) {
        return staffDao.deleteVendor(ids);
    }

    @Override
    public Integer addVendor(String staff, String[] vendors) {
        return staffDao.addVendor(staff, vendors);
    }

    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final StaffImport staffImport = new StaffImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<Staff> staffs = staffImport.analysisExcel();
            if(staffs.size()>500){
                LOGGER.info("excel数据不能超过500条");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel数据不能超过500条！");
                return map;
            }
            List<Integer> orgids = null;
            Integer orgid = null;
            Set set = new HashSet();
            for(Staff staff:staffs){
                String vendors = staff.getVendors();
                String staffNo = staff.getStaffNo();
                String email = staff.getEmail();
                String Winid = staff.getWinID();
                String jvs = staff.getJvs();
                set.add(staffNo+email);//用于判断重复数据
                int count = staffDao.findStaff(staffNo,email);
                if(count>0){
                    LOGGER.info("员工信息已存在  staffNo:"+staffNo+"--email:"+email);
                    map.put("success", Boolean.FALSE);
                    map.put("reason", "员工信息已存在  staffNo:"+staffNo+"--email:"+email);
                    return map;
                }

                if(vendors!=null && vendors.length()>0) {
                    if(vendors.contains(",")){
                            String [] vendor = vendors.split(",");
                            int vendorCount = staffDao.findVendor(vendor);
                            if(vendor.length!=vendorCount){
                                LOGGER.info("员工信息绑定供应商关系错误");
                                map.put("success", Boolean.FALSE);
                                map.put("reason", "员工信息绑定供应商关系错误  请核实供应商信息是否存在。员工号:"+staffNo+"--邮箱:"+email);
                                return map;
                            }
                    }else{
                        int vendorCount = staffDao.findVendorBySingle(vendors);
                        if(vendorCount==0){
                            LOGGER.info("员工信息绑定供应商关系错误");
                            map.put("success", Boolean.FALSE);
                            map.put("reason", "员工信息绑定供应商关系错误  请核实供应商信息是否存在。员工号:"+staffNo+"--邮箱:"+email);
                            return map;
                        }
                    }
                }
                if(jvs!=null&&jvs.length()>0){  //校验jv是否数据库中已经存在
                    if(jvs.contains(",")){
                            String jv [] = jvs.split(",");
                            orgids = staffDao.findOrgIdsByJv(jv);
                            if(orgids.size()!= jv.length){
                                LOGGER.info("员工信息绑定JV错误");
                                map.put("success", Boolean.FALSE);
                                map.put("reason", "员工信息绑定JV错误  请核实JV信息是否存在。员工号:"+staffNo+"--邮箱:"+email);
                                return map;
                            }else{
                                staff.setOrgids(orgids);
                            }
                    }else{
                             orgid = staffDao.findOrgIdSingle(jvs);
                            if(orgid==null){
                                LOGGER.info("员工信息绑定JV错误");
                                map.put("success", Boolean.FALSE);
                                map.put("reason", "员工信息绑定JV错误  请核实JV信息是否存在。员工号:"+staffNo+"--邮箱:"+email);
                                return map;
                            }else {staff.setOrgid(orgid);}
                    }
                }
                if(!Winid.startsWith("2") || Winid.length()!=9){
                    LOGGER.info("员工信息WinID错误");
                    map.put("success", Boolean.FALSE);
                    map.put("reason", "员工信息WinID错误。员工号:"+staffNo+"--邮箱:"+email);
                    return map;
                }
            }
            if(set.size()!=staffs.size()){
                LOGGER.info("excel存在重复数据！");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel存在重复数据！");
                return map;
            }
            if (!staffs.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<Staff>> entityMap =StaffImportData(staffs);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<Staff>> StaffImportData(List<Staff> staffs){
        //返回值
        final Map<String, List<Staff>> map = newHashMap();
        //导入成功的数据集
        final List<Staff> successEntityList = newArrayList();
        //导入失败的数据集
        final List<Staff> errorEntityList = newArrayList();

        staffs.forEach(staff -> {
            String winid = staff.getWinID();
            String staffNo = staff.getStaffNo();
            String email = staff.getEmail();
            String gfTaxno=staff.getGfTaxNo();
            String jvs = staff.getJvs();
            if (!winid.isEmpty() && !staffNo.isEmpty() && !email.isEmpty() &&!jvs.isEmpty()) {
                successEntityList.add(staff);
            } else {
                errorEntityList.add(staff);
            }
        });
        if(errorEntityList.size()==0){
            //如果都校验通过，保存入库
            for(Staff staff: staffs){
                //处理业务逻辑
                //保存入库
                staffDao.insertStaff(staff);
                String venders = staff.getVendors();
                String staffNo = staff.getStaffNo();
                if(venders!=null&&venders.length()>0){
                    String [] vender = venders.split(",");
                    staffDao.insertStaffAndVender(staffNo,vender);
                }
                String jvs = staff.getJvs();
                if(jvs!=null&&jvs.length()>0){
                    String[] jv = jvs.split(",");
                    staffDao.insertJv(staff); //插入之前校验jv是否都存在数据库中
                }

            }

        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);

        return map;
    }

    @Override
    public void deleteStaff(String staffNo) {
        //删除jv关系
        staffDao.deleteJvByNo(staffNo);
        //删除vendor关系表
        staffDao.deleteStaffAndVendor(staffNo);
        //删除员工表
        staffDao.deleteStaffByNo(staffNo);

    }


    @Override
    public List<JvEntity> queryJv(Query query) {
        return staffDao.queryJv(query);
    }

    @Override
    public Integer queryJvCount(Query query) {
        return staffDao.queryJvCount(query);
    }

    @Override
    public List<JvEntity> queryNotAddJv(Query query) {
        return staffDao.queryNotAddJv(query);
    }

    @Override
    public Integer queryNotAddJvCount(Query query) {
        return staffDao.queryNotAddJvCount(query);
    }

    @Override
    public Integer addJvs(String staff, String[] jvs) {
        return staffDao.addJvs(staff, jvs);
    }

    @Override
    public Integer deleteJv(String[] ids) {
        return staffDao.deleteJv(ids);
    }

    @Override
    public List<CostEntity> queryCost(Query query) {
        return staffDao.queryCost(query);
    }

    @Override
    public Integer queryCostCount(Query query) {
        return staffDao.queryCostCount(query);
    }

    @Override
    public void insertCost(CostEntity cost) {
        staffDao.insertCost(cost);
    }

    @Override
    public void deleteCost(String[] ids) {
        staffDao.deleteCost(ids);
    }

    /**
     * 导入成本中心信息
     * @param multipartFile
     * @return
     */
    @Override
    public Map<String, Object> parseCbzxExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final CostCenterImport costImport = new CostCenterImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<CostCenterEntity> costs = costImport.analysisExcel();

            if(costs.size()>500){
                LOGGER.info("excel数据不能超过500条");
                map.put("success", Boolean.FALSE);
                map.put("reason", "excel数据不能超过500条！");
                return map;
            }

            if (!costs.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<CostCenterEntity>> entityMap =CostImportData(costs);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<CostCenterEntity>> CostImportData(List<CostCenterEntity> costs){
        //返回值
        final Map<String, List<CostCenterEntity>> map = newHashMap();
        //导入成功的数据集
        final List<CostCenterEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<CostCenterEntity> errorEntityList = newArrayList();
        int count =0;
        costs.forEach(cost -> {
            String jv = cost.getJv();
            String costCenter = cost.getCostCenters();
            if (!jv.isEmpty() && !costCenter.isEmpty()) {
                try{
                    int orgid = staffDao.findOrgIdSingle(jv);
                    successEntityList.add(cost);
                }catch (Exception e){
                    e.printStackTrace();
                    errorEntityList.add(cost);
                }
            } else {
                errorEntityList.add(cost);
            }
        });
        if(errorEntityList.size()==0){
            //如果都校验通过，保存入库
            for(CostCenterEntity cost: costs){
                //处理业务逻辑
                //保存入库
                String singleCost = cost.getCostCenters();
                if(singleCost.endsWith("/")){
                    singleCost = singleCost.substring(0,singleCost.length()-1);
                }
                String jv = cost.getJv();
                int orgid = staffDao.findOrgIdSingle(jv);
                String [] arr = singleCost.split("/");
                List<String> list=new ArrayList<>();
                for (int i=0;i < arr.length;i++){
                  count = staffDao.jvRepetition(orgid,arr[i]);
                  if(count==0){
                      list.add(arr[i]);
                  }
                }
                String[] arr1 = list.toArray(new String[list.size()]);
                if(arr1.length != 0){
                    staffDao.insertOrgIdAndCosts(orgid, arr1);
             }

            }

        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);

        return map;
    }
    
    public int isExists(Integer orgid,String costcode) {
    	return staffDao.jvRepetition(orgid, costcode);
    }
}
