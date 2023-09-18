package com.xforceplus.wapp.util;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.BaseUnitTest;
import com.xforceplus.wapp.modules.customs.dto.CustomsEntryDetailsExportDto;
import com.xforceplus.wapp.modules.customs.dto.CustomsEntryExportDto;
import com.xforceplus.wapp.modules.customs.dto.CustomsQueryDto;
import com.xforceplus.wapp.modules.customs.service.CustomsService;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ChenHang
 * @Date: 2023/8/10 14:05
 */
public class TestUtile /*extends BaseUnitTest*/ {

    @Autowired
    private CustomsService customsService;

    @Test
    public void test02() {
        CustomsQueryDto queryDto = new CustomsQueryDto();
        queryDto.setPageNo(0);
        queryDto.setPageSize(10000);

        List<TDxCustomsEntity> resultList = customsService.queryEntryByPage(queryDto);
        if (CollectionUtils.isNotEmpty(resultList)) {
            customsService.entryExport(resultList, null);
        }
    }

    public void test03() {

//        String s = JSONObject.toJSONString(null);
//        System.out.println("s = " + s);

//        long startTime = System.currentTimeMillis();
        ExcelExportUtils excelExportUtils = new ExcelExportUtils();
//        List<People> list = new ArrayList<>(100);
//        for (int i = 0; i < 25; i++) {
//            list.add(new People("张三", "54"));
//            list.add(new People("李四", "19"));
//            list.add(new People("王五", "38"));
//            list.add(new People("赵六", "43"));
//        }
//        long endTime1 = System.currentTimeMillis();
//        excelExportUtils.messageExportOneSheet(list, "RMS非商入账传票清单", null, "Sheet1");

        List<A> aList = new ArrayList<>();
//        aList.add(new A("张三", "20"));
//        aList.add(new A("李四", "30"));
        List<B> bList = new ArrayList<>();
//        bList.add(new B("王五", "10", "男"));
//        bList.add(new B("赵六", "15", "吕"));

        List<List> allList = new ArrayList<>();
        allList.add(aList);
        allList.add(bList);
        ArrayList<Class> clazzs = new ArrayList<>();
        clazzs.add(A.class);
        clazzs.add(B.class);
//        excelExportUtils.messageExportMoreSheet(allList, clazzs, "RMS非商入账传票清单", "");
//
    }

