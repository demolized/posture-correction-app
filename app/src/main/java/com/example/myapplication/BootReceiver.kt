package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (sharedPreferences.getBoolean("persistent_overlay", false)) {
                val serviceIntent = Intent(context, OverlayService::class.java)
                context.startService(serviceIntent)
            }
        }
    }
}