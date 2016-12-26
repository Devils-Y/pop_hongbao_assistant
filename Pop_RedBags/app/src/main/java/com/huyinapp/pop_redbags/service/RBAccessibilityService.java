package com.huyinapp.pop_redbags.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.huyinapp.pop_redbags.R;
import com.huyinapp.pop_redbags.sharedperences.RBSharedPerences;
import com.huyinapp.pop_redbags.tools.ToastTools;

import java.util.List;

/**
 * Created by huyin on 2016/12/18.
 */

/**
 * 微信打开红包需要两次点击，先是领取，然后打开，但是QQ只需要一次点击
 * 微信红包较简单，只有一种类型，QQ红包类型多
 */
public class RBAccessibilityService extends AccessibilityService {

     //微信红包
     private static final String WECHAT_RB_TEXT = "[微信红包]";
     private static final String RB_BUTTON_CLASS_NAME = "android.widget.Button";

     /**
      * 微信状态值
      */
     private static final String STATE_NO = "0";
     private static final String STATE_OPENED = "1";
     private static final String STATE_CODE = "state_code";
     private String state;

     //QQ红包
     private static final String QQ_RB_TXT = "[QQ红包]";

     /**
      * 设置一个值用来判断接收到的notification是微信还是QQ
      * <p>
      * 默认0 为QQ
      * 1 为微信
      */
     private static final int THISQQ = 0;
     private static final int THISWECHAT = 1;
     private int QQ_OR_WECHAT = THISQQ;

     /**
      * 注意：这里是看到所有红包共同点找出的规律
      */
     private static final String RB_BUTTON_TEXT_NAME = "点击查看详情";
     private final static String RB_PASSWORD = "口令红包";
     private final static String RB_CLICK_TO_PASTE_PASSWORD = "点击输入口令";
     private final static String SEND_PASSWORD = "发送";
     private final static String GET_RB_TEXT = "领取红包";

     /**
      * QQ状态值
      */
     private static final String STATE_NO_QQ = "10";
     private static final String STATE_CLIECKED_QQ = "11";
     private static final String STATE_OPENED_QQ = "12";
     private static final String STATE_CODE_QQ = "state_code_qq";
     private String state_qq;

     @Override
     public void onAccessibilityEvent(AccessibilityEvent event) {
          String className = event.getClassName().toString();
          switch (event.getEventType()) {
               //通知栏
               case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    List<CharSequence> texts = event.getText();
                    if (!texts.isEmpty()) {
                         for (CharSequence text : texts) {
                              String content = text.toString();
                              /**
                               * 微信的notification
                               */
                              if (content.contains(WECHAT_RB_TEXT)) {
                                   //赋值给判断值
                                   QQ_OR_WECHAT = THISWECHAT;
                                   RBnotification(event);
                              }
                              /**
                               * QQ的notification
                               */
                              if (content.contains(QQ_RB_TXT)) {
                                   //赋值给判断值
                                   QQ_OR_WECHAT = THISQQ;
                                   RBnotification(event);
                              }
                         }
                    }
                    break;
               case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    //判断是QQ或者微信发送来的红包
                    if (QQ_OR_WECHAT == THISQQ) {
                         openQQHongBao(event);
                    } else {
                         openWeChatHongBao(event);
                    }
                    break;
               case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    break;
               default:
                    break;
          }
     }

