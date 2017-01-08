package com.fjut.qr1024.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/11
 */
public class StrUtil {
    /** 分页数量 */
    public static final int PAGECOUNT = 20;

    /**
     * 解决乱码问题
     *
     * @param str
     * @return
     */
    static public String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    /**
     * 实现文本复制功能
     * @param content
     */
    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /** 时间格式 */
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 时间字符串转时间戳
     *
     * @author Leon Liang
     * @created 2016/7/30 15:30
     */
    public static int timeStringToInt(String timeStr) {
        if (timeStr == null) {
            return 0;
        }
        try {
            Date date = format.parse(timeStr);
            return (int) date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 时间戳转字符串
     *
     * @author Leon Liang
     * @created 2016/7/30 16:28
     */
    public static String timeIntToString(int timeInt) {
        return format.format(timeInt);
    }

    public static long getTimeIntNow() {
        return System.currentTimeMillis();//单位是毫秒
    }

    /**
     * 和现在时间比较超过 6小时 否 刷新时间
     * @param time
     * @return
     */
    public static boolean isPassLoadTime(long time) {
        return getTimeIntNow() - time > 21600000;///注意 这里是毫秒 ,和普通时间戳有区别
    }

    /**
     * 防止Toast队列 不断显示 影响体验
     *
     * @author Leon Liang
     * @created 2016/9/2 1:38
     */
    private static Toast mToast = null;

    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    /**
     * 字符串工具 清楚Html标签
     *
     * @author Leon Liang
     * @created 2016/9/2 18:41
     */
    private static String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
    private static String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
    private static String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }
}
