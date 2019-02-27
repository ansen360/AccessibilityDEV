package com.ansen.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.KeyguardManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


import java.util.ArrayList

/**
 * Created by Ansen on 2017/1/7 10:31.
 *
 * @E-mail: tomorrow_p@163.com
 * @Blog: http://blog.csdn.net/qq_25804863
 * @Github: https://github.com/ansen360
 * @PROJECT_NAME: CodeRepository
 * @PACKAGE_NAME: com.tomorrow_p.service
 * @Description: AccessibilityService可以获取当前页面的信息(view的节点), 并且可以模拟点击事件
 */
class WXAccessibilityService : AccessibilityService() {
    private var mKeyguardManager: KeyguardManager? = null
    private var mKeyguardLock: KeyguardManager.KeyguardLock? = null
    //唤醒屏幕相关
    private var mPowerManager: PowerManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null
    private var mNodeInfos: MutableList<AccessibilityNodeInfo>? = null
    private var lastCount: Int = 0
    private var isChatUI = false
    private var isNotifyEnter = false


    override fun onServiceConnected() {
        super.onServiceConnected()
        mNodeInfos = ArrayList()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        val className = event.className.toString()
        when (eventType) {
            2048  //监听视图变化事件
            -> {
                if (isChatUI && "android.widget.TextView" == className) {
                    getIncreasePacket()
                    Log.i(TAG, "来消息")
                }

                Log.d("2048", "2048 " + event.className.toString())
            }
            // 监听有通知栏消息的事件
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                val texts = event.text
                if (!texts.isEmpty()) {
                    for (text in texts) {
                        val content = text.toString()
                        Log.d(TAG, "content: $content")
                        if (content.contains("[微信红包]")) {
                            //打开通知栏消息
                            if (event.parcelableData != null && event.parcelableData is Notification) {
                                val notification = event.parcelableData as Notification
                                val pendingIntent = notification.contentIntent
                                try {
                                    pendingIntent.send()
                                    isNotifyEnter = true
                                } catch (e: PendingIntent.CanceledException) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    }
                }
            }

            // 监听窗口发生变化的事件(判断是否进入微信红包消息界面)
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.i(TAG, "className: $className")
                if ("com.tencent.mm.ui.LauncherUI" == className) { //聊天页面
                    if (isNotifyEnter) {
                        getLastPacket()
                        Log.i(TAG, "通知栏: 开")
                    }
                    isNotifyEnter = false
                    isChatUI = true
                } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI" == className) { //红包页面
                    isChatUI = false
                    //拆红包
                    executeClick("com.tencent.mm:id/bg7")
                    //                    executeClick("com.tencent.mm:id/bga");   //返回
                    Log.i(TAG, "拆")
                } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI" == className) { //红包已打开页面
                    isChatUI = false
                    executeClick("com.tencent.mm:id/ge")   //返回
                    Log.i(TAG, "关")
                } else {
                    isChatUI = false
                }
            }
        }


    }

    private fun getIncreasePacket() {
        val rootNode = rootInActiveWindow
        recycle(rootNode)
        if (mNodeInfos!!.size > 0) {
            if (lastCount < mNodeInfos!!.size) {    //有新消息
                mNodeInfos!![mNodeInfos!!.size - 1].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                Log.i(TAG, "新红包")
            }
            lastCount = mNodeInfos!!.size
        }
        mNodeInfos!!.clear()
    }

    private fun getLastPacket() {
        val rootNode = rootInActiveWindow
        recycle(rootNode)
        if (mNodeInfos!!.size > 0) {
            mNodeInfos!![mNodeInfos!!.size - 1].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            lastCount = mNodeInfos!!.size
            mNodeInfos!!.clear()
        }
    }


    /**
     * 打印一个节点的结构
     */
    fun recycle(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            if (info.text != null) {
                if ("领取红包" == info.text.toString()) {
                    //找到一个可以点击的View,模拟点击指定事件
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    var parent: AccessibilityNodeInfo? = info.parent
                    while (parent != null) {
                        if (parent.isClickable) {
                            mNodeInfos!!.add(parent)
                            break
                        }
                        parent = parent.parent
                    }

                }
            }

        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i))
                }
            }
        }
    }


    /**
     * 查找对应控件,模拟点击事件
     */
    private fun executeClick(id: String) {
        val nodeInfo = rootInActiveWindow
        if (nodeInfo != null) {
            // 通过节点View的Text内容来查找
            //            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("开");
            //            通过节点View在xml布局中的id名称
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(id)
            for (n in list) {
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }

    }

    private fun wakeUp(b: Boolean) {
        if (b) {
            //获取电源管理器对象
            mPowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            mWakeLock = mPowerManager!!.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright")

            //点亮屏幕
            mWakeLock!!.acquire()

            //得到键盘锁管理器对象
            mKeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            mKeyguardLock = mKeyguardManager!!.newKeyguardLock("unLock")

            //解锁
            mKeyguardLock!!.disableKeyguard()
        } else {
            //锁屏
            mKeyguardLock!!.reenableKeyguard()

            //释放wakeLock，关灯
            mWakeLock!!.release()
        }

    }

    override fun onInterrupt() {

    }

    companion object {

        private val TAG = "ansen"
    }
}
