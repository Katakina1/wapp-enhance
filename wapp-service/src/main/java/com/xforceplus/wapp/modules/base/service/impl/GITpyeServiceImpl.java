package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.entity.GlTypeEntity;
import com.xforceplus.wapp.modules.base.service.GlTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("glTypeService")
public class GITpyeServiceImpl implements GlTypeService {
    @Override
    public GlTypeEntity queryObject(String schemaLabel, Long roleId) {
        return null;
    }

    @Override
    public List<GlTypeEntity> queryList(String schemaLabel, GlTypeEntity entity) {
        return null;
    }

    @Override
    public List<GlTypeEntity> queryExcelList(Map<String, Object> params) {
        return null;
    }

    @Override
    public int queryTotal(String schemaLabel, GlTypeEntity entity) {
        return 0;
    }

    @Override
    public int roleTotal(String schemaLabel, Long[] orgIds) {
        return 0;
    }

    @Override
    public void save(String schemaLabel, GlTypeEntity entity) {

    }

    @Override
    public void update(String schemaLabel, GlTypeEntity entity) {

    }

    @Override
    public void delete(String schemaLabel, Long roleid) {

    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] roleids) {
        return 0;
    }

    @Override
    public int queryByNameAndCode(String schemaLabel, String name, String code, String account, String type, String glName, Integer id) {
        return 0;
    }

    @Override
    public String queryServiceName(String code, String account) {
        return null;
    }
}
