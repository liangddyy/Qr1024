package com.fjut.qr1024.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fjut.qr1024.R;
import com.fjut.qr1024.model.Record;

import java.util.List;

/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/13
 */
public class RecordAdapter extends BaseAdapter {
    private List<Record> records;
    private Context context;

    public RecordAdapter(Context context, List<Record> records) {
        this.context = context;
        this.records = records;
    }

    public String getContent(int i){
        return records.get(i).getContent();
    }

    public void setData(List<Record> records) {
        this.records = records;
    }

    @Override
    public int getCount() {
        if (records == null) {
            return 0;
        }
        return records.size();
    }

    @Override
    public Object getItem(int i) {
        return records.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        RecordHolder recordHolder = null;

        if (view == null) {
            recordHolder = new RecordHolder();
            view = View.inflate(context, R.layout.item_history_record, null);
            recordHolder.tvName = (TextView) view.findViewById(R.id.tv_record_name);
            recordHolder.tvContent = (TextView) view.findViewById(R.id.tv_record_content);
            view.setTag(recordHolder);
        } else {
            recordHolder = (RecordHolder) view.getTag();
        }
        recordHolder.tvName.setText(records.get(i).getName() + "");
        recordHolder.tvContent.setText(records.get(i).getContent());

        return view;
    }

    public class RecordHolder {
        public TextView tvName;
        public TextView tvContent;
    }
}
