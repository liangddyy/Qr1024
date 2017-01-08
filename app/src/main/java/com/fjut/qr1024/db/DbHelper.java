package com.fjut.qr1024.db;

import android.content.Context;
import android.os.Environment;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.List;
/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/12
 */
public class DbHelper implements DbUtils.DbUpgradeListener {
    /**
     * 只实例化一次
     */
    private static DbHelper dbHelper;

    private final String dbName = "qr.db";

    private int version;

    private DbUtils dbUtils;

    private String dbPath = null;

    private static final int BUFFER_SIZE = 8192;

    /**
     * dbUtils初始化 dbHlper构造函数
     *
     * @author Leon Liang
     * @created 2016/8/20 10:33
     */
    private DbHelper(Context context) {
        // TODO: 2016/7/27 暂时不必放到外置卡。方便调试。
        dbPath = Environment.getExternalStorageDirectory() + "/aaaaaa";
        version = 1;// TODO: 2016/7/26 增加配置文件
        dbUtils = DbUtils.create(context, dbPath, dbName, version, null);//dbpath =null,不指定路径
        dbUtils.configAllowTransaction(true);
        //dbUtils.configDebug(true);// TODO: 2016/8/2
    }

    /**
     * 判断数据库文件是否存在
     *
     * @param
     * @return
     * @author Stark Zhou
     * @created 2016/8/16 9:41
     */
    public boolean ifDbFileExist() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/ecsspData");
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 只实例化一次
     *
     * @author Leon Liang
     * @created 2016/7/26 14:55
     */
    public static DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DbHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onUpgrade(DbUtils dbUtils, int i, int i1) {
        // TODO: 2016/7/26 数据库升级

    }

    /**
     * 插入单条记录
     *
     * @author Leon Liang
     * @created 2016/7/26 14:56
     */
    public synchronized boolean save(Object entity) {
        try {
            dbUtils.save(entity);
        } catch (DbException e) {
            return false;
        }
        return true;
    }

    /**
     * 插入表数据到数据库
     *
     * @author Leon Liang
     * @created 2016/7/26 15:56
     */
    public synchronized boolean saveAll(List<?> entity) {
        try {
            dbUtils.saveAll(entity);
        } catch (DbException e) {
            return false;
        }
        return true;
    }

