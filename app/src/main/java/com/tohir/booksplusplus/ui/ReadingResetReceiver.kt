package com.tohir.booksplusplus.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import com.tohir.booksplusplus.ui.MainActivity.Companion.scheduleDailyReset

class ReadingResetReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs: SharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        prefs.edit { putInt("MINUTES", 0) }
        scheduleDailyReset(context)
    }

}
