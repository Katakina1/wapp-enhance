package com.xforceplus.wapp.modules.blackwhitename.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.modules.blackwhitename.convert.SpeacialCompanyConverter;
import com.xforceplus.wapp.modules.blackwhitename.util.ExcelUtil;
import com.xforceplus.wapp.repository.dao.TXfBlackWhiteCompanyDao;
import com.xforceplus.wapp.repository.entity.TXfBlackWhiteCompanyEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
@Slf4j
public class SpeacialCompanyService extends ServiceImpl<TXfBlackWhiteCompanyDao, TXfBlackWhiteCompanyEntity> {
    private final SpeacialCompanyConverter companyConverter;

    public SpeacialCompanyService(SpeacialCompanyConverter companyConverter) {
        this.companyConverter = companyConverter;
    }

    public Tuple2<List<TXfBlackWhiteCompanyEntity>, Page<TXfBlackWhiteCompanyEntity>> page(Long current, Long size) {
        LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity> wrapper = new LambdaQueryChainWrapper<TXfBlackWhiteCompanyEntity>(baseMapper);
        Page<TXfBlackWhiteCompanyEntity> page = wrapper.page(new Page<>(current, size));
        log.debug("抬头信息分页查询,总条数:{},分页数据:{}", page.getTotal(), page.getRecords());
        return Tuple.of(companyConverter.map(page.getRecords()), page);
    }

    public List<TXfBlackWhiteCompanyEntity> parseExcel(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        List<TXfBlackWhiteCompanyEntity> importEntitiyList = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            TXfBlackWhiteCompanyEntity tXfBlackWhiteCompanyEntity = new TXfBlackWhiteCompanyEntity();
            String no = null;
            String type = null;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);

                String cellValue = ExcelUtil.getCellData(row, i);
                switch (i) {
                    case 0:
                        if (StringUtils.isEmpty(cellValue)) {
                            builder.append("第" + row.getRowNum() + "行" + "供应商6D为空");
                        }
                        tXfBlackWhiteCompanyEntity.setSupplier6d(cellValue);
                        break;
                    case 1:
                        if (StringUtils.isEmpty(cellValue)) {
                            builder.append("第" + row.getRowNum() + "行" + "SAP编码为空");
                        }
                        tXfBlackWhiteCompanyEntity.setSupplier6d(cellValue);
                        break;
                    case 2:
                        if (StringUtils.isEmpty(cellValue)) {
                            builder.append("第" + row.getRowNum() + "行" + "供应商名称为空");
                        }
                        tXfBlackWhiteCompanyEntity.setSupplier6d(cellValue);

                    case 3:
                        if (StringUtils.isEmpty(cellValue)) {
                            tXfBlackWhiteCompanyEntity.setOpenDate(DateUtils.strToDate(cellValue));
                        }
                    case 4:
                        if (StringUtils.isEmpty(cellValue)) {
                            tXfBlackWhiteCompanyEntity.setCloseDate(DateUtils.strToDate(cellValue));
                        }
                    default:
                        break;
                }

            }
            importEntitiyList.add(tXfBlackWhiteCompanyEntity);
        }
        return importEntitiyList;
    }


}
