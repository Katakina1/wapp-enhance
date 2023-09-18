package com.xforceplus.wapp.modules.overdue.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.enums.DefaultSettingEnum;
import com.xforceplus.wapp.modules.overdue.dto.RedSwitchDto;
import com.xforceplus.wapp.repository.dao.DefaultSettingDao;
import com.xforceplus.wapp.repository.entity.DefaultSettingEntity;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultSettingServiceImplTest {
    @Captor
    private ArgumentCaptor<DefaultSettingEntity> entityArgumentCaptor;

    @Mock
    private DefaultSettingDao defaultSettingDao;

    @InjectMocks
    private DefaultSettingServiceImpl service;

    @BeforeAll
    static void init() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DefaultSettingEntity.class);
    }

    @Test
    void testUpdateOSaveRedSwitch1() {
        Date start = new Date();
        Date end = new Date();
        RedSwitchDto dto = new RedSwitchDto();
        dto.setStart(start);
        dto.setEnd(end);
        ArrayList<RedSwitchDto> list = Lists.newArrayList(dto);
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue("2022-03-12,2022-04-12");
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        when(defaultSettingDao.update(any(), any())).thenReturn(1);
        Boolean asserts = service.updateOrSaveRedSwitch(list, "1");
        assertTrue(asserts);
    }

    @Test
    void testUpdateOrSaveRedSwitch2() {
        Date start = new Date();
        Date end = new Date();
        RedSwitchDto dto = new RedSwitchDto();
        dto.setStart(start);
        dto.setEnd(end);
        ArrayList<RedSwitchDto> list = Lists.newArrayList(dto);
        when(defaultSettingDao.selectOne(any())).thenReturn(null);
        when(defaultSettingDao.insert(any())).thenReturn(1);
        Boolean asserts2 = service.updateOrSaveRedSwitch(list, "1");
        assertTrue(asserts2);
        verify(defaultSettingDao).insert(entityArgumentCaptor.capture());
        DefaultSettingEntity verEntity = entityArgumentCaptor.getValue();
        assertEquals(DefaultSettingEnum.RED_INFORMATION_SWITCH.getCode(), verEntity.getCode());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String value = String.format("%s,%s", format.format(start), format.format(end));
        assertEquals(value, verEntity.getValue());
    }

    @Test
    void testUpdateOrSaveRedSwitch3() {
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue("2022-03-12,2022-04-12");
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        when(defaultSettingDao.update(any(), any())).thenReturn(1);
        Boolean asserts3 = service.updateOrSaveRedSwitch(Lists.newArrayList(), "1");
        assertTrue(asserts3);
    }

    @Test
    void testUpdateOrSaveRedSwitch4() {
        when(defaultSettingDao.selectOne(any())).thenReturn(null);
        when(defaultSettingDao.insert(any())).thenReturn(1);
        Boolean asserts4 = service.updateOrSaveRedSwitch(Lists.newArrayList(), "1");
        verify(defaultSettingDao).insert(entityArgumentCaptor.capture());
        assertTrue(asserts4);
        DefaultSettingEntity verEntity4 = entityArgumentCaptor.getValue();
        assertEquals(DefaultSettingEnum.RED_INFORMATION_SWITCH.getCode(), verEntity4.getCode());
        assertNotNull(verEntity4.getValue());
        assertEquals(StringUtils.EMPTY, verEntity4.getValue());
    }

    @Test
    void testUpdateOrSaveRedSwitch5() {
        Date start = new Date();
        Date end = new Date();
        RedSwitchDto dto = new RedSwitchDto();
        dto.setStart(start);
        dto.setEnd(end);
        RedSwitchDto dto2 = new RedSwitchDto();
        Date start2 = DateUtils.addDays(start, 5);
        Date end2 = DateUtils.addDays(start, 5);
        dto2.setStart(start2);
        dto2.setEnd(end2);
        ArrayList<RedSwitchDto> list = Lists.newArrayList(dto, dto2);
        when(defaultSettingDao.selectOne(any())).thenReturn(null);
        when(defaultSettingDao.insert(any())).thenReturn(1);
        Boolean asserts4 = service.updateOrSaveRedSwitch(list, "1");
        verify(defaultSettingDao).insert(entityArgumentCaptor.capture());
        assertTrue(asserts4);
        DefaultSettingEntity verEntity4 = entityArgumentCaptor.getValue();
        assertEquals(DefaultSettingEnum.RED_INFORMATION_SWITCH.getCode(), verEntity4.getCode());
        assertNotNull(verEntity4.getValue());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String expected = String.format("%s,%s|%s,%s", sdf.format(start), sdf.format(end), sdf.format(start2), sdf.format(end2));
        assertEquals(expected, verEntity4.getValue());
    }


    @Test
    void testGetRedSwitch() {
        String start = "2022-03-12";
        String end = "2022-04-12";
        String value = String.format("%s,%s", start, end);
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue(value);
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        List<Tuple3<Long, Date, Date>> redSwitch = service.getRedSwitch();
        assertEquals(redSwitch.size(), 1);
        Tuple3<Long, Date, Date> verTup = redSwitch.get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(start, sdf.format(verTup._1));
        assertEquals(end, sdf.format(verTup._2));

        when(defaultSettingDao.selectOne(any())).thenReturn(null);
        List<Tuple3<Long, Date, Date>> redSwitch2 = service.getRedSwitch();
        assertEquals(redSwitch2.size(), 0);

        entity.setValue(StringUtils.EMPTY);
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        List<Tuple3<Long, Date, Date>> redSwitch3 = service.getRedSwitch();
        assertEquals(redSwitch3.size(), 0);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 1, 5, 2",
            "-3, 2, 1, 5, 2",
            "-5, -1, 2, 4, 2"
    })
    void testGetRedSwitch2(int s1, int e1, int s2, int e2, int expected) {
        Date date = new Date();
        Date start1 = DateUtils.addDays(date, s1);
        Date end1 = DateUtils.addDays(date, e1);
        Date start2 = DateUtils.addDays(date, s2);
        Date end2 = DateUtils.addDays(date, e2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String value = String.format("%s,%s|%s,%s", sdf.format(start1), sdf.format(end1), sdf.format(start2), sdf.format(end2));
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue(value);
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        List<Tuple3<Long, Date, Date>> redSwitch = service.getRedSwitch();
        assertEquals(expected, redSwitch.size());
    }


    @ParameterizedTest
    @CsvSource({
            "0, 0, 1, 5, true",
            "-3, 2, 2, 4, true",
            "-3, -2, 0, 0, true",
            "-5, -1, -1, 3, true",
            "-3, -2, 3, 6, false",
            "-3, -1, -9, -4, false",
            "-5, -1, 2, 4, false",
            "5, 7, 2, 4, false"
    })
    void testIsHanging(int s1, int e1, int s2, int e2, boolean expected) {
        Date date = new Date();
        Date start1 = DateUtils.addDays(date, s1);
        Date end1 = DateUtils.addDays(date, e1);
        Date start2 = DateUtils.addDays(date, s2);
        Date end2 = DateUtils.addDays(date, e2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String value = String.format("%s,%s|%s,%s", sdf.format(start1), sdf.format(end1), sdf.format(start2), sdf.format(end2));
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue(value);
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        boolean hanging = service.isHanging();
        assertEquals(expected, hanging);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "-7, 6, true",
            "-5, -1, false",
            "1, 8, false"
    })
    void testIsHanging1(int s, int e, boolean expected) {
        Date date = new Date();
        Date start = DateUtils.addDays(date, s);
        Date end = DateUtils.addDays(date, e);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String value = String.format("%s,%s", sdf.format(start), sdf.format(end));
        DefaultSettingEntity entity = new DefaultSettingEntity();
        entity.setId(1L);
        entity.setValue(value);
        when(defaultSettingDao.selectOne(any())).thenReturn(entity);
        boolean hanging = service.isHanging();
        assertEquals(expected, hanging);
    }


    @Test
    void test() {
        System.out.println(Long.parseLong("78349501143740484"));
    }
}
