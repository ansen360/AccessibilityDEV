package com.ansen.accessibility

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val ADD_FRIENDS = "add_friends"
        val FRIEND_SQUARE = "auto_zan"
        val RED_PACKET = "red_packet"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        add_friends.isChecked = Hawk.get(ADD_FRIENDS, true);
//        auto_zan.isChecked = Hawk.get(FRIEND_SQUARE, true);
//
//        add_friends.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) Hawk.put(ADD_FRIENDS, true) else Hawk.put(ADD_FRIENDS, false)
//        }
//        auto_zan.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) Hawk.put(FRIEND_SQUARE, true) else Hawk.put(FRIEND_SQUARE, false)
//        }


    }

    fun open(view: View) {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
}