    public static void main(String[] args) {
//        ExcelExportUtils excelExportUtils = new ExcelExportUtils();
//        List<A> aList = new ArrayList<>();
////        aList.add(new A("张三", "20"));
////        aList.add(new A("李四", "30"));
//        excelExportUtils.messageExportOneSheet(aList, A.class, "RMS非商入账传票清单", null, "Sheet1");
//        List<B> bList = new ArrayList<>();
////        bList.add(new B("王五", "10", "男"));
////        bList.add(new B("赵六", "15", "吕"));
//
//        List<List> allList = new ArrayList<>();
//        allList.add(aList);
//        allList.add(bList);
//        ArrayList<Class> clazzs = new ArrayList<>();
//        clazzs.add(A.class);
//        clazzs.add(B.class);
////        excelExportUtils.messageExportMoreSheet(allList, clazzs, "测试测试", "", "sheet1", "sheet2");
        List<B> bList = new ArrayList<>();
        bList.add(new B("王五", "10", null));
        bList.add(new B("赵六", "15", "吕"));
        bList.add(new B("赵六", "15", "吕"));

        List<String> genders = bList.stream()
                .filter(iteam -> StringUtils.isNotBlank(iteam.getGender()))
                .map(B::getGender)
                .collect(Collectors.toList());
        for (String gender : genders) {
            System.out.println("gender = " + gender);
        }

        Map<String, List<B>> detailsMap = bList.stream()
                .filter(detail -> detail.getGender() != null)
                .collect(Collectors.groupingBy(B::getGender));
        for (String key : detailsMap.keySet()) {
            System.out.println("key = " + JSONObject.toJSONString(detailsMap.get(key)));
        }


    }

//    @Test
    public void test01() {
        long startTime = System.currentTimeMillis();
        ExcelExportUtils excelExportUtils = new ExcelExportUtils();
        List<People> list = new ArrayList<>(100000);
        for (int i = 0; i < 25000; i++) {
            list.add(new People("张三", "54"));
            list.add(new People("李四", "19"));
            list.add(new People("王五", "38"));
            list.add(new People("赵六", "43"));
        }
        long endTime1 = System.currentTimeMillis();
        excelExportUtils.messageExportOneSheet(list, People.class, "RMS非商入账传票清单", "", "Sheet1");

//        List<A> aList = new ArrayList<>();
//        aList.add(new A("张三", "20"));
//        aList.add(new A("李四", "30"));
//        List<B> bList = new ArrayList<>();
//        bList.add(new B("王五", "10", "男"));
//        bList.add(new B("赵六", "15", "吕"));
//        excelExportUtils.setExportDtoLists(aList, bList);
//        excelExportUtils.setClazzs(A.class, B.class);

        /*List<List> allList = new ArrayList<>();
        allList.add(aList);
        allList.add(bList);
        List<Class> classList = new ArrayList<>();
        classList.add(A.class);
        classList.add(B.class);*/

//        Map<Class, List> map = new HashMap<>();
//        map.put(A.class, aList);
//        map.put(B.class, bList);
//
//
//        excelExportUtils.messageExportMoreSheet(map,"RMS非商入账传票清单", "");
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class A{
        @ExcelProperty(value = "姓名", index = 0)
        private String name;
        @ExcelProperty(value = "年龄", index = 1)
        private String age;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class B{
        @ExcelProperty(value = "姓名", index = 0)
        private String name;
        @ExcelProperty(value = "年龄", index = 1)
        private String age;
        @ExcelProperty(value = "性别", index = 2)
        private String gender;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class People{
        @ExcelProperty(value = "姓名", index = 0)
        private String name0;
        @ExcelProperty(value = "年龄", index = 1)
        private String age1;
        @ExcelProperty(value = "姓名", index = 2)
        private String name2;
        @ExcelProperty(value = "年龄", index = 3)
        private String age3;
        @ExcelProperty(value = "姓名", index = 4)
        private String name4;
        @ExcelProperty(value = "年龄", index = 5)
        private String age5;
        @ExcelProperty(value = "姓名", index = 6)
        private String name6;
        @ExcelProperty(value = "年龄", index = 7)
        private String age7;
        @ExcelProperty(value = "姓名", index = 8)
        private String name8;
        @ExcelProperty(value = "年龄", index = 9)
        private String age9;
        @ExcelProperty(value = "姓名", index = 10)
        private String name10;
        @ExcelProperty(value = "年龄", index = 11)
        private String age11;
        @ExcelProperty(value = "姓名", index = 12)
        private String name12;
        @ExcelProperty(value = "年龄", index = 13)
        private String age13;
        @ExcelProperty(value = "姓名", index = 14)
        private String name14;
        @ExcelProperty(value = "年龄", index = 15)
        private String age15;
        @ExcelProperty(value = "姓名", index = 16)
        private String name16;
        @ExcelProperty(value = "年龄", index = 17)
        private String age17;
        @ExcelProperty(value = "姓名", index = 18)
        private String name18;
        @ExcelProperty(value = "年龄", index = 19)
        private String age19;
        @ExcelProperty(value = "姓名", index = 20)
        private String name20;
        @ExcelProperty(value = "年龄", index = 21)
        private String age21;
        @ExcelProperty(value = "姓名", index = 22)
        private String name22;
        @ExcelProperty(value = "年龄", index = 23)
        private String age23;
        @ExcelProperty(value = "姓名", index = 24)
        private String name24;
        @ExcelProperty(value = "年龄", index = 25)
        private String age25;
        @ExcelProperty(value = "姓名", index = 26)
        private String name26;
        @ExcelProperty(value = "年龄", index = 27)
        private String age27;
        @ExcelProperty(value = "姓名", index = 28)
        private String name28;
        @ExcelProperty(value = "年龄", index = 29)
        private String age29;
        @ExcelProperty(value = "姓名", index = 30)
        private String name30;
        @ExcelProperty(value = "年龄", index = 31)
        private String age31;
        @ExcelProperty(value = "姓名", index = 32)
        private String name32;
        @ExcelProperty(value = "年龄", index = 33)
        private String age33;
        @ExcelProperty(value = "姓名", index = 34)
        private String name34;
        @ExcelProperty(value = "年龄", index = 35)
        private String age35;
        @ExcelProperty(value = "姓名", index = 36)
        private String name36;
        @ExcelProperty(value = "年龄", index = 37)
        private String age37;
        @ExcelProperty(value = "姓名", index = 38)
        private String name38;
        @ExcelProperty(value = "年龄", index = 39)
        private String age39;

        public People(String name0, String age1) {
            this.name0 = name0;
            this.age1 = age1;
            this.name2 = name0;
            this.age3 = age1;
            this.name4 = name0;
            this.age5 = age1;
            this.name6 = name0;
            this.age7 = age1;
            this.name8 = name0;
            this.age9 = age1;
            this.name10 = name0;
            this.age11 = age1;
            this.name12 = name0;
            this.age13 = age1;
            this.name14 = name0;
            this.age15 = age1;
            this.name16 = name0;
            this.age17 = age1;
            this.name18 = name0;
            this.age19 = age1;
            this.name20 = name0;
            this.age21 = age1;
            this.name22 = name0;
            this.age23 = age1;
            this.name24 = name0;
            this.age25 = age1;
            this.name26 = name0;
            this.age27 = age1;
            this.name28 = name0;
            this.age29 = age1;
            this.name30 = name0;
            this.age31 = age1;
            this.name32 = name0;
            this.age33 = age1;
            this.name34 = name0;
            this.age35 = age1;
            this.name36 = name0;
            this.age37 = age1;
            this.name38 = name0;
            this.age39 = age1;
        }
    }

}
