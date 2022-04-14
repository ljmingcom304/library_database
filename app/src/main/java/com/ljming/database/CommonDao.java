package com.ljming.database;

import com.j256.ormlite.dao.Dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

interface CommonDao<T> {

    /**
     * 批处理任务，需要批量增删改的操作在该任务中处理会极大提高效率，任务发生异常后会自动回滚
     */
    void callBatchTasks(Callable callable);

    /**
     * 开启事务
     */
    void transactional();

    /**
     * 提交事务
     */
    void commit();

    /**
     * 设置Dao
     */
    void setDao(Dao<T, Serializable> dao);

    /**
     * 获取Dao
     */
    Dao<T, Serializable> getDao();

    /**
     * 指定sql语句是否开启事务操作数据库
     */
    int execute(String sql, boolean isTransactional);

    /**
     * 插入数据
     */
    void insert(T t);

    /**
     * 指定sql语句查询数据
     */
    List<String[]> query(String sql);

    /**
     * 指定sql语句查询第一条数据
     */
    String[] queryObject(String sql);

    /**
     * 指定id查询数据
     */
    T query(int id);

    /**
     * 根据条件查询第一条数据
     */
    T queryObject(String columnName, String columnValue);

    /**
     * 根据条件查询第一条数据
     */
    T queryObject(String[] columnNames, Object[] columnValues);

    /**
     * 根据条件查询第一条数据
     */
    T queryObject(Map<String,Object> column);

    /**
     * 根据条件查询所有数据
     */
    List<T> query(String columnName, Object columenValue);

    /**
     * 根据多个条件查询所有数据
     */
    List<T> query(String[] columnNames, Object[] columenValues);

    /**
     * 根据多个条件查询所有数据
     */
    List<T> query(Map<String, Object> map);

    /**
     * 查询表中所有数据
     */
    List<T> queryAll();

    /**
     * 删除指定数据
     */
    int delete(T t);

    /**
     * 批量删除数据
     */
    int delete(Collection<T> collection);

    /**
     * 删除表中所有数据
     */
    int deleteAll();

    /**
     * 按条件批量删除
     */
    int delete(Map<String,Object> column);

    /**
     * 按条件批量删除
     */
    int delete(String[] columnNames, Object[] columnValues);

    /**
     * 按条件批量删除
     */
    int delete(String columnNames, String columnValues);

    /**
     * 根据条件删除第一条数据
     */
    int deleteObject(Map<String,Object> column);

    /**
     * 根据条件删除第一条数据
     */
    int deleteObject(String[] columnNames, Object[] columnValues);

    /**
     * 根据条件删除第一条数据
     */
    int deleteObject(String columnName, String columnValue);

    /**
     * 根据sql语句进行更新
     */
    int update(String sql);

    /**
     * 更新指定数据
     */
    int update(T t);

    /**
     * 批量更新数据
     */
    int createOrUpdate(List<T> list);

    /**
     * 更新指定数据
     */
    int createOrUpdate(T t);

    /**
     * 更新指定id数据
     */
    int update(T t, Serializable id);

    boolean isTableExists();

    /**
     * 统计数据条数
     */
    long countOf();
}
