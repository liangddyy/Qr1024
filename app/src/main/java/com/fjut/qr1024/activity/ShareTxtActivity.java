package com.fjut.qr1024.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fjut.qr1024.R;
import com.fjut.qr1024.utils.DialogUtil;
import com.fjut.qr1024.utils.StrUtil;
/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/8
 */
public class ShareTxtActivity extends AppCompatActivity {

    private EditText qrStrEditText;
    private CheckBox mCheckBox;
    private ImageView qrImgImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_txt);

        initView();
    }

    private void initView() {
        qrStrEditText = (EditText) findViewById(R.id.et_qr_string);
        mCheckBox = (CheckBox) findViewById(R.id.logo);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
    }

    /**
     * 生成二维码的按钮 事件
     *
     * @param view
     */
    public void make(View view) {
        //隐藏键盘
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(qrStrEditText.getWindowToken(), 0);

        String contentString = qrStrEditText.getText().toString();
        if (!contentString.equals("")) {

            Dialog dialog = DialogUtil.getQrDialog(ShareTxtActivity.this, "扫一扫获取信息", contentString,
                    mCheckBox.isChecked() ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
                            : null);
            //            //根据字符串生成二维码图片并显示在界面上，第二个参数为图片的大小
            //            Bitmap qrCodeBitmap = EncodingUtils.createQRCode(contentString, 500, 500, mCheckBox.isChecked()
            //                    ? BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher) : null);
            //            qrImgImageView.setImageBitmap(qrCodeBitmap);
            dialog.show();

        } else {
            StrUtil.showToast(ShareTxtActivity.this, "输入为空", Toast.LENGTH_SHORT);
        }
    }
}
