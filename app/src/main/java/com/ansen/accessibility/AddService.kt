package com.ansen.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.PendingIntent
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.orhanobut.hawk.Hawk

/**
 * 微信自动加好友服务类
 */

class AddService : AccessibilityService() {

    private var isAdd = false
    private var index = 0
    private val handler = Handler()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        val classNameChr = event.className
        val className = classNameChr.toString()
        Log.d("ansen", event.toString())
        when (eventType) {
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> if (event.parcelableData != null && event.parcelableData is Notification) {
                val notification = event.parcelableData as Notification
                val content = notification.tickerText.toString()
                if (content.contains("请求添加你为朋友")) {
                    val pendingIntent = notification.contentIntent
                    try {
//                        pendingIntent.send()
                    } catch (e: PendingIntent.CanceledException) {
                        e.printStackTrace()
                    }

                }
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d(TAG, className)
                if (Hawk.get(MainActivity.ADD_FRIENDS, true)) {
                    when (className) {
                        "com.tencent.mm.ui.widget.a.c" -> {
                            Log.d(TAG, "隐私提示:  widget.a.c ")
                            val az_ = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/az_")
                            if (az_ != null && az_.size > 0) {
                                az_[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                SystemClock.sleep(500)
                                performBackClick()
                            }
                        }
                        "com.tencent.mm.ui.base.p" -> Log.d(TAG, "隐私提示:  .ui.base.p")
                        "com.tencent.mm.chatroom.ui.ChatroomInfoUI"   // 聊天信息
                            , "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI" -> {
                            Log.d(TAG, "聊天信息 页面")

                            val nodeInfo = rootInActiveWindow   // 获取当前整个活动窗口的根节点 返回值记录View的状态信息
                            // 查找 查看全部群成员 按鈕
                            val listNodes = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/title")
                            if (listNodes != null && listNodes.size == 0) {
                                Log.d(TAG, "沒找到 查看全部群成员 按鈕")

                                val listNodes1 = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list")
                                if (listNodes1 != null && listNodes1.size > 0) {

                                    listNodes1[0].performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                                    Log.d(TAG, "滑动后继续查找")
                                    SystemClock.sleep(1000)
                                    // 查找
                                    val listNodes2 = rootInActiveWindow.findAccessibilityNodeInfosByViewId("android:id/title")
                                    if (listNodes2 != null && listNodes2.size > 0) {
                                        listNodes2[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                        Log.d(TAG, "点击: " + listNodes2[0].text)
                                    }
                                }
                            } else {
                                listNodes!![0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                Log.d(TAG, "点击: " + listNodes[0].text)
                            }
                            index = 0
                            SystemClock.sleep(1000)
                        }
                        "com.tencent.mm.plugin.chatroom.ui.SeeRoomMemberUI", "com.tencent.mm.chatroom.ui.SeeRoomMemberUI" -> {
                            Log.d(TAG, "聊天成员")
                            SystemClock.sleep(3000)
                            isAdd = false
                            if(rootInActiveWindow==null){
                                return
                            }
                            val nodeInfos = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e4_")
                            if (nodeInfos != null && nodeInfos.size > 0) {
                                if (index < nodeInfos[0].childCount) {
                                    nodeInfos[0].getChild(index).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    SystemClock.sleep(1000)
                                    index++
                                } else {
                                    val b = nodeInfos[0].performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                                    SystemClock.sleep(2000)
                                    Log.d(TAG, "翻页: $b")
                                    if (b) {
                                        val nodeInfo1 = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e4_")
                                        if (nodeInfo1 != null && nodeInfo1.size > 0) {
                                            val childCount = nodeInfo1[0].childCount
                                            if (childCount > 0) {
                                                index = 0
                                            }
                                            if (index < childCount) {
                                                nodeInfo1[0].getChild(index).performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        "com.tencent.mm.plugin.profile.ui.ContactInfoUI" -> if (isAdd) {
                            performBackClick()
                            Log.d(TAG, "添加成功")
                        } else {
                            Log.d(TAG, "添加联系页面")
                            val icon = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/iq")
                            if (icon.size > 0) {
                                performBackClick()
                                Log.d(TAG, "已经添加 直接返回")
                            } else {
                                val adds = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cs")
                                if (adds != null && adds.size > 0) {
                                    adds[0].parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    Log.d(TAG, "点击添加")
                                } else {
                                    performBackClick()
                                }
                                SystemClock.sleep(1000)
                            }
                        }
                        "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI" -> {
                            Log.d(TAG, "验证申请")

                            val sends = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jx")
                            if (sends != null && sends.size > 0) {
                                sends[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                Log.d(TAG, "发送")
                            }
                            SystemClock.sleep(800)
                            isAdd = true
                        }
                    }
                }
                if (Hawk.get(MainActivity.FRIEND_SQUARE, true)) {
                    when (className) {
                        "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI" -> {
                            Log.d(TAG, "自动点赞")
                            autoZan()
                        }
                    }
                }
            }
        }
    }

    private fun performBackClick() {
        handler.postDelayed({ performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK) }, 300L)
    }

    //自动点赞
    private fun autoZan() {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            while (true) {
                val rootNode = rootInActiveWindow
                if (rootNode != null) {
                    val listNodes = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eew")
                    if (listNodes != null && listNodes.size > 0) {
                        val listNode = listNodes[0]
                        val zanNodes = listNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ee2")
                        for (zan in zanNodes) {
                            zan.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            Thread.sleep(300)
                            val zsNodes = rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/edq")
                            Thread.sleep(300)
                            if (zsNodes != null && zsNodes.size > 0) {
                                if (zsNodes[0].findAccessibilityNodeInfosByText("赞").size > 0) {
                                    zsNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                }
                            }
                            Thread.sleep(500)
                        }
                        listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
                    }
                } else {
                    break
                }
            }
        }
    }

    // 遍历控件方法
    fun recycle(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            Log.i(TAG, "child widget----------------------------" + info.className.toString())
            Log.i(TAG, "showDialog:" + info.canOpenPopup())
            Log.i(TAG, "Text：" + info.text)
            Log.i(TAG, "windowId:" + info.windowId)
            Log.i(TAG, "desc:" + info.contentDescription)
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i))
                }
            }
        }
    }

    override fun onInterrupt() {

    }

    companion object {

        private val TAG = "AddService"
    }

}
