package com.fjut.qr1024.model;

import com.fjut.qr1024.utils.StrUtil;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 扫码记录
 *
 * @author liangddyy
 * @created 2016/9/13 8:52
 */
@Table(name = "SCAN_RECORD")
public class Record extends BaseEntity{
    public Record(){
        id = (int) StrUtil.getTimeIntNow();
    }
    String type;
    String content;
    String name;
    String dec;
    int createTime;
    int updateTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}
