package com.xforceplus.wapp.modules.base.dao;

import java.util.List;
import java.util.Map;


/**
 * 基础Dao(还需在XML文件里，有对应的SQL语句)
 *
 * Created by Daily.zhang on 2018/04/12.
 */
public interface BaseDao<T> {

    void save(Map<String, Object> map);

    int save(T t);

    int update(T t);

    int delete(Object id);

    int deleteBatch(Long[] ids);

    T queryObject(Object id);

    List<T> queryList(T query);

    int queryTotal(T query);

    int queryTotal();
}
