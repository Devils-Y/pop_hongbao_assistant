package com.huyinapp.pop_redbags.sharedperences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by huyin on 2016/12/23.
 */

public class RBSharedPerences {
     public static android.content.SharedPreferences pre;

     private static void initSharePreferences(Context context,String fileName) {

          pre = context.getSharedPreferences(fileName, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
     }

     /**
      * 写入微信状态
      *
      * @param context
      * @param fileName
      * @param stateCode
      */
     public static void writeRBWeChatState(Context context, String fileName, String stateCode){
          initSharePreferences(context,fileName);
          SharedPreferences.Editor editor = pre.edit();
          editor.putString("stateCode", stateCode);
          editor.commit();
     }

     /**
      * 读取微信状态
      *
      * @param context
      * @param fileName
      * @return
      */
     public static String readRBWeChatState(Context context,String fileName){
          initSharePreferences(context,fileName);
          String stateCode = pre.getString("stateCode", "");
          return stateCode;
     }

     /**
      * 写入QQ状态
      *
      * @param context
      * @param fileName
      * @param stateCode
      */
     public static void writeRBQQstate(Context context, String fileName, String stateCode){
          initSharePreferences(context,fileName);
          SharedPreferences.Editor editor = pre.edit();
          editor.putString("stateCode_qq", stateCode);
          editor.commit();
     }

     /**
      * 读取QQ状态
      *
      * @param context
      * @param fileName
      * @return
      */
     public static String readRBQQState(Context context,String fileName){
          initSharePreferences(context,fileName);
          String stateCode = pre.getString("stateCode_qq", "");
          return stateCode;
     }
}
