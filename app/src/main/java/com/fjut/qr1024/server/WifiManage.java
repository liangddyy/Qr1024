package com.fjut.qr1024.server;

import com.fjut.qr1024.model.WifiMsg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 获取手机的所有连接过的wifi名字和密码
 * 注：
 * 1、通过Runtime.getRuntime().exec("su")获取root权限。
 * 2、通过process.getOutputStream()和process.getInputStream()获取终端的输入流和输出流。
 * 3、通过dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n")往终端中输入命令。
 * 注意，这里必须要有\n作为换行，否则会与后一个exit命令作为一个命令，最终导致命令执行失败，无法得到结果。
 * 4、通过dataInputStream获取命令执行结果，并以UTF-8的编码转换成字符串。
 * 5、使用正则表达式过滤出wifi的用户名和密码。
 */
/**
 * 描述
 *
 * @author liangddyy
 * @created 2016/9/12
 */
public class WifiManage {

    /**
     * 返回所有的wifi信息
     * @return
     */
    public List<WifiMsg> read() {
        List<WifiMsg> wifiInfos = new ArrayList<WifiMsg>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //安全问题
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiMsg wifiInfo = new WifiMsg();
                wifiInfo.setSsid(ssidMatcher.group(1));
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.setPassword(pskMatcher.group(1));
                } else {
                    wifiInfo.setPassword("");//无密码的情况
                }
                wifiInfos.add(wifiInfo);
            }
        }
        return wifiInfos;
    }
}
