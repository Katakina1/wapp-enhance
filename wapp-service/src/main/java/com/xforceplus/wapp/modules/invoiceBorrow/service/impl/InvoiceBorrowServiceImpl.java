package com.xforceplus.wapp.modules.invoiceBorrow.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.collect.dao.NoDetailedInvoiceDao;
import com.xforceplus.wapp.modules.invoiceBorrow.dao.InvoiceBorrowDao;
import com.xforceplus.wapp.modules.invoiceBorrow.entity.BorrowEntity;
import com.xforceplus.wapp.modules.invoiceBorrow.export.BorrowInvoiceGhImport;
import com.xforceplus.wapp.modules.invoiceBorrow.export.BorrowInvoiceImport;
import com.xforceplus.wapp.modules.invoiceBorrow.service.InvoiceBorrowService;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.pack.export.BindNumberImport;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.FpghExcelEntity;
import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.xforceplus.wapp.modules.report.entity.JyjlcxExcelEntity;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 发票借阅业务层实现
 */
@Service
@Transactional
public class InvoiceBorrowServiceImpl implements InvoiceBorrowService {

    private final InvoiceBorrowDao invoiceBorrowDao;

    @Autowired
    public InvoiceBorrowServiceImpl(InvoiceBorrowDao invoiceBorrowDao) {
        this.invoiceBorrowDao = invoiceBorrowDao;
    }

