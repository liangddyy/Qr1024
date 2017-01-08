package com.fjut.qr1024.activity;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fjut.qr1024.R;
import com.fjut.qr1024.adapter.CardAdapter;
import com.fjut.qr1024.model.Card;
import com.fjut.qr1024.server.LocalServer;
import com.fjut.qr1024.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/12
 */
public class ShareCardListActivity extends Activity {
    private ListView lvCard;
    private CardAdapter cardAdapter;
    private List<Card> cards;
    private List<Card> cardsLoad;

    private Card cardTempt;
    private LocalServer localServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card_list);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        lvCard = (ListView) findViewById(R.id.ListView01);
    }

    private void initData() {
        //本地服务类
        localServer = LocalServer.getInstance(ShareCardListActivity.this);

        cards = localServer.getAllCard();

        cardAdapter = new CardAdapter(ShareCardListActivity.this, cards);
        // 设置适配器
        lvCard.setAdapter(cardAdapter);

        //多线程
        new LoadCardTask().execute();
    }

    private void initEvent() {
        // 添加联系人单击事件
        lvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cardTempt = (Card) cardAdapter.getItem(arg2);

                //转换成 Card格式
                String stringContent = "BEGIN:VCARD\n" + "VERSION:3.0\n" + "N:;;;;\n" + "FN:" + cardTempt.getName()
                        + "\nTEL;TYPE=CELL:" + cardTempt.getPhone() + "\nEND:VCARD";

                Dialog dialog = DialogUtil.getQrDialog(ShareCardListActivity.this, "扫一扫添加联系人", stringContent, null);
                dialog.show();
            }
        });

        // 添加长按点击
        lvCard.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择");
                menu.add(0, 0, 0, "生成其二维码");
                menu.add(0, 1, 0, "发送短信");
                menu.add(0, 2, 0, "修改联系人");
                menu.add(0, 3, 0, "删除联系人");
            }
        });
    }

    // 长按菜单响应函数 // TODO: 2016/9/9
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                TextView titleview = (TextView) menuInfo.targetView.findViewById(R.id.ItemTitle);
                String title = titleview.getText().toString();
                Toast.makeText(ShareCardListActivity.this, "内容为:" + title, Toast.LENGTH_LONG).show(); // 显示那条数据
                break;
            case 1:
                Toast.makeText(ShareCardListActivity.this, "这里发短信", Toast.LENGTH_SHORT).show();
            case 2:
                Toast.makeText(ShareCardListActivity.this, "这里修改", Toast.LENGTH_SHORT).show();

            case 3:
                Toast.makeText(ShareCardListActivity.this, "这里删除", Toast.LENGTH_SHORT).show();
            default:
                break;
        }

        setTitle("点击了长按菜单里面的第" + item.getItemId() + "个项目");
        return super.onContextItemSelected(item);
    }

    class LoadCardTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            localServer.saveOrUpdateAll(cardsLoad);

            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Integer... integers) {

            cardsLoad = new ArrayList<>();
            Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
            if(cur==null)
                return null;
            if (cur.moveToFirst()) {
                int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
                int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                do {
                    Card card = new Card();
                    String contactId = cur.getString(idColumn);
                    card.setId(contactId);
                    // 获得联系人姓名
                    String disPlayName = cur.getString(displayNameColumn);
                    card.setName(disPlayName);

                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    // TODO: 2016/9/13 多个号码
                    String phoneNumber = null;
                    // 获取第一个号码
                    if (phones.moveToFirst()) {
                        phoneNumber = phones
                                .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    card.setPhone(phoneNumber + "");
                    cardsLoad.add(card);
                    // 查看该联系人有多少个电话号码。如果没有这返回值为0
                    //int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    //                    map.put("ItemTitle", disPlayName);
                    //                    if (phoneCount > 0) {
                    //                        // 获得联系人的电话号码
                    //                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    //                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    //                        if (phones.moveToFirst()) {
                    //                            do {
                    //                                // 遍历所有的电话号码
                    //                                String phoneNumber = phones
                    //                                        .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    //                                String phoneType = phones
                    //                                        .getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    //                                map.put("ItemText", phoneNumber);
                    //                                listItem.add(map);
                    //                                //   Log.i("phoneType", phoneType);  //要设置phonetype不然会空值
                    //                            } while (phones.moveToNext());
                    //                        }
                    //                    }
                } while (cur.moveToNext());
            }
            return null;
        }
    }
}