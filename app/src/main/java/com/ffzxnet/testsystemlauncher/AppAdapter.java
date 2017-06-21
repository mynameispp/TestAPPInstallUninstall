package com.ffzxnet.testsystemlauncher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * 创建者： feifan.pi 在 2017/6/2.
 */

public class AppAdapter extends BaseAdapter {
    private List<AppInfo> appInfos;
    //item操作监听
    private ItemOnclick itemOnclick;

    public AppAdapter(List<AppInfo> appInfos, ItemOnclick itemOnclick) {
        this.appInfos = appInfos;
        this.itemOnclick = itemOnclick;
    }

    @Override
    public int getCount() {
        return appInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppHolder appHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mian_list_app, parent, false);
            appHolder = new AppHolder(convertView);
            convertView.setTag(appHolder);
        } else {
            appHolder = (AppHolder) convertView.getTag();
        }
        final AppInfo appInfo = appInfos.get(position);
        appHolder.ico.setImageDrawable(appInfo.getAppIco());
        appHolder.name.setText(appInfo.getAppName());
        appHolder.pkgName.setText(appInfo.getAppPkgName());
        //点击ico打开APP
        appHolder.ico.setTag(appInfo);
        appHolder.ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnclick.openApp((AppInfo) v.getTag());
            }
        });
        //点击包名卸载APP
        appHolder.pkgName.setTag(appInfo);
        appHolder.pkgName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnclick.onUninstall((AppInfo) v.getTag());
            }
        });
        //是否被选中
        if (appInfo.isCheck()) {
            appHolder.checkView.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            appHolder.checkView.setImageResource(android.R.drawable.checkbox_off_background);
        }
        appHolder.checkView.setTag(appInfo);
        appHolder.checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AppInfo appInfo1 = (AppInfo) v.getTag();
                ImageView imageView = (ImageView) v;
                if (appInfo1.isCheck()) {
                    //取消选中
                    imageView.setImageResource(android.R.drawable.checkbox_off_background);
                    itemOnclick.checkAppOff(appInfo1);
                } else {
                    //选中
                    imageView.setImageResource(android.R.drawable.checkbox_on_background);
                    itemOnclick.checkApp(appInfo1);
                }

                appInfo1.setCheck(!appInfo1.isCheck());
            }
        });

        return convertView;
    }

    public static class AppHolder {
        private ImageView ico;
        private TextView name;
        private TextView pkgName;
        private ImageView checkView;

        public AppHolder(View view) {
            ico = (ImageView) view.findViewById(R.id.item_main_list_app_ico);
            name = (TextView) view.findViewById(R.id.item_main_list_app_name);
            pkgName = (TextView) view.findViewById(R.id.item_main_list_app_pkg_name);
            checkView = (ImageView) view.findViewById(R.id.item_main_list_app_check_view);
        }
    }

    public interface ItemOnclick {
        //卸载app
        void onUninstall(AppInfo appInfo);

        //打开app
        void openApp(AppInfo appInfo);

        //选中
        void checkApp(AppInfo appInfo);

        //取消选中
        void checkAppOff(AppInfo appInfo);
    }
}
