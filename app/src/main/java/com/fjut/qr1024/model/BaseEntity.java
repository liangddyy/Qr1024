package com.fjut.qr1024.model;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Unique;

/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/8
 */
public class BaseEntity {
    @Unique
    @Id
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
