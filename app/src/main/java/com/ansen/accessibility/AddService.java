package com.ansen.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * 微信自动加好友服务类
 */

public class AddService extends AccessibilityService {

    private static final String TAG = "AddService";

    private boolean isAdd = false;
    private int index = 0;
    private Handler handler = new Handler();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        CharSequence classNameChr = event.getClassName();
        String className = classNameChr.toString();
        Log.d("ansen", event.toString());
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                    Notification notification = (Notification) event.getParcelableData();
                    String content = notification.tickerText.toString();
                    if (content.contains("请求添加你为朋友")) {
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(TAG, className);
                switch (className) {
                    case "com.tencent.mm.ui.widget.a.c":
                        Log.d(TAG, "隐私提示:  widget.a.c ");
                        List<AccessibilityNodeInfo> az_ = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/az_");
                        if (az_ != null && az_.size() > 0) {
                            az_.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            SystemClock.sleep(500);
                            performBackClick();
                        }
                        break;
                    case "com.tencent.mm.ui.base.p":
                        Log.d(TAG, "隐私提示:  .ui.base.p");
                        break;
                    case "com.tencent.mm.chatroom.ui.ChatroomInfoUI":   // 聊天信息
                    case "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI":
                        Log.d(TAG, "聊天信息 页面");

                        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();   // 获取当前整个活动窗口的根节点 返回值记录View的状态信息
                        // 查找 查看全部群成员 按鈕
                        List<AccessibilityNodeInfo> listNodes = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/title");
                        if (listNodes != null && listNodes.size() == 0) {
                            Log.d(TAG, "沒找到 查看全部群成员 按鈕");

                            List<AccessibilityNodeInfo> listNodes1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
                            if (listNodes1 != null && listNodes1.size() > 0) {

                                listNodes1.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                Log.d(TAG, "滑动后继续查找");
                                SystemClock.sleep(1000);
                                // 查找
                                List<AccessibilityNodeInfo> listNodes2 = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("android:id/title");
                                if (listNodes2 != null && listNodes2.size() > 0) {
                                    listNodes2.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    Log.d(TAG, "点击: " + listNodes2.get(0).getText());
                                }
                            }
                        } else {
                            listNodes.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Log.d(TAG, "点击: " + listNodes.get(0).getText());
                        }
                        index = 0;
                        SystemClock.sleep(1000);
                        break;
                    case "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI":
                    case "com.tencent.mm.chatroom.ui.SeeRoomMemberUI":
                        Log.d(TAG, "聊天成员");
                        SystemClock.sleep(3000);
                        isAdd = false;
                        List<AccessibilityNodeInfo> nodeInfos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e4_");
                        if (nodeInfos != null && nodeInfos.size() > 0) {
                            if (index < (nodeInfos.get(0).getChildCount())) {
                                nodeInfos.get(0).getChild(index).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                SystemClock.sleep(1000);
                                index++;
                            } else {
                                boolean b = nodeInfos.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                                SystemClock.sleep(2000);
                                Log.d(TAG, "翻页: " + b);
                                if (b) {
                                    List<AccessibilityNodeInfo> nodeInfo1 = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e4_");
                                    if (nodeInfo1 != null && nodeInfo1.size() > 0) {
                                        int childCount = nodeInfo1.get(0).getChildCount();
                                        if (childCount > 0) {
                                            index = 0;
                                        }
                                        if (index < childCount) {
                                            nodeInfo1.get(0).getChild(index).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "com.tencent.mm.plugin.profile.ui.ContactInfoUI":
                        if (isAdd) {
                            performBackClick();
                            Log.d(TAG, "添加成功");
                        } else {
                            Log.d(TAG, "添加联系页面");
                            List<AccessibilityNodeInfo> icon = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/iq");
                            if (icon.size() > 0) {
                                performBackClick();
                                Log.d(TAG, "已经添加 直接返回");
                                break;
                            }
                            List<AccessibilityNodeInfo> adds = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cs");
                            if (adds != null && adds.size() > 0) {
                                adds.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                Log.d(TAG, "点击添加");
                            } else {
                                performBackClick();
                            }
                            SystemClock.sleep(1000);
                        }
                        break;
                    case "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI":
                        Log.d(TAG, "验证申请");

                        List<AccessibilityNodeInfo> sends = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jx");
                        if (sends != null && sends.size() > 0) {
                            sends.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            Log.d(TAG, "发送");
                        }
                        SystemClock.sleep(800);
                        isAdd = true;
                        break;
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

        }
    }

    private void performBackClick() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 300L);
    }

    // 遍历控件方法
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Log.i(TAG, "child widget----------------------------" + info.getClassName().toString());
            Log.i(TAG, "showDialog:" + info.canOpenPopup());
            Log.i(TAG, "Text：" + info.getText());
            Log.i(TAG, "windowId:" + info.getWindowId());
            Log.i(TAG, "desc:" + info.getContentDescription());
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

}
