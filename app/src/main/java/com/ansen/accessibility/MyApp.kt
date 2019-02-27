package com.ansen.accessibility

import android.app.Application
import com.orhanobut.hawk.Hawk
import kotlin.properties.Delegates

/**
 * 描述：
 *
 * @author CoderPig on 2018/04/12 11:43.
 */
class MyApp : Application() {
    companion object {
        var instance by Delegates.notNull<MyApp>()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(this).build()
    }
}