package com.fjut.qr1024.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fjut.qr1024.R;
import com.fjut.qr1024.model.Record;
import com.fjut.qr1024.server.LocalServer;
import com.fjut.qr1024.server.WifiConnectManager;
import com.fjut.qr1024.utils.DialogUtil;
import com.fjut.qr1024.utils.QrImgUtil;
import com.fjut.qr1024.utils.StrUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    final private int QR_SCAN = 100;
    final private int PHOTO_SCAN = 101;

    private TextView tvResult;
    private LocalServer localServer;

    //wifi begin
    private WifiManager wifiManager;
    private WifiConnectManager wifiConnectManager;
    private String mPwd;
    private String mSsid;

    private WifiInfo wifiInfo;
    //wifi end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initdata();
    }



    /**
     * 初始化数据
     */
    public void initdata() {
        localServer = LocalServer.getInstance(MainActivity.this);

        //初始化一个 wifi 管理服务
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //将获取到的 wifi 服务类 传入自定义的 Manager
        wifiConnectManager = new WifiConnectManager(wifiManager);

        // 用于界面提示
        wifiConnectManager.mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO: 2016/9/7 操作界面 的提示 
                tvResult.setText(tvResult.getText() + "\n" + msg.obj);
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 初始化视图
     */
    public void initView() {
        tvResult = (TextView) findViewById(R.id.tv_scan_result);
    }

    /**
     * 打开扫描界面扫描条形码或二维码
     * 跳转扫码界面
     *
     * @param view
     */
    public void scan(View view) {
        Intent openCameraIntent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(openCameraIntent, QR_SCAN);
    }

    /**
     * 重写返回结果的函数
     * 这里处理扫码结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case QR_SCAN://扫描
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                dealQrStr(result);
                break;
            case PHOTO_SCAN://相册图片返回
                Uri localUri = data.getData();
                String scheme = localUri.getScheme();
                String imagePath = "";
                if ("content".equals(scheme)) {
                    String[] filePathColumns = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(localUri, filePathColumns, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
                    imagePath = c.getString(columnIndex);
                    c.close();
                } else if ("file".equals(scheme)) {//小米4选择云相册中的图片是根据此方法获得路径
                    imagePath = localUri.getPath();
                }

                Result result1 = scanningImage(imagePath);
                if (result1 == null) {
                    Toast.makeText(getApplicationContext(), "无法识别的图像", Toast.LENGTH_SHORT).show();
                } else {
                    String resultStr = StrUtil.recode(result1.toString());
                    dealQrStr(resultStr);
                }
                break;
        }

    }

    private void dealQrStr(String result) {
        Record record = new Record();
        record.setContent(result);

        if (result.contains("WIFI")) {
            //Wifi识别并连接
            record.setName("WIFI:" + connectWifi(result));
        } else if (result.contains("http://") || result.contains("https://")) {
            DialogUtil.getUrlDialog(MainActivity.this, result).show();
            record.setName(result);
        } else if (result.contains("BEGIN:VCARD") && result.contains("END:VCARD")) {
            //小米通讯录格式的通讯录分享
            record.setName("联系人:" + toAddCard1(result));
        } else if (result.contains("MECARD")) {
            //华为通讯录格式的通讯录分享
            record.setName("联系人:" + toAddCard2(result));
        } else {
            //文本 弹窗
            Dialog dialog = DialogUtil.getTxtDialog(MainActivity.this, "二维码内容", result);
            dialog.show();
            record.setName("普通文本");
        }
        localServer.save(record);
    }

    /**
     *
     * @param strResult
     */
    private String toAddCard2(String strResult) {
        String nameTemp = strResult.substring(strResult.indexOf("N:"));
        nameTemp = nameTemp.substring(2, nameTemp.indexOf(";"));

        String phoneTemp = strResult.substring(strResult.indexOf("TEL:"));
        phoneTemp = phoneTemp.substring(4, phoneTemp.indexOf(";"));

        addCard(nameTemp, phoneTemp);
        return nameTemp;
    }

    /**
     * 类似小米的card格式
     *
     * @param strResult
     */
    private String toAddCard1(String strResult) {
        String nameTemp = strResult.substring(strResult.indexOf("FN:"));
        nameTemp = nameTemp.substring(3, nameTemp.indexOf("\n"));

        String phoneTemp = strResult.substring(strResult.indexOf("CELL:"));
        phoneTemp = phoneTemp.substring(5, phoneTemp.indexOf("\n"));

        addCard(nameTemp, phoneTemp);
        return nameTemp;
    }

    /**
     * 调用系统联系人添加信息
     *
     * @param name
     * @param phone
     */
    private void addCard(String name, String phone) {
        //调用系统联系人 添加
        Intent addIntent = new Intent(Intent.ACTION_INSERT,
                Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
        addIntent.setType("vnd.android.cursor.dir/person");
        addIntent.setType("vnd.android.cursor.dir/contact");
        addIntent.setType("vnd.android.cursor.dir/raw_contact");
        
        addIntent.putExtra(ContactsContract.Intents.Insert.NAME, name);

        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        startActivity(addIntent);
    }

    /**
     * 连接wifi
     *
     * @author Leon Liang
     * @created 2016/9/7 9:59
     */
    private String connectWifi(String strResult) {
        String passwordTemp = strResult.substring(strResult.indexOf("P:"));
        mPwd = passwordTemp.substring(2, passwordTemp.indexOf(";"));

        // 去掉密码 首尾的 引号
        mPwd = mPwd.substring(1, mPwd.length() - 1);

        String netWorkNameTemp = strResult.substring(strResult.indexOf("S:"));
        mSsid = netWorkNameTemp.substring(2, netWorkNameTemp.indexOf(";"));

        //创建一个对话框
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("扫描到一串WIFI信息").setIcon(R.drawable.wifi)
                .setMessage("wifi名：" + mSsid + "\n密码：" + mPwd)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("连接此wifi ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            wifiConnectManager.connect(mSsid, mPwd,
                                    mPwd.equals("") ? WifiConnectManager.WifiCipherType.WIFICIPHER_NOPASS
                                            : WifiConnectManager.WifiCipherType.WIFICIPHER_WPA);
                        } catch (Exception e) {
                            StrUtil.showToast(MainActivity.this, "好像连接失败了", Toast.LENGTH_SHORT);
                        }
                    }
                }).create();

        alertDialog.show();
        return mSsid;
    }

    /**
     * 统一处理不同的分享事件
     *
     * @param v
     */
    public void share(View v) {
        switch (v.getId()) {
            case R.id.share_wifi:
                Intent intent = new Intent(MainActivity.this, ShareWifiActivity.class);
                startActivity(intent);
                break;
            case R.id.share_txt:
                Intent intent1 = new Intent(MainActivity.this, ShareTxtActivity.class);
                startActivity(intent1);
                break;
            case R.id.share_contacts:
                Intent intent2 = new Intent(MainActivity.this, ShareCardListActivity.class);
                startActivity(intent2);
                break;
            case R.id.alipay_pay:
                toAlipay();
                break;
            case R.id.share_other:
                //跳转相册选择图片
                Intent intent4 = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent4, PHOTO_SCAN);
                break;
            case R.id.share_history:
                Intent intent5 = new Intent(MainActivity.this, QrHistoryActivity.class);
                startActivity(intent5);
                break;
            default:

                break;
        }
    }

    private void toAlipay() {
        try {
            /**
             * 利用Intent打开支付宝
             * 支付宝跳过开启动画打开扫码和付款码的url scheme分别是
             * alipayqr://platformapi/startapp?saId=10000007和
             * alipayqr://platformapi/startapp?saId=20000056
             */
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent3 = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent3);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(MainActivity.this, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据图片路径 识别图片中的二维码
     *
     * @param path
     * @return
     */
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Bitmap scanBitmap;
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        //scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        //        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        LuminanceSource source = new PlanarYUVLuminanceSource(QrImgUtil.rgb2YUV(scanBitmap), scanBitmap.getWidth(),
                scanBitmap.getHeight(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight(), false);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
