package com.ffzxnet.testsystemlauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 创建者： feifan.pi 在 2017/6/2.
 */

public class SilentInstallUtil {
    public static String silentInstall(String apkAbsolutePath) {
        String[] args = { "pm", "install", "-r", apkAbsolutePath };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return silentInstallAPK(result);
    }
    private static String silentInstallAPK(String result) {
        if (result.indexOf("Success") == -1) {
            return "哎呀，没成功╮(╯▽╰)╭";
        } else {
            return "哎呀，猜猜我装了个什么︿(￣︶￣)︿";
        }
    }
}
