package com.ansen.accessibility

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun open(view: View){
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
}
