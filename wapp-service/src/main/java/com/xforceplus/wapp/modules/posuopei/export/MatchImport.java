package com.xforceplus.wapp.modules.posuopei.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * 批量导入匹配关系
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class MatchImport extends AbstractImportExcel {
    private static final Logger LOGGER=getLogger(MatchImport.class);
    private MultipartFile file;

    public MatchImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<MatchEntity> analysisExcel(String f) throws Exception {
        List<MatchEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        if(rowCount>500){
            throw new ExcelException(999,"超过500条不可导入");
        }
        int index = 0;
        List<InvoiceEntity> invoiceEntityList=Lists.newArrayList();
        List<ClaimEntity> claimEntityList=Lists.newArrayList();
        List<PoEntity> poEntityList=Lists.newArrayList();
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）

        //校验组号
        Set<String> set=new HashSet<String>();
        List<String> list=Lists.newArrayList();
        for (int i = 1; i < rowCount+1; i++) {
            if(StringUtils.isBlank(getCellData(sheet.getRow(i),0))){
                if(!isRowEmpty(sheet.getRow(i))){
                    LOGGER.info("导入匹配关系：组号格式错误");
                    throw new ExcelException(999,"导入匹配关系：组号格式错误");
                }

            }else{
                set.add(getCellData(sheet.getRow(i),0));
                list.add(getCellData(sheet.getRow(i),0));
            }

        }

        List<String> setToList=new ArrayList<>(set);
        if(list.size()>0&&set.size()>0){

            for(int j=0;j<setToList.size();j++) {
                int count=0;
                int flag=-1;
                int n;
                int m=-1;
                for ( n = 0; n < list.size(); n++) {
                    if (list.get(n).equals(setToList.get(j))) {
                        count++;
                        if (flag == -1) {
                            //起始位置
                            flag = n;
                        }
                        if (n == (list.size()-1)&&m==-1) {
                            m = n+1;
                        }

                    } else {
                        if (flag != -1) {
                            if(m==-1){
                                m = n;
                            }

                        }
                    }
                }
                if(count!=(m-flag)){
                    LOGGER.info("导入匹配关系：组号格式错误");
                    throw new ExcelException(999,"导入匹配关系：组号格式错误");
                }
            }

        }else{
            LOGGER.info("导入匹配关系：无数据");
            throw new Exception("无数据");
        }
        //校验结束

        for (int i = 1; i < rowCount+1; i++) {

            final Row row = sheet.getRow(i);
//            Row oldRow;
            Row nextRow;
//            if(i>1){
//                oldRow=sheet.getRow(i-1);
//            }else {
//                oldRow=sheet.getRow(i);
//            }
            if(i<rowCount){
                nextRow= sheet.getRow(i+1);
            }else{
                nextRow= null;
            }
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
//                String oldArrayCode=getCellData(oldRow,0);
                String arrayCode=getCellData(row,0);
                String nextArrayCode="Aa104238";
                if(nextRow!=null){
                    nextArrayCode=getCellData(nextRow,0);
                }
                MatchEntity matchEntity=new MatchEntity();
                matchEntity.setEmpty(true);
                final List<Object> objectList = createImportCertificationEntity(row, f);

                if(objectList==null){
                    LOGGER.info("导入匹配关系：数据格式错误");
                    int n=i+1;
                    throw new ExcelException(999,"导入匹配关系：第"+n+"行数据格式错误");
                }
                if(objectList.size()>0){
                    objectList.forEach(object->{
                        if(object instanceof InvoiceEntity){
                            invoiceEntityList.add((InvoiceEntity) object);
                        }else if(object instanceof ClaimEntity){
                            claimEntityList.add((ClaimEntity) object);
                        }else if(object instanceof PoEntity){
                            poEntityList.add((PoEntity) object);
                        }
                    });

                }else {
                    LOGGER.info("导入匹配关系：未识别到有效数据");
                    matchEntity.setEmpty(false);
                    throw new ExcelException(999,"导入匹配关系：未识别到有效数据");
                }
                if((!nextArrayCode.equals(arrayCode)||nextArrayCode.equals("Aa104238"))){

                    if(matchEntity.getEmpty()){
                        List<ClaimEntity> list1=Lists.newArrayList();
                        List<PoEntity> list2=Lists.newArrayList();
                        List<InvoiceEntity> list3=Lists.newArrayList();
                        list1.addAll(claimEntityList);
                        list2.addAll(poEntityList);
                        list3.addAll(invoiceEntityList);
                        matchEntity.setClaimEntityList(list1);
                        matchEntity.setInvoiceEntityList(list3);
                        matchEntity.setPoEntityList(list2);
                        if(matchEntity.getPoEntityList().size()<=0){
                            LOGGER.info("导入匹配关系：数据格式错误");
                            int n=i+1;
                            throw new ExcelException(999,"导入匹配关系：第"+n+"行缺少订单");
                        }
                        matchEntity.setVenderid(matchEntity.getPoEntityList().get(0).getVenderid());
                        matchEntity.setPrintcode(arrayCode);
                        enjoySubsidedList.add(matchEntity);
                    }
                    claimEntityList.clear();
                    invoiceEntityList.clear();
                    poEntityList.clear();

                }



            }
        }

        return enjoySubsidedList;
    }

    /**
     * 构建导入认证实体
     * @param row 行
     * @return 导入认证实体
     */
    private List<Object> createImportCertificationEntity(Row row, String f) {
//        final MatchEntity matchEntity = new MatchEntity();


        //序号
//        matchEntity.setIndexNo(index);
        //数据校验状态
//        matchEntity.setEmpty(true);

        //发票组号(第二层校验)
        final String arrayCode=getCellData(row, 0);
        //购货单位代码
        final String jvcode=getCellData(row, 1);
        //
        //发票代码
        final String invoiceCode = getCellData(row, 2);
        //发票号码
        final String invoiceNo = getCellData(row, 3);
        //开票日期
        final String invoiceDate = getCellData(row, 4);
        //税率
        final String taxRate = getCellData(row, 5);
        //金额
        final String amount = getCellData(row, 6);
        //税额
        final String taxAmount = getCellData(row, 7);
        //价税合计
        final String totalAmount = getCellData(row, 8);
        //验证码(普票必填)
        final String checkNo = getCellData(row, 9);
        //订单号
        final String poCode = getCellData(row, 10);
        //索赔号
        final String cliamno = getCellData(row, 11);


        //订单或者索赔金额
        final String theAmount = getCellData(row, 12);






        Boolean flag = this.checkInvoiceMessage(jvcode,invoiceCode, invoiceNo, invoiceDate, amount, totalAmount, taxRate, taxAmount,checkNo,theAmount,poCode,cliamno);
        InvoiceEntity invoiceEntity=new InvoiceEntity();
        ClaimEntity claimEntity=new ClaimEntity();
        PoEntity poEntity=new PoEntity();
        claimEntity.setEmpty(false);
        poEntity.setEmpty(false);
         invoiceEntity.setEmpty(flag);   
        if(!StringUtils.isEmpty(poCode)&&StringUtils.isEmpty(cliamno)&&!StringUtils.isEmpty(theAmount)){
            poEntity.setPocode(poCode);
            poEntity.setAmountunpaid(new BigDecimal(theAmount));
            poEntity.setEmpty(true);
            poEntity.setJvcode(jvcode);
            if("5".equals(f)){
                poEntity.setVenderid(getCellData(row, 13));
                if(StringUtils.isEmpty(poEntity.getVenderid())){
                    LOGGER.info("导入匹配关系：缺少供应商号！");
                    return null;
                }

            }

        }else if(StringUtils.isEmpty(poCode)&&!StringUtils.isEmpty(cliamno)&&!StringUtils.isEmpty(theAmount)){

            claimEntity.setEmpty(true);
            claimEntity.setJvcode(jvcode);
            claimEntity.setClaimno(cliamno);
            claimEntity.setClaimAmount(new BigDecimal(theAmount));
            if("5".equals(f)){

                claimEntity.setVenderid(getCellData(row, 13));
                if(StringUtils.isEmpty(claimEntity.getVenderid())){
                    LOGGER.info("导入匹配关系：缺少供应商号！");
                    return null;
                }

            }
        }
//
        if (flag) {
            if(!StringUtils.isEmpty(invoiceCode)) {
                invoiceEntity.setJvcode(jvcode);
                invoiceEntity.setCheckNo(checkNo);
                invoiceEntity.setInvoiceCode(invoiceCode);
                invoiceEntity.setInvoiceNo(invoiceNo);
                if (StringUtils.isNotEmpty(invoiceDate)) {
                    invoiceEntity.setInvoiceDate(invoiceDate);
                } else {
                    invoiceEntity.setEmpty(false);
                }
                if("5".equals(f)){
                    invoiceEntity.setVenderid(getCellData(row, 13));
                    if(StringUtils.isEmpty(invoiceEntity.getVenderid())){
                        LOGGER.info("导入匹配关系：缺少供应商号！");
                        return null;
                    }

                }
                invoiceEntity.setInvoiceAmount(new BigDecimal(amount));
                invoiceEntity.setTotalAmount(new BigDecimal(totalAmount));
                invoiceEntity.setTaxAmount(new BigDecimal(taxAmount));
                invoiceEntity.setTaxRate(new BigDecimal(taxRate));

            }else{
                invoiceEntity.setEmpty(false);
            }
        }else {
            LOGGER.info("导入匹配关系：数据格式错误");
            return null;
        }

        List<Object> finalList= Lists.newArrayList();
        if(invoiceEntity.getEmpty()){
            finalList.add(invoiceEntity);
        }
        if(claimEntity.getEmpty()){
            finalList.add(claimEntity);
        }

        if(poEntity.getEmpty()){
            finalList.add(poEntity);
        }

        return finalList;

    }

    private Boolean checkInvoiceMessage(String jvcode,String invoiceCode, String invoiceNo, String invoiceDate, String invoiceAmount, String totalAmount, String taxRate, String taxAmount,String checkNo ,String theAmount,String poCode,String claimno) {
        Boolean flag=true;

        if(StringUtils.isEmpty(jvcode)){
            return false;
        }
        if(!jvcode.matches("[A-Z]*")){
            return false;
        }
        if(!StringUtils.isEmpty(poCode)&&!StringUtils.isEmpty(claimno)){
            return false;
        }
        if(StringUtils.isEmpty(poCode)&&StringUtils.isEmpty(claimno)){
            if(!StringUtils.isEmpty(theAmount)){
                return false;
            }

        }

        if((!StringUtils.isEmpty(poCode))||(!StringUtils.isEmpty(claimno))){
            if(StringUtils.isEmpty(theAmount)||(!CommonUtil.isValidNum(theAmount,"^-?[0-9]+(.[0-9]{1,2})?$"))){
                return false;
            }

        }
        if("04".equals(CommonUtil.getFplx(invoiceCode))){
            if(checkNo.length()!=6){
                return false;
            }
        }
        if(StringUtils.isEmpty(invoiceCode)&&StringUtils.isEmpty(invoiceNo)&&StringUtils.isEmpty(invoiceDate)&&StringUtils.isEmpty(invoiceAmount)&&StringUtils.isEmpty(totalAmount)&&StringUtils.isEmpty(taxRate)&&StringUtils.isEmpty(taxAmount)&&StringUtils.isEmpty(checkNo)){
          //只有索赔订单
            if(StringUtils.isEmpty(poCode)&&StringUtils.isEmpty(claimno)) {
                return false;
            }
        }else{
            //有发票有索赔、有发票无索赔
            if(!CommonUtil.isValidNum(invoiceCode,"^(\\d{10}|\\d{12})$")) {
                flag=false;
            }else if(!CommonUtil.isValidNum(invoiceNo,"^[\\d]{8}$")){
                flag=false;
            }else if(!CommonUtil.isValidNum(taxRate,"^[0-9]*$")){
                if(!"1.5".equals(taxRate)){
                    flag=false;
                }
            }else if((!CommonUtil.isValidNum(totalAmount,"^[0-9]+(.[0-9]{1,2})?$"))||(!CommonUtil.isValidNum(taxAmount,"^[0-9]+(.[0-9]{1,2})?$"))){
                flag=false;
            }else if((!CommonUtil.isValidNum(invoiceDate,"^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$"))){
                flag=false;
            }else if(!("04".equals(CommonUtil.getFplx(invoiceCode))||"01".equals(CommonUtil.getFplx(invoiceCode)))){
                flag=false;
            }
            if(flag){
                if(StringUtils.isEmpty(invoiceAmount)){
                    return false;
                }
                BigDecimal amount=new BigDecimal(invoiceAmount);
                if(StringUtils.isEmpty(taxRate)){
                            return false;
                }

                BigDecimal rate=new BigDecimal(taxRate).divide(new BigDecimal(100));;
                BigDecimal taxAmount1=new BigDecimal(taxAmount);
                BigDecimal rest=taxAmount1.subtract(amount.multiply(rate).setScale(2,BigDecimal.ROUND_HALF_UP));
                if("04".equals(CommonUtil.getFplx(invoiceCode))){
                    if(amount.add(taxAmount1).compareTo(new BigDecimal(totalAmount))!=0){
                        flag=false;
                    }
                }else{
                    if(rest.compareTo(BigDecimal.ZERO)>0){
                        flag=!(rest.subtract(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0);

                    }else{
                        flag=rest.add(new BigDecimal(0.05)).compareTo(BigDecimal.ZERO)>0;
                    }
                    if(amount.add(taxAmount1).compareTo(new BigDecimal(totalAmount))!=0){
                        flag=false;
                    }
                }

                Calendar c=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date=simpleDateFormat.parse(invoiceDate);
                    c.setTime(new Date());
                    c.add(Calendar.YEAR, -1);
                    c.add(Calendar.DAY_OF_MONTH,+6);
                    Date lastyear = c.getTime();

//                    if(date.compareTo(lastyear)<0){
//                        flag=false;
//                        LOGGER.info("逾期一年");
//                    }
                    if(date.compareTo(new Date())>0){
                        flag=false;
                        LOGGER.info("未来时间");
                    }
                } catch (ParseException e) {
                    flag=false;
                   LOGGER.info("日期格式错误 {}",e);
                }


            }
        }

        return flag;

    }


}
