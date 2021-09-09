package com.xforceplus.wapp.modules.cost.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.cost.dao.CostApplicationDao;
import com.xforceplus.wapp.modules.cost.dao.CostMatchBUDao;
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

public class CostMatchImport extends AbstractImportExcel {

    private MultipartFile file;

    private CostApplicationDao dao;

    private CostMatchBUDao costMatchBUDao;

    public CostMatchImport(MultipartFile file, CostApplicationDao dao) {
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
                int count = dao.checkInvoice(entity.get(1), entity.get(2));
                if(count>0){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"已存在!");
                }
                String taxRate=entity.get(4);
                String type= "";
                String code=entity.get(1);
                if(StringUtils.isEmpty(taxRate)){
                    throw new DuplicatedException("发票税率格式错误!");
                }
                if(!entity.get(10).equals("普通发票")){
                    String regex="^\\d{10,}$";
                    Pattern p=Pattern.compile(regex);
                    Matcher m=p.matcher(code);
                    if(!m.find()){
                        throw new DuplicatedException("发票代码:"+code+"格式错误！");
                    }
                    type= CommonUtil.getFplx(code);
                }
                if(!Pattern.compile("^\\d{8}$").matcher(entity.get(2)).find()){
                    throw new DuplicatedException("发票号:"+entity.get(2)+"格式错误！");
                }
                if("04".equals(type)){
                    if(entity.get(8)==""||entity.get(8)==null){
                        throw new DuplicatedException("发票号:"+entity.get(2)+"，该发票校验码不能为空！");
                    }else {
                        if (!Pattern.compile("^\\d{6}$").matcher(entity.get(8)).find()) {
                            throw new DuplicatedException("发票号:"+entity.get(2)+"校验码格式错误！");
                        }
                    }
                }

                String amount=entity.get(3);
                String taxAmount=entity.get(5);
                String totalAmount=entity.get(6);
                if(new BigDecimal(amount).compareTo(new BigDecimal(0))<0){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"发票金额不能小于0!");
                }
                if(new BigDecimal(taxAmount).compareTo(new BigDecimal(0))<0){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"发票税额不能小于0!");
                }
                if(new BigDecimal(totalAmount).compareTo(new BigDecimal(0))<0){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"发票价税合计不能小于0!");
                }
                if(new BigDecimal(amount).add(new BigDecimal(taxAmount)).compareTo(new BigDecimal(totalAmount))!=0){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"价税合计错误!");
                }
                if(!Pattern.compile("^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$").matcher(entity.get(7).trim()).find()){
                    throw new DuplicatedException("发票号:"+entity.get(2).substring(0,8)+"日期格式错误！");
                }
                if(totalAmount.equals("0")){
                    throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"价税合计不可为0!");
                }
                if(entity.get(10).equals("普通发票")){
                    int counts = dao.checkInvoices(entity.get(7).trim(), entity.get(2));
                    if(counts >0){
                        throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"普通发票已存在!");
                    }
                }
                if(entity.get(10).indexOf("专用发票")!=-1){
                    if(entity.get(2).length()>8){
                        throw new DuplicatedException("发票代码:"+entity.get(1)+",发票号码:"+entity.get(2)+"专用发票号码不能超过8位!");
                    }
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
        entity.add(getCellData(row, 7));//JV
        entity.add(getCellData(row, 8));//发票代码
        entity.add(getCellData(row, 9));//发票号码
        entity.add(getCellData(row, 10));//发票金额
        entity.add(taxRateConversion(getCellData(row, 11)));//税率
        entity.add(getCellData(row, 12));//税额
        entity.add(getCellData(row, 13));//价税合计
        entity.add(getCellData(row, 14));//开票日期
        entity.add(getCellData(row, 15));//校验码
        entity.add(getCellData(row, 0));//费用信息主键
        entity.add(getCellData(row, 16));//发票类型


        return entity;
    }

    private String taxRateConversion(String taxRate) {
        String value = "";
        if ("0%销售".equals(taxRate)) {
            value = "0";
        } else if ("3%小规模纳税人及其他".equals(taxRate)) {
            value = "310";
        } else if ("5%房屋、停车场租赁".equals(taxRate)) {
            value = "500";
        } else if ("6%其他服务(含相关水费)".equals(taxRate)) {
            value = "610";
        } else if ("6%财产保险服务".equals(taxRate)) {
            value = "630";
        } else if ("6%金融服务".equals(taxRate)) {
            value = "620";
        } else if ("10%房屋、停车场租赁".equals(taxRate)) {
            value = "1020";
        } else if ("10%电话费、专线费、快递".equals(taxRate)) {
            value = "1030";
        } else if ("10%运输".equals(taxRate)) {
            value = "1010";
        } else if ("10%销售(含煤气费和相关水费)".equals(taxRate)) {
            value = "1040";
        } else if ("11%房屋、停车场租赁".equals(taxRate)) {
            value = "1130";
        } else if ("11%电话费，专线费，快递".equals(taxRate)) {
            value = "1110";
        } else if ("11%运输".equals(taxRate)) {
            value = "1120";
        } else if ("11%销售(含煤气费及相关水费)".equals(taxRate)) {
            value = "1140";
        } else if ("13%销售(含煤气及相关水费)".equals(taxRate)) {
            value = "1310";
        } else if ("16%动产租赁".equals(taxRate)) {
            value = "1620";
        } else if ("16%销售".equals(taxRate)) {
            value = "1610";
        } else if ("17%动产租赁".equals(taxRate)) {
            value = "1720";
        } else if ("17%销售".equals(taxRate)) {
            value = "1710";
        } else if ("9%运输".equals(taxRate)) {
            value = "910";
        } else if ("9%房屋、停车场租赁".equals(taxRate)) {
            value = "920";
        } else if ("9%电话费、专线费、快递".equals(taxRate)) {
            value = "930";
        } else if ("9%销售(含煤气费和相关水费)".equals(taxRate)) {
            value = "940";
        } else if ("13%销售".equals(taxRate)) {
            value = "1320";
        } else if ("13%动产租赁".equals(taxRate)) {
            value = "1330";
        } else if ("9%旅客运输服务(专票)".equals(taxRate)) {
            value = "950";
        } else if ("3%旅客运输服务".equals(taxRate)) {
            value = "320";
        } else if ("9%旅客运输服务(普票)".equals(taxRate)) {
            value = "960";
        }else if("1%小规模".equals(taxRate)){
            value="100";
        }
        return value;
    }
}
