package com.huyinapp.pop_redbags.widget;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by huyin on 2016/12/20.
 * 自定义Toast
 * <p/>
 * question: 重复显示
 */

public class CustomToast {
     private static Toast mToast;
     private static Handler mHandler = new Handler();
     private static Runnable r = new Runnable() {
          public void run() {
               mToast.cancel();
          }
     };

     public static void showToast(Context mContext, String text, int duration) {
          mHandler.removeCallbacks(r);
          if (mToast != null)
               mToast.setText(text);
          else
               mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
          mHandler.postDelayed(r, duration);

          mToast.show();
     }

     public static void showToast(Context mContext, int resId, int duration) {
          showToast(mContext, mContext.getResources().getString(resId), duration);
     }
}