    /**
     * 删除类对应的表
     *
     * @author Leon Liang
     * @created 2016/7/27 10:36
     */
    public synchronized boolean dropByTable(Class<?> entity) {
        try {
            dbUtils.dropTable(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除一个对象
     *
     * @author Leon Liang
     * @created 2016/7/26 14:57
     */
    public synchronized boolean deleteByEntity(Object entity) {
        try {
            dbUtils.delete(entity);
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 从数据库 删除链表
     *
     * @author Leon Liang
     * @created 2016/7/26 14:57
     */
    public synchronized boolean deleteByList(List<?> entity) {
        try {
            dbUtils.deleteAll(entity);
            return true;
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 按条件删除 / 列名 对应的值
     *
     * @author Leon Liang
     * @created 2016/7/26 15:58
     */
    public synchronized boolean deleteByValue(Class<?> entity, String colun, String value) {
        try {
            dbUtils.delete(entity, WhereBuilder.b(colun, "=", value));
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新或者插入表数据
     *
     * @author Leon Liang
     * @created 2016/7/26 15:59
     */
    public synchronized boolean update(Object entity) {
        try {
            dbUtils.saveOrUpdate(entity);//先去查这个条数据 根据id来判断是存储还是更新 如果存在更新
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新指定列 单个对象
     *
     * @author Leon Liang
     * @created 2016/8/20 10:31
     */
    public synchronized boolean update(Object entity, String... updateColumnNames) {
        try {
            dbUtils.update(entity, updateColumnNames);
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新指定列 链表
     *
     * @author Leon Liang
     * @created 2016/8/20 10:31
     */
    public synchronized boolean updateAll(List<?> entity, String... updateColumnNames) {
        try {
            dbUtils.updateAll(entity, updateColumnNames);

        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 更新全部
     *
     * @author Leon Liang
     * @created 2016/8/20 10:31
     */
    public synchronized boolean updateAll(List<?> entity) {
        try {
            dbUtils.updateAll(entity);
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据id查找一条记录
     *
     * @author Leon Liang
     * @created 2016/7/26 16:00
     */
    public synchronized <T> Object searchOne(Class<T> cla, String id) {
        try {
            return dbUtils.findFirst(Selector.from(cla).where(WhereBuilder.b("id", "=", id)));
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找全部
     *
     * @author Leon Liang
     * @created 2016/7/26 16:00
     */
    public synchronized <T> List<T> search(Class<T> entity) {
        try {
            return dbUtils.findAll(Selector.from(entity));
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找全部 并以id排序输出
     *
     * @author Leon Liang
     * @created 2016/7/26 16:10
     */
    public synchronized <T> List<T> searchAll(Class<T> entityClass) {
        try {
            return dbUtils.findAll(Selector.from(entityClass).orderBy("id", true));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 条件查找
     *
     * @author Leon Liang
     * @created 2016/7/26 15:57
     */
    public synchronized <T> List<T> searchAllByValue(Class<T> entityClass, String column, String value) {
        try {
            return dbUtils.findAll(Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 条件查找 - 分页
     *
     * @author Leon Liang
     * @created 2016/7/26 15:57
     */
    public synchronized <T> List<T> searchAllByValue(Class<T> entityClass, String column, String value, int offset,
            int limit) {
        try {
            //offset 基准位置（起始位置） limit 限制大小
            return dbUtils.findAll(
                    Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)).limit(limit).offset(offset));// TODO: 2016/9/1 排序
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized <T> List<T> searchAllByInt(Class<T> entityClass, String column, int value) {
        try {
            return dbUtils.findAll(Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)));
        } catch (Exception e) {
            return null;
        }
    }

    public synchronized <T> List<T> searchAllByIntOrder(Class<T> entityClass, String column, int value,
            String orderColumn, boolean desc) {
        try {
            return dbUtils.findAll(
                    Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)).orderBy(orderColumn, desc));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 条件查找
     *
     * @author Leon Liang
     * @created 2016/7/26 15:57
     */
    public synchronized Object searchOneByValue(Class<?> entityClass, String column, String value) {
        try {
            return dbUtils.findFirst(Selector.from(entityClass).where(WhereBuilder.b(column, "=", value)));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 自定义Sql查找
     *
     * @author Leon Liang
     * @created 2016/7/26 16:57
     */
    public synchronized List<DbModel> searchBySql(SqlInfo sql) {
        try {
            return dbUtils.findDbModelAll(sql); // 自定义sql查询
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存或更新实体到DB（replace or insert）
     *
     * @author Leon Liang
     * @created 2016/7/26 16:00
     */
    public synchronized boolean saveOrUpdateAll(List<?> entity) {
        try {
            dbUtils.saveOrUpdateAll(entity);
        } catch (DbException e) {
            return false;

        }
        return true;
    }

    /**
     * 判断表是否存在
     *
     * @author Leon Liang
     * @created 2016/8/3 23:05
     */
    public synchronized boolean tableIsExist(Class<?> cls) {
        try {
            return dbUtils.tableIsExist(cls);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 转换为分页语句
     *
     * @param
     * @return
     * @author Stark Zhou
     * @created 2016/8/3 17:59
     */
    public void forPaginate(StringBuilder sql, int pageNumber, int pageSize, String oldSql) {
        int start = pageNumber * pageSize;
        sql.append(oldSql);
        sql.append(" limit ").append(start).append(",").append(pageSize);
    }

    /**
     * 删除整个数据库
     *
     * @author Leon Liang
     * @created 2016/8/20 10:49
     */
    public synchronized boolean dropDb() {
        try {
            dbUtils.dropDb();
        } catch (DbException e) {
            if (e != null)
                e.printStackTrace();
            return false;
        }
        return true;
    }
}
