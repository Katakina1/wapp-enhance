package com.xforceplus.wapp.modules.posuopei.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.posuopei.entity.AddClaimEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * 导入excel
 * @author Raymond.yan
 * @date 10/20/2018
 */
public class AddCountImport extends AbstractImportExcel {

    private MultipartFile file;

    public AddCountImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<AddClaimEntity> analysisExcel(String type) throws Exception {
        List<AddClaimEntity> enjoySubsidedList = newArrayList();
        final Workbook workBook = getWorkBook(file);
        //读取第一个标签
        final Sheet sheet = workBook.getSheetAt(0);
        final Integer rowCount = sheet.getLastRowNum();
        if(rowCount>152){
            throw  new Exception("超过150条不可导入");
        }
        int index = 0;
        //获取数据 行数从0开始，数据从第2开始取 当没有实际数据时rowCount为1（两行）
        for (int i = 2; i < rowCount + 1; i++) {
            final Row row = sheet.getRow(i);
            //如果不是空行
            if(!isRowEmpty(row)) {
                ++index;
                final AddClaimEntity claimEntity = createImportCertificationEntity(row, index,type);
                if(claimEntity.getEmpty()){
                    enjoySubsidedList.add(claimEntity);
                }else{
                    int n=i+1;
                    throw new ExcelException(999,"第"+n+"行数据错误");
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
    private AddClaimEntity createImportCertificationEntity(Row row, int index,String type) {
        final AddClaimEntity addClaimEntity = new AddClaimEntity();


        //序号
        addClaimEntity.setIndexNo(index);

        final String poCode = getCellData(row, 0);

        final String claimNo = getCellData(row, 1);

        final String receiptid = getCellData(row, 2);

        final String commodityNo = getCellData(row, 3);

        final String sysAmount = getCellData(row, 4);

        final String vendorNumber = getCellData(row, 5);

        final String systemNumber = getCellData(row, 6);

        final Boolean flag = this.checkInvoiceMessage(poCode, claimNo,receiptid, commodityNo, sysAmount, vendorNumber, systemNumber,type);
        if(flag){
            addClaimEntity.setPoCode(poCode);
            addClaimEntity.setClaimno(claimNo);
            addClaimEntity.setGoodsNo(commodityNo);
            addClaimEntity.setSystemAmount(sysAmount);
            addClaimEntity.setVendorNumber(vendorNumber);
            addClaimEntity.setSystemNumber(systemNumber);
            addClaimEntity.setNumberDifference(new BigDecimal(vendorNumber).subtract(new BigDecimal(systemNumber)).toString());
            addClaimEntity.setAmountDifference(new BigDecimal(sysAmount).setScale(2).multiply(new BigDecimal(vendorNumber).subtract(new BigDecimal(systemNumber))).toString());
            addClaimEntity.setEmpty(true);
        }else{
            addClaimEntity.setEmpty(false);
        }
        return addClaimEntity;

    }

    private Boolean checkInvoiceMessage(String poCode, String claimNo,String receiptid,String commodityNo, String sysAmount, String vendorNumber, String systemNumber,String type) {
        Boolean flag=true;
        if(StringUtils.isEmpty(sysAmount)|StringUtils.isEmpty(vendorNumber)|StringUtils.isEmpty(systemNumber)){
            flag=false;
        }
        if(type.equals("1")){
            if(StringUtils.isEmpty(poCode)){
                flag=false;
            }
        }else{
            if(StringUtils.isEmpty(claimNo)|StringUtils.isEmpty(receiptid)){
                flag=false;
            }
        }
        if(!StringUtils.isEmpty(claimNo)){
            if(!CommonUtil.isValidNum(claimNo,"^[\\d]{1,12}$")){
                flag=false;
            }
        }
        if((!CommonUtil.isValidNum(sysAmount,"^[0-9]+(.[0-9]{1,2})?$"))){
            flag=false;
        }
        if(!CommonUtil.isValidNum(vendorNumber,"^[\\d]{1,12}$")){
            flag=false;
        }
        if(!CommonUtil.isValidNum(systemNumber,"^[\\d]{1,12}$")){
            flag=false;
        }
        return flag;
    }
}
