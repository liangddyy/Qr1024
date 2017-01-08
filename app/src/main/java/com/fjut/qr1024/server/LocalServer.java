package com.fjut.qr1024.server;

import android.content.Context;

import com.fjut.qr1024.db.DbHelper;
import com.fjut.qr1024.model.Card;
import com.fjut.qr1024.model.Record;
import com.fjut.qr1024.utils.StrUtil;

import java.util.List;

/**
 * 描述
 *
 * @author Leon Liang
 * @created 2016/9/12 8:59
 */
public class LocalServer {
    private static LocalServer localServer;
    private DbHelper dbHelper;
    private Context context;

    public static LocalServer getInstance(Context context) {
        if (localServer == null) {
            localServer = new LocalServer(context.getApplicationContext());
        }
        return localServer;
    }

    public LocalServer(Context context) {
        dbHelper = DbHelper.getInstance(context);//初始化数据库
        this.context = context;
    }

    public void save(Object entity) {
        dbHelper.save(entity);
    }

    public List<Record> getAllRecordByPage(String urlId, int pageNum) {
        return dbHelper.searchAllByValue(Record.class, "id", urlId, StrUtil.PAGECOUNT * (pageNum - 1),
                StrUtil.PAGECOUNT);
    }

    public List<Record> getAllRecord() {
        return dbHelper.searchAll(Record.class);
    }

    public List<Card> getAllCard() {
        return dbHelper.searchAll(Card.class);
    }

    public void saveOrUpdateAll(List<?> entity) {
        dbHelper.saveOrUpdateAll(entity);
    }
}
