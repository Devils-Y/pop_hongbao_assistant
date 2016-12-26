package com.huyinapp.pop_redbags.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huyinapp.pop_redbags.R;
import com.huyinapp.pop_redbags.tools.ToastTools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

     private static final String TAG = "main";

     //Listen to the red envelope and grab
     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
     }

     @Override
     protected void onResume() {
          super.onResume();

          init();
     }

     //初始化，检测QQ和微信是否已安装
     private void init() {
          if (isAppInstalled(MainActivity.this, "com.tencent.mobileqq") == null) {
               decideContinue(R.string.no_mobile_qq);
          }
          if (isAppInstalled(MainActivity.this, "com.tencent.mm") == null) {
               decideContinue(R.string.no_mobile_wechat);
          }
          if (! isAccessibilitySettingsOn(getApplicationContext())) {
               needOpenAccessibilityService();
          }
     }

     /**
      * 判断是否继续
      *
      * @param resId
      */
     private void decideContinue(int resId) {
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setCancelable(false);
          builder.setTitle(R.string.tips);
          builder.setMessage(getString(resId, getString(R.string.app_name)));
          builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    ToastTools.toast(MainActivity.this, R.string.no_apk);
               }
          });
          builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    finish();
               }
          });
          builder.show();
     }

     /**
      * 判断服务是否开启
      *
      * @param mContext
      * @return
      */
     private boolean isAccessibilitySettingsOn(Context mContext) {
          int accessibilityEnabled = 0;
          final String service = "com.huyinapp.pop_redbags/com.huyinapp.pop_redbags.service.RBAccessibilityService";
          boolean accessibilityFound = false;
          try {
               accessibilityEnabled = Settings.Secure.getInt(
                         mContext.getApplicationContext().getContentResolver(),
                         android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
          } catch (Settings.SettingNotFoundException e) {
          }
          TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

          if (accessibilityEnabled == 1) {
               String settingValue = Settings.Secure.getString(
                         mContext.getApplicationContext().getContentResolver(),
                         Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
               if (settingValue != null) {
                    TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                    splitter.setString(settingValue);
                    while (splitter.hasNext()) {
                         String accessabilityService = splitter.next();
                         if (accessabilityService.equalsIgnoreCase(service)) {
                              return true;
                         }
                    }
               }
          }

          return accessibilityFound;
     }

     /**
      * 需要开启服务
      */
     private void needOpenAccessibilityService() {
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setCancelable(false);
          builder.setTitle(R.string.tips);
          builder.setMessage(getString(R.string.to_open_accessibility));
          builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    openSet();
               }
          });
          builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    finish();
               }
          });
          builder.show();
     }

     //跳转到开启页面
     private void openSet() {
          Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
          startActivity(intent);
     }

     /**
      * 检测是否安装APP
      *
      * @param context
      * @param packagename
      * @return
      */
     private PackageInfo isAppInstalled(Context context, String packagename) {
          PackageInfo packageInfo;
          try {
               packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
          } catch (PackageManager.NameNotFoundException e) {
               packageInfo = null;
               e.printStackTrace();
          }
          return packageInfo;
     }
}
