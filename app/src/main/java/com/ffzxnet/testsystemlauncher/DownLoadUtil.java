package com.ffzxnet.testsystemlauncher;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 创建者： feifan.pi 在 2017/6/5.
 */

public class DownLoadUtil {
    /**
     * 连接url
     */
    private String urlstr;
    /**
     * sd卡目录路径
     */
    private String sdcard;
    /**
     * http连接管理类
     */
    private HttpURLConnection urlcon;

    public DownLoadUtil(String url) {
        this.urlstr = url;
        //获取设备sd卡目录
        this.sdcard = Environment.getExternalStorageDirectory() + "/";
        urlcon = getConnection();
    }

    /*
     * 读取网络文本
     */
    public String downloadAsString() {
        StringBuilder sb = new StringBuilder();
        String temp = null;
        try {
            InputStream is = urlcon.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /*
     * 获取http连接处理类HttpURLConnection
     */
    private HttpURLConnection getConnection() {
        URL url;
        HttpURLConnection urlcon = null;
        try {
            url = new URL(urlstr);
            urlcon = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlcon;
    }

    /*
     * 获取连接文件长度。
     */
    public int getLength() {
        return urlcon.getContentLength();
    }

    /*
     * 写文件到sd卡 demo
     * 前提需要设置模拟器sd卡容量，否则会引发EACCES异常
     * 先创建文件夹，在创建文件
     */
    public int down2sd(String dir, String filename, Downhandler handler) {
        StringBuilder sb = new StringBuilder(sdcard)
                .append(dir);
        File file = new File(sb.toString());
        if (!file.exists()) {
            file.mkdirs();
            //创建文件夹
            Log.d("log", sb.toString());
        }
        //获取文件全名
        sb.append(filename);
        file = new File(sb.toString());

        FileOutputStream fos = null;
        try {
            InputStream is = urlcon.getInputStream();
            //创建文件
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int length;
            while ((length = is.read(buf)) != -1) {
                fos.write(buf, 0, length);
            }
            handler.setSize(buf.length);
            is.close();
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    /*
     * 内部回调接口类
     */
    public interface Downhandler {
        void setSize(int size);
    }
}
