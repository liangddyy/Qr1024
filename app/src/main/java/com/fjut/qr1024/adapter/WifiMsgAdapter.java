package com.fjut.qr1024.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fjut.qr1024.R;
import com.fjut.qr1024.model.WifiMsg;

import java.util.List;
/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/9
 */
public class WifiMsgAdapter extends BaseAdapter {
    private List<WifiMsg> wifiMsgs;
    private Context context;

    public WifiMsgAdapter(List<WifiMsg> wifiMsgs, Context context) {
        this.context = context;
        this.wifiMsgs = wifiMsgs;
    }

    @Override
    public int getCount() {
        return wifiMsgs.size();
    }

    @Override
    public Object getItem(int i) {
        return wifiMsgs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        WifiMsgHolder wifiMsgHolder = null;

        if (view == null) {
            wifiMsgHolder = new WifiMsgHolder();
            view = View.inflate(context, R.layout.item_share_wifi, null);
            wifiMsgHolder.tvName = (TextView) view.findViewById(R.id.tv_wifi_name);
            wifiMsgHolder.tvPwd = (TextView) view.findViewById(R.id.tv_wifi_pwd);
            view.setTag(wifiMsgHolder);
        } else {
            wifiMsgHolder = (WifiMsgHolder) view.getTag();
        }
        wifiMsgHolder.tvName.setText(wifiMsgs.get(i).getSsid());
        wifiMsgHolder.tvPwd.setText(wifiMsgs.get(i).getPassword());
        return view;
    }

    public String getString(int i) {

        //没有密码
        WifiMsg wifiMsg = wifiMsgs.get(i);

        if (wifiMsg.getPassword() == "") {
            return null;
        }
        //有密码
        //拼接wifi字符串
        return "WIFI:T:WPA;P:\"" + wifiMsg.getPassword() + "\";S:" + wifiMsg.getSsid() + ";";
    }

    public class WifiMsgHolder {
        public TextView tvName;
        public TextView tvPwd;
    }
}
