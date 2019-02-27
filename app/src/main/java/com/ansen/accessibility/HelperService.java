package com.ansen.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * 描述：微信监控服务类
 */

public class HelperService extends AccessibilityService {

    private static final String TAG = "HelperService";
    private Handler handler = new Handler();
    private String userName = "123";

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
//                    case "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI":
//                        addFriend();
//                        break;
//                    case "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI":
//                        verifyFriend();
//                        break;
//                    case "com.tencent.mm.plugin.profile.ui.ContactInfoUI":
//                        performBackClick();
//                        break;
//                    case "com.tencent.mm.ui.LauncherUI":
//                        if (!userName.equals("123")) {
//                            openGroup();
//                        }
//                        break;
//                    case "com.tencent.mm.ui.contact.ChatroomContactUI":
//                        if (!userName.equals("123")) {
//                            inviteGroup();
//                        }
//                        break;
//                    case "com.tencent.mm.ui.chatting.ChattingUI":
//                        if (!userName.equals("123")) {
//                            openGroupSetting();
//                        }
//                        break;
                    /*case "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI":
                        if (userName.equals("123")) {
                            performBackClick();
                        } else {
                            addToGroup();
                        }
                        break;*/
                    case "com.tencent.mm.ui.widget.a.c":
                        Log.d(TAG, "提示:  widget.a.c ");
                        List<AccessibilityNodeInfo> az_ = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/az_");
                        if (az_ != null && az_.size() > 0) {
                            az_.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            SystemClock.sleep(500);
                            performBackClick();
                        }
                        break;
                    case "com.tencent.mm.ui.base.p":
                        Log.d(TAG, "提示:  .ui.base.p");
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
//                        performBackClick();
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

    private boolean isAdd = false;
    private int index = 0;

//    public void recycle(AccessibilityNodeInfo info) {
//        if (info.getChildCount() == 0) {
//            if (info.getText() != null) {
//                if ("领取红包".equals(info.getText().toString())) {
//                    //找到一个可以点击的View,模拟点击指定事件
//                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    AccessibilityNodeInfo parent = info.getParent();
//                    while (parent != null) {
//                        if (parent.isClickable()) {
////                            mNodeInfos.add(parent);
//                            break;
//                        }
//                        parent = parent.getParent();
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < info.getChildCount(); i++) {
//                if (info.getChild(i) != null) {
//                    recycle(info.getChild(i));
//                }
//            }
//        }
//    }

    private void addFriend() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("接受");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo n : list) {
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            } else {
                performBackClick();
            }
        }
    }

    private void verifyFriend() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        //获得用户名
        if (nodeInfo != null) {
            userName = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/d0n").get(0).getText().toString();
            AccessibilityNodeInfo finishNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hd").get(0);
            finishNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void openGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ca5");
            for (AccessibilityNodeInfo info : nodes) {
                if (info.getText().toString().equals("通讯录")) {
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                            if (nodeInfo != null) {
                                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/j5");
                                for (AccessibilityNodeInfo info : nodes) {
                                    if (info.getText().toString().equals("群聊")) {
                                        info.getParent().getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        break;
                                    }
                                }
                            }
                        }
                    }, 500L);
                }
            }
        }
    }

    private void inviteGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a9v");
            for (AccessibilityNodeInfo info : nodes) {
                if (info.getText().toString().equals("小猪的Python学习交流群")) {
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    private void openGroupSetting() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/he").get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void addToGroup() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> listNodes = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
            if (listNodes != null && listNodes.size() > 0) {
                AccessibilityNodeInfo listNode = listNodes.get(0);
                listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                final AccessibilityNodeInfo scrollNodeInfo = getRootInActiveWindow();
                if (scrollNodeInfo != null) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<AccessibilityNodeInfo> nodes = scrollNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/d0b");
                            for (AccessibilityNodeInfo info : nodes) {
                                if (info.getContentDescription().toString().equals("添加成员")) {
                                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    break;
                                }
                            }
                        }
                    }, 1000L);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<AccessibilityNodeInfo> editNodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/arz");
                            if (editNodes != null && editNodes.size() > 0) {
                                AccessibilityNodeInfo editNode = editNodes.get(0);
                                Bundle arguments = new Bundle();
                                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, userName);
                                editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                            }
                        }
                    }, 2300L);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<AccessibilityNodeInfo> cbNodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/kr");
                            if (cbNodes != null) {
                                AccessibilityNodeInfo cbNode = null;
                                if (cbNodes.size() == 1) {
                                    cbNode = cbNodes.get(0);
                                } else if (cbNodes.size() == 2) {
                                    cbNode = cbNodes.get(1);
                                }
                                if (cbNode != null) {
                                    cbNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    AccessibilityNodeInfo sureNode = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/hd").get(0);
                                    sureNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }
                        }
                    }, 3000L);
                }
            }

        }

    }

    private void dialogClick() {
        AccessibilityNodeInfo inviteNode = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aln").get(0);
        inviteNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        userName = "123";
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<AccessibilityNodeInfo> sureNodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aln");
                if (sureNodes != null && sureNodes.size() > 0) {
                    AccessibilityNodeInfo sureNode = sureNodes.get(0);
                    sureNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

            }
        }, 1000L);
    }

    private void performBackClick() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 300L);
    }

    //遍历控件的方法
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
