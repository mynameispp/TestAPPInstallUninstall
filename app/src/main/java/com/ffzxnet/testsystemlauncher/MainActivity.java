package com.ffzxnet.testsystemlauncher;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AppAdapter.ItemOnclick {
    //手机内的手机app，系统除外
    private List<AppInfo> appInfos;
    //app展示列表
    private ListView listView;
    private AppAdapter appAdapter;
    //卸载安装app的监听
    private UninstallReceiver mUninstallReceiver;
    //要卸载的app包名
    private List<String> uninstallPackages;

    private String installPth = Environment.getExternalStorageDirectory() + "/install.apk";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.main_list_app);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在下载中...");

        //下载app
        findViewById(R.id.main_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                downLoadApp();
//                String appPayh = Environment.getExternalStorageDirectory() + "/install.apk";
//                String msg = SilentInstallUtil.silentInstall(appPayh);
//                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
            }

            private void downLoadApp() {
                new AsyncTask<String, String, String>() {
                    boolean downLoadOber = false;

                    @Override
                    protected String doInBackground(String... params) {
                        final DownLoadUtil downLoadUtil = new DownLoadUtil("http://imtt.dd.qq.com/16891/EDDB7A47354B8313973A0BEB44A22BB0.apk?fsname=com.feifanzhixing.o2o_4.2.6_49.apk");
                        downLoadUtil.down2sd("TestSystem/", "install.apk",
                                new DownLoadUtil.Downhandler() {

                                    @Override
                                    public void setSize(int size) {
                                        installPth = Environment.getExternalStorageDirectory() + "/TestSystem/install.apk";
                                        downLoadOber = true;
                                    }
                                });
                        while (!downLoadOber) {
                            //直到下载完，或出错的时候再提示用户
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
        //安装app
        findViewById(R.id.main_install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                install("install.apk");
//                String appPayh = Environment.getExternalStorageDirectory() + "/install.apk";
//                String msg = SilentInstallUtil.silentInstall(appPayh);
//                Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        //批量卸载app
        findViewById(R.id.main_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == uninstallPackages || uninstallPackages.size() == 0) {
                    Toast.makeText(v.getContext(), "请选择要卸载的APP", Toast.LENGTH_SHORT).show();
                } else {
                    for (String uninstallPackage : uninstallPackages) {
                        unInstall(uninstallPackage);
                    }
                }
            }
        });
        //手机内的非系统app应用
        appInfos = new ArrayList<>();
        //获取app
        getAppList();
        appAdapter = new AppAdapter(appInfos, this);
        listView.setAdapter(appAdapter);

        //监听安装卸载广播
        mUninstallReceiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);//监听卸载
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);//监听安装
        filter.addDataScheme("package");
        this.registerReceiver(mUninstallReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mUninstallReceiver);
        mUninstallReceiver = null;
        super.onDestroy();
    }

    public void install(String apkName) {
//        String fileName = Environment.getExternalStorageDirectory() + "/" + apkName;
        File file = new File(installPth);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            startActivity(intent);
        } else {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载
     */
    public void unInstall(String pageName) {
        Uri packageURI = Uri.parse("package:" + pageName);   // 包名
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivity(uninstallIntent);
    }

    /**
     * 获取非系统应用信息列表
     */
    private void getAppList() {
        appInfos.clear();
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppInfo info = new AppInfo();
                info.setAppName(packageInfo.applicationInfo.loadLabel(pm)
                        .toString());
                info.setAppPkgName(packageInfo.packageName);
                info.setAppIco(packageInfo.applicationInfo.loadIcon(pm));
                // 获取该应用安装包的Intent，用于启动该应用
                info.setAppIntent(pm.getLaunchIntentForPackage(packageInfo.packageName));
                appInfos.add(info);
            } else {
                // 系统应用　　　　　　　　
            }

        }
    }

    /**
     * 打开app
     */
    private void openAppByIntent(Intent intent) {
        // TODO Auto-generated method stub
        startActivity(intent);
    }

    //item点击卸载app
    @Override
    public void onUninstall(AppInfo appInfo) {
        unInstall(appInfo.getAppPkgName());
    }

    //item点击打开app
    @Override
    public void openApp(AppInfo appInfo) {
        openAppByIntent(appInfo.getAppIntent());
    }

    private class UninstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String data = intent.getDataString();
//            int index = data.indexOf(":");
//            String pakeName = data.substring(index + 1);

            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                getAppList();
                appAdapter.notifyDataSetChanged();
                Toast.makeText(context, "安装成功", Toast.LENGTH_LONG).show();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (uninstallPackages.contains(packageName)) {
                    //卸载成功去除要卸载的app列表数据
                    uninstallPackages.remove(packageName);
                }
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.getAppPkgName().equals(packageName)) {
                        //卸载成功更新列表
                        appInfos.remove(appInfo);
                        appAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                Toast.makeText(context, "卸载成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void checkApp(AppInfo appInfo) {
        if (null == uninstallPackages) {
            uninstallPackages = new ArrayList<>();
        }
        uninstallPackages.add(appInfo.getAppPkgName());
    }

    @Override
    public void checkAppOff(AppInfo appInfo) {
        if (uninstallPackages.contains(appInfo.getAppPkgName())) {
            uninstallPackages.remove(appInfo.getAppPkgName());
        }
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
        }
    }
}
