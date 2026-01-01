package com.tohir.booksplusplus.ui.books

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tohir.booksplusplus.ui.MainActivity.Companion.scheduleDailyReset

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleDailyReset(context)
        }
    }
}