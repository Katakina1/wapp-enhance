package com.xforceplus.wapp.modules.deduct.excelconverter;

import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Describe: excel 导出数据类型转换
 *
 * @Author xiezhongyong
 * @Date 2022-09-16
 */
public class Converter {

    public static class QueryTab implements com.alibaba.excel.converters.Converter<QueryDeductBaseResponse.QueryTabResp> {

        @Override
        public Class supportJavaTypeKey() {
            return QueryDeductBaseResponse.QueryTabResp.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public QueryDeductBaseResponse.QueryTabResp convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            return null;
        }

        @Override
        public CellData convertToExcelData(QueryDeductBaseResponse.QueryTabResp queryTabResp, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            String str = Objects.nonNull(queryTabResp) ? queryTabResp.getMessage() : StringUtils.EMPTY;
            return new CellData(str);
        }
    }
    public static class RedNotificationStatus implements com.alibaba.excel.converters.Converter<List<Integer>> {

        @Override
        public Class supportJavaTypeKey() {
            return ArrayList.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public List<Integer> convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            return null;
        }

        @Override
        public CellData convertToExcelData(List<Integer> redNotificationStatus, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            String str = StringUtils.EMPTY;
            if (CollectionUtils.isEmpty(redNotificationStatus)) {
                return new CellData(str);
            }
            str = redNotificationStatus.stream().map(en -> AgreementRedNotificationStatus.fromValue(en)).
                    filter(Objects::nonNull).map(AgreementRedNotificationStatus::getDesc).collect(Collectors.joining(","));
            return new CellData(str);
        }
    }
    public static class RedNotificationNos implements com.alibaba.excel.converters.Converter<List<String>> {

        @Override
        public Class supportJavaTypeKey() {
            return ArrayList.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public List<String> convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            return null;
        }

        @Override
        public CellData convertToExcelData(List<String> redNotificationNos, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            String str = StringUtils.EMPTY;
            if (CollectionUtils.isEmpty(redNotificationNos)) {
                return new CellData(str);
            }
            str = redNotificationNos.stream().collect(Collectors.joining(","));
            return new CellData(str);
        }
    }
}
