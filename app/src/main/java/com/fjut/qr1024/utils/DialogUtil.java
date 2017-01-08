package com.fjut.qr1024.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fjut.qr1024.R;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/11
 */
public class DialogUtil {

    /**
     * 创建一个二维码图片的对话框
     */
    static public Dialog getQrDialog(Context context, String title, String contentString, Bitmap bitmap) {

        //布局工厂 用来创建布局
        LayoutInflater factory = LayoutInflater.from(context);
        //初始化一个图片布局
        final View v1 = factory.inflate(R.layout.layout_share_wifi_qr_img, null);
        ImageView imageView = (ImageView) v1.findViewById(R.id.iv_pop_qr_img);

        //调用zxing库 生成二维码 设置图片到弹出布局
        imageView.setImageBitmap(EncodingUtils.createQRCode(contentString, 600, 600, bitmap));

        //创建一个对话框
        Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setView(v1)
                .setIcon(R.mipmap.ic_launcher)
                //                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                //                    @Override
                //                    public void onClick(DialogInterface dialog, int which) {
                //                        // TODO Auto-generated method stub
                //                    }
                //                })
                .setPositiveButton("关闭 ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        return alertDialog;
    }

    /**
     * 创建一个准备跳转网页的对话框
     * @param context
     * @param contentStr
     * @return
     */
    static public Dialog getUrlDialog(final Context context, final String contentStr) {
        Dialog alertDialog = new AlertDialog.Builder(context).setTitle(contentStr).setIcon(R.mipmap.ic_launcher)
                .setMessage("得到的内容似乎是一个网址，需要用浏览器打开吗")
                .setNegativeButton("复制内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StrUtil.copy(context, contentStr);
                        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("用浏览器打开 ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (contentStr != null && contentStr != "") {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentStr));
                                context.startActivity(intent);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "出现一个未知错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        return alertDialog;
    }

    /**
     * 创建一个普通的二维码结果的文本对话框
     * @param context
     * @param title
     * @param contentStr
     * @return
     */
    static public Dialog getTxtDialog(final Context context, String title, final String contentStr) {
        //创建一个对话框
        Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setIcon(R.mipmap.ic_launcher)
                .setMessage(contentStr).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).setPositiveButton("复制内容 ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StrUtil.copy(context, contentStr);
                        Toast.makeText(context, "已复制到剪切板", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        return alertDialog;
    }

    /**
     * 创建对话框
     * @param context
     * @param title
     * @param contentStr
     * @param listener
     * @return
     */
    static public Dialog getDialog(final Context context, String title, final String contentStr,
            Dialog.OnClickListener listener) {
        //创建一个对话框
        Dialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setIcon(R.mipmap.ic_launcher)
                .setMessage(contentStr).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("复制内容 ", listener).create();
        return alertDialog;
    }
}
