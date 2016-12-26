package com.huyinapp.pop_redbags.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.huyinapp.pop_redbags.widget.CustomToast;

/**
 * Created by huyin on 2016/12/20.
 */

public class ToastTools {
     public static void toast(final Context context, final String content) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {

               @Override
               public void run() {
                    CustomToast.showToast(context, content, 1000);
               }
          });
     }

     public static void toast(final Context context, final int resID) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {

               @Override
               public void run() {
                    CustomToast.showToast(context, resID, 1000);
               }
          });
     }


     public static void longToast(final Context context, final String content) {
          new Handler(Looper.getMainLooper()).post(new Runnable() {

               @Override
               public void run() {
                    CustomToast.showToast(context, content, 3000);
               }
          });
     }
}
