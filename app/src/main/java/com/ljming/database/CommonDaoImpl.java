package com.ljming.database;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class CommonDaoImpl<T> implements CommonDao<T> {

    private Dao<T, Serializable> dao;
    private DatabaseConnection conn;

    public CommonDaoImpl() {
        ParameterizedType type = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        @SuppressWarnings( "unchecked" )
        Class<T> clazz = (Class<T>) type.getActualTypeArguments()[0];
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        //调用Library时判断是否初始化过DatabaseHelper
        try {
            dao = databaseHelper.getDao(clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CommonDaoImpl(Dao<T, Serializable> dao) {
        this.dao = dao;
    }

    @Override
    public void setDao(Dao<T, Serializable> dao) {
        this.dao = dao;
    }

    @Override
    public Dao<T, Serializable> getDao() {
        return dao;
    }

    @Override
    public void callBatchTasks(Callable callable) {
        try {
            dao.callBatchTasks(callable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int execute(String sql, boolean isTransactional) {
        DatabaseConnection connection = null;
        Savepoint savePoint = null;
        try {
            if (isTransactional) {
                connection = dao.startThreadConnection();
                savePoint = connection.setSavePoint(null);
            }
            return dao.executeRaw(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.commit(savePoint);
                    dao.endThreadConnection(connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    @Override
    public void insert(T t) {
        try {
            dao.createIfNotExists(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String[]> query(String sql) {
        try {
            GenericRawResults<String[]> queryRaw = dao.queryRaw(sql);
            List<String[]> results = queryRaw.getResults();
            queryRaw.close();
            return results;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] queryObject(String sql) {
        try {
            GenericRawResults<String[]> queryRaw = dao.queryRaw(sql);
            String[] results = queryRaw.getFirstResult();
            queryRaw.close();
            return results;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T query(int id) {
        T t = null;
        try {
            t = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public T queryObject(String columnName, String columnValue) {
        List<T> list = query(columnName, columnValue);
        int size = list.size();
        if (size > 0) {
            return list.get(size - 1);
        }
        return null;
    }

    @Override
    public T queryObject(String[] columnNames, Object[] columnValues) {
        List<T> list = query(columnNames, columnValues);
        int size = list.size();
        if (size > 0) {
            return list.get(size - 1);
        }
        return null;
    }

    @Override
    public T queryObject(Map<String, Object> column) {
        List<T> list = query(column);
        int size = list.size();
        if (size > 0) {
            return list.get(size - 1);
        }
        return null;
    }

    @Override
    public List<T> query(String columnName, Object columnValue) {
        QueryBuilder<T, Serializable> queryBuilder = dao.queryBuilder();
        List<T> list = new ArrayList<T>();
        try {
            queryBuilder.where().eq(columnName, columnValue);
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            list = dao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<T> query(String[] columnNames, Object[] columnValues) {
        if (columnNames.length != columnValues.length) {
            throw new InvalidParameterException("参数不匹配");
        }
        QueryBuilder<T, Serializable> queryBuilder = dao.queryBuilder();
        List<T> list = new ArrayList<T>();
        try {
            Where<T, Serializable> wheres = queryBuilder.where();
            for (int i = 0; i < columnNames.length; i++) {
                if (i == 0) {
                    wheres.eq(columnNames[i], columnValues[i]);
                } else {
                    wheres.and().eq(columnNames[i], columnValues[i]);
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            list = dao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<T> query(Map<String, Object> map) {
        QueryBuilder<T, Serializable> queryBuilder = dao.queryBuilder();
        List<T> list = new ArrayList<T>();
        try {
            if (!map.isEmpty()) {
                Where<T, Serializable> wheres = queryBuilder.where();
                Set<String> keys = map.keySet();
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.addAll(keys);
                for (int i = 0; i < arrayList.size(); i++) {
                    if (i == 0) {
                        wheres.eq(arrayList.get(i), map.get(arrayList.get(i)));
                    } else {
                        wheres.and().eq(arrayList.get(i),
                                map.get(arrayList.get(i)));
                    }
                }
            }
            PreparedQuery<T> preparedQuery = queryBuilder.prepare();
            list = dao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<T> queryAll() {
        List<T> list = new ArrayList<>();
        try {
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int delete(T t) {
        try {
            return dao.delete(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(Collection<T> collection) {
        try {
            return dao.delete(collection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int deleteAll() {
        List<T> list = this.queryAll();
        try {
            return dao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int delete(Map<String, Object> column) {
        List<T> list = query(column);
        if (null != list && !list.isEmpty()) {
            return delete(list);
        }
        return 0;
    }

    @Override
    public int delete(String[] columnNames, Object[] columenValues) {
        List<T> list = query(columnNames, columenValues);
        if (null != list && !list.isEmpty()) {
            return delete(list);
        }
        return 0;
    }

    @Override
    public int delete(String columnNames, String columnValues) {
        List<T> list = query(columnNames, columnValues);
        if (null != list && !list.isEmpty()) {
            return delete(list);
        }
        return 0;
    }

    @Override
    public int deleteObject(Map<String, Object> column) {
        T t = queryObject(column);
        if (null != t) {
            return delete(t);
        }
        return 0;
    }

    @Override
    public int deleteObject(String[] columnNames, Object[] columnValues) {
        T t = queryObject(columnNames, columnValues);
        if (null != t) {
            return delete(t);
        }
        return 0;
    }

    @Override
    public int deleteObject(String columnName, String columnValue) {
        T t = queryObject(columnName, columnValue);
        if (null != t) {
            return delete(t);
        }
        return 0;
    }

    @Override
    public int update(String sql) {
        try {
            return dao.updateRaw(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public int createOrUpdate(List<T> datas) {
        try {
            transactional();
            for (int i = 0; i < datas.size(); i++) {
                dao.createOrUpdate(datas.get(i));
            }
            commit();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int createOrUpdate(T t) {
        try {
            CreateOrUpdateStatus status = dao.createOrUpdate(t);
            return status.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(T t) {
        try {
            return dao.update(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(T t, Serializable id) {
        try {
            return dao.updateId(t, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isTableExists() {
        try {
            return dao.isTableExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long countOf() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void transactional() {
        try {
            conn = dao.startThreadConnection();
            dao.setAutoCommit(conn, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            dao.commit(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    dao.endThreadConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