     // 监听到红包的notification，打开通知
     private void RBnotification(AccessibilityEvent event) {
          if (event.getParcelableData() != null
                    && event.getParcelableData() instanceof Notification) {
               Notification notification = (Notification) event
                         .getParcelableData();
               PendingIntent pendingIntent = notification.contentIntent;
               try {
                    pendingIntent.send();
               } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
               }
          }
     }

     //检测到QQ红包
     private void openQQHongBao(AccessibilityEvent event) {
          state_qq = STATE_NO_QQ;
//          getRunningActivityName();
          if ("cooperation.qwallet.plugin.QWalletPluginProxyActivity".equals(event.getClassName())) {
               state_qq = STATE_OPENED_QQ;
               if (RBSharedPerences.readRBQQState(getApplicationContext(), STATE_CODE_QQ).equals(state_qq)) {
                    performGlobalAction(GLOBAL_ACTION_HOME);
                    RBSharedPerences.writeRBQQstate(getApplicationContext(), STATE_CODE_QQ, STATE_NO_QQ);
               }
          } else if ("com.tencent.mobileqq.activity.SplashActivity".equals(event.getClassName())) {
               //拆红包
               state_qq = STATE_CLIECKED_QQ;
               openQQPacket();
          }
     }

     //领取打开QQ红包
     private void openQQPacket() {
          AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
          if (nodeInfo != null) {
               AccessibilityNodeInfo targetNode = null;
               targetNode = findNodeInfosByText(nodeInfo, RB_BUTTON_TEXT_NAME);
               //普通红包
               if (targetNode != null) {
                    performClick(targetNode);

                    RBSharedPerences.writeRBQQstate(getApplicationContext(), STATE_CODE_QQ, STATE_OPENED_QQ);
               }
               //口令红包
               else {
                    AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
                    if (accessibilityNodeInfo != null) {
                         List<AccessibilityNodeInfo> nodeInfos = accessibilityNodeInfo
                                   .findAccessibilityNodeInfosByText(RB_PASSWORD);
                         for (AccessibilityNodeInfo nodeInfo1 : nodeInfos) {
                              targetNode = nodeInfos.get(nodeInfos.size() - 1);
                              performClick(targetNode);
                              writePassword();
                         }
                    }
               }
          }
     }

     //写入并发送口令
     private void writePassword() {
          AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
          if (accessibilityNodeInfo != null) {
               findWidgetByText(accessibilityNodeInfo, RB_CLICK_TO_PASTE_PASSWORD);
          }
          if (accessibilityNodeInfo != null) {
               findWidgetByText(accessibilityNodeInfo, SEND_PASSWORD);
               RBSharedPerences.writeRBQQstate(getApplicationContext(), STATE_CODE_QQ, STATE_OPENED_QQ);
          }
     }

     /**
      * 获取红包上的文本
      *
      * @param node
      * @return
      */
     private String getHongbaoText(AccessibilityNodeInfo node) {
          String content;
          try {
               AccessibilityNodeInfo i = node.getParent().getChild(0);
               content = i.getText().toString();
          } catch (NullPointerException npe) {
               return null;
          }
          return content;
     }

     //检测到微信红包
     private void openWeChatHongBao(AccessibilityEvent event) {
          state = STATE_NO;
//          getRunningActivityName();
          if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
               //点中红包
               getWeChatPacket();
          } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
               //拆红包
               openWeChatPacket();
          } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
               state = STATE_OPENED;
               //拆完红包后返回
               if (RBSharedPerences.readRBWeChatState(getApplicationContext(), STATE_CODE).equals(state)) {
                    performGlobalAction(GLOBAL_ACTION_HOME);
                    RBSharedPerences.writeRBWeChatState(getApplicationContext(), STATE_CODE, STATE_NO);
               }
          }
     }

     /**
      * 通过文件查找控件并点击
      *
      * @param accessibilityNodeInfo
      * @param Keyword
      */
     private void findWidgetByText(AccessibilityNodeInfo accessibilityNodeInfo, String Keyword) {
          List<AccessibilityNodeInfo> nodeInfos = accessibilityNodeInfo
                    .findAccessibilityNodeInfosByText(Keyword);
          for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
               AccessibilityNodeInfo node = nodeInfos.get(nodeInfos.size() - 1);
               performClick(node);
          }
     }


     //领取红包
     private void getWeChatPacket() {
          AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
          if (accessibilityNodeInfo != null) {
               findWidgetByText(accessibilityNodeInfo, GET_RB_TEXT);
          }
     }

     //拆开红包
     @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
     private void openWeChatPacket() {
          AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
          if (nodeInfo != null) {
               AccessibilityNodeInfo targetNode = null;
               targetNode = findNodeInfosByClassName(nodeInfo, RB_BUTTON_CLASS_NAME);
               performClick(targetNode);
               //设置值
               RBSharedPerences.writeRBWeChatState(getApplicationContext(), STATE_CODE, STATE_OPENED);
          }
     }


     //通过组件名字查找
     public static AccessibilityNodeInfo findNodeInfosByClassName(AccessibilityNodeInfo nodeInfo, String className) {
          if (TextUtils.isEmpty(className)) {
               return null;
          }
          for (int i = 0; i < nodeInfo.getChildCount(); i++) {
               AccessibilityNodeInfo node = nodeInfo.getChild(i);
               if (className.equals(node.getClassName())) {
                    return node;
               }
          }
          return null;
     }

     //点击事件
     public static void performClick(AccessibilityNodeInfo nodeInfo) {
          if (nodeInfo == null) {
               return;
          }
          if (nodeInfo.isClickable()) {
               nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
          } else {
               performClick(nodeInfo.getParent());
          }
     }

     @Override
     public void onInterrupt() {
          ToastTools.toast(getApplicationContext(), R.string.accessibility_stop);
     }

     public void onServiceConnected() {
//          AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//          serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//          serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//          serviceInfo.packageNames = new String[]{"com.tencent.mobileqq,com.tencent.mm"};
//          serviceInfo.notificationTimeout = 100;
//          setServiceInfo(serviceInfo);
          ToastTools.toast(getApplicationContext(), R.string.accessibility_start);
     }

     /**
      * 通过文本查找
      */
     public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
          List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
          if (list == null || list.isEmpty()) {
               return null;
          }
          return list.get(0);
     }


     /**
      * 检测当前页面的className
      */
     private void getRunningActivityName() {
          ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
          String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
          ToastTools.toast(getApplicationContext(), runningActivity);
          Log.e("asas", "asasasas---------->" + runningActivity);
     }
}
