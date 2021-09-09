package com.xforceplus.wapp.modules.posuopei.export;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.posuopei.entity.AddClaimEntity;
import com.xforceplus.wapp.modules.posuopei.entity.ImportEntity;
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
public class AddClaimImport extends AbstractImportExcel {

    private MultipartFile file;

    public AddClaimImport(MultipartFile file) {
        this.file = file;
    }

    /**
     * 将excel文件数据变为实体集
     * @return 导入认证实体集
     * @throws ExcelException 读取异常
     */
    public List<AddClaimEntity> analysisExcel() throws Exception {
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
                final AddClaimEntity claimEntity = createImportCertificationEntity(row, index);
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
    private AddClaimEntity createImportCertificationEntity(Row row, int index) {
        final AddClaimEntity addClaimEntity = new AddClaimEntity();


        //序号
        addClaimEntity.setIndexNo(index);

        final String poCode = getCellData(row, 0);

        final String claimNo = getCellData(row, 1);

        final String commodityNo = getCellData(row, 2);

        final String sysAmount = getCellData(row, 3);

        final String vendorAmount = getCellData(row, 4);

        final String num = getCellData(row, 5);

        final Boolean flag = this.checkInvoiceMessage(poCode, claimNo, commodityNo, sysAmount, vendorAmount, num);
        if(flag){
            addClaimEntity.setPoCode(poCode);
            addClaimEntity.setClaimno(claimNo);
            addClaimEntity.setGoodsNo(commodityNo);
            addClaimEntity.setVendorAmount(vendorAmount);
            addClaimEntity.setSystemAmount(sysAmount);
            addClaimEntity.setNumber(num);
            addClaimEntity.setEmpty(true);
            addClaimEntity.setDifference(new BigDecimal(sysAmount).setScale(2).subtract(new BigDecimal(vendorAmount).setScale(2)).multiply(new BigDecimal(num)).toString());
        }else{
            addClaimEntity.setEmpty(false);
        }
        return addClaimEntity;

    }

    private Boolean checkInvoiceMessage(String poCode, String claimNo, String commodityNo, String sysAmount, String vendorAmount, String num) {
        Boolean flag=true;
        if(StringUtils.isEmpty(claimNo)|StringUtils.isEmpty(sysAmount)|StringUtils.isEmpty(vendorAmount)|StringUtils.isEmpty(num)){
            flag=false;
        }
        if(!CommonUtil.isValidNum(claimNo,"^[\\d]{1,12}$")){
            flag=false;
        }
        if(!StringUtils.isEmpty(poCode)){
            if(!CommonUtil.isValidNum(poCode,"^[\\d]{1,12}$")){
                flag=false;
            }
        }
        if(!StringUtils.isEmpty(commodityNo)){
            if(!CommonUtil.isValidNum(commodityNo,"^[\\d]{1,12}$")){
                flag=false;
            }
        }
        if((!CommonUtil.isValidNum(sysAmount,"^[0-9]+(.[0-9]{1,2})?$"))||(!CommonUtil.isValidNum(vendorAmount,"^[0-9]+(.[0-9]{1,2})?$"))){
            flag=false;
        }
        if(!CommonUtil.isValidNum(num,"^[\\d]{1,12}$")){
            flag=false;
        }
        return flag;
    }
}