    @Override
    public PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceBorrowList(Map<String, Object> map) {
        final PagedQueryResult<ComprehensiveInvoiceQueryEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = invoiceBorrowDao.getInvoiceBorrowCount(map);

        //需要返回的集合
        List<ComprehensiveInvoiceQueryEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = invoiceBorrowDao.queryInvoiceBorrowList(map);
            final Map<String, BigDecimal> totalMap = invoiceBorrowDao.getInvoiceBorrowSumAmount(map);
            pagedQueryResult.setSummationTotalAmount(totalMap.get("summationTotalAmount"));
            pagedQueryResult.setSummationTaxAmount(totalMap.get("summationTaxAmount"));
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public void save(BorrowEntity borrowEntity) {
        for(String uuid :borrowEntity.getIds()) {
            ComprehensiveInvoiceQueryEntity record = invoiceBorrowDao.getDataByuuid(uuid);
            borrowEntity.setInvoiceCode(record.getInvoiceCode());
            borrowEntity.setInvoiceNo(record.getInvoiceNo());
            //保存借阅记录
            int result = invoiceBorrowDao.save(borrowEntity);
            if(result>0){
                //如果操作类型为0-借阅，修改底账表借阅状态为1-已借阅,如果操作类型为1-归还，修改底账表借阅状态为0-未借阅
                if("0".equals(borrowEntity.getOperateType())) {
                    invoiceBorrowDao.updateBorrowStatus(uuid, "1", borrowEntity.getBorrowDate());
                } else{
                    invoiceBorrowDao.updateBorrowStatus(uuid, "0", borrowEntity.getBorrowDate());
                }
            }
        }
    }
    public void saveBorr(BorrowEntity borrowEntity) {
            ComprehensiveInvoiceQueryEntity record = invoiceBorrowDao.getDataByuuid(borrowEntity.getUuid());
            borrowEntity.setInvoiceCode(record.getInvoiceCode());
            borrowEntity.setInvoiceNo(record.getInvoiceNo());
            //保存借阅记录
            int result = invoiceBorrowDao.save(borrowEntity);
            if(result>0){
                //如果操作类型为0-借阅，修改底账表借阅状态为1-已借阅,如果操作类型为1-归还，修改底账表借阅状态为0-未借阅
                if("0".equals(borrowEntity.getOperateType())) {
                    invoiceBorrowDao.updateBorrowjy(borrowEntity.getUuid(), "1",borrowEntity.getBorrowDate(),borrowEntity.getBorrowUser(),borrowEntity.getBorrowReason(),borrowEntity.getBorrowDept());
                } else{
                    invoiceBorrowDao.updateBorrowgh(borrowEntity.getUuid(), "0",borrowEntity.getBorrowDate(),borrowEntity.getBorrowUser());
                }
            }

    }
    @Override
    public PagedQueryResult<BorrowEntity> queryBorrowRecordList(Map<String, Object> map) {
        final PagedQueryResult<BorrowEntity> pagedQueryResult = new PagedQueryResult<>();
        final Integer count = invoiceBorrowDao.getBorrowRecordCount(map);

        //需要返回的集合
        List<BorrowEntity> infoArrayList = newArrayList();
        if (count > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = invoiceBorrowDao.queryBorrowRecordList(map);
        }
        pagedQueryResult.setTotalCount(count);
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public List<FpjyExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<FpjyExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号"	,"JVCODE"	,"COMPANYCODE",	"发票代码",	"发票号码",	"开票日期",	"购方名称",	"销方名称"	,"供应商号",	"金额",	"税额",	"税率",	"价税合计",	"凭证号"	,"扫描流水号"	,"装订册号",	"装箱号"	,"EPS_NO","ID","借阅人","借阅日期","借阅原因","部门"};
        reflect(list2,s);
        for (int i = 0; i < list.size(); i++) {
            FpjyExcelEntity entity2=new FpjyExcelEntity();
            ComprehensiveInvoiceQueryEntity entity=list.get(i);
            //序号
           entity2.setCell0( i+1+"");
            //jvcode
           entity2.setCell1( entity.getJvCode()== null ? "" :entity.getJvCode());
            //companycode
           entity2.setCell2( entity.getCompanyCode()== null ? "" :entity.getCompanyCode());
            //发票代码
           entity2.setCell3(  entity.getInvoiceCode());
            //发票号码
           entity2.setCell4(  entity.getInvoiceNo());
            //开票日期
           entity2.setCell5(  formatDateString(entity.getInvoiceDate()));
            //购方名称
           entity2.setCell6(  entity.getGfName());
            //销方名称
           entity2.setCell7( entity.getXfName());
            //供应商号
           entity2.setCell8(entity.getVenderId()==null?"":entity.getVenderId());
            //金额
           entity2.setCell9(String.valueOf( entity.getInvoiceAmount()==null?0:entity.getInvoiceAmount()));
            //税额
            if(entity.getTaxAmount()!=null) {
               entity2.setCell10(  String.valueOf(entity.getTaxAmount()));
            } else {
               entity2.setCell10(  "");
            }
            //税率
           entity2.setCell11( entity.getTaxRate() == null ? "" : entity.getTaxRate().stripTrailingZeros().toPlainString());
            //价税合计
            if(entity.getTotalAmount()!=null) {
               entity2.setCell12( String.valueOf( entity.getTotalAmount()));
            } else{
               entity2.setCell12(  "");
            }
            //凭证号
           entity2.setCell13( entity.getCertificateNo()== null ? "" :entity.getCertificateNo());
            //扫描流水号
           entity2.setCell14( entity.getScanningSeriano()== null ? "" :entity.getScanningSeriano());
            //装订册号
           entity2.setCell15( entity.getBbindingno()== null ? "" :entity.getBbindingno());
            //装箱号
           entity2.setCell16( entity.getPackingno()== null ? "" :entity.getPackingno());
            //eps号
           entity2.setCell17( entity.getEpsNo()== null ? "" :entity.getEpsNo());
           entity2.setCell18(entity.getUuid());
           entity2.setCell19("");
           entity2.setCell20("");
           entity2.setCell21("");
            entity2.setCell22("");
            list2.add(entity2);
        }


        return list2;
    }

    private void reflect(List<FpjyExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FpjyExcelEntity e=new FpjyExcelEntity();
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            String name = fields[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = e.getClass().getMethod("get" + name);
                String value = (String) m.invoke(e); // 调用getter方法获取属性值
                if (value == null) {
                    m = e.getClass().getMethod("set"+name,String.class);
                    m.invoke(e, s[i]);
                }
            }
        }
        list2.add(e);
    }

