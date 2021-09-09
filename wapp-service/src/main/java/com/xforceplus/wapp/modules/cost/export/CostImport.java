package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.cost.dao.CostApplicationDao;
import com.xforceplus.wapp.modules.cost.exception.DuplicatedException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

public class CostImport extends AbstractImportExcel {

    private MultipartFile file;

    private CostApplicationDao dao;

    public CostImport(MultipartFile file, CostApplicationDao dao) {
        this.file = file;
        this.dao = dao;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<List<String>> analysisExcel() throws Exception {
        List<List<String>> list = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum()+1;

        //获取数据,从第四行开始取
        for (int i = 3; i < rowCount; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                final List<String> entity = createCostImportEntity(row);
                int count = dao.checkInvoice(entity.get(0), entity.get(7).substring(0,8));
                if(count>0){
                    throw new DuplicatedException("发票代码:"+entity.get(0)+",发票号码:"+entity.get(7).substring(0,8)+"已存在!");
                }
                String taxRate=entity.get(6);
                String type= "";
                String code=entity.get(0);
                if(StringUtils.isEmpty(taxRate)){
                    throw new DuplicatedException("发票税率格式错误!");
                }
                if(!entity.get(5).equals("普通发票")){
                    String regex="^\\d{10,}$";
                    Pattern p=Pattern.compile(regex);
                    Matcher m=p.matcher(code);
                    if(!m.find()){
                        throw new DuplicatedException("发票代码:"+code+"格式错误！");
                    }
                     type= CommonUtil.getFplx(code);
                }
                if(!Pattern.compile("^\\d{8}$").matcher(entity.get(7).substring(0,8)).find()){
                    throw new DuplicatedException("发票号:"+entity.get(7).substring(0,8)+"格式错误！");
                }

                if("04".equals(type)){
                    if(entity.get(1)==""||entity.get(1)==null){
                        throw new DuplicatedException("发票号:"+entity.get(7).substring(0,8)+"，该发票校验码不能为空！");
                    }else {
                        if (!Pattern.compile("^\\d{6}$").matcher(entity.get(1)).find()) {
                            throw new DuplicatedException("发票号:"+entity.get(7).substring(0,8)+"校验码格式错误！");
                        }
                    }
                }

                String amount=entity.get(2);
                String taxAmount=entity.get(3);
                String totalAmount=entity.get(4);

                if(new BigDecimal(amount).add(new BigDecimal(taxAmount)).compareTo(new BigDecimal(totalAmount))!=0){
                    throw new DuplicatedException("发票代码:"+entity.get(0)+",发票号码:"+entity.get(7).substring(0,8)+"价税合计错误!");
                }
                if(!Pattern.compile("^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$").matcher(entity.get(11).trim()).find()
                        || !Pattern.compile("^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$").matcher(entity.get(14).trim()).find()
                        || !Pattern.compile("^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$").matcher(entity.get(15).trim()).find()){
                    throw new DuplicatedException("发票号:"+entity.get(7).substring(0,8)+"日期格式错误！");
                }
                list.add(entity);
            }
        }

        return list;
    }

    /**
     * 构建导入费用实体
     * @param row 行
     * @return 导入费用实体
     */
    private List<String> createCostImportEntity(Row row) {
        final List<String> entity = newArrayList();
        //发票信息
        entity.add(getCellData(row, 0));//发票代码
        entity.add(getCellData(row, 2));//校验码
        entity.add(getCellData(row, 3));//发票金额
        entity.add(getCellData(row, 4));//税额
        entity.add(getCellData(row, 5));//价税合计
        //税率信息
        entity.add(getCellData(row, 6));//InvoiceType
        entity.add(taxRateConversion(getCellData(row, 7)));//TaxRate
        entity.add(getCellData(row, 8));//InvoiceNumber
        entity.add(getCellData(row, 9));//InvoiceAmount
        entity.add(getCellData(row, 10));//TaxAmount
        entity.add(getCellData(row, 11));//WithoutTaxAmount
        entity.add(getCellData(row, 12));//InvoiceDate
        //费用信息
        entity.add(getCellData(row, 13));//FeesType
        entity.add(getCellData(row, 14));//FeesDepartment
        entity.add(getCellData(row, 15));//FeesTime
        entity.add(getCellData(row, 16));//FeesTimeTo
        entity.add(getCellData(row, 17));//FeesDescription
        entity.add(getCellData(row, 18));//FeesCount
        entity.add(getCellData(row, 19));//ProjectID

        //发票信息补充
        entity.add(getCellData(row, 1));//jvcode

        return entity;
    }

    private String taxRateConversion(String taxRate){
        String value="";
        if("0%销售".equals(taxRate)){
            value="0";
        }else if("3%小规模纳税人及其他".equals(taxRate)){
            value="310";
        }else if("5%房屋、停车场租赁".equals(taxRate)){
            value="500";
        }else if("6%其他服务(含相关水费)".equals(taxRate)){
            value="610";
        }else if("6%财产保险服务".equals(taxRate)){
            value="630";
        }else if("6%金融服务".equals(taxRate)){
            value="620";
        }else if("10%房屋、停车场租赁".equals(taxRate)){
            value="1020";
        }else if("10%电话费、专线费、快递".equals(taxRate)){
            value="1030";
        }else if("10%运输".equals(taxRate)){
            value="1010";
        }else if("10%销售(含煤气费和相关水费)".equals(taxRate)){
            value="1040";
        }else if("11%房屋、停车场租赁".equals(taxRate)){
            value="1130";
        }else if("11%电话费，专线费，快递".equals(taxRate)){
            value="1110";
        }else if("11%运输".equals(taxRate)){
            value="1120";
        }else if("11%销售(含煤气费及相关水费)".equals(taxRate)){
            value="1140";
        }else if("13%销售(含煤气及相关水费)".equals(taxRate)){
            value="1310";
        }else if("16%动产租赁".equals(taxRate)){
            value="1620";
        }else if("16%销售".equals(taxRate)){
            value="1610";
        }else if("17%动产租赁".equals(taxRate)){
            value="1720";
        }else if("17%销售".equals(taxRate)){
            value="1710";
        }
        else if("9%运输".equals(taxRate)){
            value="910";
        }else if("9%房屋、停车场租赁".equals(taxRate)){
            value="920";
        }else if("9%电话费、专线费、快递".equals(taxRate)){
            value="930";
        }else if("9%销售(含煤气费和相关水费)".equals(taxRate)){
            value="940";
        }else if("13%销售".equals(taxRate)){
            value="1320";
        }else if("13%动产租赁".equals(taxRate)){
            value="1330";
        }else if("9%旅客运输服务(专票)".equals(taxRate)){
            value="950";
        }else if("3%旅客运输服务".equals(taxRate)){
            value="320";
        }else if("9%旅客运输服务(普票)".equals(taxRate)){
            value="960";
        }else if("1%小规模".equals(taxRate)){
            value="100";
        }
        return value;





















    }
}