    @Override
    public List<FpghExcelEntity> transformExcle2(List<ComprehensiveInvoiceQueryEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<FpghExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号"	,"JVCODE"	,"COMPANYCODE",	"发票代码",	"发票号码",	"开票日期",	"购方名称",	"销方名称"	,"供应商号",	"金额",	"税额",	"税率",	"价税合计",	"凭证号"	,"扫描流水号"	,"装订册号",	"装箱号"	,"EPS_NO","ID","归还人","归还日期"};
        reflect2(list2,s);
        for (int i = 0; i < list.size(); i++) {
            FpghExcelEntity entity2=new FpghExcelEntity();
            ComprehensiveInvoiceQueryEntity entity=list.get(i);
            //序号
            entity2.setCell0( i+1+"");
            //jvcode
            entity2.setCell1( entity.getJvCode()== null ? "" :entity.getJvCode());
            //companycode
            entity2.setCell2( entity.getCompanyCode()== null ? "" :entity.getCompanyCode());
            //发票代码
            entity2.setCell3(  entity.getInvoiceCode());
            //发票号码
            entity2.setCell4(  entity.getInvoiceNo());
            //开票日期
            entity2.setCell5(  formatDateString(entity.getInvoiceDate()));
            //购方名称
            entity2.setCell6(  entity.getGfName());
            //销方名称
            entity2.setCell7( entity.getXfName());
            //供应商号
            entity2.setCell8(entity.getVenderId()==null?"":entity.getVenderId());
            //金额
            entity2.setCell9(String.valueOf( entity.getInvoiceAmount()==null?0:entity.getInvoiceAmount()));
            //税额
            if(entity.getTaxAmount()!=null) {
                entity2.setCell10(  String.valueOf(entity.getTaxAmount()));
            } else {
                entity2.setCell10(  "");
            }
            //税率
            entity2.setCell11( entity.getTaxRate() == null ? "" : entity.getTaxRate().stripTrailingZeros().toPlainString());
            //价税合计
            if(entity.getTotalAmount()!=null) {
                entity2.setCell12( String.valueOf( entity.getTotalAmount()));
            } else{
                entity2.setCell12(  "");
            }
            //凭证号
            entity2.setCell13( entity.getCertificateNo()== null ? "" :entity.getCertificateNo());
            //扫描流水号
            entity2.setCell14( entity.getScanningSeriano()== null ? "" :entity.getScanningSeriano());
            //装订册号
            entity2.setCell15( entity.getBbindingno()== null ? "" :entity.getBbindingno());
            //装箱号
            entity2.setCell16( entity.getPackingno()== null ? "" :entity.getPackingno());
            //eps号
            entity2.setCell17( entity.getEpsNo()== null ? "" :entity.getEpsNo());
            entity2.setCell18(entity.getUuid());
            entity2.setCell19("");
            entity2.setCell20("");
            list2.add(entity2);
        }


        return list2;
    }
    private void reflect2(List<FpghExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FpghExcelEntity e=new FpghExcelEntity();
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            String name = fields[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = e.getClass().getMethod("get" + name);
                String value = (String) m.invoke(e); // 调用getter方法获取属性值
                if (value == null) {
                    m = e.getClass().getMethod("set"+name,String.class);
                    m.invoke(e, s[i]);
                }
            }
        }
        list2.add(e);
    }
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }













    @Override
    public List<JyjlcxExcelEntity> transformExcle3(List<BorrowEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<JyjlcxExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号"	,"JVCODE"	,"COMPANYCODE","凭证号",		"发票号码",	"装箱号",	"装订册号",	"借阅日期"	,"借阅人",	"借阅原因",	"借阅部门",	"归还日期",	"归还人",	"供应商号"	,"EPS_NO"	};
        reflect3(list2,s);
        for (int i = 0; i < list.size(); i++) {
            JyjlcxExcelEntity entity2=new JyjlcxExcelEntity();
            BorrowEntity entity=list.get(i);
            //序号
            entity2.setCell0( i+1+"");
            //jvcode
            entity2.setCell1(entity.getJvCode()== null ? "" :entity.getJvCode());
            //companycode
            entity2.setCell2(entity.getCompanyCode()== null ? "" :entity.getCompanyCode());
            //凭证号
            entity2.setCell3(entity.getCertificateNo()== null ? "" :entity.getCertificateNo());
            //发票号码
            entity2.setCell4( entity.getInvoiceNo());
            //装箱号
            entity2.setCell5(entity.getPackingno()== null ? "" :entity.getPackingno());
            //装订册号
            entity2.setCell6(entity.getBbindingno()== null ? "" :entity.getBbindingno());
            //借阅日期
            if("0".equals(entity.getOperateType())) {
                entity2.setCell7( formatDateString(entity.getBorrowDate()));
            } else{
                entity2.setCell7( "");
            }
            //借阅人
            if("0".equals(entity.getOperateType())) {
                entity2.setCell8( entity.getBorrowUser());
            } else{
                entity2.setCell8( "");
            }
            //借阅原因
            if("0".equals(entity.getOperateType())) {
                entity2.setCell9( entity.getBorrowReason());
            } else {
                entity2.setCell9( "");
            }
            //借阅部门
            if("0".equals(entity.getOperateType())) {
                entity2.setCell10( entity.getBorrowDept());
            } else{
                entity2.setCell10( "");
            }
            //归还日期
            if("1".equals(entity.getOperateType())) {
                entity2.setCell11( formatDateString(entity.getBorrowDate()));
            } else{
                entity2.setCell11( "");
            }
            //归还人
            if("1".equals(entity.getOperateType())) {
                entity2.setCell12(entity.getBorrowUser());
            } else{
                entity2.setCell12("");
            }
            //供应商号
            entity2.setCell13(entity.getVenderId()==null?"":entity.getVenderId());

            //eps号
            entity2.setCell14(entity.getEpsNo()==null?"":entity.getEpsNo());
            
            

            list2.add(entity2);
        }


        return list2;
    }
    private void reflect3(List<JyjlcxExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JyjlcxExcelEntity e=new JyjlcxExcelEntity();
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            String name = fields[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = e.getClass().getMethod("get" + name);
                String value = (String) m.invoke(e); // 调用getter方法获取属性值
                if (value == null) {
                    m = e.getClass().getMethod("set"+name,String.class);
                    m.invoke(e, s[i]);
                }
            }
        }
        list2.add(e);
    }

    @Override
    public Map<String, Object> parseExcel(MultipartFile multipartFile) {
        //进入解析excel方法
        final BorrowInvoiceImport enterPackageNumberImport = new BorrowInvoiceImport(multipartFile);
        Map<String, Object> map = newHashMap();
        try {
            map = enterPackageNumberImport.analysisExcel();
            if(map.get("status").equals("false")){
                map.put("success", Boolean.FALSE);
                map.put("reason", "导入数据不能超过5000条！");
            }
            List<BorrowEntity> redInvoiceList = (List<BorrowEntity>)map.get("enjoySubsidedList");

            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<BorrowEntity>> entityMap =RedInvoiceImportData(redInvoiceList);
                map.put("reason", entityMap.get("successEntityList").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            e.printStackTrace();
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }
    @Override
    public Map<String, Object> parseExcelGh(MultipartFile multipartFile) {
        //进入解析excel方法
        final BorrowInvoiceGhImport enterPackageNumberImport = new BorrowInvoiceGhImport(multipartFile);
        Map<String, Object> map = newHashMap();
        try {
            map = enterPackageNumberImport.analysisExcel();
            if(map.get("status").equals("false")){
                map.put("success", Boolean.FALSE);
                map.put("reason", "导入数据不能超过5000条！");
            }
            List<BorrowEntity> redInvoiceList = (List<BorrowEntity>)map.get("enjoySubsidedList");

            if (!redInvoiceList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<BorrowEntity>> entityMap =RedInvoiceImportData(redInvoiceList);
                map.put("reason", entityMap.get("successEntityList").size());
                map.put("errorCount", entityMap.get("errorEntityList").size());
            } else {
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            e.printStackTrace();
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }
    private Map<String, List<BorrowEntity>> RedInvoiceImportData(List<BorrowEntity> redInvoiceList){
        //返回值
        final Map<String, List<BorrowEntity>> map = newHashMap();
        //导入成功的数据集
        final List<BorrowEntity> successEntityList = Lists.newArrayList();
        //导入失败的数据集
        final List<BorrowEntity> errorEntityList = Lists.newArrayList();
        //导入失败的数据集1
        final List<BorrowEntity> errorEntityList1 = Lists.newArrayList();

        redInvoiceList.forEach(redInvoiceData -> {
            String borrowUser = redInvoiceData.getBorrowUser();
            if(borrowUser.isEmpty()){
                errorEntityList.add(redInvoiceData);
            } else {
                successEntityList.add(redInvoiceData);
            }
        });

        //如果都校验通过，保存入库
        for(BorrowEntity red: successEntityList){
            saveBorr(red);
        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);
        return map;
    }



}
